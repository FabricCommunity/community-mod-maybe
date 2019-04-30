package communitymod.block;

import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class UraniumOreBlock extends Block {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public static final DustParticleParameters PARTICLES = new DustParticleParameters(0.3F, 0.95F, 0.05F, 0.5F);

	public UraniumOreBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, false));
	}

	public int getLuminance(BlockState state) {
		return state.get(LIT) ? super.getLuminance(state) : 0;
	}

	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		light(state, world, pos);
		super.onBlockBreakStart(state, world, pos, player);
	}

	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		light(world.getBlockState(pos), world, pos);
		super.onSteppedOn(world, pos, entity);
	}

	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hitresult) {
		light(state, world, pos);
		return super.activate(state, world, pos, player, hand, hitresult);
	}

	private static void light(BlockState state, World world, BlockPos pos) {
		spawnParticles(world, pos);
		if (!state.get(LIT)) {
			world.setBlockState(pos, state.with(LIT, true), 3);
		}
	}

	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			world.setBlockState(pos, state.with(LIT, false), 3);
		}
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			spawnParticles(world, pos);
		}
	}

	private static void spawnParticles(World world, BlockPos pos) {
		Random random = world.random;
		Direction[] directions = Direction.values();
		int dircount = directions.length;

		for (int i = 0; i < dircount; ++i) {
			Direction direction = directions[i];
			BlockPos pos2 = pos.offset(direction);
			if (!world.getBlockState(pos2).isFullOpaque(world, pos2) && world.random.nextInt(16) == 0) {
				Direction.Axis axis = direction.getAxis();
				double dx = axis == Direction.Axis.X ? 0.5D + 0.5625D * direction.getOffsetX() : random.nextFloat();
				double dy = axis == Direction.Axis.Y ? 0.5D + 0.5625D * direction.getOffsetY() : random.nextFloat();
				double dz = axis == Direction.Axis.Z ? 0.5D + 0.5625D * direction.getOffsetZ() : random.nextFloat();
				world.addParticle(PARTICLES, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
		factory.with(LIT);
	}
}
