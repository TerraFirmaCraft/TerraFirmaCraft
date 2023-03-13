/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.AnimalProductEvent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class DairyAnimal extends ProducingMammal
{
    public DairyAnimal(EntityType<? extends DairyAnimal> animal, Level level, TFCSounds.EntitySound sounds, ProducingMammalConfig config)
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
                final AnimalProductEvent event = new AnimalProductEvent(level, blockPosition(), player, this, milk, held, 1);

                if (!MinecraftForge.EVENT_BUS.post(event)) // if the event is NOT cancelled
                {
                    final FluidTank sourceFluidHandler = new FluidTank(Integer.MAX_VALUE);
                    sourceFluidHandler.setFluid(event.getFluidProduct());

                    FluidHelpers.transferBetweenItemAndOther(held, destFluidItemHandler, sourceFluidHandler, destFluidItemHandler, sound -> {
                        player.playSound(SoundEvents.COW_MILK, 1.0f, 1.0f); // play a custom sound, not the bucket fill sound
                    }, new FluidHelpers.AfterTransferWithPlayer(player, hand));

                    setProductsCooldown();
                    addUses(event.getUses());

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
            component = Helpers.translatable(MOD_ID + ".tooltip.animal.male_milk", getTypeName().getString());
        }
        else if (getAgeType() == Age.OLD)
        {
            component = Helpers.translatable(MOD_ID + ".tooltip.animal.old", getTypeName().getString());
        }
        else if (getAgeType() == Age.CHILD)
        {
            component = Helpers.translatable(MOD_ID + ".tooltip.animal.young", getTypeName().getString());
        }
        else if (getFamiliarity() <= produceFamiliarity.get())
        {
            component = Helpers.translatable(MOD_ID + ".tooltip.animal.low_familiarity", getTypeName().getString());
        }
        else if (!hasProduct())
        {
            component = Helpers.translatable(MOD_ID + ".tooltip.animal.no_milk", getTypeName().getString());
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

    @Override
    public MutableComponent getProductReadyName()
    {
        return Helpers.translatable("tfc.jade.product.milk");
    }
}
