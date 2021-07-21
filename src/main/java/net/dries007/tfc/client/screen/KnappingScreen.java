/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.screen.button.KnappingButton;
import net.dries007.tfc.common.container.KnappingContainer;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class KnappingScreen extends TFCContainerScreen<KnappingContainer>
{
    private static final ResourceLocation KNAPPING_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/knapping.png");
    private final ResourceLocation buttonLocation;
    private final ResourceLocation buttonDisabledLocation;

    public KnappingScreen(KnappingContainer container, PlayerInventory inv, ITextComponent name)
    {
        super(container, inv, name, KNAPPING_BACKGROUND);
        imageHeight = 186;
        inventoryLabelY += 22;
        titleLabelY -= 2;
        ResourceLocation regName = container.stackCopy.getItem().getRegistryName();
        assert regName != null;
        buttonLocation = new ResourceLocation(MOD_ID, "textures/gui/knapping/" + regName.getPath() + ".png");
        buttonDisabledLocation = container.usesDisabledTex ? new ResourceLocation(MOD_ID, "textures/gui/knapping/" + regName.getPath() + "_disabled.png") : null;
    }

    @Override
    protected void init()
    {
        super.init();
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int bx = (width - getXSize()) / 2 + 12 + 16 * x;
                int by = (height - getYSize()) / 2 + 12 + 16 * y;
                addButton(new KnappingButton(x + 5 * y, bx, by, 16, 16, buttonLocation, menu.sound));
            }
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        // Check if the container has been updated
        if (menu.requiresReset)
        {
            for (Widget button : buttons)
            {
                if (button instanceof KnappingButton)
                {
                    button.visible = menu.getSlotState((((KnappingButton) button).id));
                }
            }
            menu.requiresReset = false;
        }
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        if (menu.usesDisabledTex && minecraft != null)
        {
            minecraft.getTextureManager().bind(buttonDisabledLocation);
            for (Widget button : buttons)
            {
                if (!button.visible && button instanceof KnappingButton)
                {
                    matrixStack.pushPose();
                    blit(matrixStack, button.x, button.y, 0, 0, 16, 16, 16, 16);
                    matrixStack.popPose();
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int clickType, double dragX, double dragY)
    {
        if (clickType == 0)
        {
            mouseClicked(x, y, clickType);
        }
        return super.mouseDragged(x, y, clickType, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double x, double y, int clickType)
    {
        if (clickType == 0)
        {
            for (Widget widget : buttons)
            {
                if (widget instanceof KnappingButton && widget.isMouseOver(x, y))
                {
                    menu.setSlotState(((KnappingButton) widget).id, false);
                }
            }
        }
        return super.mouseClicked(x, y, clickType);
    }
}
