/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.entities.ai.FindNestGoal;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class OviparousAnimal extends ProducingAnimal
{
    public float flapping = 1f;
    private final ForgeConfigSpec.IntValue hatchDays;
    public float oFlap;
    public float flap;
    public float oFlapSpeed;
    public float flapSpeed;
    private float nextFlap = 1f;

    public OviparousAnimal(EntityType<? extends OviparousAnimal> type, Level level, Supplier<? extends SoundEvent> ambient, Supplier<? extends SoundEvent> hurt, Supplier<? extends SoundEvent> death, Supplier<? extends SoundEvent> step, ForgeConfigSpec.DoubleValue adultFamiliarityCap, ForgeConfigSpec.IntValue daysToAdulthood, ForgeConfigSpec.IntValue usesToElderly, ForgeConfigSpec.BooleanValue eatsRottenFood, ForgeConfigSpec.IntValue produceTicks, ForgeConfigSpec.DoubleValue produceFamiliarity, ForgeConfigSpec.IntValue hatchDays)
    {
        super(type, level, ambient, hurt, death, step, adultFamiliarityCap, daysToAdulthood, usesToElderly, eatsRottenFood, produceTicks, produceFamiliarity);
        this.hatchDays = hatchDays;
    }

    @Override
    public void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(2, new FindNestGoal(this));
    }

    @Override
    protected boolean isFlapping()
    {
        return flyDist > nextFlap;
    }

    @Override
    protected void onFlap()
    {
        nextFlap = flyDist + flapSpeed / 2.0F;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dims)
    {
        return dims.height * 0.92F;
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        oFlap = flap;
        oFlapSpeed = flapSpeed;
        flapSpeed += (onGround ? -1.0F : 4.0F) * 0.3F;
        flapSpeed = Mth.clamp(flapSpeed, 0.0F, 1.0F);
        if (!onGround && flapping < 1.0F)
        {
            flapping = 1.0F;
        }

        flapping *= 0.9F;
        final Vec3 move = getDeltaMovement();
        if (!onGround && move.y < 0.0D)
        {
            setDeltaMovement(move.multiply(1.0D, 0.6D, 1.0D));
        }
        flap += flapping * 2.0F;
    }

    @Override
    public boolean hasProduct()
    {
        return getGender() == Gender.FEMALE && getAgeType() == Age.ADULT && (getProducedTick() <= 0 || getProductsCooldown() <= 0);
    }

    @Override
    public Type getTFCAnimalType()
    {
        return Type.OVIPAROUS;
    }

    @SuppressWarnings("unchecked")
    public ItemStack makeEgg()
    {
        ItemStack stack = new ItemStack(Items.EGG);
        if (isFertilized())
        {
            stack.getCapability(EggCapability.CAPABILITY).ifPresent(egg -> {
                OviparousAnimal baby = ((EntityType<OviparousAnimal>) getType()).create(level);
                if (baby != null)
                {
                    baby.setGender(Gender.valueOf(random.nextBoolean()));
                    baby.setBirthDay((int) Calendars.SERVER.getTotalDays());
                    baby.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
                    egg.setFertilized(baby, Calendars.SERVER.getTotalDays() + hatchDays.get());
                }
            });
        }
        return stack;
    }
}
