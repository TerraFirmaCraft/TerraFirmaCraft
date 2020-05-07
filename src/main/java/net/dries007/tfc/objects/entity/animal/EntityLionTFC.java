/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIAttackMeleeTFC;
import net.dries007.tfc.objects.entity.ai.EntityAIWanderHuntArea;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

@ParametersAreNonnullByDefault
public class EntityLionTFC extends EntityAnimalMammal implements IPredator
{
    private static final float MONTHS_TO_ADULTHOOD = 60.0f;
    private static final float MONTHS_TO_FULL_GESTATION = 7.0f;

    //Values that has a visual effect on client
    private static final DataParameter<Integer> MOUTH_TICKS = EntityDataManager.createKey(EntityLionTFC.class, DataSerializers.VARINT);

    @SuppressWarnings("unused")
    public EntityLionTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(MONTHS_TO_ADULTHOOD));
    }

    public EntityLionTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(1.2F, 1.2F);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.SAVANNA || biomeType == BiomeHelper.BiomeType.PLAINS))
        {
            return ConfigTFC.WORLD.predatorSpawnRarity;
        }
        return 0;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.MALE_AND_FEMALES;
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
    public int getDaysToAdulthood()
    {
        return (int) Math.ceil(MONTHS_TO_ADULTHOOD * CalendarTFC.CALENDAR_TIME.getDaysInMonth());
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
            EntityLionTFC baby = new EntityLionTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return (long) Math.ceil(MONTHS_TO_FULL_GESTATION * CalendarTFC.CALENDAR_TIME.getDaysInMonth());
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(MOUTH_TICKS, 0);
    }

    public int getMouthTicks()
    {
        return this.dataManager.get(MOUTH_TICKS);
    }

    public void setMouthTicks(int value)
    {
        this.dataManager.set(MOUTH_TICKS, value);
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return TFCSounds.ANIMAL_LION_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.ANIMAL_LION_DEATH;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
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
    protected void initEntityAI()
    {
        EntityAIWander wander = new EntityAIWanderHuntArea(this, 1.0D);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.5F));
        this.tasks.addTask(2, new EntityAILionAttack().setWanderAI(wander));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, wander);
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.34D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSounds.ANIMAL_LION_CRY : TFCSounds.ANIMAL_LION_SAY;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_GRAN_FELINE;
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
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F); // Close enough
    }

    /**
     * Adds a bit of animation to the attack
     */
    protected class EntityAILionAttack extends EntityAIAttackMeleeTFC<EntityLionTFC>
    {
        protected int attackTicks;

        public EntityAILionAttack()
        {
            super(EntityLionTFC.this, 1.3D, 1.5D, AttackBehavior.DAYLIGHT_ONLY);
            this.attackTicks = 0;
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
            this.attackTicks = 0;
            EntityLionTFC.this.setMouthTicks(0);
        }

        @Override
        public void updateTask()
        {
            super.updateTask();
            ++this.attackTicks;
            EntityLionTFC.this.setMouthTicks(attackTicks);
        }
    }
}
