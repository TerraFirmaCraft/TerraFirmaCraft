/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Client side methods for proxy use
 */
public final class ClientHelpers
{
    public static final Direction[] DIRECTIONS_AND_NULL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, null};

    @Nullable
    public static Level getLevel()
    {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static Player getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @Nullable
    public static BlockPos getTargetedPos()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.hitResult instanceof BlockHitResult block)
        {
            return block.getBlockPos();
        }
        return null;
    }

    public static boolean hasShiftDown()
    {
        return Screen.hasShiftDown();
    }

}