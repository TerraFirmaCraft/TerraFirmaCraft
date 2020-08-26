/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.io.IOException;

import net.minecraft.client.resources.ColorMapLoader;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class WaterColorReloadListener extends ReloadListener<int[]>
{
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MOD_ID, "textures/colormap/water.png");

    @Override
    @SuppressWarnings("deprecation")
    protected int[] prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        try
        {
            return ColorMapLoader.loadColors(resourceManagerIn, TEXTURE_LOCATION);
        }
        catch (IOException ioexception)
        {
            throw new IllegalStateException("Failed to load water color texture", ioexception);
        }
    }

    @Override
    protected void apply(int[] objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        WaterColors.setWaterColors(objectIn);
    }
}
