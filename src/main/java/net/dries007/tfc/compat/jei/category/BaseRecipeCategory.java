/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import org.jetbrains.annotations.NotNull;

public abstract class BaseRecipeCategory<T> implements IRecipeCategory<T>
{
    protected static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/jei/icons.png");

    protected static <R> List<R> collapse(List<List<R>> list) //todo: this sucks. We think of ingredients much differently than JEI does, so this has to do
    {
        List<R> returnList = new ArrayList<>(7);
        for (List<R> usuallySingletonList : list)
        {
            returnList.addAll(usuallySingletonList);
        }
        return returnList;
    }

    protected static List<ItemStack> collapseWithAmount(Ingredient ingredient, int amount)
    {
        return Arrays.stream(ingredient.getItems()).map(stack -> new ItemStack(stack.getItem(), amount).copy()).collect(Collectors.toList());
    }

    protected static List<FluidStack> collapse(FluidStackIngredient ingredient)
    {
        // Setting this to 1000 makes the liquid amount on the tooltip to display incorrectly
        // use IRecipeSlotBuilder.setFluidRenderer(1, false, 16, 16)} to make the liquid display in the whole slot
        return ingredient.ingredient().getMatchingFluids().stream().map(fluid -> new FluidStack(fluid, ingredient.amount())).collect(Collectors.toList());
    }

    protected static Ingredient collapse(BlockIngredient ingredient)
    {
        return Ingredient.of(ingredient.getValidBlocks().stream().map(ItemStack::new).filter(item -> !item.isEmpty()));
    }

    @NotNull
    protected static List<ItemStack> collapse(ItemStackIngredient input)
    {
        return Arrays.stream(input.ingredient().getItems()).peek(stack -> stack.setCount(input.count())).toList();
    }

    protected final IDrawableStatic slot;
    protected final IDrawableStatic fire;
    protected final IDrawableAnimated fireAnimated;
    protected final IDrawableStatic arrow;
    protected final IDrawableAnimated arrowAnimated;

    private final RecipeType<T> type;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;

    public BaseRecipeCategory(RecipeType<T> type, IGuiHelper helper, IDrawable background, ItemStack icon)
    {
        this.type = type;
        this.title = new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".jei." + type.getUid().getPath());
        this.background = background;
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, icon);
        this.slot = helper.getSlotDrawable();

        this.fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic fireAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(fireAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);

        this.arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends T> getRecipeClass()
    {
        return type.getRecipeClass();
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getUid()
    {
        return type.getUid();
    }

    @Override
    public RecipeType<T> getRecipeType()
    {
        return type;
    }

    @Override
    public Component getTitle()
    {
        return title;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }
}
