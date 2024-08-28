/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class InstantFluidBarrelRecipe extends BarrelRecipe
{
    public static final MapCodec<InstantFluidBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedFluidIngredient.FLAT_CODEC.fieldOf("primary_fluid").forGetter(c -> c.inputFluid),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("added_fluid").forGetter(c -> c.addedFluid),
        FluidStack.CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(c -> c.outputFluid),
        SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.BREWING_STAND_BREW)).forGetter(c -> c.sound)
    ).apply(i, InstantFluidBarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InstantFluidBarrelRecipe> STREAM_CODEC = StreamCodec.composite(
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        SizedFluidIngredient.STREAM_CODEC, c -> c.addedFluid,
        FluidStack.OPTIONAL_STREAM_CODEC, c -> c.outputFluid,
        ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), c -> c.sound,
        InstantFluidBarrelRecipe::new
    );

    private final SizedFluidIngredient addedFluid;

    public InstantFluidBarrelRecipe(SizedFluidIngredient primaryFluid, SizedFluidIngredient addedFluid, FluidStack outputFluid, Holder<SoundEvent> sound)
    {
        super(Optional.empty(), primaryFluid, ItemStackProvider.empty(), outputFluid, sound);
        this.addedFluid = addedFluid;
    }

    public SizedFluidIngredient getAddedFluid()
    {
        return addedFluid;
    }

    @Override
    public boolean matches(BarrelInventory container)
    {
        // Must match the input with either the item slot, or fluid IO slot.
        return matches(container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM), container.getFluidInTank(0))
            || matches(container.getStackInSlot(BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN), container.getFluidInTank(0));
    }

    public boolean matches(ItemStack inputStack, FluidStack fluidStack)
    {
        // As with instant recipes, we must have enough added input to fully convert the existing fluid.
        final FluidStack extractableFluid = FluidHelpers.getContainedFluid(inputStack);
        return inputFluid.test(fluidStack) && addedFluid.test(extractableFluid);
    }

    @Override
    public void assembleOutputs(BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {

            // Extract input fluid - this will be converted, so we need to unconditionally drain all of it, and void excess.
            final FluidStack primaryFluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // This can be invoked either with the stack in the primary input slot, or the stack in the fluid input slot, and the behavior will change accordingly
            // If we match a slot in the input slot, we continue, otherwise we assume matching the fluid IO slots
            final boolean inputIsItemSlot = matches(inventory.getStackInSlot(BarrelBlockEntity.SLOT_ITEM), primaryFluid);
            final ItemStack originalStack = Helpers.removeStack(inventory, inputIsItemSlot ? BarrelBlockEntity.SLOT_ITEM : BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN);
            final IFluidHandlerItem fluidHandler = originalStack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM);

            if (fluidHandler == null)
            {
                return;
            }

            final FluidStack addedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

            // Calculate the multiplier in use for this recipe.
            // Both fluid ingredients are required to be > 0
            final int multiplier = Math.min(
                primaryFluid.getAmount() / inputFluid.amount(),
                addedFluid.getAmount() / this.addedFluid.amount()
            );

            // Output fluid
            // Figure out exactly how much of the input fluid to consume, and attempt to consume that amount.
            // If we can't consume exactly that amount, we are aggressive and void excess.
            final int targetAddedFluid = multiplier * this.addedFluid.amount();
            final FluidStack actualAddedFluid = fluidHandler.drain(targetAddedFluid, IFluidHandler.FluidAction.SIMULATE);
            if (actualAddedFluid.isEmpty() || actualAddedFluid.getAmount() < targetAddedFluid)
            {
                // Drain everything, which was checked earlier
                fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            }
            else
            {
                // We drained exactly how much we needed to
                fluidHandler.drain(targetAddedFluid, IFluidHandler.FluidAction.EXECUTE);
            }

            // Set the output fluid
            final FluidStack outputFluid = this.outputFluid.copy();

            outputFluid.setAmount(Math.min(TFCConfig.SERVER.barrelCapacity.get(), outputFluid.getAmount() * multiplier));
            inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);

            // Set the input item
            // We removed it entirely later, so we just need to put it in the slot where the excess is
            FluidHelpers.updateContainerItem(originalStack, fluidHandler, (newOriginalStack, newContainerStack) -> {
                inventory.setStackInSlot(inputIsItemSlot ? BarrelBlockEntity.SLOT_ITEM : BarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, newOriginalStack);
                if (!newContainerStack.isEmpty())
                {
                    inventory.insertItemWithOverflow(newContainerStack);
                }
            });
        });
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.INSTANT_FLUID_BARREL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BARREL_INSTANT_FLUID.get();
    }
}
