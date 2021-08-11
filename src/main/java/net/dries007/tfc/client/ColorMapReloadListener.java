/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.io.IOException;
import java.util.function.Consumer;

import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

public class ColorMapReloadListener extends SimplePreparableReloadListener<int[]>
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
    protected int[] prepare(ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        try
        {
            return LegacyStuffWrapper.getPixels(resourceManagerIn, textureLocation);
        }
        catch (IOException ioexception)
        {
            throw new IllegalStateException("Failed to load colormap", ioexception);
        }
    }

    @Override
    protected void apply(int[] objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        consumer.accept(objectIn);
    }
}