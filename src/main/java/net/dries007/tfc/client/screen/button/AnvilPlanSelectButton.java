/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.screen.AnvilPlanScreen;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;

public class AnvilPlanSelectButton extends Button
{
    private final ItemStack result;
    private final int page; // The page this button is on
    private int currentPage; // The page selected by the root gui

    public AnvilPlanSelectButton(int x, int y, int page, final AnvilRecipe recipe, OnTooltip tooltip)
    {
        super(x, y, 18, 18, TextComponent.EMPTY, button -> {
            if (button.active)
            {
                final CompoundTag tag = new CompoundTag();
                tag.putString("recipe", recipe.getId().toString());
                PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(0, tag));
            }
        }, tooltip);

        this.result = recipe.getResultItem();
        this.page = page;
        this.currentPage = 0;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
        this.visible = this.active = this.currentPage == this.page;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, AnvilPlanScreen.BACKGROUND);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            blit(poseStack, x, y, 176, 0, width, height, 256, 256);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(result, x + 1, y + 1);
        }
    }
}
