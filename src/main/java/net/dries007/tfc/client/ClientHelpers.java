/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Client side methods for proxy use
 */
public class ClientHelpers
{
    @Nullable
    public static World getWorld()
    {
        return Minecraft.getInstance().level;
    }
}