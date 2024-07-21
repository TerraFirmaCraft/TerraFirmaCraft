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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

/**
 * A shaped recipe type which uses {@link ItemStackProvider} as it's output mechanism
 * It also requires that the recipe specify which (of the crafting grid) inputs is responsible for the item stack provider's "input" stack.
 */
public class AdvancedShapedRecipe extends ShapedRecipe
{
    public static final MapCodec<AdvancedShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        // Copied from ShapedRecipe.Serializer.CODEC, as we want to avoid the "result" field as a strict item stack
        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
        ShapedRecipePattern.MAP_CODEC.forGetter(c -> c.pattern),
        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        ItemStackProvider.CODEC.optionalFieldOf("remainder").forGetter(c -> c.remainder),
        Codec.INT.optionalFieldOf("input_row", 0).forGetter(c -> c.inputRow),
        Codec.INT.optionalFieldOf("input_column", 0).forGetter(c -> c.inputColumn)
    ).apply(i, AdvancedShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedShapedRecipe> STREAM_CODEC = StreamCodec.composite(
        RecipeSerializer.SHAPED_RECIPE.streamCodec(), c -> c,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.remainder,
        ByteBufCodecs.VAR_INT, c -> c.inputRow,
        ByteBufCodecs.VAR_INT, c -> c.inputColumn,
        AdvancedShapedRecipe::new
    );

    private final ItemStackProvider result;
    private final Optional<ItemStackProvider> remainder;
    private final int inputSlot, inputRow, inputColumn;

    private AdvancedShapedRecipe(ShapedRecipe parent, ItemStackProvider result, Optional<ItemStackProvider> remainder, int inputRow, int inputColumn)
    {
        this(parent.getGroup(), parent.category(), parent.pattern, parent.showNotification(), result, remainder, inputRow, inputColumn);
    }

    public AdvancedShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, boolean showNotification, ItemStackProvider result, Optional<ItemStackProvider> remainder, int inputRow, int inputColumn)
    {
        super(group, category, pattern, ItemStack.EMPTY, showNotification);

        this.result = result;
        this.remainder = remainder;
        this.inputSlot = RecipeHelpers.dissolveRowColumn(inputRow, inputColumn, pattern.width());
        this.inputRow = inputRow;
        this.inputColumn = inputColumn;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        RecipeHelpers.setCraftingInput(input);
        final int matchSlot = RecipeHelpers.translateMatch(this, inputSlot, input);
        final ItemStack inputStack = matchSlot != -1 ? input.getItem(matchSlot).copy() : ItemStack.EMPTY;
        final ItemStack output = result.getSingleStack(inputStack);
        RecipeHelpers.clearCraftingInput();
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
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
        return result.dependsOnInput();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPED_CRAFTING.get();
    }
}
