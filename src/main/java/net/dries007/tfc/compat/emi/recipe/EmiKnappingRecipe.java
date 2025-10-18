/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.platform.InputConstants;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public class EmiKnappingRecipe extends BasicRecipe<KnappingRecipe>
{
    private final SizedIngredient inputItem;
    private final PatternWidget pattern;

    public EmiKnappingRecipe(EmiRecipeCategory category, ResourceLocation id, KnappingRecipe recipe)
    {
        super(category, id, 250, 250);

        inputItem = recipe.getIngredient() != null
            // If this knapping recipe has an ingredient, we need to apply the count of the type's ingredient to it
            // See TerraFirmaCraft#2725
            ? new SizedIngredient(recipe.getIngredient(), recipe.knappingType().get().inputItem().count())
            : recipe.knappingType().get().inputItem();

        inputs.add(new EmiSizedIngredient(inputItem));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
        pattern = new PatternWidget(recipe, recipe.getPattern(), inputItem.getItems(), 5, 5);
    }

    @Override
    public int getDisplayHeight()
    {
        return pattern.getBounds().bottom() + 5;
    }

    @Override
    public int getDisplayWidth()
    {
        return pattern.getBounds().right() + 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.add(pattern);
        Bounds bounds = pattern.getBounds();
        int height = bounds.y() - TextWidget.Alignment.CENTER.offset(bounds.height());
        Widget arrow = widgets.addFillingArrow(bounds.right() + 4, height - 8, 3000);
        widgets.addSlot(outputs.getFirst(), arrow.getBounds().right() + 4, height - 9).recipeContext(this);
    }

    private static class PatternWidget extends Widget
    {
        private static final int INCREMENT = 1000;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final KnappingPattern pattern;
        private final KnappingType knappingType;
        private final ItemStack[] stacks;
        private @Nullable ItemStack displayStack;
        private @Nullable ResourceLocation high;
        private @Nullable ResourceLocation low;
        private long lastGenerate = 0;
        private int displayIndex;


        public PatternWidget(KnappingRecipe recipe, KnappingPattern pattern, ItemStack[] input, int x, int y)
        {
            this.x = x;
            this.y = y;
            this.width = pattern.getWidth();
            this.height = pattern.getHeight();
            this.pattern = pattern;
            stacks = input;
            knappingType = recipe.knappingType().get();
        }

        @Override
        public Bounds getBounds()
        {
            return new Bounds(x, y, width * 16, height * 16);
        }

        private void cycleTextures()
        {
            long time = System.currentTimeMillis() / INCREMENT;
            if (displayStack == null || time > lastGenerate)
            {
                lastGenerate = time;
                if (displayStack != null && Screen.hasShiftDown())
                {
                    return;
                }
                displayIndex = (displayIndex + 1) % stacks.length;
                displayStack = stacks[displayIndex];
                high = KnappingScreen.getHighTexture(displayStack);
                low = KnappingScreen.getLowTexture(knappingType, displayStack);
            }
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            cycleTextures();
            draw.fill(x - 1, y - 1, x + width * 16 + 1, y + height * 16 + 1, 0xffaaaaaa);
            for (int xi = 0; xi < width; xi++)
            {
                for (int yi = 0; yi < height; yi++)
                {
                    int xp = x + 16 * xi;
                    int yp = y + 16 * yi;
                    if (pattern.get(xi, yi) && xi < pattern.getWidth() && yi < pattern.getHeight())
                    {
                        drawTex(high, draw, xp, yp);
                    }
                    else
                    {
                        drawTex(low, draw, xp, yp);
                    }
                }
            }
        }

        private void drawTex(@Nullable ResourceLocation location, GuiGraphics draw, int xp, int yp)
        {
            if (location != null)
            {
                draw.blit(location, xp, yp, 0, 0, 16, 16, 16, 16);
            }
        }

        @Override
        public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY)
        {
            List<ClientTooltipComponent> lines = new ArrayList<>();
            if (displayStack != null)
            {
                List<Component> display = displayStack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL);
                lines.addAll(display.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList());
                lines.add(new StackTooltip(displayStack));
            }
            return lines;
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button)
        {
            if (displayStack == null)
            {
                return true;
            }
            // No access to the EMI keybinds for this, sorry folks
            if (button == InputConstants.MOUSE_BUTTON_LEFT)
            {
                EmiApi.displayRecipes(EmiStack.of(displayStack));
            }
            else if (button == InputConstants.MOUSE_BUTTON_RIGHT)
            {
                EmiApi.displayUses(EmiStack.of(displayStack));
            }
            return true;
        }
    }

    private record StackTooltip(ItemStack stack) implements ClientTooltipComponent
    {
        @Override
        public int getHeight()
        {
            return 18;
        }

        @Override
        public int getWidth(Font font)
        {
            return 18;
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics draw)
        {
            draw.renderItem(stack, x, y);

            String amount = String.valueOf(stack.getCount());
            int xp = x + getWidth(font) - Math.min(14, font.width(amount));
            int yp = y + getHeight() - font.lineHeight;

            draw.pose().pushPose();
            draw.pose().translate(0, 0, 300); // Try to position the text in front of the item
            draw.drawString(font, amount, xp, yp, 0xffffffff, true);
            draw.pose().popPose();
        }
    }
}
