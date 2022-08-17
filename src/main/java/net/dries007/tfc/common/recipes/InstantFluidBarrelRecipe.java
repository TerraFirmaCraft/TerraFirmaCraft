/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class InstantFluidBarrelRecipe extends BarrelRecipe
{
    private final FluidStackIngredient addedFluid;

    public InstantFluidBarrelRecipe(ResourceLocation id, Builder builder, FluidStackIngredient addedFluid)
    {
        super(id, builder);
        this.addedFluid = addedFluid;
    }

    @Override
    public boolean matches(BarrelBlockEntity.BarrelInventory container, @Nullable Level level)
    {
        // Must match the input with either the item slot, or fluid IO slot.
        return matches(container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM), container.getFluidInTank(0)) || matches(container.getStackInSlot(BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN), container.getFluidInTank(0));
    }

    public boolean matches(ItemStack inputStack, FluidStack fluidStack)
    {
        // As with instant recipes, we must have enough added input to fully convert the existing fluid.
        final FluidStack extractableFluid = inputStack.getCapability(Capabilities.FLUID_ITEM)
            .map(cap -> cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
            .orElse(FluidStack.EMPTY);
        return inputFluid.test(fluidStack) && addedFluid.test(extractableFluid);
    }

    @Override
    public void assembleOutputs(BarrelBlockEntity.BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {

            // Extract input fluid - this will be converted, so we need to unconditionally drain all of it, and void excess.
            final FluidStack primaryFluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // This can be invoked either with the stack in the primary input slot, or the stack in the fluid input slot, and the behavior will change accordingly
            // If we match a slot in the input slot, we continue, otherwise we assume matching the fluid IO slots
            final boolean inputIsItemSlot = matches(inventory.getStackInSlot(BarrelBlockEntity.SLOT_ITEM), primaryFluid);
            final ItemStack originalStack = Helpers.removeStack(inventory, inputIsItemSlot ? BarrelBlockEntity.SLOT_ITEM : BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN);
            final IFluidHandlerItem fluidHandler = Helpers.getCapability(originalStack.copy(), Capabilities.FLUID_ITEM);

            if (fluidHandler == null)
            {
                return;
            }

            final FluidStack addedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

            // Calculate the multiplier in use for this recipe.
            // Both fluid ingredients are required to be > 0
            final int multiplier = Math.min(primaryFluid.getAmount() / inputFluid.amount(), addedFluid.getAmount() / this.addedFluid.amount());

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

    public static class Serializer extends RecipeSerializerImpl<InstantFluidBarrelRecipe>
    {
        @Override
        public InstantFluidBarrelRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient primaryFluid = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "primary_fluid"));
            final FluidStackIngredient addedFluid = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "added_fluid"));

            final FluidStack outputFluid = JsonHelpers.getFluidStack(json, "output_fluid");
            final SoundEvent sound = json.has("sound") ? JsonHelpers.getRegistryEntry(json, "sound", ForgeRegistries.SOUND_EVENTS) : SoundEvents.BREWING_STAND_BREW;

            return new InstantFluidBarrelRecipe(recipeId, new Builder(ItemStackIngredient.EMPTY, primaryFluid, ItemStackProvider.empty(), outputFluid, sound), addedFluid);
        }

        @Nullable
        @Override
        public InstantFluidBarrelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Builder builder = Builder.fromNetworkFluidsOnly(buffer);
            final FluidStackIngredient addedFluid = FluidStackIngredient.fromNetwork(buffer);
            return new InstantFluidBarrelRecipe(recipeId, builder, addedFluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, InstantFluidBarrelRecipe recipe)
        {
            Builder.toNetworkFluidsOnly(recipe, buffer);
            recipe.addedFluid.toNetwork(buffer);
        }
    }
}
