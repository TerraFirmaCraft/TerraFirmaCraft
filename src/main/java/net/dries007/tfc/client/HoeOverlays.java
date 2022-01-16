/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.config.TFCConfig;

public class HoeOverlays
{
    public static boolean render(Minecraft minecraft, Window window, PoseStack stack)
    {
        final Level world = minecraft.level;
        final BlockPos targetedPos = ClientHelpers.getTargetedPos();
        if (world != null && targetedPos != null)
        {
            final BlockState targetedState = world.getBlockState(targetedPos);
            if (targetedState.getBlock() instanceof HoeOverlayBlock overlayBlock)
            {
                final List<Component> lines = new ArrayList<>();
                overlayBlock.addHoeOverlayInfo(world, targetedPos, targetedState, lines, TFCConfig.CLIENT.enableDebug.get());
                if (!lines.isEmpty())
                {
                    int x = window.getGuiScaledWidth() / 2 + 3;
                    int y = window.getGuiScaledHeight() / 2 + 8;
                    for (Component line : lines)
                    {
                        drawCenteredText(minecraft, stack, line, x, y);
                        y += 12;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static void drawCenteredText(Minecraft minecraft, PoseStack stack, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        minecraft.font.draw(stack, text, x - textWidth, y, 0xCCCCCC);
    }
}
