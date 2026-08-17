/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.camel;

import com.mojang.serialization.Dynamic;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.CommonAnimalData;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.config.animals.MammalConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class DromedaryCamel extends AbstractCamel implements HorseProperties
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return createBaseHorseAttributes()
            .add(Attributes.MAX_HEALTH, 32.0)
            .add(Attributes.MOVEMENT_SPEED, 0.1F)
            .add(Attributes.JUMP_STRENGTH, 0.42F)
            .add(Attributes.STEP_HEIGHT, 1.5);
    }

    private static final CommonAnimalData ANIMAL_DATA = CommonAnimalData.create(DromedaryCamel.class);
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(DromedaryCamel.class, EntityDataSerializers.LONG);

    @Nullable private CompoundTag genes;
    private final AnimalConfig config;
    private final MammalConfig mammalConfig;

    public DromedaryCamel(EntityType<? extends Camel> type, Level level, MammalConfig config)
    {
        super(type, level);
        this.config = config.inner();
        this.mammalConfig = config;
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TFCCamelAi.makeBrain(TFCCamelAi.brainProvider().makeBrain(dynamic));
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        super.createGenes(tag, maleProperties);
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        super.applyGenes(tag, babyProperties);
    }

    @Override
    public void setInLove(@Nullable Player player) {} // nobody could love a camel

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        DromedaryCamel other = (DromedaryCamel) otherAnimal;
        return this.getGender() != other.getGender()
            && this.isReadyToMate() && other.isReadyToMate()
            && checkExtraBreedConditions(other);
    }

    @Override
    public boolean checkExtraBreedConditions(TFCAnimalProperties otherAnimal)
    {
        if (otherAnimal instanceof DromedaryCamel otherCamel)
        {
            return vanillaParentingCheck(this) && vanillaParentingCheck(otherCamel);
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        InteractionResult result = super.mobInteract(player, hand);
        if (result == InteractionResult.PASS)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (!this.isBaby())
            {
                if (this.isTamed() && player.isSecondaryUseActive())
                {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                if (this.isVehicle())
                {
                    return InteractionResult.PASS;
                }
            }

            if (!stack.isEmpty())
            {
                InteractionResult res = stack.interactLivingEntity(player, this, hand);
                if (res.consumesAction())
                {
                    return res;
                }

                if (!this.isTamed())
                {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                final boolean canBeSaddled = !this.isBaby() && !this.isSaddled() && stack.is(Items.SADDLE);
                if (this.isBodyArmorItem(stack) || canBeSaddled)
                {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }

            if (this.isBaby())
            {
                return InteractionResult.PASS;
            }
            else
            {
                if (isTamed() && getOwnerUUID() == null)
                {
                    tameWithName(player);
                }
                if (canAddPassenger(player))
                {
                    this.doPlayerRide(player);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return result;
    }

    @Override
    public boolean isTamed()
    {
        return getFamiliarity() > TAMED_FAMILIARITY;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        if (spawnType != MobSpawnType.BREEDING)
        {
            initCommonAnimalData(level, difficulty, spawnType);
        }
        setPregnantTime(-1L);
        return spawnData;
    }

    @Override
    public MammalConfig getMammalConfig()
    {
        return mammalConfig;
    }

    @Override
    public long getPregnantTime()
    {
        return entityData.get(PREGNANT_TIME);
    }

    @Override
    public void setPregnantTime(long day)
    {
        entityData.set(PREGNANT_TIME, day);
    }

    @Override
    public @Nullable CompoundTag getGenes()
    {
        return genes;
    }

    @Override
    public void setGenes(@Nullable CompoundTag tag)
    {
        genes = tag;
    }

    @Override
    public CommonAnimalData animalData()
    {
        return ANIMAL_DATA;
    }

    @Override
    public AnimalConfig animalConfig()
    {
        return config;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        animalData().define(builder);
        builder.define(PREGNANT_TIME, -1L);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        saveCommonAnimalData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        readCommonAnimalData(nbt);
    }

    @Override
    public boolean isBaby()
    {
        return getAgeType() == Age.CHILD;
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0);
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Nullable
    @Override
    public DromedaryCamel getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        final AgeableMob mob = super.getBreedOffspring(level, other);
        return mob instanceof DromedaryCamel camel ? camel : null;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data)
    {
        super.onSyncedDataUpdated(data);
        if (ANIMAL_DATA.birthTick().equals(data))
        {
            refreshDimensions();
        }
    }

    @Override
    protected void customServerAiStep()
    {
        // Don't want to call super.customServerAiStep() here because of CamelAi.updateActivity(this)
        ((Brain<DromedaryCamel>) getBrain()).tick((ServerLevel) level(), this);
        TFCCamelAi.updateActivity(this);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 20 == 0)
        {
            tickAnimalData();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource src)
    {
        return src.is(DamageTypes.CACTUS) || super.isInvulnerableTo(src);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCGroundPathNavigation(this, level);
    }

    @Override
    public boolean isInWall()
    {
        return !level().isClientSide && super.isInWall();
    }

    @Override
    protected void pushEntities()
    {
        if (!level().isClientSide) super.pushEntities();
    }
}
