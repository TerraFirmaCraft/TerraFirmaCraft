/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.client.ClientHelpers;

public abstract class TFCContainerScreen<C extends Container> extends ContainerScreen<C>
{
    protected final ResourceLocation texture;

    public TFCContainerScreen(C container, PlayerInventory playerInventory, ITextComponent name, ResourceLocation texture)
    {
        super(container, playerInventory, name);
        this.texture = texture;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderTooltip(mouseX, mouseY);
    }

    @Override
    protected void renderBg(float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground();
    }

    @SuppressWarnings("ConstantConditions")
    protected void drawDefaultBackground()
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(texture);
        ClientHelpers.drawTexturedRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}