/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.surfacebuilders;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

public class TFCSurfaceBuilders
{
    public static final ConfiguredSurfaceBuilder<?> DEFAULT_THIN = new ConfiguredSurfaceBuilder<>(new TFCDefaultSurfaceBuilder(), new TFCSurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), 1));
    public static final ConfiguredSurfaceBuilder<?> DEFAULT_NORMAL = new ConfiguredSurfaceBuilder<>(new TFCDefaultSurfaceBuilder(), new TFCSurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), 3));
    public static final ConfiguredSurfaceBuilder<?> DEFAULT_THICK = new ConfiguredSurfaceBuilder<>(new TFCDefaultSurfaceBuilder(), new TFCSurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), 6));

    public static final ConfiguredSurfaceBuilder<?> SHORE = new ConfiguredSurfaceBuilder<>(new TFCDefaultSurfaceBuilder(), new TFCSurfaceBuilderConfig(Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState(), 4));

    public static final ConfiguredSurfaceBuilder<?> RIVER = new ConfiguredSurfaceBuilder<>(new TFCDefaultSurfaceBuilder(), new TFCSurfaceBuilderConfig(Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), 6));
}
