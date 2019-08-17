/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.*;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class EntityRabbitTFC extends EntityAnimalMammal implements IAnimalTFC
{
    private static final int DAYS_TO_ADULTHOOD = 240;
    private static final int DAYS_TO_FULL_GESTATION = 30;

    private static final DataParameter<Integer> RABBIT_TYPE = EntityDataManager.createKey(EntityRabbitTFC.class, DataSerializers.VARINT);

    public static void registerFixesRabbit(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityRabbitTFC.class);
    }

    private static int getRandomGrowth()
    {
        int lifeTimeDays = Constants.RNG.nextInt(DAYS_TO_ADULTHOOD * 4);
        return (int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays);
    }

    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;

    @SuppressWarnings("unused")
    public EntityRabbitTFC(World worldIn)
    {
        this(worldIn, Gender.fromBool(Constants.RNG.nextBoolean()), getRandomGrowth());
    }

    public EntityRabbitTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.4F, 0.5F);
        this.jumpHelper = new EntityRabbitTFC.RabbitJumpHelper(this);
        this.moveHelper = new EntityRabbitTFC.RabbitMoveHelper(this);
        this.setMovementSpeed(0.0D);
    }

    @Override
    public boolean isValidSpawnConditions(Biome biome, float temperature, float rainfall)
    {
        return temperature > -10 && rainfall > 150;
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

            EntityRabbitTFC.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityRabbitTFC.RabbitJumpHelper) this.jumpHelper;

            if (!entityrabbit$rabbitjumphelper.getIsJumping())
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

    public int getRabbitType()
    {
        return this.dataManager.get(RABBIT_TYPE).intValue();
    }

    public void setRabbitType(int rabbitTypeId)
    {
        this.dataManager.set(RABBIT_TYPE, Integer.valueOf(rabbitTypeId));
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

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("RabbitType", this.getRabbitType());
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setRabbitType(compound.getInteger("RabbitType"));
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 5 + rand.nextInt(5); // 5-10
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityRabbitTFC baby = new EntityRabbitTFC(this.world, Gender.fromBool(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return DAYS_TO_FULL_GESTATION;
    }

    public SoundCategory getSoundCategory()
    {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "carrot");
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(RABBIT_TYPE, Integer.valueOf(0));
    }

    @Override
    public float getPercentToAdulthood()
    {
        if (this.getAge() != Age.CHILD)
            return 1;
        double value = (CalendarTFC.PLAYER_TIME.getTotalDays() - this.getBirthDay()) / (double) DAYS_TO_ADULTHOOD;
        if (value > 1f)
            value = 1f;
        if (value < 0f)
            value = 0;
        return (float) value;
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.PLAYER_TIME.getTotalDays() >= this.getBirthDay() + DAYS_TO_ADULTHOOD ? Age.ADULT : Age.CHILD;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityRabbitTFC.AIPanic(this, 2.2D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.2D));
        for (ItemStack is : OreDictionary.getOres("carrot"))
        {
            Item item = is.getItem();
            this.tasks.addTask(3, new EntityAITempt(this, 1.4D, item, false));
        }
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 2.0D, 2.0D));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityMob.class, 4.0F, 2.0D, 2.0D));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
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
        int i = this.getRandomRabbitType();

        if (livingdata instanceof EntityRabbitTFC.RabbitTypeData)
        {
            i = ((EntityRabbitTFC.RabbitTypeData) livingdata).typeData;
        }
        else
        {
            livingdata = new EntityRabbitTFC.RabbitTypeData(i);
        }

        this.setRabbitType(i);

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

    protected void createEatingParticles()
    {
        BlockCarrot blockcarrot = (BlockCarrot) Blocks.CARROTS;
        IBlockState iblockstate = blockcarrot.withAge(blockcarrot.getMaxAge());
        this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
            this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
            this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
            this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D,
            0.0D, Block.getStateId(iblockstate));
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.rotationYaw = (float) (MathHelper.atan2(z - this.posZ, x - this.posX) * (180D / Math.PI)) - 90.0F;
    }

    private void enableJumpControl()
    {
        ((EntityRabbitTFC.RabbitJumpHelper) this.jumpHelper).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((EntityRabbitTFC.RabbitJumpHelper) this.jumpHelper).setCanJump(false);
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

    private int getRandomRabbitType()
    {
        Biome biome = this.world.getBiome(new BlockPos(this));
        int i = this.rand.nextInt(100);
        if (biome.isSnowyBiome())
        {
            return i < 5 ? 7 : (i < 10 ? 6 : (i < 80 ? 1 : 3));
        }
        else
        {
            return i < 50 ? 0 : (i < 90 ? 5 : (i < 95 ? 2 : 4));
        }
    }

    static class RabbitMoveHelper extends EntityMoveHelper
    {
        private final EntityRabbitTFC rabbit;
        private double nextJumpSpeed;

        public RabbitMoveHelper(EntityRabbitTFC rabbit)
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
            if (this.rabbit.onGround && !this.rabbit.isJumping && !((EntityRabbitTFC.RabbitJumpHelper) this.rabbit.jumpHelper).getIsJumping())
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

    public static class RabbitTypeData implements IEntityLivingData
    {
        public int typeData;

        public RabbitTypeData(int type)
        {
            this.typeData = type;
        }
    }

    static class AIPanic extends EntityAIPanic
    {
        private final EntityRabbitTFC rabbit;

        public AIPanic(EntityRabbitTFC rabbit, double speedIn)
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

    public class RabbitJumpHelper extends EntityJumpHelper
    {
        private final EntityRabbitTFC rabbit;
        private boolean canJump;

        public RabbitJumpHelper(EntityRabbitTFC rabbit)
        {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean getIsJumping()
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
