/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import java.util.function.Supplier;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.util.events.AnimalProductEvent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class DairyAnimal extends ProducingMammal
{
    public DairyAnimal(EntityType<? extends TFCAnimal> animal, Level level, Supplier<? extends SoundEvent> ambient, Supplier<? extends SoundEvent> hurt, Supplier<? extends SoundEvent> death, Supplier<? extends SoundEvent> step, ForgeConfigSpec.DoubleValue adultFamiliarityCap, ForgeConfigSpec.IntValue daysToAdulthood, ForgeConfigSpec.IntValue usesToElderly, ForgeConfigSpec.BooleanValue eatsRottenFood, ForgeConfigSpec.IntValue childCount, ForgeConfigSpec.IntValue gestationDays, ForgeConfigSpec.IntValue milkTicks, ForgeConfigSpec.DoubleValue milkingFamiliarity)
    {
        super(animal, level, ambient, hurt, death, step, adultFamiliarityCap, daysToAdulthood, usesToElderly, eatsRottenFood, childCount, gestationDays, milkTicks, milkingFamiliarity);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (getFamiliarity() > produceFamiliarity.get() && isReadyForAnimalProduct())
            {
                // copy the stack because we do not know if we'll need to replace it or not yet
                ItemStack bucket = held.copy();
                boolean filled = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(itemCap -> {
                    FluidStack milk = new FluidStack(getMilkFluid(), 1000);
                    return itemCap.fill(milk, IFluidHandler.FluidAction.EXECUTE) > 0;
                }).orElse(false);
                if (filled)
                {
                    // at this point we are guaranteeing milking will happen. The question is how?
                    playSound(SoundEvents.COW_MILK, getSoundVolume(), getVoicePitch());
                    AnimalProductEvent event = new AnimalProductEvent(level, blockPosition(), player, this, bucket, held, 1);
                    // if the event is NOT cancelled
                    if (!MinecraftForge.EVENT_BUS.post(event))
                    {
                        player.setItemInHand(hand, event.getProduct());
                        addUses(event.getUses());
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                sendTooltip(level, player);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hasProduct()
    {
        return getGender() == Gender.FEMALE && getAgeType() == Age.ADULT && getProductsCooldown() == 0;
    }

    private void sendTooltip(Level level, Player player)
    {
        TranslatableComponent component = null;
        if (getGender() == Gender.MALE)
        {
            component = new TranslatableComponent(MOD_ID + ".tooltip.animal.male_milk", getTypeName().getString());
        }
        else if (getAgeType() == Age.OLD)
        {
            component = new TranslatableComponent(MOD_ID + ".tooltip.animal.old", getTypeName().getString());
        }
        else if (getAgeType() == Age.CHILD)
        {
            component = new TranslatableComponent(MOD_ID + ".tooltip.animal.young", getTypeName().getString());
        }
        else if (getFamiliarity() <= produceFamiliarity.get())
        {
            component = new TranslatableComponent(MOD_ID + ".tooltip.animal.low_familiarity", getTypeName().getString());
        }
        else if (!hasProduct())
        {
            component = new TranslatableComponent(MOD_ID + ".tooltip.animal.no_milk", getTypeName().getString());
        }
        if (component != null && level.isClientSide)
        {
            player.displayClientMessage(component, true);
        }
    }

    public Fluid getMilkFluid()
    {
        return ForgeMod.MILK.get();
    }
}
