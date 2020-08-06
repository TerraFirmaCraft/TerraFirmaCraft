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
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityDonkey;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.entity.EntitiesTFC;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EntityDonkeyTFC extends EntityDonkey implements IAnimalTFC, ILivestock
{
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityDonkeyTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityDonkeyTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityDonkeyTFC.class, DataSerializers.FLOAT);
    //Is this female fertilized?
    private static final DataParameter<Boolean> FERTILIZED = EntityDataManager.createKey(EntityDonkeyTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HALTER = EntityDataManager.createKey(EntityDonkeyTFC.class, DataSerializers.BOOLEAN);
    // The time(in days) this entity became pregnant
    private static final DataParameter<Long> PREGNANT_TIME = EntityDataManager.createKey(EntityDonkeyTFC.class, EntitiesTFC.getLongDataSerializer());
    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    private long lastDeath; //Last time(in days) this entity checked for dying of old age
    private boolean birthMule;
    private float geneJump, geneHealth, geneSpeed; // Basic genetic selection based on vanilla's horse offspring

    public EntityDonkeyTFC(World world)
    {
        this(world, Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(ConfigTFC.Animals.DONKEY.adulthood, ConfigTFC.Animals.DONKEY.elder));
    }

    public EntityDonkeyTFC(World world, Gender gender, int birthDay)
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
    public boolean isFertilized()
    {
        return dataManager.get(FERTILIZED);
    }

    @Override
    public void setFertilized(boolean value)
    {
        dataManager.set(FERTILIZED, value);
    }

    @Override
    public void onFertilized(@Nonnull IAnimalTFC male)
    {
        this.setPregnantTime(CalendarTFC.PLAYER_TIME.getTotalDays());
        // If mating with other types of horse, mark children to be mules
        if (male.getClass() != this.getClass())
        {
            this.birthMule = true;
        }
        // Save genes
        EntityAnimal father = (EntityAnimal) male;
        this.geneHealth = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getModifiedMaxHealth()) / 3.0D);
        this.geneSpeed = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed()) / 3.0D);
        this.geneJump = (float) ((father.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength()) / 3.0D);
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.DONKEY.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.DONKEY.elder;
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

    public boolean isHalter()
    {
        return dataManager.get(HALTER);
    }

    public void setHalter(boolean value)
    {
        dataManager.set(HALTER, value);
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
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS))
        {
            return ConfigTFC.Animals.DONKEY.rarity;
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
        return 5;
    }

    public long getPregnantTime()
    {
        return dataManager.get(PREGNANT_TIME);
    }

    public void setPregnantTime(long pregnantTime)
    {
        dataManager.set(PREGNANT_TIME, pregnantTime);
    }

    public long gestationDays()
    {
        return ConfigTFC.Animals.DONKEY.gestation;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
        getDataManager().register(FERTILIZED, false);
        getDataManager().register(PREGNANT_TIME, -1L);
        getDataManager().register(HALTER, false);
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
        nbt.setBoolean("birthMule", birthMule);
        nbt.setFloat("geneSpeed", geneSpeed);
        nbt.setFloat("geneJump", geneJump);
        nbt.setFloat("geneHealth", geneHealth);
        nbt.setBoolean("halter", isHalter());
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
        this.birthMule = nbt.getBoolean("birthMule");
        this.geneSpeed = nbt.getFloat("geneSpeed");
        this.geneJump = nbt.getFloat("geneJump");
        this.geneHealth = nbt.getFloat("geneHealth");
        this.setHalter(nbt.getBoolean("halter"));
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
            else if (!isHalter() && OreDictionaryHelper.doesStackMatchOre(stack, "halter"))
            {
                if (this.getAge() != Age.CHILD && getFamiliarity() > 0.15f)
                {
                    if (!this.world.isRemote)
                    {
                        this.consumeItemFromStack(player, stack);
                        this.setHalter(true);
                    }
                    return true;
                }
                else
                {
                    // Show tooltips
                    if (!this.world.isRemote)
                    {
                        if (this.getAge() == Age.CHILD)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.young", getName()));
                        }
                        else
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.low_familiarity", getName()));
                        }

                    }
                    return false;
                }
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
    protected void initEntityAI()
    {
        EntityAnimalTFC.addCommonLivestockAI(this, 1.2D);
        EntityAnimalTFC.addCommonPreyAI(this, 1.2);
        tasks.addTask(2, new EntityAIMate(this, 1.0D, EntityHorseTFC.class)); // Missing horses (for mules)
        tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
        tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
    }

    @Override
    public void setScaleForAge(boolean child)
    {
        double ageScale = 1 / (2.0D - getPercentToAdulthood());
        this.setScale((float) ageScale);
    }

    @Override
    protected boolean handleEating(EntityPlayer player, ItemStack stack)
    {
        return false; // Stop exploits
    }

    @Override
    protected void mountTo(EntityPlayer player)
    {
        if (isHalter())
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
            if (this.hasChest() && this.ticksExisted % 20 == 0)
            {
                // Apply overburdened when carrying more than one heavy item
                int hugeHeavyCount = 0;
                for (int i = 2; i < this.horseChest.getSizeInventory(); ++i)
                {
                    ItemStack stack = this.horseChest.getStackInSlot(i);
                    if (CapabilityItemSize.checkItemSize(stack, Size.HUGE, Weight.VERY_HEAVY))
                    {
                        hugeHeavyCount++;
                        if (hugeHeavyCount >= 2)
                        {
                            break;
                        }
                    }
                }
                if (hugeHeavyCount >= 2)
                {
                    // Does not work when ridden, mojang bug: https://bugs.mojang.com/browse/MC-121788
                    this.addPotionEffect(new PotionEffect(PotionEffectsTFC.OVERBURDENED, 25, 125, false, false));
                }
            }
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
                if (findFemaleMate())
                {
                    this.setInLove(null);
                }
            }
            if (this.getAge() == Age.OLD && lastDeath < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                this.lastDeath = CalendarTFC.PLAYER_TIME.getTotalDays();
                // Randomly die of old age, tied to entity UUID and calendar time
                final Random random = new Random(this.entityUniqueID.getMostSignificantBits() * CalendarTFC.PLAYER_TIME.getTotalDays());
                if (random.nextDouble() < ConfigTFC.Animals.DONKEY.oldDeathChance)
                {
                    this.setDead();
                }
            }
        }
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_DONKEY;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal instanceof EntityHorseTFC || otherAnimal instanceof EntityDonkeyTFC)
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
            this.setFertilized(true);
            this.resetInLove();
            this.onFertilized((IAnimalTFC) other);
        }
        else if (other == this)
        {
            // Only called if this animal is interacted with a spawn egg
            EntityDonkeyTFC baby = new EntityDonkeyTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            this.setOffspringAttributes(this, baby);
            return baby;
        }
        return null;
    }

    /**
     * Find and charms a near female horse/donkey
     * Used by males to try mating with females
     *
     * @return true if found and charmed a female
     */
    private boolean findFemaleMate()
    {
        List<AbstractHorse> list = this.world.getEntitiesWithinAABB(AbstractHorse.class, this.getEntityBoundingBox().grow(8.0D));
        for (AbstractHorse ent : list)
        {
            if (ent instanceof EntityHorseTFC || ent instanceof EntityDonkeyTFC)
            {
                IAnimalTFC animal = (IAnimalTFC) ent;
                if (animal.getGender() == Gender.FEMALE && animal.isReadyToMate() && !ent.isInLove())
                {
                    ent.setInLove(null);
                    return true;
                }
            }
        }
        return false;
    }

    private void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.DONKEY.babies;
        for (int i = 0; i < numberOfChildren; i++)
        {
            // Birth one animal
            IAnimalTFC baby;
            if (birthMule)
            {
                baby = new EntityMuleTFC(this.world);
            }
            else
            {
                baby = new EntityDonkeyTFC(this.world);
            }
            baby.setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
            EntityAnimal animal = (EntityAnimal) baby;
            animal.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            if (geneHealth > 0)
            {
                animal.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(geneHealth);
            }
            if (geneSpeed > 0)
            {
                animal.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(geneSpeed);
            }
            if (geneJump > 0)
            {
                animal.getEntityAttribute(JUMP_STRENGTH).setBaseValue(geneJump);
            }
            world.spawnEntity(animal);
        }
        geneJump = 0;
        geneSpeed = 0;
        geneJump = 0;
        birthMule = false;
    }
}
