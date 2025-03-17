/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class PatternedGroundSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = PatternedGroundSurfaceBuilder::new;

    private final NormalSurfaceBuilder surfaceBuilder;
    private final Noise2D edgeNoise;

    public PatternedGroundSurfaceBuilder(Seed seed)
    {
        this.surfaceBuilder = NormalSurfaceBuilder.INSTANCE;
        this.edgeNoise = BiomeNoise.patternedGround(seed.seed());
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (edgeNoise.noise(context.pos().getX(), context.pos().getZ()) * context.weight() >= -0.60)
        {
            surfaceBuilder.buildSurface(context, startY, endY);
        }
        else
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.MUD, SurfaceStates.MUD, SurfaceStates.GRAVEL, SurfaceStates.MUD, SurfaceStates.MUD);
        }
    }
}