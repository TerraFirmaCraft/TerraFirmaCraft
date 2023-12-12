package net.dries007.tfc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.util.Helpers;

public class SewingTableScreen extends AbstractContainerScreen<SewingTableContainer>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/sewing.png");
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 16;

    private int lastSewingX = -1;
    private int lastSewingY = -1;

    public SewingTableScreen(SewingTableContainer menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        imageHeight += 30;
        inventoryLabelY += 30;
        titleLabelY -= 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isSewing(mouseX, mouseY))
        {
            final int x = getSewingX(mouseX);
            final int y = getSewingY(mouseY);
            lastSewingX = x;
            lastSewingY = y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isSewing(double mouseX, double mouseY)
    {
        return RenderHelpers.isInside((int) mouseX, (int) mouseY, X_OFFSET + leftPos, Y_OFFSET + topPos, 117 - X_OFFSET, 75 - Y_OFFSET);
    }

    private int getSewingX(double mouseX)
    {
        return (int) (mouseX - X_OFFSET - leftPos);
    }

    private int getSewingY(double mouseY)
    {
        return (int) (mouseY - Y_OFFSET - topPos);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (lastSewingX != -1 && lastSewingY != -1)
        {
            graphics.blit(TEXTURE, lastSewingX + X_OFFSET + leftPos, lastSewingY + Y_OFFSET + topPos, 192, 0, 5, 5);

            if (isSewing(mouseX, mouseY))
            {
                final PoseStack stack = graphics.pose();
                final VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.lines());
                stack.pushPose();
                buffer.vertex(stack.last().pose(), lastSewingX + X_OFFSET + leftPos, lastSewingY + Y_OFFSET + topPos, 1f).color(255, 255, 255, 255).normal(0, 0, 1).endVertex();
                buffer.vertex(stack.last().pose(), mouseX, mouseY, 1f).color(255, 255, 255, 255).normal(0, 0, 1).endVertex();
                stack.popPose();
                graphics.bufferSource().endBatch(RenderType.lines());
            }
        }

    }

}
