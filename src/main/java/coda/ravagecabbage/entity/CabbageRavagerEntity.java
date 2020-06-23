package coda.ravagecabbage.entity;

import coda.ravagecabbage.init.ItemInit;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class CabbageRavagerEntity extends RavagerEntity {
    private static final DataParameter<Boolean> BABY = EntityDataManager.createKey(CabbageRavagerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(TameableEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected int growingAge;

    public CabbageRavagerEntity(EntityType<? extends CabbageRavagerEntity> type, World world) {
        super(type, world);
    }

    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setGrowingAge(-24000);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(BABY, false);
        this.dataManager.register(TAMED, false);
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        IAttributeInstance attribute = getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        attribute.setBaseValue(attribute.getBaseValue() * 2);
    }

    public int getGrowingAge() {
        if (this.world.isRemote) {
            return this.dataManager.get(BABY) ? -1 : 1;
        } else {
            return this.growingAge;
        }
    }

    public void setGrowingAge(int age) {
        int i = this.growingAge;
        this.growingAge = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.dataManager.set(BABY, age < 0);
        }
    }

    public void livingTick() {
        super.livingTick();
        if (this.world.isRemote) {
            if (this.isAlive() && isTamed()) {
                int i = this.getGrowingAge();
                if (i < 0) {
                    ++i;
                    this.setGrowingAge(i);
                } else if (i > 0) {
                    --i;
                    this.setGrowingAge(i);
                }
            }
        }
    }

    public Optional<UUID> getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerId(@Nullable UUID uniqueId) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            Optional<UUID> id = this.getOwnerId();
            return id.map(uuid -> this.world.getPlayerByUuid(uuid)).orElse(null);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Age", this.getGrowingAge());
        this.getOwnerId().ifPresent(id -> compound.putUniqueId("OwnerUUID", id));
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setGrowingAge(compound.getInt("Age"));
        if (compound.hasUniqueId("OwnerUUID")) this.setOwnerId(compound.getUniqueId("OwnerUUID"));
    }

    @OnlyIn(Dist.CLIENT)
    protected void playTameEffect() {
        IParticleData iparticledata = ParticleTypes.HEART;
        for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(iparticledata, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 7) {
            this.playTameEffect();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isTamed() {
        return this.dataManager.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.dataManager.set(TAMED, tamed);
    }

    @Override
    public boolean isChild() {
        return getGrowingAge() < 0;
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ItemInit.RAVAGER_MILK) {
            if (!player.abilities.isCreativeMode) stack.shrink(1);
            playTameEffect();
            world.setEntityState(this, (byte) 7);
            return true;
        }
        return super.processInteract(player, hand);
    }

    public Team getTeam() {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    public void onDeath(DamageSource cause) {
        if (!this.world.isRemote && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
        }

        super.onDeath(cause);
    }
}
