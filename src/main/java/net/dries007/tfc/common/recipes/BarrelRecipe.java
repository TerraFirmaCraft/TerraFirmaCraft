package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public abstract class BarrelRecipe implements ISimpleRecipe<BarrelBlockEntity.BarrelInventory>
{
    private final ResourceLocation id;

    protected final ItemStackIngredient inputItem;
    protected final FluidStackIngredient inputFluid;
    protected final ItemStackProvider outputItem;
    protected final FluidStack outputFluid;

    public BarrelRecipe(ResourceLocation id, Builder builder)
    {
        this.id = id;
        this.inputItem = builder.inputItem;
        this.inputFluid = builder.inputFluid;
        this.outputItem = builder.outputItem;
        this.outputFluid = builder.outputFluid;
    }

    public void assembleOutputs(BarrelBlockEntity.BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {
            // Remove all inputs
            final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
            final FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // Calculate the multiplier in use for this recipe
            final int multiplier;
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

            // Output items
            // All output items, and then remaining input items, get inserted into the output overflow
            final ItemStack outputItem = this.outputItem.getStack(stack);
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
                outputFluid.setAmount(Math.min(TFCConfig.SERVER.barrelCapacity.get(), outputFluid.getAmount() * multiplier));
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
        return outputItem.getStack(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    public record Builder(ItemStackIngredient inputItem, FluidStackIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid)
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

            return new Builder(inputItem, inputFluid, outputItem, outputFluid);
        }

        public static Builder fromNetwork(FriendlyByteBuf buffer)
        {
            final ItemStackIngredient inputItem = ItemStackIngredient.fromNetwork(buffer);
            final FluidStackIngredient inputFluid = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackProvider outputItem = ItemStackProvider.fromNetwork(buffer);
            final FluidStack outputFluid = FluidStack.readFromPacket(buffer);

            return new Builder(inputItem, inputFluid, outputItem, outputFluid);
        }

        public static void toNetwork(BarrelRecipe recipe, FriendlyByteBuf buffer)
        {
            recipe.inputItem.toNetwork(buffer);
            recipe.inputFluid.toNetwork(buffer);
            recipe.outputItem.toNetwork(buffer);
            recipe.outputFluid.writeToPacket(buffer);
        }
    }
}
