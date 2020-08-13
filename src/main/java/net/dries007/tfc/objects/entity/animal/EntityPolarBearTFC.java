/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIAttackMeleeTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIStandAttack;
import net.dries007.tfc.objects.entity.ai.EntityAIWanderHuntArea;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class EntityPolarBearTFC extends EntityPolarBear implements IAnimalTFC, IPredator, EntityAIStandAttack.IEntityStandAttack
{
    private static final int DAYS_TO_ADULTHOOD = 180;
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityPolarBearTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityPolarBearTFC.class, DataSerializers.VARINT);

    private int warningSoundTicks = 0;

    @SuppressWarnings("unused")
    public EntityPolarBearTFC(World world)
    {
        this(world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(DAYS_TO_ADULTHOOD, 0));
    }

    public EntityPolarBearTFC(World world, IAnimalTFC.Gender gender, int birthDay)
    {
        super(world);
        this.setSize(1.4F, 1.7F);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
    }

    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable ageable)
    {
        return new EntityPolarBearTFC(this.world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays()); // Used by spawn eggs
    }

    @Override
    protected void initEntityAI()
    {
        EntityAIWander wander = new EntityAIWanderHuntArea(this, 1.0D);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStandAttack<>(this, 1.2D, 2.0D, EntityAIAttackMeleeTFC.AttackBehavior.DAYLIGHT_ONLY).setWanderAI(wander));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, wander);
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));

        int priority = 2;
        for (String input : ConfigTFC.Animals.POLAR_BEAR.huntCreatures)
        {
            ResourceLocation key = new ResourceLocation(input);
            EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(key);
            if (entityEntry != null)
            {
                Class<? extends Entity> entityClass = entityEntry.getEntityClass();
                if (EntityLivingBase.class.isAssignableFrom(entityClass))
                {
                    //noinspection unchecked
                    this.targetTasks.addTask(priority++, new EntityAINearestAttackableTarget<>(this, (Class<EntityLivingBase>) entityClass, false));
                }
            }
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        // Reset values to match brown bear
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_POLAR_BEAR;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (this.ticksExisted % 100 == 0)
        {
            setScaleForAge(false);
        }
        if (this.warningSoundTicks > 0)
        {
            --this.warningSoundTicks;
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn)
    {
        double attackDamage = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        if (this.isChild())
        {
            attackDamage /= 2;
        }
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) attackDamage);
        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }
        return flag;
    }

    @Override
    public void setStand(boolean standing)
    {
        super.setStanding(standing);
    }

    @Override
    public void playWarning()
    {
        if (this.warningSoundTicks <= 0)
        {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Override
    public IAnimalTFC.Gender getGender()
    {
        return IAnimalTFC.Gender.valueOf(this.dataManager.get(GENDER));
    }

    @Override
    public void setGender(IAnimalTFC.Gender gender)
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
        return 0;
    }

    @Override
    public float getFamiliarity()
    {
        return 0;
    }

    @Override
    public void setFamiliarity(float value)
    {
    }

    @Override
    public boolean isFertilized() { return false; }

    @Override
    public void setFertilized(boolean value)
    {
    }

    @Override
    public int getDaysToAdulthood()
    {
        return DAYS_TO_ADULTHOOD;
    }

    @Override
    public int getDaysToElderly()
    {
        return 0;
    }

    @Override
    public boolean isHungry()
    {
        return false;
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
            (biomeType == BiomeHelper.BiomeType.TUNDRA || biomeType == BiomeHelper.BiomeType.TAIGA))
        {
            return ConfigTFC.Animals.POLAR_BEAR.rarity;
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
        return 3;
    }

    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        if (!this.hasHome())
        {
            this.setHomePosAndDistance(this.getPosition(), 80);
        }
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", getGender().toBool());
        nbt.setInteger("birth", getBirthDay());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setGender(IAnimalTFC.Gender.valueOf(nbt.getBoolean("gender")));
        this.setBirthDay(nbt.getInteger("birth"));
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
    public boolean canMateWith(@Nonnull EntityAnimal otherAnimal)
    {
        return false; // This animal shouldn't have mating mechanics since it's not farmable
    }
}
