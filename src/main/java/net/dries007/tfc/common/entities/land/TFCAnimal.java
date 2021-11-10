package net.dries007.tfc.common.entities.land;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public abstract class TFCAnimal extends Animal implements TFCAnimalProperties
{
    public static final long MATING_COOLDOWN_DEFAULT_TICKS = ICalendar.TICKS_IN_HOUR * 2;

    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(TFCAnimal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BIRTHDAY = SynchedEntityData.defineId(TFCAnimal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> FAMILIARITY = SynchedEntityData.defineId(TFCAnimal.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> USES = SynchedEntityData.defineId(TFCAnimal.class, EntityDataSerializers.INT);
    //Is this female fertilized? (in oviparous, the egg laying is fertilized, for mammals this is pregnancy)
    private static final EntityDataAccessor<Boolean> FERTILIZED = SynchedEntityData.defineId(TFCAnimal.class, EntityDataSerializers.BOOLEAN);

    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females

    public TFCAnimal(EntityType<? extends Animal> type, Level level)
    {
        super(type, level);
        this.matingTime = Calendars.get(level).getTicks();
        this.lastFDecay = Calendars.get(level).getTotalDays();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(GENDER, true);
        entityData.define(BIRTHDAY, 0);
        entityData.define(FAMILIARITY, 0F);
        entityData.define(USES, 0);
        entityData.define(FERTILIZED, false);
    }

    @Override
    public TFCAnimalProperties.Gender getGender()
    {
        return Gender.valueOf(entityData.get(GENDER));
    }

    @Override
    public void setGender(Gender gender)
    {
        entityData.set(GENDER, gender.toBool());
    }

    @Override
    public int getBirthDay()
    {
        return entityData.get(BIRTHDAY);
    }

    @Override
    public void setBirthDay(int value)
    {
        entityData.set(BIRTHDAY, value);
    }

    @Override
    public float getFamiliarity()
    {
        return entityData.get(FAMILIARITY);
    }

    @Override
    public void setFamiliarity(float value)
    {
        entityData.set(FAMILIARITY, Mth.clamp(value, 0F, 1F));
    }

    @Override
    public int getUses()
    {
        return entityData.get(USES);
    }

    @Override
    public void setUses(int uses)
    {
        entityData.set(USES, uses);
    }

    @Override
    public boolean isFertilized()
    {
        return entityData.get(FERTILIZED);
    }

    @Override
    public void setFertilized(boolean fertilized)
    {
        entityData.set(FERTILIZED, fertilized);
    }

    @Override
    public boolean isHungry()
    {
        return lastFed < Calendars.SERVER.getTotalDays();
    }

    @Override
    public boolean isReadyToMate()
    {
        if (this.getAgeType() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || this.isHungry())
        {
            return false;
        }
        return this.matingTime + MATING_COOLDOWN_DEFAULT_TICKS <= Calendars.SERVER.getTicks();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("gender", getGender().toBool());
        nbt.putInt("birth", getBirthDay());
        nbt.putLong("fed", lastFed);
        nbt.putLong("decay", lastFDecay);
        nbt.putBoolean("fertilized", isFertilized());
        nbt.putLong("mating", matingTime);
        nbt.putFloat("familiarity", getFamiliarity());
        nbt.putInt("uses", getUses());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        this.setGender(Gender.valueOf(nbt.getBoolean("gender")));
        this.setBirthDay(nbt.getInt("birth"));
        this.lastFed = nbt.getLong("fed");
        this.lastFDecay = nbt.getLong("decay");
        this.matingTime = nbt.getLong("mating");
        this.setFertilized(nbt.getBoolean("fertilized"));
        this.setFamiliarity(nbt.getFloat("familiarity"));
        this.setUses(nbt.getInt("uses"));
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if (pReason != MobSpawnType.BREEDING)
        {
            setGender(Gender.valueOf(random.nextBoolean()));
            setAge(0);
            setBirthDay(EntityHelpers.getRandomGrowth(level, getDaysToAdulthood()));
            setFamiliarity(0);
            setFertilized(false);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos)
    {
        if (getTFCAnimalType() != Type.OVIPAROUS)
        {
            super.checkFallDamage(pY, pOnGround, pState, pPos);
        }
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
            else if (this.isFood(stack) && player.isShiftKeyDown() && isLivestock())
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
                            //todo mating pregnant chat message
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        TFCAnimal other = (TFCAnimal) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    /**
     * Eat food + raises familiarization
     * If your animal would refuse to eat said stack (because rotten or anything), return false here
     * This function is called after every other check is made (animal is hungry for the day + this is a valid food)
     *
     * @param stack the food stack to eat
     * @return true if eaten, false otherwise
     */
    protected InteractionResult eatFood(@Nonnull ItemStack stack, InteractionHand hand, Player player)
    {
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
            level.playSound(null, this.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.AMBIENT, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        if (!eatsRottenFood())
        {
            Optional<Boolean> rot = stack.getCapability(FoodCapability.CAPABILITY).map(IFood::isRotten);
            if (rot.isPresent() && rot.get())
            {
                return false;
            }
        }
        return stack.is(getFoodTag());
    }

    protected boolean isLivestock()
    {
        return true; // todo this replaces ICreatureTFC, not sure what to do with it right now
    }
}