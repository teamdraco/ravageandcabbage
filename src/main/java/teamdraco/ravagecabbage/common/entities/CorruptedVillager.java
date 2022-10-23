package teamdraco.ravagecabbage.common.entities;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import teamdraco.ravagecabbage.registry.RCEntities;

public class CorruptedVillager extends Villager {

	int corruptionTick = 1800;
	int healthTick = 1800;
	private static final EntityDataAccessor<Boolean> CURING = SynchedEntityData.defineId(CorruptedVillager.class, EntityDataSerializers.BOOLEAN);


	public CorruptedVillager(EntityType<? extends Villager> p_35381_, Level p_35382_) {
		super(p_35381_, p_35382_);
	}

	public boolean isCuring() {
		return this.entityData.get(CURING);
	}

	private void setCuring(boolean isCuring) {
		this.entityData.set(CURING, isCuring);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CURING, false);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("IsCuring", this.isCuring());
		compound.putInt("CorruptionTick", corruptionTick);
		compound.putInt("HealthTick", healthTick);
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setCuring(compound.getBoolean("IsCuring"));
		corruptionTick = compound.getInt("CorruptionTick");
		healthTick = compound.getInt("HealthTick");
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34378_, DifficultyInstance p_34379_, MobSpawnType p_34380_, @Nullable SpawnGroupData p_34381_, @Nullable CompoundTag p_34382_) {
		this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_34378_.getBiome(this.blockPosition()))));
		return super.finalizeSpawn(p_34378_, p_34379_, p_34380_, p_34381_, p_34382_);
	}



	public void tick() {
		super.tick();
		System.out.println(corruptionTick);
		System.out.println(healthTick);
		if (corruptionTick % 10 == 0 && !this.isCuring()) {
			this.addParticlesAroundSelf(ParticleTypes.SPLASH);
		} else {
			if (healthTick % 10 == 0 && this.isCuring()) {
				this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
			}
		}
		if (!this.isCuring()) {
			corruptionTick--;
		}
		if (this.hasEffect(MobEffects.REGENERATION)) {
			this.setCuring(true);
		}
		if (this.isCuring()) {
			healthTick--;
		}
		if (corruptionTick <= 0) {
			RCRavagerEntity ravager = new RCRavagerEntity(RCEntities.RAVAGER.get(), level);
			Vec3 pos = new Vec3(this.getX(), this.getY(), this	.getZ());
			ravager.setPos(pos);
			this.remove(RemovalReason.DISCARDED);
			level.addFreshEntity(ravager);
		}
		if (healthTick <= 0) {
			Villager villager = new Villager(EntityType.VILLAGER, this.getLevel());
			Vec3 pos = new Vec3(this.getX(), this.getY(), this.getZ());
			villager.setPos(pos);
			villager.setVillagerData(this.getVillagerData());
			level.addFreshEntity(villager);
			this.remove(RemovalReason.DISCARDED);
		}

	}

}
