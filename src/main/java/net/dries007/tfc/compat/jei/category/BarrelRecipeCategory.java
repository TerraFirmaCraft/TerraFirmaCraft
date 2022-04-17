/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrelRecipeCategory<T extends BarrelRecipe> extends BaseRecipeCategory<T>
{

    protected static final String FLUID_INPUT = "fluidInput";
    protected static final String ITEM_INPUT = "itemInput";
    protected static final String FLUID_OUTPUT = "fluidOutput";
    protected static final String ITEM_OUTPUT = "itemOutput";
    protected @Nullable IRecipeSlotBuilder inputFluidSlot;
    protected @Nullable IRecipeSlotBuilder inputItemSlot;
    protected @Nullable IRecipeSlotBuilder outputFluidSlot;
    protected @Nullable IRecipeSlotBuilder outputItemSlot;

    public BarrelRecipeCategory(RecipeType<T> type, IGuiHelper helper, int width, int height, Wood iconType)
    {
        super(type, helper, helper.createBlankDrawable(width, height), new ItemStack(TFCBlocks.WOODS.get(iconType).get(Wood.BlockType.BARREL).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        inputFluidSlot = null;
        inputItemSlot = null;
        outputFluidSlot = null;
        outputItemSlot = null;
        int[] positions = slotPositions(recipe);
        List<FluidStack> inputFluid = getFluidInput(recipe);
        List<ItemStack> inputItem = getItemInput(recipe);
        RecipeResult<FluidStack> outputFluid = getFluidResult(recipe);
        RecipeResult<List<ItemStack>> outputItem = getItemResult(recipe);
        if (!inputFluid.isEmpty())
        {
            inputFluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, inputItem.isEmpty() ? positions[1] : positions[0], 5).setSlotName(FLUID_INPUT);
            inputFluidSlot.addIngredients(VanillaTypes.FLUID, inputFluid);
            inputFluidSlot.setFluidRenderer(1, false, 16, 16);
        }
        if (!inputItem.isEmpty())
        {
            inputItemSlot = builder.addSlot(RecipeIngredientRole.INPUT, positions[1], 5).setSlotName(ITEM_INPUT);
            inputItemSlot.addItemStacks(inputItem);
        }
        if (!outputFluid.result().isEmpty())
        {
            outputFluidSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, positions[2], 5).setSlotName(FLUID_OUTPUT);
            outputFluidSlot.addIngredient(VanillaTypes.FLUID, outputFluid.result());
            outputFluidSlot.setFluidRenderer(1, false, 16, 16);
            if (!inputFluid.isEmpty() && !outputFluid.transforms())
            {
                builder.createFocusLink(inputFluidSlot, outputFluidSlot);
            }
        }
        if (!outputItem.result().isEmpty())
        {
            outputItemSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, outputFluid.result().isEmpty() ? positions[2] : positions[3], 5).setSlotName(ITEM_OUTPUT);
            outputItemSlot.addItemStacks(outputItem.result());
            if (!inputItem.isEmpty() && !outputItem.transforms())
            {
                builder.createFocusLink(inputItemSlot, outputItemSlot);
            }
        }

    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        int[] positions = slotPositions(recipe);
        int arrowPosition = arrowPosition(recipe);
        slot.draw(stack, positions[1] - 1, 4);
        if (recipeSlots.findSlotByName(FLUID_INPUT).isPresent() && recipeSlots.findSlotByName(ITEM_INPUT).isPresent())
        {
            slot.draw(stack, positions[0] - 1, 4);
        }
        slot.draw(stack, positions[2] - 1, 4);
        if (recipeSlots.findSlotByName(FLUID_OUTPUT).isPresent() && recipeSlots.findSlotByName(ITEM_OUTPUT).isPresent())
        {
            slot.draw(stack, positions[3] - 1, 4);
        }

        arrow.draw(stack, arrowPosition, 5);
        arrowAnimated.draw(stack, arrowPosition, 5);
    }

    protected int[] slotPositions(T recipe)
    {
        return new int[] {6, 26, 76, 96};
    }

    protected int arrowPosition(T recipe)
    {
        return 48;
    }

    protected static RecipeResult<List<ItemStack>> itemStackProviderIngredient(ItemStackProvider output, ItemStackIngredient input)
    {
        // Leaving this because it may be true still
        // todo: this sucks and may not be properly sensitive to everything
        // todo: maybe we should just list the item stack modifiers
        ItemStack[] possibleItems = input.ingredient().getItems();
        List<ItemStack> items = new ArrayList<>(possibleItems.length);
        boolean transforms = false;
        for (ItemStack item : possibleItems)
        {
            ItemStack result = output.getStack(item);
            result.setCount(input.count());
            items.add(result);
            if (!Helpers.isItem(result, item.getItem())) transforms = true;
        }
        return new RecipeResult<>(transforms, items);
    }

    @NotNull
    protected List<FluidStack> getFluidInput(T recipe)
    {
        return collapse(recipe.getInputFluid());
    }

    @NotNull
    protected List<ItemStack> getItemInput(T recipe)
    {
        ItemStackIngredient input = recipe.getInputItem();
        return Arrays.stream(input.ingredient().getItems()).peek(stack -> stack.setCount(input.count())).toList();
    }

    @NotNull
    protected RecipeResult<List<ItemStack>> getItemResult(T recipe)
    {
        RecipeResult<List<ItemStack>> output = itemStackProviderIngredient(recipe.getOutputItem(), recipe.getInputItem());
        if (!output.result().isEmpty() && output.result().stream().anyMatch(stack -> !stack.isEmpty())) return output;

        ItemStack result = recipe.getResultItem();
        return new RecipeResult<>(!isSame(result, recipe.getInputItem().ingredient().getItems(), ItemStack::getItem), result.isEmpty() ? List.of() : List.of(result));
    }

    @NotNull
    protected RecipeResult<FluidStack> getFluidResult(T recipe)
    {
        return new RecipeResult<>(!isSame(recipe.getOutputFluid().getFluid(), recipe.getInputFluid().ingredient().getMatchingFluids()), recipe.getOutputFluid());
    }

    protected static <I, C> boolean isSame(I result, I[] inputs, Function<I, C> mapper)
    {
        return inputs.length == 1 && Stream.of(inputs).map(mapper).anyMatch(v -> mapper.apply(result) == v);
    }

    protected static <I> boolean isSame(I result, Collection<I> inputs)
    {
        return inputs.size() == 1 && inputs.contains(result);
    }

    /**
     * @param transforms determines if the result is the same item as the input, if false a {@link IRecipeLayoutBuilder#createFocusLink(IRecipeSlotBuilder...) focus link} is created between the input and output
     */
    protected record RecipeResult<I>(boolean transforms, I result) {}

}
