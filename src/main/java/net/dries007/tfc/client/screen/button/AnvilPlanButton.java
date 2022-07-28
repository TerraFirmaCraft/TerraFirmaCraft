/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class AnvilPlanButton extends Button
{
    private final AnvilBlockEntity anvil;

    public AnvilPlanButton(AnvilBlockEntity anvil, int guiLeft, int guiTop, OnTooltip tooltip)
    {
        super(guiLeft + 21, guiTop + 40, 18, 18, Helpers.translatable("tfc.tooltip.anvil_plan_button"), button -> {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(AnvilContainer.PLAN_ID, null));
        }, tooltip);

        this.anvil = anvil;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, AnvilScreen.BACKGROUND);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        blit(poseStack, x, y, 218, 0, width, height, 256, 256);

        final AnvilRecipe recipe = getRecipe();
        if (recipe != null)
        {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(recipe.getResultItem(), x + 1, y + 1);
        }
        else
        {
            blit(poseStack, x + 1, y + 1, 236, 0, 16, 16, 256, 256);
        }

        if (isHoveredOrFocused())
        {
            renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    @Nullable
    private AnvilRecipe getRecipe()
    {
        final Level level = anvil.getLevel();
        final Forging forging = anvil.getMainInputForging();
        if (level != null && forging != null)
        {
            return forging.getRecipe(level);
        }
        return null;
    }
}
