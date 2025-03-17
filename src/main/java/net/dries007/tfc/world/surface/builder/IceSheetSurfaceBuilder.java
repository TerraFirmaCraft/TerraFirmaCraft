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

public class IceSheetSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory NORMAL = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialBase(seed.seed()), BiomeNoise.iceSheetSurfaceHeight(seed.seed()), true, false);
    public static final SurfaceBuilderFactory EDGE = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialBase(seed.seed()).addConstant(1.6), BiomeNoise.iceSheetSurfaceHeight(seed.seed()), true, false);
    public static final SurfaceBuilderFactory EDGE_LAKE = seed -> new IceSheetSurfaceBuilder(BiomeNoise.lake(seed.seed()), BiomeNoise.iceSheetSurfaceHeight(seed.seed()), false, false);
    public static final SurfaceBuilderFactory HIDDEN_LAKE = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialOceanicBase(seed.seed()), BiomeNoise.iceSheetSurfaceHeight(seed.seed()), false, false);
    public static final SurfaceBuilderFactory ICE_SHEET_MOUNTAINS = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialCirques(seed.seed()).addConstant(39), BiomeNoise.montaneIceSheetSurfaceHeight(seed.seed()).max(BiomeNoise.glacialCirquesIceSurfaceHeight(seed.seed()).addConstant(39)), false, true);
    public static final SurfaceBuilderFactory GLACIATED_MOUNTAINS = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialCirques(seed.seed()).addConstant(39), BiomeNoise.glacialCirquesIceSurfaceHeight(seed.seed()).addConstant(39), false, true);
    public static final SurfaceBuilderFactory OCEANIC = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialOceanicBase(seed.seed()), BiomeNoise.oceanicIceSheetSurfaceHeight(seed.seed()), false, false);
    public static final SurfaceBuilderFactory ICE_SHEET_OCEANIC_MOUNTAINS = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialCirques(seed.seed()), BiomeNoise.oceanicIceSheetSurfaceHeight(seed.seed()).max(BiomeNoise.glacialCirquesIceSurfaceHeight(seed.seed())), false, true);
    public static final SurfaceBuilderFactory GLACIATED_OCEANIC_MOUNTAINS = seed -> new IceSheetSurfaceBuilder(BiomeNoise.glacialCirques(seed.seed()), BiomeNoise.glacialCirquesIceSurfaceHeight(seed.seed()), false, true);

    private final Noise2D iceSurfaceNoise;
    private final Noise2D baseNoise;
    private final boolean hasMoraines;
    private final boolean hasStonyPeaks;

    IceSheetSurfaceBuilder(Noise2D baseNoise, Noise2D iceSurfaceNoise, boolean hasMoraines, boolean hasStonyPeaks)
    {
        this.baseNoise = baseNoise;
        this.iceSurfaceNoise = iceSurfaceNoise;
        this.hasMoraines = hasMoraines;
        this.hasStonyPeaks = hasStonyPeaks;
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

        if (hasStonyPeaks && startY > glacierSurfaceHeight + 2.5)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY);
        }
        else if (startY < glacierBaseHeight - 1.5)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
        }
        else
        {
            int surfaceDepth = -1;
            int surfaceY;

            final SurfaceState snowState = SurfaceStates.SNOW;
            final SurfaceState iceState = SurfaceStates.PACKED_ICE;
            final SurfaceState blueIceState = SurfaceStates.BLUE_ICE;
            final SurfaceState moraineTopState = SurfaceStates.SNOWY_MORAINE;
            final SurfaceState moraineState = SurfaceStates.MORAINE;

            for (int y = startY; y >= glacierBaseHeight - 2; --y)
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
                    else
                    {
                        // Subsurface layers
                        context.setBlockState(y, y == startY ? moraineTopState : moraineState);
                    }
                }
            }
        }
    }
}
