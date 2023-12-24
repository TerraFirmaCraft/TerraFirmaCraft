package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;
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
    protected void init()
    {
        super.init();
        addRenderableWidget(new ImageButton(leftPos + 125, topPos + 18, 20, 20, 236, 0, 20, TEXTURE, 256, 256, button -> {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(SewingTableContainer.BURLAP_ID, null));
        }, TFCItems.BURLAP_CLOTH.get().getDefaultInstance().getHoverName()));

        addRenderableWidget(new ImageButton(leftPos + 150, topPos + 18, 20, 20, 236, 40, 20, TEXTURE, 256, 256, button -> {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(SewingTableContainer.WOOL_ID, null));
        }, TFCItems.WOOL_CLOTH.get().getDefaultInstance().getHoverName()));

        addRenderableWidget(new ImageButton(leftPos + 125, topPos + 43, 20, 20, 236, 80, 20, TEXTURE, 256, 256, button -> {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(SewingTableContainer.REMOVE_ID, null));
        }));

        addRenderableWidget(new ImageButton(leftPos + 150, topPos + 43, 20, 20, 236, 120, 20, TEXTURE, 256, 256, button -> {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(SewingTableContainer.NEEDLE_ID, null));
        }));

        int i = 0;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                final int id = i + SewingTableContainer.PLACED_SLOTS_OFFSET;
                final ImageButton button = new ImageButton(getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 12, 12, 208, 32, 0, TEXTURE, 256, 256, btn -> {
                    PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(id, null));
                });
                addRenderableWidget(button);
                i++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isSewing(mouseX, mouseY) && menu.getActiveMaterial() == SewingTableContainer.NEEDLE_ID)
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

    private int getScreenX(int posX)
    {
        return posX == -1 ? 0 : posX + X_OFFSET + leftPos;
    }

    private int getScreenY(int posY)
    {
        return posY == -1 ? 0 : posY + Y_OFFSET + topPos;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        renderBackground(graphics);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int i = 0;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                final int mat = menu.getPlacedMaterial(i);
                if (mat != -1)
                {
                    graphics.blit(TEXTURE, getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 208, mat == SewingTableContainer.BURLAP_ID ? 16 : 0, 12, 12);
                }
                i++;
            }
        }

        if (lastSewingX != -1 && lastSewingY != -1)
        {
            graphics.blit(TEXTURE, getScreenX(lastSewingX), getScreenY(lastSewingY), 192, 0, 5, 5);
        }

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (menu.getActiveMaterial() == SewingTableContainer.BURLAP_ID)
        {
            graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 16, 12, 12);
        }
        else if (menu.getActiveMaterial() == SewingTableContainer.WOOL_ID)
        {
            graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 0, 12, 12);
        }
        else
        {
            graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 48, 16, 16);
        }
    }
}
