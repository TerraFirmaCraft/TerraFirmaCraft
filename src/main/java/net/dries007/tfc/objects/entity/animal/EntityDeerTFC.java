/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.LootTableListTFC;
import net.dries007.tfc.util.TFCSoundEvents;
import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class EntityDeerTFC extends EntityAnimalMammal implements IAnimalTFC
{
    private static final int DAYS_TO_ADULTHOOD = 720;
    private static final int DAYS_TO_FULL_GESTATION = 210;

    private static int getRandomGrowth()
    {
        int lifeTimeDays = Constants.RNG.nextInt(DAYS_TO_ADULTHOOD * 4);
        return (int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays);
    }

    @SuppressWarnings("unused")
    public EntityDeerTFC(World worldIn)
    {
        this(worldIn, Gender.fromBool(Constants.RNG.nextBoolean()),
            getRandomGrowth());
    }

    public EntityDeerTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(1.3F, 1.9F);
    }

    @Override
    public boolean isValidSpawnConditions(Biome biome, float temperature, float rainfall)
    {
        return temperature > -20 && temperature < 20 && rainfall > 75;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityDeerTFC baby = new EntityDeerTFC(this.world, Gender.fromBool(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
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
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == ItemsTFC.SALT;
    }

    @Override
    public float getPercentToAdulthood()
    {
        if (this.getAge() != Age.CHILD) return 1;
        double value = (CalendarTFC.PLAYER_TIME.getTotalDays() - this.getBirthDay()) / (double) DAYS_TO_ADULTHOOD;
        if (value > 1f) value = 1f;
        if (value < 0f) value = 0;
        return (float) value;
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.PLAYER_TIME.getTotalDays() >= this.getBirthDay() + DAYS_TO_ADULTHOOD ? Age.ADULT : Age.CHILD;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return TFCSoundEvents.ANIMAL_DEER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSoundEvents.ANIMAL_DEER_DEATH;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.3D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1D, ItemsTFC.SALT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSoundEvents.ANIMAL_DEER_CRY : TFCSoundEvents.ANIMAL_DEER_SAY;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableListTFC.ANIMALS_DEER;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_HORSE_STEP, 0.15F, 1.0F);
    }
}
