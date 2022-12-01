/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import com.mojang.serialization.Dynamic;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.livestock.LivestockAi;
import net.dries007.tfc.common.entities.ai.livestock.OviparousAi;
import net.dries007.tfc.config.animals.OviparousAnimalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.events.AnimalProductEvent;

public abstract class OviparousAnimal extends ProducingAnimal
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Chicken.createAttributes().add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.5);
    }

    public float flapping = 1f;
    public float oFlap;
    public float flap;
    public float oFlapSpeed;
    public float flapSpeed;
    private float nextFlap = 1f;
    private boolean crowed;
    private final ForgeConfigSpec.IntValue hatchDays;

    public OviparousAnimal(EntityType<? extends OviparousAnimal> type, Level level, TFCSounds.EntitySound sounds, OviparousAnimalConfig config)
    {
        super(type, level, sounds, config.inner());
        this.hatchDays = config.hatchDays();
    }

    /**
     * Allows high jumping {@link Rabbit#getJumpPower()}
     */
    @Override
    protected float getJumpPower()
    {
        if (!moveControl.hasWanted() || moveControl.getWantedY() <= getY() + 0.5D && moveControl.getSpeedModifier() > 1.1f)
        {
            return super.getJumpPower() * 1.2f;
        }
        return super.getJumpPower();
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        if (level.getGameTime() % 20 == 0 && random.nextInt(3) == 0 && getBrain().getActiveNonCoreActivity().filter(p -> p == Activity.AVOID).isPresent())
        {
            getJumpControl().jump();
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        final long time = level.getDayTime() % 24000;
        if (!crowed && time > 0 && time < 1000 && random.nextInt(10) == 0)
        {
            if (getGender().toBool())
            {
                playSound(TFCSounds.ROOSTER_CRY.get(), getSoundVolume() * 1.2f, getVoicePitch());
            }
            else if (getAmbientSound() != null)
            {
                playSound(getAmbientSound(), getSoundVolume() * 0.5f, getVoicePitch());
            }
            crowed = true;
        }
        if (time > 1000)
        {
            crowed = false;
        }
    }

    @Override
    protected Brain.Provider<? extends OviparousAnimal> brainProvider()
    {
        return Brain.provider(OviparousAi.MEMORY_TYPES, OviparousAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return OviparousAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<? extends OviparousAnimal> getBrain()
    {
        return (Brain<OviparousAnimal>) super.getBrain();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void tickBrain()
    {
        ((Brain<OviparousAnimal>) getBrain()).tick((ServerLevel) level, this);
        LivestockAi.updateActivity(this);
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
        if (isPassenger()) flapSpeed = 0F;
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
    public boolean causeFallDamage(float amount, float speed, DamageSource src)
    {
        return false;
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
                    baby.setBirthDay(Calendars.SERVER.getTotalDays());
                    baby.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
                    egg.setFertilized(baby, Calendars.SERVER.getTotalDays() + hatchDays.get());
                }
            });
        }
        AnimalProductEvent event = new AnimalProductEvent(level, blockPosition(), null, this, stack, ItemStack.EMPTY, 1);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            addUses(event.getUses());
        }
        return event.getProduct();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return EntityHelpers.pluck(player, hand, this) ? InteractionResult.sidedSuccess(level.isClientSide) : super.mobInteract(player, hand);
    }

    @Override
    public MutableComponent getProductReadyName()
    {
        return Helpers.translatable("tfc.jade.product.eggs");
    }
}
