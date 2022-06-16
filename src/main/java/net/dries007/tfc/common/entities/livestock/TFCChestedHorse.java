/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.util.Locale;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class TFCChestedHorse extends AbstractChestedHorse implements TFCAnimalProperties
{
    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BIRTHDAY = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> FAMILIARITY = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> USES = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FERTILIZED = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.BOOLEAN);
    private static final CommonAnimalData ANIMAL_DATA = new CommonAnimalData(GENDER, BIRTHDAY, FAMILIARITY, USES, FERTILIZED);

    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    private final Supplier<? extends SoundEvent> ambient;
    private final Supplier<? extends SoundEvent> hurt;
    private final Supplier<? extends SoundEvent> death;
    private final Supplier<? extends SoundEvent> step;
    private final Supplier<? extends SoundEvent> eat;
    private final Supplier<? extends SoundEvent> angry;
    private final AnimalConfig config;

    public TFCChestedHorse(EntityType<? extends TFCChestedHorse> type, Level level, TFCSounds.EntitySound sounds, Supplier<? extends SoundEvent> eatSound, Supplier<? extends SoundEvent> angrySound, AnimalConfig config)
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
        this.config = config;
    }

    // BEGIN COPY-PASTE FROM TFC ANIMAL

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
    public boolean isReadyToMate()
    {
        if (this.getAgeType() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || this.isHungry())
        {
            return false;
        }
        return this.matingTime + TFCAnimal.MATING_COOLDOWN_DEFAULT_TICKS <= Calendars.SERVER.getTicks();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        saveCommonAnimalData(nbt);
        nbt.putLong("fed", lastFed);
        nbt.putLong("decay", lastFDecay);
        nbt.putLong("mating", matingTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        readCommonAnimalData(nbt);
        this.lastFed = nbt.getLong("fed");
        this.lastFDecay = nbt.getLong("decay");
        this.matingTime = nbt.getLong("mating");
    }

    @Override
    public boolean isBaby()
    {
        return getAgeType() == Age.CHILD;
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0); // no-op vanilla aging
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof TFCAnimalProperties otherFertile)
        {
            this.setFertilized(true);
            this.resetLove();
            this.onFertilized(otherFertile);
        }
        else if (other == this)
        {
            TFCAnimal baby = ((EntityType<TFCAnimal>) getType()).create(level);
            if (baby != null)
            {
                baby.setGender(Gender.valueOf(random.nextBoolean()));
                baby.setBirthDay((int) Calendars.SERVER.getTotalDays());
                baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
                return baby;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag)
    {
        if (reason != MobSpawnType.BREEDING)
        {
            setGender(Gender.valueOf(random.nextBoolean()));
            setAge(0);
            setBirthDay(EntityHelpers.getRandomGrowth(this.level, getDaysToAdulthood()));
            setFamiliarity(0);
            setFertilized(false);
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
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
    public boolean isFood(ItemStack stack)
    {
        return TFCAnimalProperties.super.isFood(stack);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        registerCommonData();
    }

    @Override
    public boolean isHungry()
    {
        return lastFed < Calendars.SERVER.getTotalDays();
    }

    @Override
    public Type getTFCAnimalType()
    {
        return Type.MAMMAL;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!level.isClientSide())
        {
            // Is it time to decay familiarity?
            // If this entity was never fed(eg: new born, wild)
            // or wasn't fed yesterday(this is the starting of the second day)
            if (this.lastFDecay > -1 && this.lastFDecay + 1 < Calendars.SERVER.getTotalDays())
            {
                float familiarity = getFamiliarity();
                if (familiarity < 0.3f)
                {
                    familiarity -= 0.02 * (Calendars.SERVER.getTotalDays() - this.lastFDecay);
                    this.lastFDecay = Calendars.SERVER.getTotalDays();
                    this.setFamiliarity(familiarity);
                }
            }
            if (this.getGender() == Gender.MALE && this.isReadyToMate())
            {
                this.matingTime = Calendars.SERVER.getTicks();
                EntityHelpers.findFemaleMate(this);
            }
            //todo unimplemented: despawning if left in the wild? dying when old?
        }
    }

    protected InteractionResult eatFood(@Nonnull ItemStack stack, InteractionHand hand, Player player)
    {
        heal(1f);
        if (!this.level.isClientSide)
        {
            lastFed = Calendars.SERVER.getTotalDays();
            lastFDecay = lastFed; //No decay needed
            this.usePlayerItem(player, hand, stack);
            if (this.getAgeType() == Age.CHILD || this.getFamiliarity() < getAdultFamiliarityCap())
            {
                float familiarity = this.getFamiliarity() + 0.06f;
                if (this.getAgeType() != Age.CHILD)
                {
                    familiarity = Math.min(familiarity, getAdultFamiliarityCap());
                }
                this.setFamiliarity(familiarity);
            }
            playSound(SoundEvents.PLAYER_BURP, 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof SpawnEggItem)
            {
                return super.mobInteract(player, hand); // Let vanilla spawn a baby
            }
            else if (this.isFood(stack) && player.isShiftKeyDown())
            {
                if (this.isHungry())
                {
                    return eatFood(stack, hand, player);
                }
                else
                {
                    if (!level.isClientSide())
                    {
                        //Show tooltips
                        if (this.isFertilized() && this.getTFCAnimalType() == Type.MAMMAL)
                        {
                            player.displayClientMessage(new TranslatableComponent("tfc.tooltip.animal.pregnant", getTypeName().getString()), true);
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getTypeName()
    {
        return new TranslatableComponent(getType().getDescriptionId() + "." + getGender().name().toLowerCase(Locale.ROOT));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return ambient.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return death.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block)
    {
        this.playSound(step.get(), 0.15F, 1.0F);
    }

    @Override
    public void setMated()
    {
        matingTime = Calendars.get().getTicks();
    }

    // BEGIN HORSE SPECIFIC STUFF

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        // todo: special horse handling
        if (otherAnimal.getClass() != getClass()) return false;
        TFCChestedHorse other = (TFCChestedHorse) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Override
    protected SoundEvent getEatingSound()
    {
        return eat.get();
    }

    @Override
    protected SoundEvent getAngrySound()
    {
        return angry.get();
    }
}
