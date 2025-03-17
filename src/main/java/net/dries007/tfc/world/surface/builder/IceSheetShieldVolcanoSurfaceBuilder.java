/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;


import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class IceSheetShieldVolcanoSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory ICE_SHEET = seed -> new IceSheetShieldVolcanoSurfaceBuilder(seed, BiomeNoise.glaciatedShieldVolcano(seed.seed(), BiomeNoise.hotSpotIntensity(seed.seed())), BiomeNoise.iceSheetSurfaceHeight(seed.seed()).max(BiomeNoise.shieldVolcanoIceSheetSurface(seed.seed(), BiomeNoise.hotSpotIntensity(seed.seed()))), false, true, SEA_LEVEL_Y);
    public static final SurfaceBuilderFactory GLACIATED = seed -> new IceSheetShieldVolcanoSurfaceBuilder(seed, BiomeNoise.glaciatedShieldVolcano(seed.seed(), BiomeNoise.hotSpotIntensity(seed.seed())), BiomeNoise.iceSheetSurfaceHeight(seed.seed()).max(BiomeNoise.shieldVolcanoGlacierSurface(seed.seed(), BiomeNoise.hotSpotIntensity(seed.seed()))), false, true, SEA_LEVEL_Y + 30);


    private final Noise2D iceSurfaceNoise;
    private final Noise2D baseNoise;
    private final boolean hasMoraines;
    private final boolean hasStonyPeaks;
    private final int minFreezingHeight;
    private final SurfaceBuilder baseVolcanoSurfaceBuilder;

    IceSheetShieldVolcanoSurfaceBuilder(Seed seed, Noise2D baseNoise, Noise2D iceSurfaceNoise, boolean hasMoraines, boolean hasStonyPeaks, int minFreezingHeight)
    {
        this.baseNoise = baseNoise;
        this.iceSurfaceNoise = iceSurfaceNoise;
        this.hasMoraines = hasMoraines;
        this.hasStonyPeaks = hasStonyPeaks;
        this.minFreezingHeight = minFreezingHeight;
        this.baseVolcanoSurfaceBuilder = ShieldVolcanoSurfaceBuilder.DORMANT.apply(seed);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final int x = context.pos().getX();
        final int z = context.pos().getZ();

        final int glacierBaseHeight = (int) Math.ceil(baseNoise.noise(x, z));
        final int glacierSurfaceHeight = (int) Math.ceil(iceSurfaceNoise.noise(x, z));

        int iceDepth;
        // Base Groundwater check allows for exposed ice near where rivers cut into ice sheet
        if (hasMoraines && context.baseGroundwater() <= 20f)
        {
            final double moraineCrestHeight = Math.min((0.5 * (glacierSurfaceHeight + glacierBaseHeight)), glacierBaseHeight + 18);
            iceDepth = Math.max((int) ((startY - moraineCrestHeight) * 2), 0);
        }
        else
        {
            iceDepth = 35;
        }

        if (startY < minFreezingHeight || (hasStonyPeaks && startY > glacierSurfaceHeight + 2.5) || (startY < glacierBaseHeight - 1.5))
        {
            this.baseVolcanoSurfaceBuilder.buildSurface(context, startY, endY);
        }
        else
        {
            int surfaceDepth = -1;
            int surfaceY = 0;

            final SurfaceState snowState = SurfaceStates.SNOW;
            final SurfaceState iceState = SurfaceStates.PACKED_ICE;
            final SurfaceState blueIceState = SurfaceStates.BLUE_ICE;
            final SurfaceState moraineTopState = SurfaceStates.SNOWY_BASALT_MORAINE;
            final SurfaceState moraineState = SurfaceStates.BASALT_MORAINE;
            final SurfaceState basaltState = SurfaceStates.BASALT;

            for (int y = startY; y >= glacierBaseHeight - 22; --y)
            {
                final BlockState stateAt = context.getBlockState(y);
                if (stateAt.isAir())
                {
                    surfaceDepth = -1; // Reached air, reset surface depth
                }
                else if (context.isDefaultBlock(stateAt))
                {
                    // All in this if statement only occurs on the first cycle/when air resets the cycle
                    if (surfaceDepth == -1)
                    {
                        surfaceY = y; // Reached surface. Place top state and switch to subsurface layers

                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, -3);
                        if (surfaceDepth <= -1)
                        {
                            // skip placing snow on steep slopes
                            if (iceDepth < 1)
                            {
                                context.setBlockState(y, moraineState);
                            }
                            // avoids placing ice on steep slopes where glacier base height = terrain height
                            else if (y <= glacierBaseHeight)
                            {
                                iceDepth = 0;
                            }
                            else
                            {
                                context.setBlockState(y, iceState);
                            }
                        }
                        else if (iceDepth == 0 || y <= context.getSeaLevel() || y < glacierBaseHeight)
                        {
                            // Skip placing snow where there is no glacier, or underwater
                            context.setBlockState(y, moraineState);
                        }
                        else
                        {
                            context.setBlockState(y, snowState);
                        }
                        surfaceDepth = 1;
                    }
                    else if (iceDepth > 0 && y > glacierBaseHeight)
                    {
                        // Subsurface layers
                        iceDepth--;
                        context.setBlockState(y, y < glacierSurfaceHeight - 16 ? blueIceState : iceState);
                    }
                    else if (y > glacierBaseHeight)
                    {
                        // Subsurface layers
                        context.setBlockState(y, y == startY ? moraineTopState : moraineState);
                    }
                    else
                    {
                        // Subsurface layers
                        context.setBlockState(y, basaltState);
                    }
                }
            }
        }
    }
}
