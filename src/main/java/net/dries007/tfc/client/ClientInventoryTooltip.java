/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blocks.TooltipBlock;
import net.dries007.tfc.util.Helpers;

public class ClientInventoryTooltip implements ClientTooltipComponent
{
    public static final ResourceLocation TEXTURE_LOCATION = Helpers.identifier("textures/gui/device_image_tooltip.png");

    private final TooltipBlock.Instance tooltip;

    public ClientInventoryTooltip(TooltipBlock.Instance tooltip)
    {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight()
    {
        return tooltip.height() * 18 + 6; // slots x 18px + 1px border + 2px padding
    }

    @Override
    public int getWidth(Font font)
    {
        return tooltip.width() * 18 + 2; // slots x 18px + 1px of border outside the slot
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics)
    {
        // Render the background, 1px outside the span of the slots. It renders a single pixel from the texture (at 0, 18)
        graphics.blit(TEXTURE_LOCATION, mouseX, mouseY + 2, 18 * tooltip.width() + 2, 18 * tooltip.width() + 2, 0, 18, 1, 1, 128, 128);
        for (int slot = 0; slot < tooltip.items().size(); slot++)
        {
            final int slotX = mouseX + (slot % tooltip.width()) * 18 + 1;
            final int slotY = mouseY + (slot / tooltip.width()) * 18 + 3;
            final ItemStack stack = tooltip.items().get(slot);

            // Render each slot at its respective position, followed by the items atop it
            graphics.blit(TEXTURE_LOCATION, slotX, slotY, 0, 0f, 0f, 18, 18, 128, 128);
            graphics.renderItem(stack, slotX + 1, slotY + 1, slot);
            graphics.renderItemDecorations(font, stack, slotX + 1, slotY + 1);
        }
    }
}
