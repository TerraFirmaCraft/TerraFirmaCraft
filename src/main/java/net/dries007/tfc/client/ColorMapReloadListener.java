/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.io.IOException;
import java.util.function.Consumer;

import net.minecraft.client.resources.ColorMapLoader;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ColorMapReloadListener extends ReloadListener<int[]>
{
    private final ResourceLocation textureLocation;
    private final Consumer<int[]> consumer;

    public ColorMapReloadListener(Consumer<int[]> consumer, ResourceLocation textureLocation)
    {
        this.textureLocation = textureLocation;
        this.consumer = consumer;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int[] prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        try
        {
            return ColorMapLoader.getPixels(resourceManagerIn, textureLocation);
        }
        catch (IOException ioexception)
        {
            throw new IllegalStateException("Failed to load colormap", ioexception);
        }
    }

    @Override
    protected void apply(int[] objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        consumer.accept(objectIn);
    }
}