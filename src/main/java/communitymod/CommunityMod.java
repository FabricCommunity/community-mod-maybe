package communitymod;

import communitymod.block.UraniumOreBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class CommunityMod implements ModInitializer {
	public static final String MODID = "communitymod";

	public static final Block URANIUM_ORE = new UraniumOreBlock(
			FabricBlockSettings.of(Material.STONE).strength(3.0F, 3.0F).lightLevel(5).ticksRandomly().build());
	public static final Block URANIUM_GLASS = new GlassBlock(FabricBlockSettings.of(Material.GLASS)
			.sounds(BlockSoundGroup.GLASS).strength(3.0F, 3.0F).lightLevel(10).build()) {
		public BlockRenderLayer getRenderLayer() {
			return BlockRenderLayer.TRANSLUCENT;
		}
	};

	public static final Item URANIUM_NUGGET = new Item(
			new Item.Settings().itemGroup(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON));
	public static final Item URANIUM_INGOT = new Item(
			new Item.Settings().itemGroup(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "uranium_ore"), URANIUM_ORE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "uranium_ore"),
				new BlockItem(URANIUM_ORE, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
		Registry.register(Registry.BLOCK, new Identifier(MODID, "uranium_glass"), URANIUM_GLASS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "uranium_glass"),
				new BlockItem(URANIUM_GLASS, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));

		Registry.register(Registry.ITEM, new Identifier(MODID, "uranium_nugget"), URANIUM_NUGGET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "uranium_ingot"), URANIUM_INGOT);
	}
}
