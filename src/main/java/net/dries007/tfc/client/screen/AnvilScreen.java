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
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.ForgeSteps;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.component.forge.ForgingComponent;
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

        if (TerraFirmaCraft.JEI)
        {
            graphics.blit(texture, guiLeft + 26, guiTop + 24, 0, 207, 9, 14);
        }

        assert level != null;

        // Draw rule icons
        final @Nullable Forging forging = blockEntity.getMainInputForging();
        if (forging != null)
        {
            final ForgingComponent view = forging.view();

            // Draw the progress indicators
            final int progress = view.work();
            graphics.blit(texture, guiLeft + 13 + progress, guiTop + 100, 176, 0, 5, 5);

            final int target = view.target();
            graphics.blit(texture, guiLeft + 13 + target, guiTop + 94, 181, 0, 5, 5);

            final ForgeSteps steps = view.steps();
            final AnvilRecipe recipe = view.recipe();
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
            final ForgeStep[] stepSequence = {steps.last().orElse(null), steps.secondLast().orElse(null), steps.thirdLast().orElse(null)};
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
        final Forging forging = blockEntity.getMainInputForging();
        if (level != null)
        {
            final @Nullable AnvilRecipe recipe = forging.view().recipe();
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
