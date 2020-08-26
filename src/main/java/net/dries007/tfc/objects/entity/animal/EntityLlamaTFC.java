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

import net.minecraft.block.BlockChest;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.entity.EntitiesTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIPanicTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class EntityLlamaTFC extends EntityLlama implements IAnimalTFC, ILivestock
{
    //Values that has a visual effect on client
    protected static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityLlamaTFC.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityLlamaTFC.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityLlamaTFC.class, DataSerializers.FLOAT);
    //Is this female fertilized?
    private static final DataParameter<Boolean> FERTILIZED = EntityDataManager.createKey(EntityLlamaTFC.class, DataSerializers.BOOLEAN);
    // The time(in days) this entity became pregnant
    private static final DataParameter<Long> PREGNANT_TIME = EntityDataManager.createKey(EntityLlamaTFC.class, EntitiesTFC.getLongDataSerializer());
    protected long lastFed; //Last time(in days) this entity was fed
    protected long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    protected long matingTime; //The last time(in ticks) this male tried fertilizing females
    protected long lastDeath; //Last time(in days) this entity checked for dying of old age
    protected float geneJump, geneHealth, geneSpeed, geneStrength; // Basic genetic selection based on vanilla's llama offspring
    protected int geneVariant;

    @SuppressWarnings("unused")
    public EntityLlamaTFC(World world)
    {
        this(world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(ConfigTFC.Animals.LLAMA.adulthood, ConfigTFC.Animals.LLAMA.elder));
    }

    public EntityLlamaTFC(World world, IAnimalTFC.Gender gender, int birthDay)
    {
        super(world);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.matingTime = CalendarTFC.PLAYER_TIME.getTicks();
        this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.setFertilized(false);
        this.geneHealth = 0;
        this.geneJump = 0;
        this.geneSpeed = 0;
        this.geneStrength = 0;
        this.geneVariant = 0;
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        if (!world.isRemote)
        {
            setChested(false); // Don't drop chest
        }
        super.onDeath(cause);
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if (!stack.isEmpty())
        {
            boolean holdingChest = false;
            if (stack.getItem() instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock) stack.getItem();
                holdingChest = itemBlock.getBlock() instanceof BlockChest;
            }
            if (stack.getItem() == Items.SPAWN_EGG)
            {
                return super.processInteract(player, hand); // Let vanilla spawn a baby
            }
            else if (!this.hasChest() && this.isTame() && holdingChest)
            {
                this.setChested(true);
                this.playChestEquipSound();
                this.initHorseChest();
                if (!player.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }
                return true;
            }
            else if (this.isFood(stack) && player.isSneaking() && getAdultFamiliarityCap() > 0.0F)
            {
                if (this.isHungry())
                {
                    // Refuses to eat rotten stuff
                    IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
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
    public boolean isFertilized() { return dataManager.get(FERTILIZED); }

    @Override
    public void setFertilized(boolean value)
    {
        dataManager.set(FERTILIZED, value);
    }

    @Override
    public void onFertilized(@Nonnull IAnimalTFC male)
    {
        this.setPregnantTime(CalendarTFC.PLAYER_TIME.getTotalDays());
        int selection = this.rand.nextInt(9);
        int i;
        if (selection < 4)
        {
            i = this.getVariant();
        }
        else if (selection < 8)
        {
            i = ((EntityLlamaTFC) male).getVariant();
        }
        else
        {
            // Mutation
            i = this.rand.nextInt(4);
        }
        this.geneVariant = i;
        EntityLlamaTFC father = (EntityLlamaTFC) male;
        this.geneHealth = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getModifiedMaxHealth()) / 3.0D);
        this.geneSpeed = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed()) / 3.0D);
        this.geneJump = (float) ((father.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength()) / 3.0D);

        this.geneStrength = this.rand.nextInt(Math.max(this.getStrength(), father.getStrength())) + 1;
        if (this.rand.nextFloat() < 0.03F)
        {
            this.geneStrength++;
        }
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.LLAMA.gestation;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.LLAMA.elder;
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
        String entityString = EntityList.getEntityString(this);
        return new TextComponentTranslation(MOD_ID + ".animal." + entityString + "." + this.getGender().name().toLowerCase());
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
    public void setGrowingAge(int age)
    {
        super.setGrowingAge(0); // Ignoring this
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == IAnimalTFC.Age.CHILD;
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
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.TEMPERATE_FOREST || biomeType == BiomeHelper.BiomeType.TUNDRA))
        {
            return ConfigTFC.Animals.LLAMA.rarity;
        }
        return 0;
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
        return 4;
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
        if (this.getFamiliarity() > 0.15f)
        {
            super.mountTo(player);
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
            if (this.isFertilized() && CalendarTFC.PLAYER_TIME.getTotalDays() >= getPregnantTime() + gestationDays())
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
                if (random.nextDouble() < ConfigTFC.Animals.LLAMA.oldDeathChance)
                {
                    this.setDead();
                }
            }
        }
    }

    public long gestationDays()
    {
        return ConfigTFC.Animals.LLAMA.gestation;
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
        nbt.setLong("pregnant", getPregnantTime());
        nbt.setFloat("geneSpeed", geneSpeed);
        nbt.setFloat("geneJump", geneJump);
        nbt.setFloat("geneHealth", geneHealth);
        nbt.setFloat("geneStrength", geneStrength);
        nbt.setInteger("geneVariant", geneVariant);
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
        this.setPregnantTime(nbt.getLong("pregnant"));
        this.geneSpeed = nbt.getFloat("geneSpeed");
        this.geneJump = nbt.getFloat("geneJump");
        this.geneHealth = nbt.getFloat("geneHealth");
        this.geneStrength = nbt.getFloat("geneStrength");
        this.geneVariant = nbt.getInteger("geneVariant");
    }

    @Override
    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIPanicTFC(this, 1.4D));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
        getDataManager().register(PREGNANT_TIME, -1L);
        getDataManager().register(FERTILIZED, false);
    }

    @Override
    protected boolean handleEating(EntityPlayer player, ItemStack stack)
    {
        return false; // Stop exploits
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_LLAMA;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityLlamaTFC other = (EntityLlamaTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Nullable
    @Override
    public EntityLlama createChild(@Nonnull EntityAgeable other)
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
            return new EntityLlamaTFC(this.world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
        }
        return null;
    }

    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.LLAMA.babies; //one always
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityLlamaTFC baby = new EntityLlamaTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            if (this.geneHealth > 0)
            {
                baby.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.geneHealth);
            }
            if (this.geneSpeed > 0)
            {
                baby.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.geneSpeed);
            }
            if (this.geneJump > 0)
            {
                baby.getEntityAttribute(JUMP_STRENGTH).setBaseValue(this.geneJump);
            }
            if (this.geneStrength > 0)
            {
                this.setStrength((int) this.geneStrength);
            }
            baby.setVariant(geneVariant);
            geneJump = 0;
            geneSpeed = 0;
            geneJump = 0;
            geneStrength = 0;
            geneVariant = 0;
            this.world.spawnEntity(baby);
        }
    }

    public long getPregnantTime() { return dataManager.get(PREGNANT_TIME); }

    public void setPregnantTime(long pregnantTime) { dataManager.set(PREGNANT_TIME, pregnantTime); }
}
