/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.AnvilPlanButton;
import net.dries007.tfc.client.screen.button.AnvilStepButton;
import net.dries007.tfc.client.screen.button.AnvilWeldButton;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class AnvilScreen extends BlockEntityScreen<AnvilBlockEntity, AnvilContainer>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/anvil.png");

    public AnvilScreen(AnvilContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);

        inventoryLabelY += 41;
        imageHeight += 41;
    }

    @Override
    protected void init()
    {
        super.init();

        addRenderableWidget(new AnvilPlanButton(blockEntity, getGuiLeft(), getGuiTop()));
        addRenderableWidget(new AnvilWeldButton(blockEntity, getGuiLeft(), getGuiTop()));

        for (ForgeStep step : ForgeStep.VALUES)
        {
            addRenderableWidget(new AnvilStepButton(step, getGuiLeft(), getGuiTop()));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        final Level level = blockEntity.getLevel();
        final int guiLeft = getGuiLeft(), guiTop = getGuiTop();

        if (TerraFirmaCraft.JEI)
        {
            graphics.blit(texture, guiLeft + 141, guiTop + 40, 0, 207, 9, 14);
        }

        assert level != null;


        final Forging forging = blockEntity.getMainInputForging();

        // Draw the progress indicators
        final int target = forging.target();
        final int range = TFCConfig.SERVER.anvilAcceptableWorkRange.get();
        final AnvilRecipe recipe = forging.getRecipe();
        if (recipe != null)
        {
            // progress indicator
            graphics.blit(texture, guiLeft + 11 + forging.work(), guiTop + 104, 176, 0, 5, 5);

            // target indicator
            if (range < 2)
            {
                // render the pointer
                graphics.blit(texture, guiLeft + 11 + target, guiTop + 98, 181, 0, 5, 5);
            }
            else
            {
                // render the bracket
                final int leftLimit = Math.max(0, target - range);
                final int rightLimit = Math.min(145, target + range);

                // left
                graphics.blit(texture, guiLeft + 11 + leftLimit, guiTop + 96, 176, 7, 5, 7);
                // right
                graphics.blit(texture, guiLeft + 11 + rightLimit, guiTop + 96, 186, 7, 5, 7);

                // bar
                for (int i = leftLimit + 2; i < rightLimit - 1; i++)
                {
                    graphics.blit(texture, guiLeft + 15 + i, guiTop + 94, 192, 5, 1, 5);
                }

                // center
                if (range > 2)
                {
                    graphics.blit(texture, guiLeft + 13 + (rightLimit + leftLimit) / 2, guiTop + 94, 181, 5, 5, 5);
                }

            }
        }

        // Draw rule icons
        if (recipe != null)
        {
            final List<ForgeRule> rules = recipe.getRules();
            for (int i = 0; i < rules.size(); i++)
            {
                final ForgeRule rule = rules.get(i);
                if (rule != null)
                {
                    final int xOffset = i * 19;

                    // The rule icon
                    graphics.blit(texture, guiLeft + 61 + xOffset, guiTop + 13, 16, 16, rule.iconX(), rule.iconY() - 16, 16, 16, 256, 256);

                    // The overlay
                    if (forging.matches(rule))
                    {
                        RenderSystem.setShaderColor(0f, 0.6f, 0.2f, 1f); // Green
                    }
                    else
                    {
                        RenderSystem.setShaderColor(1f, 0.4f, 0, 1f); // Red
                    }

                    graphics.blit(texture, guiLeft + 59 + xOffset, guiTop + 13, 198, rule.overlayY(), 20, 22);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                }
            }
        }

        // Draw step icons
        int index = 0;
        for (ForgeStep step : forging.lastSteps())
        {
            graphics.blit(texture, guiLeft + 99 - (index * 19), guiTop + 34, 16, 16, step.iconX(), step.iconY() - 16, 16, 16, 256, 256);
            index++;
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        final Level level = blockEntity.getLevel();
        final Forging forging = blockEntity.getMainInputForging();
        if (level != null)
        {
            final @Nullable AnvilRecipe recipe = forging.getRecipe();
            if (recipe != null)
            {
                final List<ForgeRule> rules = recipe.getRules();
                for (int i = 0; i < rules.size(); i++)
                {
                    final ForgeRule rule = rules.get(i);
                    if (rule != null)
                    {
                        final int xOffset = i * 19;
                        final int x = getGuiLeft() + 64 + xOffset;
                        final int y = getGuiTop() + 16;
                        if (mouseX > x && mouseX < x + 10 && mouseY > y && mouseY < y + 10)
                        {
                            graphics.renderTooltip(font, rule.getDescriptionId(), mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }
}
