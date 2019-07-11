/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.IEgg;
import net.dries007.tfc.objects.entity.ai.EntityAIFindNest;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntityChickenTFC extends EntityAnimalOviparous
{
    private static final int DAYS_TO_ADULTHOOD = 124;
    private static final int DAYS_TO_HATCH_EGG = 21;

    //Copy from vanilla's EntityChicken, used by renderer to properly handle wing flap
    public float wingRotation;
    public float destPos;
    public float oFlapSpeed;
    public float oFlap;
    public float wingRotDelta = 1.0F;

    private static int getRandomGrowth()
    {
        int lifeTimeDays = Constants.RNG.nextInt(DAYS_TO_ADULTHOOD * 4);
        return (int) (CalendarTFC.INSTANCE.getTotalDays() - lifeTimeDays);
    }

    public EntityChickenTFC(World worldIn)
    {
        this(worldIn, Gender.fromBool(Constants.RNG.nextBoolean()),
            getRandomGrowth());
    }

    public EntityChickenTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.9F, 0.9F);
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float) ((double) this.destPos + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);

        if (!this.onGround && this.wingRotDelta < 1.0F)
        {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);

        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
    }

    @Override
    public NonNullList<ItemStack> layEggs()
    {
        NonNullList<ItemStack> eggs = super.layEggs();
        ItemStack egg = new ItemStack(Items.EGG);
        if (this.isFertilized())
        {
            IEgg cap = egg.getCapability(CapabilityEgg.CAPABILITY, null);
            if (cap != null)
            {
                cap.setFertilized(new EntityChickenTFC(this.world), DAYS_TO_HATCH_EGG + CalendarTFC.INSTANCE.getTotalDays());
            }
        }
        this.setFertilized(false);
        eggs.add(egg);
        return eggs;
    }

    @Override
    public float getPercentToAdulthood()
    {
        if (this.getAge() != Age.CHILD) return 1;
        double value = (CalendarTFC.INSTANCE.getTotalDays() - this.getBirthDay()) / (double) DAYS_TO_ADULTHOOD;
        if (value > 1f) value = 1f;
        if (value < 0f) value = 0;
        return (float) value;
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.INSTANCE.getTotalDays() >= this.getBirthDay() + DAYS_TO_ADULTHOOD ? Age.ADULT : Age.CHILD;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIMate(this, 1D));
        this.tasks.addTask(4, new EntityAIFindNest(this, 1D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }
}
