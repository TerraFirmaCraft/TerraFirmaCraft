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

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
// Changes in config allow placing this animal in livestock and still respawn
public class EntityOcelotTFC extends EntityOcelot implements IAnimalTFC, ILivestock
{
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityOcelotTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityOcelotTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityOcelotTFC.class, DataSerializers.FLOAT);
    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private boolean fertilized; //Is this female fertilized?
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    private long lastDeath; //Last time(in days) this entity checked for dying of old age
    private long pregnantTime; // The time(in days) this entity became pregnant

    @SuppressWarnings("unused")
    public EntityOcelotTFC(World world)
    {
        this(world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(ConfigTFC.Animals.OCELOT.adulthood, ConfigTFC.Animals.OCELOT.elder));
    }

    public EntityOcelotTFC(World world, IAnimalTFC.Gender gender, int birthDay)
    {
        super(world);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.matingTime = CalendarTFC.PLAYER_TIME.getTicks();
        this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.fertilized = false;
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
        return 0.4f;
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
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.OCELOT.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.OCELOT.elder;
    }

    @Override
    public boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || this.isHungry())
            return false;
        return this.matingTime + EntityAnimalTFC.MATING_COOLDOWN_DEFAULT_TICKS <= CalendarTFC.PLAYER_TIME.getTicks();
    }

    @Override
    public boolean isHungry()
    {
        return lastFed < CalendarTFC.PLAYER_TIME.getTotalDays();
    }

    @Override
    public IAnimalTFC.Type getType()
    {
        return IAnimalTFC.Type.MAMMAL;
    }

    @Override
    public TextComponentTranslation getAnimalName()
    {
        String entityString = isTamed() ? "cattfc" : EntityList.getEntityString(this);
        return new TextComponentTranslation(MOD_ID + ".animal." + entityString + "." + this.getGender().name().toLowerCase());
    }

    @Override
    public void setGrowingAge(int age)
    {
        super.setGrowingAge(0); // Ignoring this
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == IAnimalTFC.Age.CHILD;
    }

    @Override
    public void setScaleForAge(boolean child)
    {
        double ageScale = 1 / (2.0D - getPercentToAdulthood());
        this.setScale((float) ageScale);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.TROPICAL_FOREST || biomeType == BiomeHelper.BiomeType.SAVANNA))
        {
            return ConfigTFC.Animals.OCELOT.rarity;
        }
        return 0;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.MOTHER_AND_CHILDREN_OR_SOLO_MALE;
    }

    @Override
    public int getMinGroupSize()
    {
        return 1;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 4;
    }

    public long gestationDays()
    {
        return ConfigTFC.Animals.OCELOT.gestation;
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
            if (this.isFertilized() && CalendarTFC.PLAYER_TIME.getTotalDays() >= pregnantTime + gestationDays())
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
                EntityAnimalTFC.findFemaleMate(this);
            }
            if (this.getAge() == Age.OLD && lastDeath < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
                // Randomly die of old age, tied to entity UUID and calendar time
                final Random random = new Random(this.entityUniqueID.getMostSignificantBits() * CalendarTFC.PLAYER_TIME.getTotalDays());
                if (random.nextDouble() < ConfigTFC.Animals.OCELOT.oldDeathChance)
                {
                    this.setDead();
                }
            }
        }
    }

    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.OCELOT.babies;
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityOcelotTFC baby = new EntityOcelotTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            if (this.isTamed())
            {
                baby.setOwnerId(this.getOwnerId());
                baby.setTamed(true);
                baby.setTameSkin(this.getTameSkin());
            }
            this.world.spawnEntity(baby);
        }
    }

    @Override
    protected void initEntityAI()
    {
        super.initEntityAI();

        int priority = 1;
        for (String input : ConfigTFC.Animals.OCELOT.huntCreatures)
        {
            ResourceLocation key = new ResourceLocation(input);
            EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(key);
            if (entityEntry != null)
            {
                Class<? extends Entity> entityClass = entityEntry.getEntityClass();
                if (EntityLivingBase.class.isAssignableFrom(entityClass))
                {
                    //noinspection unchecked
                    this.targetTasks.addTask(priority++, new EntityAITargetNonTamed<>(this, (Class<EntityLivingBase>) entityClass, false, ent -> true));
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
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_OCELOT;
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
            else if (!this.isTamed())
            {
                // Ocelots -> Cats transformation before familiarization
                if (isFood(itemstack) && player.getDistanceSq(this) < 9.0D)
                {
                    if (!player.isCreative())
                    {
                        itemstack.shrink(1);
                    }
                    if (!this.world.isRemote)
                    {
                        if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player))
                        {
                            this.setTamedBy(player);
                            this.setTameSkin(1 + this.world.rand.nextInt(3));
                            this.playTameEffect(true);
                            this.aiSit.setSitting(true);
                            this.world.setEntityState(this, (byte) 7);
                        }
                        else
                        {
                            this.playTameEffect(false);
                            this.world.setEntityState(this, (byte) 6);
                        }
                    }
                    return true;
                }
                return false;
            }
            else if (this.isFood(itemstack) && player.isSneaking() && getAdultFamiliarityCap() > 0.0F)
            {
                if (this.isHungry())
                {
                    // Refuses to eat rotten stuff
                    IFood cap = itemstack.getCapability(CapabilityFood.CAPABILITY, null);
                    if (cap != null)
                    {
                        if (cap.isRotten())
                        {
                            return false;
                        }
                    }
                    if (!this.world.isRemote)
                    {
                        lastFed = CalendarTFC.PLAYER_TIME.getTotalDays();
                        lastFDecay = lastFed; //No decay needed
                        this.consumeItemFromStack(player, itemstack);
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

    @Nullable
    @Override
    public EntityOcelotTFC createChild(@Nonnull EntityAgeable other)
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
            // Try to return to vanilla's default method a baby of this animal, as if bred normally
            EntityOcelotTFC baby = new EntityOcelotTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            if (this.isTamed())
            {
                baby.setOwnerId(this.getOwnerId());
                baby.setTamed(true);
                baby.setTameSkin(this.getTameSkin());
            }
            return baby;
        }
        return null;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityOcelotTFC other = (EntityOcelotTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.world.checkNoEntityCollision(getEntityBoundingBox())
            && this.world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
            && !this.world.containsAnyLiquid(getEntityBoundingBox())
            && BlocksTFC.isGround(this.world.getBlockState(this.getPosition().down()));
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
}
