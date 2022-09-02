/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

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
import net.minecraftforge.fluids.capability.IFluidHandler;

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
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getCapability(Capabilities.FLUID_ITEM).isPresent())
        {
            if (getFamiliarity() > produceFamiliarity.get() && isReadyForAnimalProduct())
            {
                // copy the stack because we do not know if we'll need to replace it or not yet
                ItemStack bucket = held.copy();
                boolean filled = bucket.getCapability(Capabilities.FLUID_ITEM).map(itemCap -> {
                    FluidStack milk = new FluidStack(getMilkFluid(), FluidHelpers.BUCKET_VOLUME);
                    return itemCap.fill(milk, IFluidHandler.FluidAction.EXECUTE) > 0;
                }).orElse(false);
                if (filled)
                {
                    // at this point we are guaranteeing milking will happen. The question is how?
                    setProductsCooldown();
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
}
