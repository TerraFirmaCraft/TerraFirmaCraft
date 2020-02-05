/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.entity.ai.*;
import net.dries007.tfc.util.calendar.CalendarTFC;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class EntityWolfTFC extends EntityTameableTFC implements IAnimalTFC
{
    private static final int DAYS_TO_ADULTHOOD = 360;
    private static final int DAYS_TO_FULL_GESTATION = 70;

    private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.createKey(EntityWolfTFC.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> BEGGING = EntityDataManager.createKey(EntityWolfTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityWolfTFC.class, DataSerializers.VARINT);

    private float headRotationCourse;
    private float headRotationCourseOld;
    private boolean isWet;
    private boolean isShaking;
    private float timeWolfIsShaking;
    private float prevTimeWolfIsShaking;

    @SuppressWarnings("unused")
    public EntityWolfTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(DAYS_TO_ADULTHOOD));
    }

    public EntityWolfTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.6F, 0.85F);
        this.setTamed(false);
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
        return 1;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 5;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround)
        {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
            this.world.setEntityState(this, (byte) 8);
        }
        if (!this.world.isRemote && this.getAttackTarget() == null && this.isAngry())
        {
            this.setAngry(false);
        }
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1 + rand.nextInt(1); //1-2
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityWolfTFC baby = new EntityWolfTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
            UUID uuid = this.getOwnerId();
            if (uuid != null)
            {
                baby.setOwnerId(uuid);
                baby.setTamed(true);
            }
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return DAYS_TO_FULL_GESTATION;
    }

    @Override
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return DAYS_TO_ADULTHOOD;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return (stack.getItem() == Items.BONE) || (stack.getItem() instanceof ItemFood && ((ItemFood) stack.getItem()).isWolfsFavoriteMeat());
    }

    @SideOnly(Side.CLIENT)
    public boolean isWolfWet()
    {
        return this.isWet;
    }

    @SideOnly(Side.CLIENT)
    public float getShadingWhileWet(float p_70915_1_)
    {
        return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70915_1_) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float p_70923_1_, float p_70923_2_)
    {
        float f = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70923_1_ + p_70923_2_) / 1.8F;
        if (f < 0.0F)
        {
            f = 0.0F;
        }
        else if (f > 1.0F)
        {
            f = 1.0F;
        }
        return MathHelper.sin(f * 3.1415927F) * MathHelper.sin(f * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float p_70917_1_)
    {
        return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * p_70917_1_) * 0.15F * 3.1415927F;
    }

    @Override
    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }

    @SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        if (this.isAngry())
        {
            return 1.5393804F;
        }
        else
        {
            return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.dataManager.get(DATA_HEALTH_ID)) * 0.02F) * 3.1415927F : 0.62831855F;
        }
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        if (super.processInteract(player, hand)) // Process feeding first
        {
            return true;
        }
        ItemStack itemstack = player.getHeldItem(hand);
        if (this.isTamed())
        {
            if (!itemstack.isEmpty())
            {
                if (itemstack.getItem() instanceof ItemFood && ((ItemFood) itemstack.getItem()).isWolfsFavoriteMeat() && this.dataManager.get(DATA_HEALTH_ID) < 20.0F)
                {
                    if (!player.isCreative())
                    {
                        itemstack.shrink(1);
                    }
                    this.heal((float) ((ItemFood) itemstack.getItem()).getHealAmount(itemstack));
                    return true;
                }
                else if (itemstack.getItem() == Items.DYE) // todo color by ore dictionary?
                {
                    EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());
                    if (enumdyecolor != this.getCollarColor())
                    {
                        this.setCollarColor(enumdyecolor);
                        if (!player.isCreative())
                        {
                            itemstack.shrink(1);
                        }
                        return true;
                    }
                }
            }
            else if (this.isOwner(player))
            {
                if (!this.world.isRemote)
                {
                    this.aiSit.setSitting(!this.isSitting());
                    this.isJumping = false;
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                }
                return true;
            }
        }
        else if (isFood(itemstack) && !this.isAngry() && !this.isTamed() && getFamiliarity() >= 0.3f)
        {
            if (!this.world.isRemote)
            {
                if (!player.isCreative())
                {
                    itemstack.shrink(1);
                }
                if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player))
                {
                    this.setTamedBy(player);
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.playTameEffect(true);
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

    public boolean isAngry()
    {
        return (this.dataManager.get(TAMED) & 2) != 0;
    }

    public void setAngry(boolean angry)
    {
        byte b0 = this.dataManager.get(TAMED);
        if (angry)
        {
            this.dataManager.set(TAMED, (byte) (b0 | 2));
        }
        else
        {
            this.dataManager.set(TAMED, (byte) (b0 & -3));
        }
    }

    public EnumDyeColor getCollarColor()
    {
        return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR) & 15);
    }

    public void setCollarColor(EnumDyeColor collarcolor)
    {
        this.dataManager.set(COLLAR_COLOR, collarcolor.getDyeDamage());
    }

    public boolean isBegging()
    {
        return this.dataManager.get(BEGGING);
    }

    public void setBegging(boolean beg)
    {
        this.dataManager.set(BEGGING, beg);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Angry", this.isAngry());
        compound.setByte("CollarColor", (byte) this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setAngry(compound.getBoolean("Angry"));
        if (compound.hasKey("CollarColor", 99))
        {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }

    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player)
    {
        return !this.isAngry() && super.canBeLeashedTo(player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 8)
        {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void setTamed(boolean tamed)
    {
        super.setTamed(tamed);
        if (tamed)
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
    {
        if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast))
        {
            if (target instanceof EntityWolfTFC)
            {
                EntityWolfTFC entitywolf = (EntityWolfTFC) target;
                if (entitywolf.isTamed() && entitywolf.getOwner() == owner)
                {
                    return false;
                }
            }
            if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target))
            {
                return false;
            }
            else
            {
                return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DATA_HEALTH_ID, this.getHealth());
        this.dataManager.register(BEGGING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initEntityAI()
    {
        this.aiSit = new EntityAISitTFC(this);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(6, new EntityAIFollowOwnerTFC(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(9, new EntityAIBegTFC(this, 8.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTargetTFC(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTargetTFC(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamedTFC(this, EntityAnimalTFC.class, false, (Predicate<Entity>) entity -> entity instanceof EntitySheepTFC || entity instanceof EntityPigTFC || entity instanceof EntityRabbitTFC));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, AbstractSkeleton.class, false));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        if (this.isTamed())
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
    {
        super.setAttackTarget(entitylivingbaseIn);
        if (entitylivingbaseIn == null)
        {
            this.setAngry(false);
        }
        else if (!this.isTamed())
        {
            this.setAngry(true);
        }

    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.headRotationCourseOld = this.headRotationCourse;
        if (this.isBegging())
        {
            this.headRotationCourse += (1.0F - this.headRotationCourse) * 0.4F;
        }
        else
        {
            this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;
        }

        if (this.isWet())
        {
            this.isWet = true;
            this.isShaking = false;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        }
        else if ((this.isWet || this.isShaking) && this.isShaking)
        {
            if (this.timeWolfIsShaking == 0.0F)
            {
                this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
            this.timeWolfIsShaking += 0.05F;
            if (this.prevTimeWolfIsShaking >= 2.0F)
            {
                this.isWet = false;
                this.isShaking = false;
                this.prevTimeWolfIsShaking = 0.0F;
                this.timeWolfIsShaking = 0.0F;
            }

            if (this.timeWolfIsShaking > 0.4F)
            {
                float f = (float) this.getEntityBoundingBox().minY;
                int i = (int) (MathHelper.sin((this.timeWolfIsShaking - 0.4F) * 3.1415927F) * 7.0F);
                for (int j = 0; j < i; ++j)
                {
                    float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double) f1, f + 0.8F, this.posZ + (double) f2, this.motionX, this.motionY, this.motionZ);
                }
            }
        }

    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        if (this.isAngry())
        {
            return SoundEvents.ENTITY_WOLF_GROWL;
        }
        else if (this.rand.nextInt(3) != 0)
        {
            return SoundEvents.ENTITY_WOLF_AMBIENT;
        }
        else
        {
            return this.isTamed() && this.dataManager.get(DATA_HEALTH_ID) < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
        }
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_WOLF;
    }

    @Override
    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    @Override
    protected void updateAITasks()
    {
        this.dataManager.set(DATA_HEALTH_ID, this.getHealth());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            Entity entity = source.getTrueSource();
            if (this.aiSit != null)
            {
                this.aiSit.setSitting(false);
            }
            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }
        return flag;
    }
}