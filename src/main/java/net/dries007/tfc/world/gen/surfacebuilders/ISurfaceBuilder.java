package net.dries007.tfc.world.gen.surfacebuilders;

import java.util.Random;

import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.gen.rock.RockData;

/**
 * This is seperate from vanilla's surface builder for several reasons:
 * - we do a much more complex calculation involving
 */
public interface ISurfaceBuilder
{
    // Surface parts (rock data -> variants of blocks)
    ISurfacePart GRASS = ISurfacePart.soil(SoilBlockType.GRASS);
    ISurfacePart DRY_GRASS = ISurfacePart.soil(SoilBlockType.DRY_GRASS);
    ISurfacePart DIRT = ISurfacePart.soil(SoilBlockType.DIRT);
    ISurfacePart SAND = ISurfacePart.sand();
    ISurfacePart GRAVEL = ISurfacePart.rock(Rock.BlockType.GRAVEL);
    ISurfacePart RAW = ISurfacePart.rock(Rock.BlockType.RAW);

    // All grouped surface parts (similar to vanilla surface builder configs)
    TFCSurfaceBuilderConfig GRASS_DIRT_GRAVEL = new TFCSurfaceBuilderConfig(GRASS, DIRT, GRAVEL);
    TFCSurfaceBuilderConfig DRY_GRASS_DIRT_GRAVEL = new TFCSurfaceBuilderConfig(DRY_GRASS, DIRT, GRAVEL);
    TFCSurfaceBuilderConfig SAND_SAND_GRAVEL = new TFCSurfaceBuilderConfig(SAND, SAND, GRAVEL);
    TFCSurfaceBuilderConfig THICK_GRASS_DIRT_GRAVEL = new TFCSurfaceBuilderConfig(GRASS, DIRT, GRAVEL, GRAVEL);
    TFCSurfaceBuilderConfig THICK_DRY_GRASS_DIRT_GRAVEL = new TFCSurfaceBuilderConfig(DRY_GRASS, DIRT, GRAVEL, GRAVEL);
    TFCSurfaceBuilderConfig THICK_SAND_SAND_GRAVEL = new TFCSurfaceBuilderConfig(SAND, SAND, GRAVEL, GRAVEL);
    TFCSurfaceBuilderConfig GRAVEL_GRAVEL_GRAVEL = new TFCSurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
    TFCSurfaceBuilderConfig SAND_SAND_SAND = new TFCSurfaceBuilderConfig(SAND, SAND, SAND);

    // All surface builder placement algorithms
    ISurfaceBuilder DEFAULT = new CompositeSurfaceBuilder(75f, 125f, new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 4), new DefaultSurfaceBuilder(DRY_GRASS_DIRT_GRAVEL, 4), new DefaultSurfaceBuilder(GRASS_DIRT_GRAVEL, 4), true);
    ISurfaceBuilder DEFAULT_THICK = new CompositeSurfaceBuilder(75f, 125f, new DefaultSurfaceBuilder(THICK_SAND_SAND_GRAVEL, 7), new DefaultSurfaceBuilder(THICK_DRY_GRASS_DIRT_GRAVEL, 7), new DefaultSurfaceBuilder(THICK_GRASS_DIRT_GRAVEL, 6), true);
    ISurfaceBuilder DEFAULT_THIN = new CompositeSurfaceBuilder(75f, 125f, new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 0), new DefaultSurfaceBuilder(DRY_GRASS_DIRT_GRAVEL, 1), new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 1), true);
    ISurfaceBuilder SHORE = new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 4);
    ISurfaceBuilder STONE_SHORE = new DefaultSurfaceBuilder(GRAVEL_GRAVEL_GRAVEL, 1);
    ISurfaceBuilder FRESHWATER = new CompositeSurfaceBuilder(8f, new DefaultSurfaceBuilder(GRAVEL_GRAVEL_GRAVEL, 2), new DefaultSurfaceBuilder(SAND_SAND_SAND, 3), false);
    ISurfaceBuilder SEAWATER = new CompositeSurfaceBuilder(8f, new DefaultSurfaceBuilder(GRAVEL_GRAVEL_GRAVEL, 0), new DefaultSurfaceBuilder(SAND_SAND_SAND, 1), false);


    void buildSurface(Random random, IChunk chunkIn, RockData data, int x, int z, int startHeight, float temperature, float rainfall, float noise);
}
