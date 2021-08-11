/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

/**
 * Modified from {@link BlockClusterFeatureConfig}
 */
public class TFCRandomPatchConfig implements FeatureConfiguration
{
    public static final Codec<TFCRandomPatchConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(c -> c.stateProvider),
        BlockPlacer.CODEC.fieldOf("block_placer").forGetter(c -> c.blockPlacer),
        Codecs.LENIENT_BLOCKSTATE.listOf().fieldOf("whitelist").forGetter(c -> c.whitelist.stream().map(Block::defaultBlockState).collect(Collectors.toList())),
        Codecs.LENIENT_BLOCKSTATE.listOf().fieldOf("blacklist").forGetter(c -> ImmutableList.copyOf(c.blacklist)),
        Codec.INT.optionalFieldOf("tries", 64).forGetter(c -> c.tries),
        Codec.BOOL.optionalFieldOf("use_density", false).forGetter(c -> c.useDensity),
        Codec.INT.optionalFieldOf("xspread", 7).forGetter(c -> c.xSpread),
        Codec.INT.optionalFieldOf("yspread", 3).forGetter(c -> c.ySpread),
        Codec.INT.optionalFieldOf("zspread", 7).forGetter(c -> c.zSpread),
        Codec.BOOL.optionalFieldOf("can_replace_air", true).forGetter(c -> c.canReplaceAir),
        Codec.BOOL.optionalFieldOf("can_replace_water", false).forGetter(c -> c.canReplaceWater),
        Codec.BOOL.optionalFieldOf("can_replace_surface_water", false).forGetter(c -> c.canReplaceSurfaceWater),
        Codec.BOOL.optionalFieldOf("project", true).forGetter(c -> c.project),
        Codec.BOOL.optionalFieldOf("project_to_ocean_floor", false).forGetter(c -> c.projectToOceanFloor),
        Codec.BOOL.optionalFieldOf("project_each_location", true).forGetter(c -> c.projectEachLocation),
        Codec.BOOL.optionalFieldOf("only_underground", false).forGetter(c -> c.onlyUnderground)
    ).apply(instance, TFCRandomPatchConfig::new));

    public final BlockStateProvider stateProvider;
    public final BlockPlacer blockPlacer;
    public final Set<Block> whitelist;
    public final Set<BlockState> blacklist;
    public final int tries;
    public final boolean useDensity;
    public final int xSpread;
    public final int ySpread;
    public final int zSpread;
    public final boolean canReplaceAir;
    public final boolean canReplaceWater;
    public final boolean canReplaceSurfaceWater;
    public final boolean project;
    public final boolean projectToOceanFloor;
    public final boolean projectEachLocation;
    public final boolean onlyUnderground;

    public TFCRandomPatchConfig(BlockStateProvider stateProvider, BlockPlacer blockPlacer, List<BlockState> whitelist, List<BlockState> blacklist, int tries, boolean useDensity, int xSpread, int ySpread, int zSpread, boolean canReplaceAir, boolean canReplaceWater, boolean canReplaceSurfaceWater, boolean project, boolean projectToOceanFloor, boolean projectEachLocation, boolean onlyUnderground)
    {
        this.stateProvider = stateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = whitelist.stream().map(BlockBehaviour.BlockStateBase::getBlock).collect(Collectors.toSet());
        this.blacklist = ImmutableSet.copyOf(blacklist);
        this.tries = tries;
        this.useDensity = useDensity;
        this.xSpread = xSpread;
        this.ySpread = ySpread;
        this.zSpread = zSpread;
        this.canReplaceAir = canReplaceAir;
        this.canReplaceWater = canReplaceWater;
        this.canReplaceSurfaceWater = canReplaceSurfaceWater;
        this.project = project;
        this.projectToOceanFloor = projectToOceanFloor;
        this.projectEachLocation = projectEachLocation;
        this.onlyUnderground = onlyUnderground;
    }
}
