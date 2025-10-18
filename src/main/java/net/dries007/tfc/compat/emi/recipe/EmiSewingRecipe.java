/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.client.screen.SewingTableScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.common.recipes.SewingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

import static net.dries007.tfc.common.container.SewingTableContainer.*;

public class EmiSewingRecipe extends AutoLayoutRecipe<SewingRecipe>
{
    private final SewingRecipe recipe;

    public EmiSewingRecipe(ResourceLocation id, SewingRecipe recipe)
    {
        super(EmiIntegration.SEWING, id, recipe);
        this.recipe = recipe;
        init(recipe);
    }

    @Override
    protected int getPaddingTop()
    {
        return 70;
    }

    @Override
    protected int getPaddingRight()
    {
        return 4;
    }

    @Override
    protected int getPaddingLeft()
    {
        return 4;
    }

    @Override
    protected void processRecipe(SewingRecipe recipe)
    {
        int wool = 0;
        int burlap = 0;
        int string = 0;
        for (int i = 0; i < 32; i++)
        {
            int material = recipe.getSquare(i);
            if (material == SewingTableContainer.BURLAP_ID)
            {
                burlap++;
            }
            else if (material == SewingTableContainer.WOOL_ID)
            {
                wool++;
            }
        }
        for (int i = 0; i < 45; i++)
        {
            if (recipe.getStitch(i))
            {
                string++;
            }
        }


        if (wool > 0)
        {
            inputs.add(EmiIngredient.of(TFCTags.Items.SEWING_LIGHT_CLOTH, Math.ceilDiv(wool, SQUARES_PER_CLOTH)));
        }
        if (burlap > 0)
        {
            inputs.add(EmiIngredient.of(TFCTags.Items.SEWING_DARK_CLOTH, Math.ceilDiv(burlap, SQUARES_PER_CLOTH)));
        }
        if (string > 0)
        {
            inputs.add(EmiIngredient.of(Tags.Items.STRINGS, Math.ceilDiv(string, STITCHES_PER_YARN)));
        }

        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);
        widgets.add(new SewingPatternWidget(6, 6, recipe));
    }

    public SewingRecipe getRecipe()
    {
        return recipe;
    }

    private static class SewingPatternWidget extends Widget
    {
        private static final int MATERIAL_SPRITE_SIZE = 12;
        private static final int OFFSET = 8;
        private final int x;
        private final int y;
        private final int height;
        private final int width;
        private final SewingRecipe recipe;

        public SewingPatternWidget(int x, int y, SewingRecipe recipe)
        {
            this.recipe = recipe;
            this.x = x;
            this.y = y;
            width = 112;
            height = 64;
        }

        @Override
        public Bounds getBounds()
        {
            return new Bounds(x, y, width, height);
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            drawTex(draw, 0, 0, 8, 14, width, height);

            SewingTableScreen.forEachClothSquare((xp, yp, i) -> {
                final int material = recipe.getSquare(i);
                if (material != -1)
                {
                    if (material == SewingTableContainer.BURLAP_ID)
                    {
                        drawBurlap(draw, xp, yp);
                    }
                    else
                    {
                        drawWool(draw, xp, yp);
                    }
                }
                drawPips(draw, xp, yp);
            });
            SewingTableScreen.forEachStitch((xp, yp, i) -> {
                if (recipe.getStitch(i))
                {
                    drawStitch(draw, xp, yp);
                }
                return false;
            });
        }

        private void drawTex(GuiGraphics draw, int xp, int yp, int u, int v, int w, int h)
        {
            draw.blit(SewingTableScreen.TEXTURE, x + xp, y + yp, u, v, w, h);
        }

        private void drawStitch(GuiGraphics draw, int xp, int yp)
        {
            drawTex(draw, xp * MATERIAL_SPRITE_SIZE + OFFSET - 2, yp * MATERIAL_SPRITE_SIZE + OFFSET - 2, 192, 0, 5, 5);
        }

        private void drawWool(GuiGraphics draw, int xp, int yp)
        {
            drawTex(draw, xp * MATERIAL_SPRITE_SIZE + OFFSET, yp * MATERIAL_SPRITE_SIZE + OFFSET, 208, 0, MATERIAL_SPRITE_SIZE, MATERIAL_SPRITE_SIZE);
        }

        private void drawBurlap(GuiGraphics draw, int xp, int yp)
        {
            drawTex(draw, xp * MATERIAL_SPRITE_SIZE + OFFSET, yp * MATERIAL_SPRITE_SIZE + OFFSET, 208, 16, MATERIAL_SPRITE_SIZE, MATERIAL_SPRITE_SIZE);
        }

        private void drawPips(GuiGraphics draw, int xp, int yp)
        {
            drawTex(draw, xp * MATERIAL_SPRITE_SIZE + OFFSET, yp * MATERIAL_SPRITE_SIZE + OFFSET, 208, 32, MATERIAL_SPRITE_SIZE, MATERIAL_SPRITE_SIZE);
        }
    }
}
