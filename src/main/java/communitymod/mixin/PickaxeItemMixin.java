package communitymod.mixin;

import communitymod.api.CustomBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import communitymod.CommunityMod;
import net.minecraft.block.BlockState;
import net.minecraft.item.PickaxeItem;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {

    @Inject(method = "isEffectiveOn", at = @At("HEAD"), cancellable = true)
    private void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (state.getBlock() instanceof CustomBlock) {
            CustomBlock block = (CustomBlock) state.getBlock();
            PickaxeItem item = (((PickaxeItem) (Object) this));
            int level = item.getType().getMiningLevel();
            if (level >= block.getEffectivenessLevel()) {
                ci.setReturnValue(true);
                ci.cancel();
            } else {
                ci.setReturnValue(false);
                ci.cancel();
            }
        }
    }
}
