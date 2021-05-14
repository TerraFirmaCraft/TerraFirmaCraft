package net.dries007.tfc.common.entities.aquatic;

import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.AquaticMovementController;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.mixin.entity.ai.goal.GoalSelectorAccessor;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class JellyfishEntity extends TFCAbstractGroupFishEntity
{
    private static final DataParameter<Integer> DATA_VARIANT = EntityDataManager.defineId(JellyfishEntity.class, DataSerializers.INT);
    private static final ResourceLocation[] LOCATIONS = {
        new ResourceLocation(MOD_ID, "textures/entity/animal/jellyfish_blue.png"),
        new ResourceLocation(MOD_ID, "textures/entity/animal/jellyfish_red.png"),
        new ResourceLocation(MOD_ID, "textures/entity/animal/jellyfish_yellow.png"),
        new ResourceLocation(MOD_ID, "textures/entity/animal/jellyfish_purple.png"),
        new ResourceLocation(MOD_ID, "textures/entity/animal/jellyfish_orange.png")
    };

    public JellyfishEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
        moveControl = new AquaticMovementController(this, false, 1);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Set<PrioritizedGoal> availableGoals = ((GoalSelectorAccessor) goalSelector).getAvailableGoals();
        availableGoals.removeIf(priority -> priority.getGoal() instanceof AvoidEntityGoal);

        goalSelector.addGoal(2, new TFCAvoidEntityGoal<>(this, OrcaEntity.class, 8.0F, 5.0D, 5.4D));
    }


    @Override
    public void playerTouch(PlayerEntity entityIn)
    {
        entityIn.hurt(DamageSource.GENERIC, 1.0F);//todo once bulk is merged put a dmg source in
        super.playerTouch(entityIn);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT dataTag)
    {
        spawnData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnData, dataTag);
        if (dataTag != null && dataTag.contains("BucketVariantTag", 3))
        {
            setVariant(dataTag.getInt("BucketVariantTag"));
        }
        else
        {
            setVariant(random.nextInt(LOCATIONS.length));
        }
        return spawnData;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        setVariant(compound.getInt("Variant"));
    }

    public void setVariant(int variant)
    {
        entityData.set(DATA_VARIANT, variant);
    }

    public int getVariant()
    {
        return entityData.get(DATA_VARIANT);
    }

    public ResourceLocation getTexture()
    {
        return LOCATIONS[getVariant()];
    }

    @Override
    protected ItemStack getBucketItemStack()
    {
        return new ItemStack(Items.SAND);
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }
}
