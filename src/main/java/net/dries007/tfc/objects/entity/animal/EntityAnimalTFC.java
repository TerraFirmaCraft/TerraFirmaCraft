/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIPanicTFC;
import net.dries007.tfc.objects.entity.ai.EntityAITamableAvoidPlayer;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class EntityAnimalTFC extends EntityAnimal implements IAnimalTFC
{
    public static final long MATING_COOLDOWN_DEFAULT_TICKS = ICalendar.TICKS_IN_HOUR * 2;

    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);
    //Is this female fertilized? (in oviparous, the egg laying is fertilized, for mammals this is pregnancy)
    private static final DataParameter<Boolean> FERTILIZED = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);

    /**
     * Gets a random growth for this animal
     * ** Static ** So it can be used by class constructor
     *
     * @param daysToAdult number of days needed for this animal to be an adult
     * @param daysToElder number of days needed after adult to this animal be an elder. 0 = ignore
     * @return a random long value containing the days of growth for this animal to spawn
     * **Always spawn adults** (so vanilla respawn mechanics only creates adults of this animal)
     */
    public static int getRandomGrowth(int daysToAdult, int daysToElder)
    {
        int randomFactor = daysToElder > 0 ? (int) (daysToElder * 1.25f) : daysToAdult * 4;
        int lifeTimeDays = daysToAdult + Constants.RNG.nextInt(randomFactor);
        return (int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays);
    }

    /**
     * Find and charms a near female animal of this animal
     * Used by males to try mating with females
     */
    public static <T extends EntityAnimal & IAnimalTFC> void findFemaleMate(T maleAnimal)
    {
        List<EntityAnimal> list = maleAnimal.world.getEntitiesWithinAABB(maleAnimal.getClass(), maleAnimal.getEntityBoundingBox().grow(8.0D));
        for (EntityAnimal femaleAnimal : list)
        {
            IAnimalTFC female = (IAnimalTFC) femaleAnimal;
            if (female.getGender() == Gender.FEMALE && !femaleAnimal.isInLove() && female.isReadyToMate())
            {
                femaleAnimal.setInLove(null);
                maleAnimal.setInLove(null);
                break;
            }
        }
    }

    public static <T extends EntityAnimal & IAnimalTFC> void addCommonLivestockAI(T entity, double speedMult)
    {
        entity.tasks.addTask(2, new EntityAIMate(entity, 1.0D));

        for (ItemStack is : OreDictionary.getOres("grain"))
        {
            Item item = is.getItem();
            entity.tasks.addTask(3, new EntityAITempt(entity, 1.1D, item, false));
        }

        double farSpeed = .8D * speedMult;
        double nearSpeed = 1.1D * speedMult;
        entity.tasks.addTask(4, new EntityAITamableAvoidPlayer<>(entity, 6.0F, farSpeed, nearSpeed));
        entity.tasks.addTask(6, new EntityAIEatGrass(entity));
    }

    public static void addWildPreyAI(EntityAnimal entity, double speedMult)
    {
        double farSpeed = .8D * speedMult;
        double nearSpeed = 1.1D * speedMult;

        entity.tasks.addTask(4, new EntityAIAvoidEntity<>(entity, EntityPlayer.class, 12.0F, farSpeed, nearSpeed));
    }

    public static void addCommonPreyAI(EntityAnimal entity, double speedMult)
    {
        double farSpeed = .8D * speedMult;
        double nearSpeed = 1.1D * speedMult;

        entity.tasks.addTask(0, new EntityAISwimming(entity));
        entity.tasks.addTask(1, new EntityAIPanicTFC(entity, 1.4D * speedMult));
        //space for livestock AIMate and AITempt
        entity.tasks.addTask(4, new EntityAIAvoidEntity<>(entity, EntityWolfTFC.class, 8.0F, farSpeed, nearSpeed));
        entity.tasks.addTask(4, new EntityAIAvoidEntity<>(entity, EntityAnimalMammal.class, Predicates.instanceOf(IPredator.class), 12.0F, farSpeed, nearSpeed));
        entity.tasks.addTask(4, new EntityAIAvoidEntity<>(entity, EntityMob.class, 8.0F, farSpeed * 0.7D, nearSpeed * 0.7D));
        // space for follow parent for mammals, find nest for oviparous, and eat grass for livestock
        entity.tasks.addTask(7, new EntityAIWanderAvoidWater(entity, 1.0D));
        entity.tasks.addTask(8, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
        entity.tasks.addTask(9, new EntityAILookIdle(entity));
    }

    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    private long lastDeath; //Last time(in days) this entity checked for dying of old age

    @SuppressWarnings("unused")
    public EntityAnimalTFC(World worldIn)
    {
        super(worldIn);
    }

    public EntityAnimalTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.matingTime = CalendarTFC.PLAYER_TIME.getTicks();
        this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.setFertilized(false);
    }

    @Override
    public Gender getGender()
    {
        return Gender.valueOf(this.dataManager.get(GENDER));
    }

    @Override
    public void setGender(Gender gender)
    {
        this.dataManager.set(GENDER, gender.toBool());
    }

    @Override
    public int getBirthDay()
    {
        return this.dataManager.get(BIRTHDAY);
    }

    @Override
    public void setBirthDay(int value)
    {
        this.dataManager.set(BIRTHDAY, value);
    }

    @Override
    public float getFamiliarity()
    {
        return this.dataManager.get(FAMILIARITY);
    }

    @Override
    public void setFamiliarity(float value)
    {
        if (value < 0f) value = 0f;
        if (value > 1f) value = 1f;
        this.dataManager.set(FAMILIARITY, value);
    }

    @Override
    public boolean isFertilized() { return dataManager.get(FERTILIZED); }

    @Override
    public void setFertilized(boolean value) { dataManager.set(FERTILIZED, value); }

    @Override
    public boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || this.isHungry())
            return false;
        return this.matingTime + MATING_COOLDOWN_DEFAULT_TICKS <= CalendarTFC.PLAYER_TIME.getTicks();
    }

    @Override
    public boolean isHungry()
    {
        return lastFed < CalendarTFC.PLAYER_TIME.getTotalDays();
    }

    @Override
    public TextComponentTranslation getAnimalName()
    {
        String entityString = EntityList.getEntityString(this);
        return new TextComponentTranslation(MOD_ID + ".animal." + entityString + "." + this.getGender().name().toLowerCase());
    }

    @Nullable
    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof IAnimalTFC)
        {
            this.setFertilized(true);
            this.resetInLove();
            this.onFertilized((IAnimalTFC) other);
        }
        else if (other == this)
        {
            // Only called if this animal is interacted with a spawn egg
            // Try to return to vanilla's default method a baby of this animal, as if bred normally
            try
            {
                EntityAnimalTFC baby = this.getClass().getConstructor(World.class).newInstance(this.world);
                baby.setGender(Gender.valueOf(Constants.RNG.nextBoolean()));
                baby.setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
                baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
                return baby;
            }
            catch (Exception ignored)
            {
            }
        }
        return null;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
        getDataManager().register(FERTILIZED, false);
    }

    @Override
    public void setGrowingAge(int age)
    {
        super.setGrowingAge(0); // Ignoring this
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == Age.CHILD;
    }

    @Override
    public void setScaleForAge(boolean child)
    {
        double ageScale = 1 / (2.0D - getPercentToAdulthood());
        this.setScale((float) ageScale);
    }

    /**
     * Ignore fall damage like vanilla chickens. Implemented here because all TFC Oviparous animals don't take fall damage.
     * Ostriches would escape fall damage too.
     */
    @Override
    public void fall(float distance, float damageMultiplier)
    {
        if (this.getType() != Type.OVIPAROUS)
        {
            super.fall(distance, damageMultiplier);
        }
    } //disable fall damage for oviparous only, like vanilla

    @Nonnull
    @Override
    public String getName()
    {
        if (this.hasCustomName())
        {
            return this.getCustomNameTag();
        }
        else
        {
            return getAnimalName().getFormattedText();
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (this.ticksExisted % 100 == 0)
        {
            setScaleForAge(false);
        }
        if (!this.world.isRemote)
        {
            // Is it time to decay familiarity?
            // If this entity was never fed(eg: new born, wild)
            // or wasn't fed yesterday(this is the starting of the second day)
            if (this.lastFDecay > -1 && this.lastFDecay + 1 < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                float familiarity = getFamiliarity();
                if (familiarity < 0.3f)
                {
                    familiarity -= 0.02 * (CalendarTFC.PLAYER_TIME.getTotalDays() - this.lastFDecay);
                    this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
                    this.setFamiliarity(familiarity);
                }
            }
            if (this.getGender() == Gender.MALE && this.isReadyToMate())
            {
                this.matingTime = CalendarTFC.PLAYER_TIME.getTicks();
                findFemaleMate(this);
            }
            if (this.getAge() == Age.OLD && lastDeath < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
                // Randomly die of old age, tied to entity UUID and calendar time
                final Random random = new Random(this.entityUniqueID.getMostSignificantBits() * CalendarTFC.PLAYER_TIME.getTotalDays());
                if (random.nextDouble() < getOldDeathChance())
                {
                    this.setDead();
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", getGender().toBool());
        nbt.setInteger("birth", getBirthDay());
        nbt.setLong("fed", lastFed);
        nbt.setLong("decay", lastFDecay);
        nbt.setBoolean("fertilized", this.isFertilized());
        nbt.setLong("mating", matingTime);
        nbt.setFloat("familiarity", getFamiliarity());
        nbt.setLong("lastDeath", lastDeath);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setGender(Gender.valueOf(nbt.getBoolean("gender")));
        this.setBirthDay(nbt.getInteger("birth"));
        this.lastFed = nbt.getLong("fed");
        this.lastFDecay = nbt.getLong("decay");
        this.matingTime = nbt.getLong("mating");
        this.setFertilized(nbt.getBoolean("fertilized"));
        this.setFamiliarity(nbt.getFloat("familiarity"));
        this.lastDeath = nbt.getLong("lastDeath");

    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.world.checkNoEntityCollision(getEntityBoundingBox())
            && this.world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
            && !this.world.containsAnyLiquid(getEntityBoundingBox())
            && BlocksTFC.isGround(this.world.getBlockState(this.getPosition().down()));
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty())
        {
            if (itemstack.getItem() == Items.SPAWN_EGG)
            {
                return super.processInteract(player, hand); // Let vanilla spawn a baby
            }
            else if (this.isFood(itemstack) && player.isSneaking() && getCreatureType() == CreatureType.LIVESTOCK)
            {
                if (this.isHungry())
                {
                    return eatFood(itemstack, player);
                }
                else
                {
                    if (!this.world.isRemote)
                    {
                        //Show tooltips
                        if (this.isFertilized() && this.getType() == Type.MAMMAL)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.mating.pregnant", getName()));
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityAnimalTFC other = (EntityAnimalTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    public abstract double getOldDeathChance();

    /**
     * Eat food + raises familiarization
     * If your animal would refuse to eat said stack (because rotten or anything), return false here
     * This function is called after every other check is made (animal is hungry for the day + this is a valid food)
     *
     * @param stack the food stack to eat
     * @return true if eaten, false otherwise
     */
    protected boolean eatFood(@Nonnull ItemStack stack, EntityPlayer player)
    {
        if (!this.world.isRemote)
        {
            lastFed = CalendarTFC.PLAYER_TIME.getTotalDays();
            lastFDecay = lastFed; //No decay needed
            this.consumeItemFromStack(player, stack);
            if (this.getAge() == Age.CHILD || this.getFamiliarity() < getAdultFamiliarityCap())
            {
                float familiarity = this.getFamiliarity() + 0.06f;
                if (this.getAge() != Age.CHILD)
                {
                    familiarity = Math.min(familiarity, getAdultFamiliarityCap());
                }
                this.setFamiliarity(familiarity);
            }
            world.playSound(null, this.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.AMBIENT, 1.0F, 1.0F);
            TFCTriggers.FAMILIARIZATION_TRIGGER.trigger((EntityPlayerMP) player, this); // Trigger familiarization change
        }
        return true;
    }
}
