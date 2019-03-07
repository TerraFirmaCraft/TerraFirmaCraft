/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.Fluid;

public class FluidSpriteCache
{
    private static final Map<Fluid, TextureAtlasSprite> CACHE = new HashMap<>();

    public static TextureAtlasSprite getSprite(Fluid fluid)
    {
        TextureAtlasSprite sprite = CACHE.get(fluid);

        if (sprite == null)
        {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
            CACHE.put(fluid, sprite);
        }

        return sprite;
    }

    public static void clear()
    {
        CACHE.clear();
    }
}
