package communitymod.block;

import communitymod.api.RadiationSubject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UraniumOreBlock extends Block {
	public UraniumOreBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && player instanceof RadiationSubject) {
			RadiationSubject subject = (RadiationSubject) player;
			// *evil laughter* naive players would mine everything that looks like ore!
			subject.increaseRadiationLevel(1);
		}
		super.onBreak(world, pos, state, player);
	}

}
