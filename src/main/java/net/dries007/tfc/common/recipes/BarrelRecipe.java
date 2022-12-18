/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public abstract class BarrelRecipe implements ISimpleRecipe<BarrelBlockEntity.BarrelInventory>
{
    private final ResourceLocation id;

    protected final ItemStackIngredient inputItem;
    protected final FluidStackIngredient inputFluid;
    protected final ItemStackProvider outputItem;
    protected final FluidStack outputFluid;
    protected final SoundEvent sound;

    public BarrelRecipe(ResourceLocation id, Builder builder)
    {
        this.id = id;
        this.inputItem = builder.inputItem;
        this.inputFluid = builder.inputFluid;
        this.outputItem = builder.outputItem;
        this.outputFluid = builder.outputFluid;
        this.sound = builder.sound;
    }

    public void assembleOutputs(BarrelBlockEntity.BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {
            // Remove all inputs
            final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
            final FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // Calculate the multiplier in use for this recipe
            int multiplier;
            if (inputItem.count() == 0)
            {
                multiplier = fluid.getAmount() / inputFluid.amount();
            }
            else if (inputFluid.amount() == 0)
            {
                multiplier = stack.getCount() / inputItem.count();
            }
            else
            {
                multiplier = Math.min(fluid.getAmount() / inputFluid.amount(), stack.getCount() / inputItem.count());
            }

            // Trim multiplier to a maximum fluid capacity of output
            if (!outputFluid.isEmpty())
            {
                int capacity = TFCConfig.SERVER.barrelCapacity.get();
                if (outputFluid.isFluidEqual(fluid))
                {
                    capacity -= fluid.getAmount();
                }
                int maxMultiplier = capacity / outputFluid.getAmount();
                multiplier = Math.min(multiplier, maxMultiplier);
            }

            // Output items
            // All output items, and then remaining input items, get inserted into the output overflow
            final ItemStack outputItem = this.outputItem.getSingleStack(stack);
            if (!outputItem.isEmpty())
            {
                Helpers.consumeInStackSizeIncrements(outputItem, multiplier * outputItem.getCount(), inventory::insertItemWithOverflow);
            }
            final int remainingItemCount = stack.getCount() - multiplier * inputItem.count();
            if (remainingItemCount > 0)
            {
                final ItemStack remainingStack = stack.copy();
                remainingStack.setCount(remainingItemCount);
                inventory.insertItemWithOverflow(remainingStack);
            }

            // Output fluid
            // If there's no output fluid, keep as much of the input as possible
            // If there is an output fluid, excess input is voided
            final FluidStack outputFluid = this.outputFluid.copy();
            if (outputFluid.isEmpty())
            {
                // Try and keep as much of the original input as possible
                final int retainAmount = fluid.getAmount() - (multiplier * this.inputFluid.amount());
                if (retainAmount > 0)
                {
                    final FluidStack retainedFluid = fluid.copy();
                    retainedFluid.setAmount(retainAmount);
                    inventory.fill(retainedFluid, IFluidHandler.FluidAction.EXECUTE);
                }
            }
            else
            {
                int amount = outputFluid.getAmount() * multiplier;
                if (outputFluid.isFluidEqual(fluid))
                {
                    amount = amount + fluid.getAmount();
                }
                outputFluid.setAmount(Math.min(TFCConfig.SERVER.barrelCapacity.get(), amount));
                inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    @Override
    public boolean matches(BarrelBlockEntity.BarrelInventory container, @Nullable Level level)
    {
        return inputItem.test(container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM)) && inputFluid.test(container.getFluidInTank(0));
    }

    @Override
    public ItemStack getResultItem()
    {
        return outputItem.getEmptyStack();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    public ItemStackIngredient getInputItem()
    {
        return inputItem;
    }

    public ItemStackProvider getOutputItem()
    {
        return outputItem;
    }

    public FluidStackIngredient getInputFluid()
    {
        return inputFluid;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    public SoundEvent getCompleteSound()
    {
        return sound;
    }

    public TranslatableComponent getTranslationComponent()
    {
        return Helpers.translatable("tfc.recipe.barrel." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
    }

    public record Builder(ItemStackIngredient inputItem, FluidStackIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid, SoundEvent sound)
    {
        public static Builder fromJson(JsonObject json)
        {
            final ItemStackIngredient inputItem = json.has("input_item") ? ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "input_item")) : ItemStackIngredient.EMPTY;
            final FluidStackIngredient inputFluid = json.has("input_fluid") ? FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "input_fluid")) : FluidStackIngredient.EMPTY;

            if (inputItem == ItemStackIngredient.EMPTY && inputFluid == FluidStackIngredient.EMPTY)
            {
                throw new JsonParseException("Barrel recipe must have at least one of input_item or input_fluid");
            }

            final ItemStackProvider outputItem = json.has("output_item") ? ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "output_item")) : ItemStackProvider.empty();
            final FluidStack outputFluid = json.has("output_fluid") ? JsonHelpers.getFluidStack(JsonHelpers.getAsJsonObject(json, "output_fluid")) : FluidStack.EMPTY;
            final SoundEvent sound = json.has("sound") ? JsonHelpers.getRegistryEntry(json, "sound", ForgeRegistries.SOUND_EVENTS) : SoundEvents.BREWING_STAND_BREW;

            return new Builder(inputItem, inputFluid, outputItem, outputFluid, sound);
        }

        public static Builder fromNetwork(FriendlyByteBuf buffer)
        {
            final ItemStackIngredient inputItem = ItemStackIngredient.fromNetwork(buffer);
            final ItemStackProvider outputItem = ItemStackProvider.fromNetwork(buffer);
            final Builder builder = fromNetworkFluidsOnly(buffer);

            return new Builder(inputItem, builder.inputFluid, outputItem, builder.outputFluid, builder.sound);
        }

        public static void toNetwork(BarrelRecipe recipe, FriendlyByteBuf buffer)
        {
            recipe.inputItem.toNetwork(buffer);
            recipe.outputItem.toNetwork(buffer);
            toNetworkFluidsOnly(recipe, buffer);
        }

        public static Builder fromNetworkFluidsOnly(FriendlyByteBuf buffer)
        {
            final FluidStackIngredient inputFluid = FluidStackIngredient.fromNetwork(buffer);
            final FluidStack outputFluid = FluidStack.readFromPacket(buffer);
            final SoundEvent sound = buffer.readRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS);

            return new Builder(ItemStackIngredient.EMPTY, inputFluid, ItemStackProvider.empty(), outputFluid, sound);
        }

        public static void toNetworkFluidsOnly(BarrelRecipe recipe, FriendlyByteBuf buffer)
        {
            recipe.inputFluid.toNetwork(buffer);
            recipe.outputFluid.writeToPacket(buffer);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS, recipe.sound);
        }
    }
}
