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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

/**
 * A shaped recipe type which uses {@link ItemStackProvider} as it's output mechanism
 * It also requires that the recipe specify which (of the crafting grid) inputs is responsible for the item stack provider's "input" stack.
 */
public class AdvancedShapedRecipe extends ShapedRecipe
{
    public static final MapCodec<AdvancedShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        RecipeSerializer.SHAPED_RECIPE.codec().forGetter(c -> c),
        ItemStackProvider.CODEC.optionalFieldOf("result_provider").forGetter(c -> c.result),
        ItemStackProvider.CODEC.optionalFieldOf("remainder").forGetter(c -> c.remainder),
        Codec.INT.optionalFieldOf("input_row", 0).forGetter(c -> c.inputRow),
        Codec.INT.optionalFieldOf("input_column", 0).forGetter(c -> c.inputColumn)
    ).apply(i, AdvancedShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedShapedRecipe> STREAM_CODEC = StreamCodec.composite(
        RecipeSerializer.SHAPED_RECIPE.streamCodec(), c -> c,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.result,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.remainder,
        ByteBufCodecs.VAR_INT, c -> c.inputRow,
        ByteBufCodecs.VAR_INT, c -> c.inputColumn,
        AdvancedShapedRecipe::new
    );

    private final Optional<ItemStackProvider> result;
    private final Optional<ItemStackProvider> remainder;
    private final int inputSlot, inputRow, inputColumn;

    public AdvancedShapedRecipe(ShapedRecipe parent, Optional<ItemStackProvider> result, Optional<ItemStackProvider> remainder, int inputRow, int inputColumn)
    {
        // todo: needs an AT for pattern
        super(parent.getGroup(), parent.category(), null /*parent.pattern()*/, RecipeHelpers.getResultUnsafe(parent), parent.showNotification());

        this.result = result;
        this.remainder = remainder;
        this.inputSlot = RecipeHelpers.dissolveRowColumn(inputRow, inputColumn, 3); //parent.pattern().width);
        this.inputRow = inputRow;
        this.inputColumn = inputColumn;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        return result.map(result -> {
            RecipeHelpers.setCraftingInput(input);
            final int matchSlot = RecipeHelpers.translateMatch(this, inputSlot, input);
            final ItemStack inputStack = matchSlot != -1 ? input.getItem(matchSlot).copy() : ItemStack.EMPTY;
            final ItemStack output = result.getSingleStack(inputStack);
            RecipeHelpers.clearCraftingInput();
            return output;
        }).orElseGet(() -> super.assemble(input, registries));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.map(ItemStackProvider::getEmptyStack).orElseGet(() -> super.getResultItem(registries));
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        return remainder.map(remainder -> RecipeHelpers.getRemainderItemsWithProvider(input, remainder))
            .orElseGet(() -> super.getRemainingItems(input));
    }

    @Override
    public boolean isSpecial()
    {
        return result.isPresent() && result.get().dependsOnInput();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPED_CRAFTING.get();
    }
}
