/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.Metal;

public class LampFuelRecipeCategory extends BaseRecipeCategory<LampFuel>
{
    private final IDrawableStatic lampBg;
    private final IDrawableStatic lampFg;

    public LampFuelRecipeCategory(RecipeType<LampFuel> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(140, 38), new ItemStack(TFCBlocks.METALS.get(Metal.Default.BLUE_STEEL).get(Metal.BlockType.LAMP).get()));
        this.lampBg = helper.createDrawable(BaseRecipeCategory.ICONS, 0, 48, 20, 20);
        this.lampFg = helper.createDrawable(BaseRecipeCategory.ICONS, 20, 48, 20, 20);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LampFuel recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 11)
            .addIngredients(JEIIntegration.FLUID_STACK, recipe.getFluidIngredient().all().map(f -> new FluidStack(f, 1000)).toList())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.CATALYST, 43, 11)
            .addIngredients(BaseRecipeCategory.collapse(recipe.getValidLamps()))
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(LampFuel recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY)
    {

        lampBg.draw(graphics, 22, 11);
        if (recipe.getBurnRate() <= 0)
        {
            // Negative rate shows the foreground image permanently.
            lampFg.draw(graphics, 22, 11);
        }
        else
        {
            // Animated Foreground Lamp.
            long time = System.currentTimeMillis() % (recipe.getBurnRate() * 5L);
            int remainingHeight = 20 - (int) (20 * (time / (float) (recipe.getBurnRate() * 5)));

            if (remainingHeight > 0)
            {
                int burnOffset = 20 - remainingHeight;

                graphics.blit(
                    BaseRecipeCategory.ICONS,
                    22, 11 + burnOffset,
                    20, 48 + burnOffset,
                    20, remainingHeight,
                    256, 256
                );
            }
        }

        int secondsPerMb = recipe.getBurnRate() / 20;
        Object burnTime = secondsPerMb <= 0 ? "∞" : secondsPerMb;

        Component text = Component.translatable("tfc.jei.lamp_fuel.burn_rate", burnTime);
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, text, 65, 15, 0xFFFFFF, true);
    }

    @Override
    public List<Component> getTooltipStrings(LampFuel recipe, IRecipeSlotsView recipeSlots, double mouseX, double mouseY)
    {
        int lampCapacity = TFCConfig.SERVER.lampCapacity.get();
        int daysPerLamp = recipe.getBurnRate() * lampCapacity / 24000;
        Object burnDays = daysPerLamp <= 0 ? "∞" : daysPerLamp;

        Font font = Minecraft.getInstance().font;
        Component text = Component.translatable("tfc.jei.lamp_fuel.burn_rate", recipe.getBurnRate() / 20 <= 0 ? "∞" : recipe.getBurnRate() / 20);

        if (mouseX >= 65 && mouseX < 65 + font.width(text) && mouseY >= 15 && mouseY < 15 + font.lineHeight)
        {
            return List.of(Component.translatable("tfc.jei.lamp_fuel.days", burnDays, lampCapacity));
        }
        return List.of();
    }
}
