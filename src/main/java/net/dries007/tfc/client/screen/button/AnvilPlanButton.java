/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.network.ScreenButtonPacket;

public class AnvilPlanButton extends Button
{
    private final AnvilBlockEntity anvil;

    public AnvilPlanButton(AnvilBlockEntity anvil, int guiLeft, int guiTop)
    {
        super(guiLeft + 21, guiTop + 40, 18, 18, Component.translatable("tfc.tooltip.anvil_plan"), button -> {
            PacketDistributor.sendToServer(new ScreenButtonPacket(AnvilContainer.PLAN_ID));
        }, RenderHelpers.NARRATION);
        setTooltip(Tooltip.create(Component.translatable("tfc.tooltip.anvil_plan")));

        this.anvil = anvil;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int x = getX();
        int y = getY();
        graphics.blit(AnvilScreen.BACKGROUND, x, y, 218, 0, width, height, 256, 256);

        final AnvilRecipe recipe = getRecipe();
        if (recipe != null)
        {
            final RegistryAccess access = ClientHelpers.getLevelOrThrow().registryAccess();
            graphics.renderItem(recipe.getResultItem(access), x + 1, y + 1);
            graphics.renderItemDecorations(Minecraft.getInstance().font, recipe.getResultItem(access), x + 1, y + 1);
        }
        else
        {
            final boolean workable = anvil.getLevel() != null && AnvilRecipe.hasAny(anvil.getLevel(), anvil.getInventory().getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN), anvil.getTier());
            graphics.blit(AnvilScreen.BACKGROUND, x + 1, y + 1, workable ? 236 : 219, workable ? 0 : 51, 16, 16, 256, 256);
        }

    }

    @Nullable
    private AnvilRecipe getRecipe()
    {
        return anvil.getMainInputForging().getRecipe();
    }
}
