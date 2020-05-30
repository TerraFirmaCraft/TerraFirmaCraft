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

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IHuntable;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

@ParametersAreNonnullByDefault
public class EntityHareTFC extends EntityAnimalMammal implements IHuntable
{
    private static final int DAYS_TO_ADULTHOOD = 16;
    private static final DataParameter<Integer> HARE_TYPE = EntityDataManager.createKey(EntityHareTFC.class, DataSerializers.VARINT);

    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;

    @SuppressWarnings("unused")
    public EntityHareTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(DAYS_TO_ADULTHOOD, 0));
    }

    public EntityHareTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.4F, 0.5F);
        this.jumpHelper = new RabbitJumpHelper(this);
        this.moveHelper = new EntityHareTFC.RabbitMoveHelper(this);
        this.setMovementSpeed(0.0D);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS || biomeType == BiomeHelper.BiomeType.SAVANNA
                || biomeType == BiomeHelper.BiomeType.TEMPERATE_FOREST || biomeType == BiomeHelper.BiomeType.TROPICAL_FOREST ||
                biomeType == BiomeHelper.BiomeType.DESERT))
        {
            return ConfigTFC.Animals.RABBIT.rarity;
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
        return 4;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 7;
    }

    @Override
    public void updateAITasks()
    {
        super.updateAITasks();

        if (this.currentMoveTypeDuration > 0)
        {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround)
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            EntityHareTFC.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityHareTFC.RabbitJumpHelper) this.jumpHelper;

            if (!entityrabbit$rabbitjumphelper.isJumping())
            {
                if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0)
                {
                    Path path = this.navigator.getPath();
                    Vec3d vec3d = new Vec3d(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());

                    if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
                    {
                        vec3d = path.getPosition(this);
                    }

                    this.calculateRotationYaw(vec3d.x, vec3d.z);
                    this.startJumping();
                }
            }
            else if (!entityrabbit$rabbitjumphelper.canJump())
            {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround;
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 1)
        {
            this.createRunningParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    public int getHareType()
    {
        return this.dataManager.get(HARE_TYPE);
    }

    public void setHareType(int hareTypeId)
    {
        this.dataManager.set(HARE_TYPE, hareTypeId);
    }

    @SideOnly(Side.CLIENT)
    public float getJumpCompletion(float p_175521_1_)
    {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + p_175521_1_) / (float) this.jumpDuration;
    }

    public void setMovementSpeed(double newSpeed)
    {
        this.getNavigator().setSpeed(newSpeed);
        this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
    }

    public void startJumping()
    {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChildren = 5 + rand.nextInt(5); // 5-10
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityHareTFC baby = new EntityHareTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return 0;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(HARE_TYPE, 0);
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.jumpTicks != this.jumpDuration)
        {
            ++this.jumpTicks;
        }
        else if (this.jumpDuration != 0)
        {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("HareType", this.getHareType());
    }

    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setHareType(compound.getInteger("HareType"));
    }

    @Nonnull
    @Override
    public SoundCategory getSoundCategory()
    {
        return SoundCategory.NEUTRAL;
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
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        return false;
    }

    @Override
    public double getOldDeathChance()
    {
        return 0;
    }

    @Override
    protected void initEntityAI()
    {
        double speedMult = 2.2D;
        EntityAnimalTFC.addWildPreyAI(this, speedMult);
        EntityAnimalTFC.addCommonPreyAI(this, speedMult);

        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof EntityAIPanic);

        this.tasks.addTask(1, new EntityHareTFC.AIPanic(this, 1.4D * speedMult));
        this.tasks.addTask(2, new EntityAIMate(this, 1.2D));
        for (ItemStack is : OreDictionary.getOres("carrot"))
        {
            Item item = is.getItem();
            this.tasks.addTask(3, new EntityAITempt(this, 1.4D, item, false));
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_RABBIT;
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        int i = this.getRandomHareType();

        if (livingdata instanceof EntityHareTFC.HareTypeData)
        {
            i = ((EntityHareTFC.HareTypeData) livingdata).typeData;
        }
        else
        {
            livingdata = new EntityHareTFC.HareTypeData(i);
        }

        this.setHareType(i);

        return livingdata;
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    protected float getJumpUpwardsMotion()
    {
        if (!this.collidedHorizontally && (!this.moveHelper.isUpdating() || this.moveHelper.getY() <= this.posY + 0.5D))
        {
            Path path = this.navigator.getPath();

            if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
            {
                Vec3d vec3d = path.getPosition(this);

                if (vec3d.y > this.posY + 0.5D)
                {
                    return 0.5F;
                }
            }

            return this.moveHelper.getSpeed() <= 0.6D ? 0.2F : 0.3F;
        }
        else
        {
            return 0.5F;
        }
    }

    protected void jump()
    {
        super.jump();
        double d0 = this.moveHelper.getSpeed();

        if (d0 > 0.0D)
        {
            double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;

            if (d1 < 0.010000000000000002D)
            {
                this.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
            }
        }

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte) 1);
        }
    }

    public void setJumping(boolean jumping)
    {
        super.setJumping(jumping);

        if (jumping)
        {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.rotationYaw = (float) (MathHelper.atan2(z - this.posZ, x - this.posX) * (180D / Math.PI)) - 90.0F;
    }

    private void enableJumpControl()
    {
        ((EntityHareTFC.RabbitJumpHelper) this.jumpHelper).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((EntityHareTFC.RabbitJumpHelper) this.jumpHelper).setCanJump(false);
    }

    private void updateMoveTypeDuration()
    {
        if (this.moveHelper.getSpeed() < 2.2D)
        {
            this.currentMoveTypeDuration = 10;
        }
        else
        {
            this.currentMoveTypeDuration = 1;
        }
    }

    private void checkLandingDelay()
    {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    private int getRandomHareType()
    {
        float temperature = 0;
        float rainfall = 0;
        float floraDensity = 0;
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        int i = this.rand.nextInt(100);

        if (biomeType == BiomeHelper.BiomeType.SAVANNA)
        {
            return i < 50 ? 1 : (i < 90 ? 1 : 3);
        }
        if (biomeType == BiomeHelper.BiomeType.DESERT)
        {
            return i < 10 ? 3 : (i < 90 ? 0 : 1);
        }
        else
        {
            return i < 50 ? 0 : (i < 90 ? 1 : 2);
        }
    }

    static class RabbitMoveHelper extends EntityMoveHelper
    {
        private final EntityHareTFC rabbit;
        private double nextJumpSpeed;

        public RabbitMoveHelper(EntityHareTFC rabbit)
        {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public void setMoveTo(double x, double y, double z, double speedIn)
        {
            if (this.rabbit.isInWater())
            {
                speedIn = 1.5D;
            }

            super.setMoveTo(x, y, z, speedIn);

            if (speedIn > 0.0D)
            {
                this.nextJumpSpeed = speedIn;
            }
        }

        public void onUpdateMoveHelper()
        {
            if (this.rabbit.onGround && !this.rabbit.isJumping && !((EntityHareTFC.RabbitJumpHelper) this.rabbit.jumpHelper).isJumping())
            {
                this.rabbit.setMovementSpeed(0.0D);
            }
            else if (this.isUpdating())
            {
                this.rabbit.setMovementSpeed(this.nextJumpSpeed);
            }

            super.onUpdateMoveHelper();
        }
    }

    public static class HareTypeData implements IEntityLivingData
    {
        public int typeData;

        public HareTypeData(int type)
        {
            this.typeData = type;
        }
    }

    static class AIPanic extends EntityAIPanic
    {
        private final EntityHareTFC rabbit;

        public AIPanic(EntityHareTFC rabbit, double speedIn)
        {
            super(rabbit, speedIn);
            this.rabbit = rabbit;
        }

        public void updateTask()
        {
            super.updateTask();
            this.rabbit.setMovementSpeed(this.speed);
        }
    }

    public static class RabbitJumpHelper extends EntityJumpHelper
    {
        private final EntityHareTFC rabbit;
        private boolean canJump;

        public RabbitJumpHelper(EntityHareTFC rabbit)
        {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean isJumping()
        {
            return this.isJumping;
        }

        public boolean canJump()
        {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn)
        {
            this.canJump = canJumpIn;
        }

        public void doJump()
        {
            if (this.isJumping)
            {
                this.rabbit.startJumping();
                this.isJumping = false;
            }
        }
    }
}
