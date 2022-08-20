/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.livestock.CommonAnimalData;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.mixin.accessor.HorseAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class TFCHorse extends Horse implements HorseProperties
{
    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(TFCHorse.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> BIRTHDAY = SynchedEntityData.defineId(TFCHorse.class, EntityHelpers.LONG_SERIALIZER);
    private static final EntityDataAccessor<Float> FAMILIARITY = SynchedEntityData.defineId(TFCHorse.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> USES = SynchedEntityData.defineId(TFCHorse.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FERTILIZED = SynchedEntityData.defineId(TFCHorse.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> OLD_DAY = SynchedEntityData.defineId(TFCHorse.class, EntityHelpers.LONG_SERIALIZER);
    private static final CommonAnimalData ANIMAL_DATA = new CommonAnimalData(GENDER, BIRTHDAY, FAMILIARITY, USES, FERTILIZED, OLD_DAY);
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(TFCHorse.class, EntityHelpers.LONG_SERIALIZER);

    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    @Nullable private CompoundTag genes;
    private TFCAnimalProperties.Age lastAge = TFCAnimalProperties.Age.CHILD;
    private final Supplier<? extends SoundEvent> ambient;
    private final Supplier<? extends SoundEvent> hurt;
    private final Supplier<? extends SoundEvent> death;
    private final Supplier<? extends SoundEvent> step;
    private final Supplier<? extends SoundEvent> eat;
    private final Supplier<? extends SoundEvent> angry;
    private final AnimalConfig config;
    private final MammalConfig mammalConfig;

    public TFCHorse(EntityType<? extends TFCHorse> type, Level level)
    {
        this(type, level, TFCSounds.HORSE, () -> SoundEvents.HORSE_EAT, () -> SoundEvents.HORSE_ANGRY, TFCConfig.SERVER.horseConfig);
    }

    public TFCHorse(EntityType<? extends TFCHorse> type, Level level, TFCSounds.EntitySound sounds, Supplier<? extends SoundEvent> eatSound, Supplier<? extends SoundEvent> angrySound, MammalConfig config)
    {
        super(type, level);
        this.matingTime = Calendars.get(level).getTicks();
        this.lastFDecay = Calendars.get(level).getTotalDays();
        this.ambient = sounds.ambient();
        this.hurt = sounds.hurt();
        this.death = sounds.death();
        this.step = sounds.step();
        this.eat = eatSound;
        this.angry = angrySound;
        this.config = config.inner();
        this.mammalConfig = config;
    }

    // HORSE SPECIFIC STUFF

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        HorseProperties.super.createGenes(tag, maleProperties);
        AbstractHorse female = getEntity();
        AbstractHorse male = (AbstractHorse) maleProperties;
        tag.putDouble("jumpStrength", male.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + female.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
        tag.putDouble("movementSpeed", male.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + female.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
        final boolean isMule = maleProperties instanceof TFCDonkey;
        tag.putBoolean("isMule", isMule);
        if (!isMule && male instanceof TFCHorse maleHorse)
        {
            tag.putInt("markings1", maleHorse.getMarkings().getId());
            tag.putInt("markings2", getMarkings().getId());
            tag.putInt("variant1", maleHorse.getVariant().getId());
            tag.putInt("variant2", getVariant().getId());
        }
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        HorseProperties.super.applyGenes(tag, babyProperties);
        if (babyProperties instanceof TFCHorse baby && tag.contains("markings1")) // if we actually set the genes, and we have a horse, set the traits.
        {
            EntityHelpers.setNullableAttribute(baby, Attributes.JUMP_STRENGTH, (tag.getDouble("jumpStrength") + generateRandomJumpStrength()) / 3);
            EntityHelpers.setNullableAttribute(baby, Attributes.MOVEMENT_SPEED, (tag.getDouble("movementSpeed") + generateRandomSpeed()) / 3);

            Variant variant;
            final int i = this.random.nextInt(9);
            if (i < 4)
            {
                variant = Variant.byId(tag.getInt("markings1"));
            }
            else if (i < 8)
            {
                variant = Variant.byId(tag.getInt("markings1"));
            }
            else
            {
                variant = Util.getRandom(Variant.values(), this.random);
            }

            final int j = this.random.nextInt(5);
            Markings markings;
            if (j < 2)
            {
                markings = Markings.byId(tag.getInt("variant1"));
            }
            else if (j < 4)
            {
                markings = Markings.byId(tag.getInt("variant2"));
            }
            else
            {
                markings = Util.getRandom(Markings.values(), this.random);
            }

            ((HorseAccessor) baby).invoke$setVariantAndTypeMarkings(variant, markings);
        }
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.HORSE_FOOD;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 3);
        goalSelector.addGoal(3, new TemptGoal(this, 1.25f, Ingredient.of(getFoodTag()), false));
    }

    @Override
    public EntityType<?> getEntityTypeForBaby()
    {
        final CompoundTag genes = getGenes();
        return genes != null && genes.contains("isMule") && genes.getBoolean("isMule") ? TFCEntities.MULE.get() : TFCEntities.HORSE.get();
    }

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        return otherAnimal instanceof TFCAnimalProperties other && this.getGender() != other.getGender() && other.isReadyToMate() && checkExtraBreedConditions(other);
    }

    @Override
    public boolean checkExtraBreedConditions(TFCAnimalProperties otherAnimal)
    {
        if (HorseProperties.super.checkExtraBreedConditions(otherAnimal) && (otherAnimal instanceof TFCDonkey || otherAnimal instanceof Horse))
        {
            AbstractHorse otherHorse = (AbstractHorse) otherAnimal;
            return TFCChestedHorse.vanillaParentingCheck(this) && TFCChestedHorse.vanillaParentingCheck(otherHorse);
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        // triggers feeding actions
        InteractionResult result = HorseProperties.super.mobInteract(player, hand);
        if (result == InteractionResult.PASS)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (!this.isBaby())
            {
                if (this.isTamed() && player.isSecondaryUseActive())
                {
                    this.openInventory(player);
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }

                if (this.isVehicle())
                {
                    return InteractionResult.PASS; // vanilla calls super
                }
            }

            if (!stack.isEmpty())
            {
                // food eating in vanilla is here we handled it in interface super

                // this is vanilla making adding saddles complicated
                InteractionResult res = stack.interactLivingEntity(player, this, hand);
                if (res.consumesAction())
                {
                    return res;
                }

                if (!this.isTamed())
                {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }

                final boolean canBeSaddled = !this.isBaby() && !this.isSaddled() && stack.is(Items.SADDLE);
                if (this.isArmor(stack) || canBeSaddled)
                {
                    this.openInventory(player);
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }

            if (this.isBaby())
            {
                return InteractionResult.PASS; // vanilla calls super here
            }
            else
            {
                if (isTamed() && getOwnerUUID() == null) // tfc: add an owner
                {
                    tameWithName(player);
                }
                this.doPlayerRide(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
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
    protected SoundEvent getEatingSound()
    {
        super.getEatingSound();
        return eat.get();
    }

    @Override
    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return angry.get();
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level.getBlockState(blockPosition()), TFCTags.Blocks.PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }

    // BEGIN COPY-PASTE FROM TFC ANIMAL

    @Override
    public TFCAnimalProperties.Age getLastAge()
    {
        return lastAge;
    }

    @Override
    public void setLastAge(TFCAnimalProperties.Age lastAge)
    {
        this.lastAge = lastAge;
    }


    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag)
    {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
        if (reason != MobSpawnType.BREEDING)
        {
            initCommonAnimalData();
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
    public void setGenes(@Nullable CompoundTag tag)
    {
        genes = tag;
    }

    @Override
    @Nullable
    public CompoundTag getGenes()
    {
        return genes;
    }

    @Override
    public AnimalConfig animalConfig()
    {
        return config;
    }

    @Override
    public CommonAnimalData animalData()
    {
        return ANIMAL_DATA;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        registerCommonData();
        entityData.define(PREGNANT_TIME, -1L);
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
        return getAgeType() == TFCAnimalProperties.Age.CHILD;
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0); // no-op vanilla aging
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof TFCAnimalProperties otherFertile)
        {
            this.onFertilized(otherFertile);
        }
        else if (other == this)
        {
            AgeableMob baby = ((EntityType<AgeableMob>) getEntityTypeForBaby()).create(level);
            if (baby instanceof TFCAnimalProperties prop)
            {
                setBabyTraits(prop);
                return baby;
            }
        }
        return null;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data)
    {
        super.onSyncedDataUpdated(data);
        if (BIRTHDAY.equals(data))
        {
            refreshDimensions();
        }
    }

    @Override
    public long getLastFamiliarityDecay()
    {
        return lastFDecay;
    }

    @Override
    public void setLastFamiliarityDecay(long days)
    {
        lastFDecay = days;
    }

    @Override
    public void setLastFed(long fed)
    {
        lastFed = fed;
    }

    @Override
    public long getLastFed()
    {
        return lastFed;
    }

    @Override
    public void setMated(long ticks)
    {
        matingTime = ticks;
    }

    @Override
    public long getMated()
    {
        return matingTime;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level.getGameTime() % 20 == 0)
        {
            tickAnimalData();
        }
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return HorseProperties.super.isFood(stack);
    }

    @Override
    public Component getTypeName()
    {
        return getGenderedTypeName();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return ambient.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        super.getHurtSound(src);
        return hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return death.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block)
    {
        super.playStepSound(pos, block);
        this.playSound(step.get(), 0.15F, 1.0F);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getBrightness(pos) - 0.5F;
    }
}
