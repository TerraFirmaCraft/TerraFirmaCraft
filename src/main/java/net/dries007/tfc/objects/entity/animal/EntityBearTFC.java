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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

//import net.dries007.tfc.objects.entity.ai.EntityAIAttackMeleeTFC;

@ParametersAreNonnullByDefault
public class EntityBearTFC extends EntityAnimalMammal implements IPredator
{
    private static final int DAYS_TO_ADULTHOOD = 1800;
    private static final int DAYS_TO_FULL_GESTATION = 210;
    private static final DataParameter<Boolean> IS_STANDING;

    static
    {
        IS_STANDING = EntityDataManager.createKey(EntityBearTFC.class, DataSerializers.BOOLEAN);
    }

    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;

    @SuppressWarnings("unused")
    public EntityBearTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(DAYS_TO_ADULTHOOD));
    }

    public EntityBearTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(1.2F, 1.2F);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.TAIGA || biomeType == BiomeHelper.BiomeType.TEMPERATE_FOREST))
        {
            return ConfigTFC.WORLD.predatorSpawnRarity;
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
    public int getDaysToAdulthood()
    {
        return DAYS_TO_ADULTHOOD;
    }

    @Override
    public boolean isFood(ItemStack it)
    {
        return it.getItem() == Items.FISH;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityBearTFC baby = new EntityBearTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return DAYS_TO_FULL_GESTATION;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return TFCSounds.ANIMAL_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.ANIMAL_BEAR_DEATH;
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

    public boolean isStanding()
    {
        return this.dataManager.get(IS_STANDING);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSounds.ANIMAL_BEAR_CRY : TFCSounds.ANIMAL_BEAR_SAY;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_BEAR;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    public void setStanding(boolean standing)
    {
        this.dataManager.set(IS_STANDING, standing);
    }

    @SideOnly(Side.CLIENT)
    public float getStandingAnimationScale(float p_189795_1_)
    {
        return (this.clientSideStandAnimation0 + (this.clientSideStandAnimation - this.clientSideStandAnimation0) * p_189795_1_) / 6.0F;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityBearTFC.AIBearStandAttack()); // Watch and "warn" near players, attack by standing (like polar bears)
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    public void onUpdate()
    {
        super.onUpdate();
        if (this.world.isRemote)
        {
            this.clientSideStandAnimation0 = this.clientSideStandAnimation;
            if (this.isStanding())
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            }
            else
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0)
        {
            --this.warningSoundTicks;
        }

    }

    protected void playWarningSound()
    {
        if (this.warningSoundTicks <= 0)
        {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }

    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(IS_STANDING, false);
    }

    private class AIBearStandAttack extends EntityAIAttackMelee
    {
        public AIBearStandAttack()
        {
            super(EntityBearTFC.this, 1.25D, true);
        }

        @Override
        public void resetTask()
        {
            EntityBearTFC.this.setStanding(false);
            super.resetTask();
        }

        @Override
        protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr)
        {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTick <= 0)
            {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(enemy);
                EntityBearTFC.this.setStanding(false);
            }
            else if (distToEnemySqr <= d0 * 2.0D)
            {
                if (this.attackTick <= 0)
                {
                    EntityBearTFC.this.setStanding(false);
                    this.attackTick = 20;
                }

                if (this.attackTick <= 10)
                {
                    EntityBearTFC.this.setStanding(true);
                    EntityBearTFC.this.playWarningSound();
                }
            }
            else
            {
                this.attackTick = 20;
                EntityBearTFC.this.setStanding(false);
            }

        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget)
        {
            return 4.0F + attackTarget.width;
        }
    }

}
