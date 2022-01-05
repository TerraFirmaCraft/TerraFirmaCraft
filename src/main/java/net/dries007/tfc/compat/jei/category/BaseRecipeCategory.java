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
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;

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

    protected static List<ItemStack> collapse(Ingredient ingredient)
    {
        return Arrays.asList(ingredient.getItems());
    }

    protected static List<FluidStack> collapse(FluidStackIngredient ingredient)
    {
        return ingredient.getMatchingFluids().stream().map(fluid -> new FluidStack(fluid, 1000)).collect(Collectors.toList());
    }

    protected final IDrawableStatic slot;
    protected final IDrawableStatic fire;
    protected final IDrawableAnimated fireAnimated;
    protected final IDrawableStatic arrow;
    protected final IDrawableAnimated arrowAnimated;


    private final ResourceLocation uId;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final Class<? extends T> recipeClass;

    public BaseRecipeCategory(ResourceLocation uId, IGuiHelper helper, IDrawable background, ItemStack icon, Class<? extends T> recipeClass)
    {
        this.uId = uId;
        this.title = new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".jei." + uId.getPath());
        this.background = background;
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, icon);
        this.recipeClass = recipeClass;
        this.slot = helper.getSlotDrawable();

        this.fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic fireAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(fireAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);

        this.arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public ResourceLocation getUid()
    {
        return uId;
    }

    @Override
    public Class<? extends T> getRecipeClass()
    {
        return recipeClass;
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
