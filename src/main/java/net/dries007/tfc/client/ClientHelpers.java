/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Client side methods for proxy use
 */
public final class ClientHelpers
{
    @Nullable
    public static Level getWorld()
    {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static Player getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static boolean hasShiftDown()
    {
        return Screen.hasShiftDown();
    }
}