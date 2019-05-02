package communitymod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import communitymod.CommunityMod;
import communitymod.api.RadiationSubject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements RadiationSubject {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	public int getRadiationLevel() {
		return this.dataTracker.get(RadiationSubject.RADIATION_LEVEL);
	}

	@Override
	public void setRadiationLevel(int radiationLevel) {
		this.dataTracker.set(RadiationSubject.RADIATION_LEVEL, MathHelper.clamp(radiationLevel, 0, 255));
	}

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	protected void initDataTracker(CallbackInfo ci) {
		this.dataTracker.startTracking(RadiationSubject.RADIATION_LEVEL, 0);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	protected void tick(CallbackInfo ci) {
		int radiation = getRadiationLevel();
		if (radiation > 0 && this.age % 256 * 10 == 0) {
			decreaseRadiationLevel(1);
			radiation--;
		}
		Entity castWorkaround = this;
		LivingEntity living = (LivingEntity) castWorkaround;
		if (radiation > 0 && this.age % (256 - radiation) == 0 && !living.isUndead()) {
			// golden apples won't help
			this.damage(CommunityMod.RADIATION_DAMAGE, 1);
		}
		if (radiation > 250 && !living.hasStatusEffect(StatusEffects.GLOWING)) {
			// that could be achievement! *evil laughter*
			living.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000));
		}
	}

	@Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("RadiationLevel", getRadiationLevel());
	}

	@Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		setRadiationLevel(tag.getInt("RadiationLevel"));
	}
}
