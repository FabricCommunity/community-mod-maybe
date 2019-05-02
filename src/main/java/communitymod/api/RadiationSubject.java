package communitymod.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public interface RadiationSubject {
	static TrackedData<Integer> RADIATION_LEVEL = DataTracker.registerData(LivingEntity.class,
			TrackedDataHandlerRegistry.INTEGER);

	int getRadiationLevel();

	void setRadiationLevel(int radiationLevel);

	default void increaseRadiationLevel(int amount) {
		this.setRadiationLevel(this.getRadiationLevel() + amount);
	}

	default void decreaseRadiationLevel(int amount) {
		this.setRadiationLevel(this.getRadiationLevel() - amount);
	}
}
