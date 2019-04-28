package communitymod;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.objectweb.asm.ClassReader;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfig implements IMixinConfigPlugin {
	private final List<String> bonus = new ArrayList<>();

	@Override
	public void onLoad(String mixinPackage) {
		Map<String, byte[]> worms = new HashMap<>();

		try {
			for (String place : new String[] {
					"/assets/communitymod/textures/blocks/berry_bush.png",
					"/assets/communitymod/textures/items/pizza.png",
			}) {
				BufferedImage dream = ImageIO.read(MixinConfig.class.getResourceAsStream(place));
				ByteArrayOutputStream thought = new ByteArrayOutputStream();

				try (SmallStream stream = new SmallStream(thought)) {
					for (int y = 0; y < dream.getHeight(); y++) {
						for (int x = 0; x < dream.getWidth(); x++) {
							for (byte offset = 16; offset >= 0; offset -= 8) {
								stream.write(2, dream.getRGB(x, y) >> offset & 0x3);
							}
						}
					}
				}

				String worm = new ClassReader(thought.toByteArray()).getClassName();
				worms.put('/' + worm + ".class", thought.toByteArray());

				if (worm.replace('/', '.').startsWith(mixinPackage)) {
					bonus.add(worm.substring(mixinPackage.length()).replace('/', '.'));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Terrible times", e);
		}

		try {
			Method theMethod = null;
			for (Method method : MixinConfig.class.getClassLoader().getClass().getDeclaredMethods()) {
				if (method.getReturnType() == Void.TYPE && method.getParameterCount() == 1 && method.getParameterTypes()[0] == URL.class) {
					theMethod = method;
					break;
				}
			}
			if (theMethod == null) throw new IllegalStateException("Very sad times");

			theMethod.setAccessible(true);
			theMethod.invoke(MixinConfig.class.getClassLoader(), LoopHoleHandler.engauge(worms));
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("The worst times", e);
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
		return bonus;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}

class SmallStream extends OutputStream {
	private static final int MASK[] = {
			0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
			0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff,
			0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff,
			0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
			0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,0xffffffff
	};

	private final OutputStream out;
	private int buffer = 0, bitsLeft = 8;

	public SmallStream(OutputStream out) {
		this.out = out;
	}

	public void write(int bitz, int bits) throws IOException {
		bits &= MASK[bitz];

		while (bitz >= bitsLeft) {
			buffer = buffer << bitsLeft | bits >> bitz - bitsLeft;
			write(buffer);

			bits &= MASK[bitz - bitsLeft];
			bitz -= bitsLeft;
			bitsLeft = 8;
			buffer = 0;
		}

		if (bitz > 0) {
			buffer = buffer << bitz | bits;
			bitsLeft -= bitz;
		}
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void flush() throws IOException {
		if (bitsLeft != 8) {
			write(buffer << bitsLeft);
			buffer = 0;
			bitsLeft = 8;
		}

		out.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}
}

class LoopHoleHandler extends URLStreamHandler {
	private final Map<String, byte[]> holes;

	public static URL engauge(Map<String, byte[]> holes) {
		try {
			return new URL("fabric-community", null, -1, "/", new LoopHoleHandler(holes));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Troubled waters", e);
		}
	}

	public LoopHoleHandler(Map<String, byte[]> holes) {
		this.holes = holes;
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		byte[] loop = holes.get(url.getPath());
		return loop != null ? new URLConnection(url) {
			@Override
			public Permission getPermission() throws IOException {
				return null;
			}

			@Override
			public void connect() throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(loop);
			}
		} : null;
	}
}