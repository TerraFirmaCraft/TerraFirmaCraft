/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.dries007.tfc.util.Helpers;

public class SewingTableScreen extends AbstractContainerScreen<SewingTableContainer>
{
    public static void forEachStitch(TriFunction<Integer, Integer, Integer, Boolean> action)
    {
        int i = 0;
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                if (action.apply(x, y, i))
                    return;
                i++;
            }
        }
    }

    public static void forEachClothSquare(TriConsumer<Integer, Integer, Integer> action)
    {
        int i = 0;
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                action.accept(x, y, i);
                i++;
            }
        }
    }

    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/sewing.png");
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 16;

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
        createButton(leftPos + 125, topPos + 18, 20, 236, 0, 20, SewingTableContainer.BURLAP_ID, "tfc.tooltip.sewing.dark_cloth");
        createButton(leftPos + 150, topPos + 18, 20, 236, 40, 20, SewingTableContainer.WOOL_ID, "tfc.tooltip.sewing.light_cloth");
        createButton(leftPos + 125, topPos + 43, 20, 236, 80, 20, SewingTableContainer.REMOVE_ID, "tfc.tooltip.sewing.remove_stitch");
        createButton(leftPos + 150, topPos + 43, 20, 236, 120, 20, SewingTableContainer.NEEDLE_ID, "tfc.tooltip.sewing.stitch");

        forEachClothSquare((x, y, i) -> {
            final int id = i + SewingTableContainer.PLACED_SLOTS_OFFSET;
            createButton(getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 12, 208, 32, 0, id, null);
        });
    }

    private void createButton(int x, int y, int size, int u, int v, int yDiffTex, int packetButtonId, @Nullable String translationKey)
    {
        ImageButton button;
        if (translationKey != null)
        {
            button = new ImageButton(x, y, size, size, u, v, yDiffTex, TEXTURE, 256, 256, btn -> {
                if (menu.getCarried().isEmpty())
                    PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(packetButtonId, null));
            }, Component.translatable(translationKey));
            button.setTooltip(Tooltip.create(Component.translatable(translationKey)));
        }
        else
        {
            button = new ImageButton(x, y, size, size, u, v, yDiffTex, TEXTURE, 256, 256, btn -> {
                if (menu.getCarried().isEmpty())
                    PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(packetButtonId, null));
            });
        }
        addRenderableWidget(button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        final int mat = menu.getActiveMaterial();
        if (isSewing(mouseX, mouseY) && (mat == SewingTableContainer.NEEDLE_ID || mat == SewingTableContainer.REMOVE_ID))
        {
            final int sewX = getSewingX(mouseX);
            final int sewY = getSewingY(mouseY);
            forEachStitch((x, y, i) -> {
                final int leftX = x * 12 + 6;
                final int topY = y * 12 + 6;
                // offset by 6 inwards to center the click on the corner
                if (RenderHelpers.isInside(sewX + 6, sewY + 6, leftX, topY, 12, 12) && (menu.getStitchAt(i) == 1 || mat == SewingTableContainer.NEEDLE_ID))
                {
                    final CompoundTag tag = new CompoundTag();
                    tag.putInt("id", i);
                    tag.putInt("stitchType", mat == SewingTableContainer.NEEDLE_ID ? 1 : 0);
                    PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(SewingTableContainer.PLACE_STITCH_ID, tag));
                    return true;
                }
                return false;
            });
            if (mat == SewingTableContainer.NEEDLE_ID)
                return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int clickType, double dragX, double dragY)
    {
        if (clickType == 0 && menu.getActiveMaterial() != SewingTableContainer.NEEDLE_ID && isSewing(x, y))
        {
            mouseClicked(x, y, clickType);
        }
        return super.mouseDragged(x, y, clickType, dragX, dragY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        renderBackground(graphics);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        forEachClothSquare((x, y, i) -> {
            final int mat = menu.getPlacedMaterial(i);
            if (mat != -1)
            {
                graphics.blit(TEXTURE, getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 208, mat == SewingTableContainer.BURLAP_ID ? 16 : 0, 12, 12);
            }
        });

        forEachStitch((x, y, i) -> {
            final int stitch = menu.getStitchAt(i);
            if (stitch == 1)
            {
                graphics.blit(TEXTURE, getScreenX(x * 12 + 6) - 2, getScreenY(y * 12 + 6) - 2, 192, 0, 5, 5);
            }
            return false;
        });
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);

        if (!menu.canPickup(SewingTableContainer.SLOT_YARN))
            renderSlotHighlight(graphics, 8, 83, 1);
        if (!menu.canPickup(SewingTableContainer.SLOT_INPUT_1))
            renderSlotHighlight(graphics, 62, 83, 1);
        if (!menu.canPickup(SewingTableContainer.SLOT_INPUT_2))
            renderSlotHighlight(graphics, 80, 83, 1);

        if (menu.getCarried().isEmpty() && RenderHelpers.isInside(mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight))
        {
            final int mat = menu.getActiveMaterial();
            if (mat == SewingTableContainer.BURLAP_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 16, 12, 12);
            }
            else if (mat == SewingTableContainer.WOOL_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 0, 12, 12);
            }
            else if (mat == SewingTableContainer.REMOVE_ID || mat == SewingTableContainer.NEEDLE_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 48, 16, 16);
            }
        }


        final int burlapCount = menu.getBurlapCount();
        final int woolCount = menu.getWoolCount();
        final int yarnCount = menu.getYarnCount();

        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(burlapCount, 99)), 135, 30, burlapCount == 0 ? 0x404040 : 0xFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(woolCount, 99)), 160, 30, woolCount == 0 ? 0x404040 : 0xFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(yarnCount, 99)), 160, 53, yarnCount == 0 ? 0x404040 : 0xFFFFFF);
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

}
