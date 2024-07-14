/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.AnimalProductEvent;

import static net.dries007.tfc.TerraFirmaCraft.*;

public abstract class DairyAnimal extends ProducingMammal
{
    public DairyAnimal(EntityType<? extends DairyAnimal> animal, Level level, TFCSounds.EntityId sounds, ProducingMammalConfig config)
    {
        super(animal, level, sounds, config);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        final IFluidHandlerItem destFluidItemHandler = Helpers.getCapability(held, Capabilities.FLUID_ITEM);

        if (!held.isEmpty() && destFluidItemHandler != null)
        {
            if (getFamiliarity() > produceFamiliarity.get() && isReadyForAnimalProduct())
            {
                final FluidStack milk = new FluidStack(getMilkFluid(), FluidHelpers.BUCKET_VOLUME);
                final AnimalProductEvent event = new AnimalProductEvent(level(), blockPosition(), player, this, milk, held, 1);

                if (!NeoForge.EVENT_BUS.post(event).isCanceled())
                {
                    final FluidTank sourceFluidHandler = new FluidTank(Integer.MAX_VALUE);
                    sourceFluidHandler.setFluid(event.getFluidProduct());

                    FluidHelpers.transferBetweenItemAndOther(held, destFluidItemHandler, sourceFluidHandler, destFluidItemHandler, sound -> {
                        player.playSound(SoundEvents.COW_MILK, 1.0f, 1.0f); // play a custom sound, not the bucket fill sound
                    }, FluidHelpers.with(player, hand));

                    setProductsCooldown();
                    addUses(event.getUses());

                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                sendTooltip(level(), player);
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
        MutableComponent component = null;
        if (getGender() == Gender.MALE)
        {
            component = Component.translatable(MOD_ID + ".tooltip.animal.male_milk", getTypeName().getString());
        }
        else if (getAgeType() == Age.OLD)
        {
            component = Component.translatable(MOD_ID + ".tooltip.animal.old", getTypeName().getString());
        }
        else if (getAgeType() == Age.CHILD)
        {
            component = Component.translatable(MOD_ID + ".tooltip.animal.young", getTypeName().getString());
        }
        else if (getFamiliarity() <= produceFamiliarity.get())
        {
            component = Component.translatable(MOD_ID + ".tooltip.animal.low_familiarity", getTypeName().getString());
        }
        else if (!hasProduct())
        {
            component = Component.translatable(MOD_ID + ".tooltip.animal.no_milk", getTypeName().getString());
        }
        if (component != null && level.isClientSide)
        {
            player.displayClientMessage(component, true);
        }
    }

    public Fluid getMilkFluid()
    {
        return NeoForgeMod.MILK.get();
    }

    @Override
    public MutableComponent getProductReadyName()
    {
        return Component.translatable("tfc.jade.product.milk");
    }
}
