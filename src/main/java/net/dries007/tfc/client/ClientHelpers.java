/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

import com.mojang.blaze3d.matrix.MatrixStack;

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