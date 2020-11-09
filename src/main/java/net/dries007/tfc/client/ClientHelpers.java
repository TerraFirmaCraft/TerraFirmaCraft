/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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