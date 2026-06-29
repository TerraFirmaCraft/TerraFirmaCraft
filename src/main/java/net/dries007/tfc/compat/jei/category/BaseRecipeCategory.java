/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.Helpers;

public abstract class BaseRecipeCategory<T extends Recipe<?>> extends AbstractRecipeCategory<RecipeHolder<T>>
{
    public static final ResourceLocation ICONS = Helpers.identifier("textures/gui/jei/icons.png");

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> recipe, IFocusGroup focuses)
    {
        setRecipe(builder, recipe.value(), focuses);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, T holder, IFocusGroup focuses)
    {

    }

    @Override
    public void draw(RecipeHolder<T> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        draw(holder.value(), recipeSlotsView, graphics, mouseX, mouseY);
    }

    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {

    }


    /**
     * Do not call outside the level. Duh.
     */
    public static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
    }

    public static List<FluidStack> collapse(SizedFluidIngredient ingredient)
    {
        // Setting this to 1000 makes the liquid amount on the tooltip to display incorrectly
        // use IRecipeSlotBuilder.setFluidRenderer(1, false, 16, 16)} to make the liquid display in the whole slot
        return Arrays.asList(ingredient.getFluids());
    }

    public static List<ItemStack> collapse(SizedIngredient input)
    {
        return Arrays.stream(input.ingredient().getItems())
            .map(stack -> FoodCapability.setTransientNonDecaying(stack.copyWithCount(input.count())))
            .toList();
    }

    public static List<ItemStack> collapse(ItemStackProvider output)
    {
        return List.of(output.getEmptyStack());
    }

    public static List<ItemStack> collapse(List<ItemStack> inputs, ItemStackProvider output)
    {
        if (inputs.isEmpty())
        {
            return List.of(output.getEmptyStack());
        }
        return inputs.stream()
            .map(output::getSingleStackDisplayOnly)
            .map(FoodCapability::setTransientNonDecaying) // Avoid decaying in JEI views
            .toList();
    }

    public static Ingredient collapse(BlockIngredient ingredient)
    {
        return Ingredient.of(ingredient.all().map(ItemStack::new).filter(item -> !item.isEmpty()));
    }

    protected final IDrawableStatic slot;
    protected final IDrawableStatic fire;
    protected final IDrawableAnimated fireAnimated;
    protected final IDrawableStatic arrow;
    protected final IDrawableAnimated arrowAnimated;

    public BaseRecipeCategory(RecipeType<RecipeHolder<T>> type, IGuiHelper helper, int width, int height, ItemStack icon)
    {
        super(type, Component.translatable(TerraFirmaCraft.MOD_ID + ".jei." + type.getUid().getPath()), helper.createDrawableIngredient(JEIIntegration.ITEM_STACK, FoodCapability.setNonDecaying(icon)), width, height);
        this.slot = helper.getSlotDrawable();

        this.fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic fireAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(fireAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);

        this.arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

}
