/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.CrateBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

public class CrateOverlays
{
    public static boolean render(Minecraft minecraft, GuiGraphics graphics)
    {
        final Level level = minecraft.level;
        final BlockPos targetedPos = ClientHelpers.getTargetedPos();
        if (level != null && targetedPos != null && level.getBlockEntity(targetedPos) instanceof CrateBlockEntity crate)
        {
            final ItemStackHandler inv = crate.getInventory();

            ItemStack contained = ItemStack.EMPTY;
            int total = 0;
            for (ItemStack stack : Helpers.iterate(inv))
            {
                if (!stack.isEmpty())
                {
                    if (contained.isEmpty())
                    {
                        contained = stack;
                    }
                    total += stack.getCount();
                }
            }

            final Component line;
            if (contained.isEmpty())
            {
                line = Component.translatable("tfc.tooltip.crate.empty");
            }
            else
            {
                final int percent = total * 100 / (CrateBlockEntity.SLOTS * contained.getMaxStackSize());
                line = Tooltips.countOfItem(contained, total).append(" (" + percent + "%)");
            }

            final int x = graphics.guiWidth() / 2 + 3;
            final int y = graphics.guiHeight() / 2 + 8;
            drawCenteredText(minecraft, graphics, line, x, y);
            return true;
        }
        return false;
    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, 0xFFFFFF, true);
    }
}
