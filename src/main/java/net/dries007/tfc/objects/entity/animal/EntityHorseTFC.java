/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings({"WeakerAccess", "unused"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityHorseTFC extends EntityHorse implements IAnimalTFC
{
    protected static final int DAYS_TO_ADULTHOOD = 1120;
    protected static final int DAYS_TO_FULL_GESTATION = 240;
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);
    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private boolean fertilized; //Is this female fertilized?
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    private long lastDeath; //Last time(in days) this entity checked for dying of old age
    private long pregnantTime; // The time(in days) this entity became pregnant
    private boolean birthMule;
    private float geneJump, geneHealth, geneSpeed; // Basic genetic selection based on vanilla's horse offspring
    private int geneHorseVariant;

    public EntityHorseTFC(World world)
    {
        this(world, Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(DAYS_TO_ADULTHOOD));
    }

    public EntityHorseTFC(World world, Gender gender, int birthDay)
    {
        super(world);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.lastFed = -1;
        this.matingTime = -1;
        this.lastDeath = -1;
        this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.fertilized = false;
        this.birthMule = false;
        this.geneHealth = 0;
        this.geneJump = 0;
        this.geneSpeed = 0;
        this.geneHorseVariant = 0;
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
    public float getAdultFamiliarityCap()
    {
        return 0.35f;
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
    public boolean isFertilized() { return this.fertilized; }

    @Override
    public void setFertilized(boolean value)
    {
        this.fertilized = value;
    }

    @Override
    public void onFertilized(@Nonnull IAnimalTFC male)
    {
        this.pregnantTime = CalendarTFC.PLAYER_TIME.getTotalDays();
        // If mating with other types of horse, mark children to be mules
        if (male.getClass() != this.getClass())
        {
            this.birthMule = true;
        }
        else
        {
            int selection = this.rand.nextInt(9);
            int i;
            if (selection < 4)
            {
                i = this.getHorseVariant();
            }
            else if (selection < 8)
            {
                i = ((EntityHorse) male).getHorseVariant();
            }
            else
            {
                // Mutation
                i = this.rand.nextInt(7);
            }
            this.geneHorseVariant = i;
        }
        EntityAnimal father = (EntityAnimal) male;
        this.geneHealth = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getModifiedMaxHealth()) / 3.0D);
        this.geneSpeed = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed()) / 3.0D);
        this.geneJump = (float) ((father.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength()) / 3.0D);
    }

    @Override
    public int getDaysToAdulthood()
    {
        return DAYS_TO_ADULTHOOD;
    }

    @Override
    public boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || !this.isHungry())
            return false;
        return this.matingTime == -1 || this.matingTime + EntityAnimalTFC.MATING_COOLDOWN_DEFAULT_TICKS <= CalendarTFC.PLAYER_TIME.getTicks();
    }

    @Override
    public boolean isHungry()
    {
        if (lastFed == -1) return true;
        return lastFed < CalendarTFC.PLAYER_TIME.getTotalDays();
    }

    @Override
    public Type getType()
    {
        return Type.MAMMAL;
    }

    @Override
    public TextComponentTranslation getAnimalName()
    {
        String entityString = EntityList.getEntityString(this);
        return new TextComponentTranslation(MOD_ID + ".animal." + entityString + "." + this.getGender().name().toLowerCase());
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.world.checkNoEntityCollision(getEntityBoundingBox())
            && this.world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
            && !this.world.containsAnyLiquid(getEntityBoundingBox());
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
    public int getSpawnWeight(Biome biome, float temperature, float rainfall)
    {
        return 100;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.ELDER_AND_POPULATION;
    }

    @Override
    public int getMinGroupSize()
    {
        return 2;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 5;
    }

    @Override
    public void setScaleForAge(boolean child)
    {
        double ageScale = 1 / (2.0D - getPercentToAdulthood());
        this.setScale((float) ageScale);
    }

    @Override
    protected void mountTo(EntityPlayer player)
    {
        if (!this.isTame() && !this.getLeashed())
        {
            return;
        }
        super.mountTo(player);
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            if (this.isFertilized() && CalendarTFC.PLAYER_TIME.getTotalDays() >= pregnantTime + DAYS_TO_FULL_GESTATION)
            {
                birthChildren();
                this.setFertilized(false);
            }
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
                if (findFemaleMate())
                {
                    this.setInLove(null);
                }
            }
            if (this.getAge() == Age.OLD || lastDeath < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                if (lastDeath == -1)
                {
                    // First time check, to avoid dying at the same time this animal spawned, we skip the first day
                    this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
                }
                else
                {
                    this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
                    // Randomly die of old age, tied to entity UUID and calendar time
                    final Random random = new Random(this.entityUniqueID.getMostSignificantBits() * CalendarTFC.PLAYER_TIME.getTotalDays());
                    if (random.nextDouble() < ConfigTFC.GENERAL.chanceAnimalDeath)
                    {
                        this.setDead();
                    }
                }
            }
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", getGender().toBool());
        nbt.setInteger("birth", getBirthDay());
        nbt.setLong("fed", lastFed);
        nbt.setLong("decay", lastFDecay);
        nbt.setBoolean("fertilized", this.fertilized);
        nbt.setLong("mating", matingTime);
        nbt.setFloat("familiarity", getFamiliarity());
        nbt.setLong("lastDeath", lastDeath);
        nbt.setLong("pregnant", pregnantTime);
        nbt.setBoolean("birthMule", birthMule);
        nbt.setFloat("geneSpeed", geneSpeed);
        nbt.setFloat("geneJump", geneJump);
        nbt.setFloat("geneHealth", geneHealth);
        nbt.setInteger("geneHorseVariant", geneHorseVariant);
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
        this.fertilized = nbt.getBoolean("fertilized");
        this.setFamiliarity(nbt.getFloat("familiarity"));
        this.lastDeath = nbt.getLong("lastDeath");
        this.pregnantTime = nbt.getLong("pregnant");
        this.birthMule = nbt.getBoolean("birthMule");
        this.geneSpeed = nbt.getFloat("geneSpeed");
        this.geneJump = nbt.getFloat("geneSpeed");
        this.geneHealth = nbt.getFloat("geneSpeed");
        this.geneHorseVariant = nbt.getInteger("geneHorseVariant");
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_HORSE;
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
            else if (this.isFood(itemstack) && player.isSneaking() && getAdultFamiliarityCap() > 0.0F)
            {
                if (this.isHungry())
                {
                    if (!this.world.isRemote)
                    {
                        lastFed = CalendarTFC.PLAYER_TIME.getTotalDays();
                        lastFDecay = lastFed; //No decay needed
                        this.consumeItemFromStack(player, itemstack);
                        float familiarity = this.getFamiliarity() + 0.06f;
                        if (this.getAge() != Age.CHILD)
                        {
                            familiarity = Math.min(familiarity, getAdultFamiliarityCap());
                        }
                        this.setFamiliarity(familiarity);
                        world.playSound(null, this.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }
                    return true;
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
        return super.processInteract(player, hand);
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal instanceof IAnimalTFC && otherAnimal instanceof AbstractHorse)
        {
            IAnimalTFC other = (IAnimalTFC) otherAnimal;
            return this.getGender() != other.getGender() && this.isInLove() && otherAnimal.isInLove();
        }
        return false;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof IAnimalTFC)
        {
            this.fertilized = true;
            this.resetInLove();
            this.onFertilized((IAnimalTFC) other);
        }
        else if (other == this)
        {
            // Only called if this animal is interacted with a spawn egg
            EntityHorseTFC baby = new EntityHorseTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            this.setOffspringAttributes(this, baby);
            baby.setHorseVariant(this.getHorseVariant());
            return baby;
        }
        return null;
    }

    /**
     * Find and charms a near female horse/donkey/mule
     * Used by males to try mating with females
     *
     * @return true if found and charmed a female
     */
    private boolean findFemaleMate()
    {
        List<AbstractHorse> list = this.world.getEntitiesWithinAABB(AbstractHorse.class, this.getEntityBoundingBox().grow(8.0D));
        for (AbstractHorse ent : list)
        {
            if (ent instanceof IAnimalTFC && ((IAnimalTFC) ent).getGender() == Gender.FEMALE && !ent.isInLove() && ((IAnimalTFC) ent).isReadyToMate())
            {
                ent.setInLove(null);
                return true;
            }
        }
        return false;
    }

    private void birthChildren()
    {
        // Birth one animal
        IAnimalTFC baby;
        if (birthMule)
        {
            baby = new EntityMuleTFC(this.world);
        }
        else
        {
            baby = new EntityHorseTFC(this.world);
            ((EntityHorseTFC) baby).setHorseVariant(this.geneHorseVariant);
        }
        baby.setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
        baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
        EntityAnimal animal = (EntityAnimal) baby;
        animal.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
        if (this.geneHealth > 0)
        {
            animal.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.geneHealth);
        }
        if (this.geneSpeed > 0)
        {
            animal.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.geneSpeed);
        }
        if (this.geneJump > 0)
        {
            animal.getEntityAttribute(JUMP_STRENGTH).setBaseValue(this.geneJump);
        }
        geneJump = 0;
        geneSpeed = 0;
        geneJump = 0;
        this.world.spawnEntity(animal);
    }
}
