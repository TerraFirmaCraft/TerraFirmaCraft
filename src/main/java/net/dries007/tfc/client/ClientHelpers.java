/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * This class exists because forge can't bloody well make a usable mapping system... like jesus christ really?
 * Enough with "oh they were not obfuscated so we can't map them". Pfft. Fabric had no problem with this.
 * Thank you gigahertz for making this usable https://gist.github.com/gigaherz/f61fe604f38e27afad4d1553bc6cf311
 */
public class ClientHelpers
{
    public static void drawTexturedRect(int x, int y, int width, int height, TextureAtlasSprite sprite)
    {
        AbstractGui.blit(x, y, 0, width, height, sprite);
    }

    public static void drawTexturedRect(int x, int y, float u, float v, int width, int height)
    {
        AbstractGui.blit(x, y, 0, u, v, width, height, 256, 256);
    }

    public static void drawTexturedRect(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)
    {
        AbstractGui.blit(x, y, 0, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexturedScaledRect(int x, int y, int width, int height, float u, float v, int actualWidth, int actualHeight)
    {
        AbstractGui.blit(x, y, width, height, u, v, actualWidth, actualHeight, 256, 256);
    }

    public static void drawTexturedScaledRect(int x, int y, int width, int height, float u, float v, int actualWidth, int actualHeight, int textureWidth, int textureHeight)
    {
        AbstractGui.blit(x, y, width, height, u, v, actualWidth, actualHeight, textureWidth, textureHeight);
    }
}
