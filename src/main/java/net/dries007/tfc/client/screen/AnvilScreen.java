/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.screen.button.AnvilPlanButton;
import net.dries007.tfc.client.screen.button.AnvilStepButton;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgeSteps;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
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

        if (Helpers.isJEIEnabled())
        {
            graphics.blit(texture, guiLeft + 26, guiTop + 24, 0, 207, 9, 14);
        }

        assert level != null;

        // Draw rule icons
        final @Nullable Forging forging = blockEntity.getMainInputForging();
        if (forging != null)
        {
            // Draw the progress indicators
            final int progress = forging.getWork();
            graphics.blit(texture, guiLeft + 13 + progress, guiTop + 100, 176, 0, 5, 5);

            final int target = forging.getWorkTarget();
            graphics.blit(texture, guiLeft + 13 + target, guiTop + 94, 181, 0, 5, 5);

            final ForgeSteps steps = forging.getSteps();
            final AnvilRecipe recipe = forging.getRecipe(level);
            if (recipe != null)
            {
                final ForgeRule[] rules = recipe.getRules();
                for (int i = 0; i < rules.length; i++)
                {
                    final ForgeRule rule = rules[i];
                    if (rule != null)
                    {
                        final int xOffset = i * 19;

                        // The rule icon
                        graphics.blit(texture, guiLeft + 64 + xOffset, guiTop + 10, 10, 10, rule.iconX(), rule.iconY(), 32, 32, 256, 256);

                        // The overlay
                        if (rule.matches(steps))
                        {
                            RenderSystem.setShaderColor(0f, 0.6f, 0.2f, 1f); // Green
                        }
                        else
                        {
                            RenderSystem.setShaderColor(1f, 0.4f, 0, 1f); // Red
                        }

                        graphics.blit(texture, guiLeft + 59 + xOffset, guiTop + 7, 198, rule.overlayY(), 20, 22);
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    }
                }
            }

            // Draw step icons
            final ForgeStep[] stepSequence = {steps.last(), steps.secondLast(), steps.thirdLast()};
            for (int i = 0; i < 3; i++)
            {
                final ForgeStep step = stepSequence[i];
                if (step != null)
                {
                    final int xOffset = i * 19;
                    graphics.blit(texture, guiLeft + 64 + xOffset, guiTop + 31, 10, 10, step.iconX(), step.iconY(), 32, 32, 256, 256);
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        final Level level = blockEntity.getLevel();
        final @Nullable Forging forging = blockEntity.getMainInputForging();
        if (forging != null && level != null)
        {
            final AnvilRecipe recipe = forging.getRecipe(level);
            if (recipe != null)
            {
                final ForgeRule[] rules = recipe.getRules();
                for (int i = 0; i < rules.length; i++)
                {
                    final ForgeRule rule = rules[i];
                    if (rule != null)
                    {
                        final int xOffset = i * 19;
                        final int x = getGuiLeft() + 64 + xOffset;
                        final int y = getGuiTop() + 10;
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
