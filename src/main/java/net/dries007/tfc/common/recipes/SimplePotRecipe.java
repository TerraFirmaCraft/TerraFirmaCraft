/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.common.recipes.outputs.PotOutput;

public class SimplePotRecipe extends PotRecipe
{
    public static final MapCodec<SimplePotRecipe> CODEC = RecordCodecBuilder.<SimplePotRecipe>mapCodec(i -> i.group(
        PotRecipe.CODEC.forGetter(c -> c),
        FluidStack.CODEC.optionalFieldOf("fluid_output", FluidStack.EMPTY).forGetter(c -> c.outputFluid),
        ItemStackProvider.CODEC.listOf(0, 5).fieldOf("item_output").forGetter(c -> c.outputItems)
    ).apply(i, SimplePotRecipe::new))
        .validate(recipe -> {
            final boolean anyProvidersDependOnInput = recipe.outputItems.stream().anyMatch(ItemStackProvider::dependsOnInput);
            if (anyProvidersDependOnInput && recipe.outputItems.size() != recipe.itemIngredients.size())
            {
                return DataResult.error(() -> "At least one output is an ItemStackProvider that depends on the input. This is only allowed if there are (1) equal number of inputs and outputs, and (2) All inputs and outputs are the same");
            }
            return DataResult.success(recipe);
        });

    public static final StreamCodec<RegistryFriendlyByteBuf, SimplePotRecipe> STREAM_CODEC = StreamCodec.composite(
        PotRecipe.STREAM_CODEC, c -> c,
        FluidStack.STREAM_CODEC, c -> c.outputFluid,
        ItemStackProvider.STREAM_CODEC.apply(ByteBufCodecs.list(5)), c -> c.outputItems,
        SimplePotRecipe::new
    );

    protected final FluidStack outputFluid;
    protected final List<ItemStackProvider> outputItems;

    public SimplePotRecipe(PotRecipe base, FluidStack outputFluid, List<ItemStackProvider> outputProviders)
    {
        super(base);
        this.outputFluid = outputFluid;
        this.outputItems = outputProviders;
    }

    public FluidStack getDisplayFluid()
    {
        return outputFluid;
    }

    public List<ItemStackProvider> getOutputItems()
    {
        return outputItems;
    }

    @Override
    public PotOutput getOutput(PotBlockEntity.PotInventory inventory)
    {
        // Compute the outputs here, before the pot inventory is cleared
        final List<ItemStack> outputs = new ArrayList<>(5);
        for (int i = 0; i < Math.min(outputItems.size(), inventory.getSlots()); i++)
        {
            final ItemStack input = inventory.getStackInSlot(PotBlockEntity.SLOT_EXTRA_INPUT_START + i);
            outputs.add(outputItems.get(i).getSingleStack(input));
        }
        return new SimpleOutput(outputFluid.copy(), outputs);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SIMPLE.get();
    }

    /**
     * Has no persistent output, thus uses the {@link PotOutput#EMPTY} output type.
     */
    record SimpleOutput(FluidStack fluidOutput, List<ItemStack> itemOutputs) implements PotOutput
    {
        @Override
        public void onFinish(PotBlockEntity.PotInventory inventory)
        {
            // Copy the outputs to the pot inventory
            for (int i = 0; i < itemOutputs.size(); i++)
            {
                inventory.setStackInSlot(PotBlockEntity.SLOT_EXTRA_INPUT_START + i, itemOutputs.get(i));
            }
            inventory.fill(fluidOutput, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
