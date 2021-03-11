package net.dries007.tfc.world.feature.plant;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

/**
 * Modified from {@link BlockClusterFeatureConfig}
 */
public class TFCRandomPatchConfig implements IFeatureConfig
{
    public static final Codec<TFCRandomPatchConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(c -> c.stateProvider),
        BlockPlacer.CODEC.fieldOf("block_placer").forGetter(c -> c.blockPlacer),
        Codecs.LENIENT_BLOCKSTATE.listOf().fieldOf("whitelist").forGetter(c -> c.whitelist.stream().map(Block::defaultBlockState).collect(Collectors.toList())),
        Codecs.LENIENT_BLOCKSTATE.listOf().fieldOf("blacklist").forGetter(c -> ImmutableList.copyOf(c.blacklist)),
        Codec.INT.fieldOf("tries").orElse(128).forGetter(c -> c.tries),
        Codec.BOOL.fieldOf("use_density").orElse(false).forGetter(c -> c.useDensity),
        Codec.INT.fieldOf("xspread").orElse(7).forGetter(c -> c.xSpread),
        Codec.INT.fieldOf("yspread").orElse(3).forGetter(c -> c.ySpread),
        Codec.INT.fieldOf("zspread").orElse(7).forGetter(c -> c.zSpread),
        Codec.BOOL.fieldOf("can_replace_air").orElse(true).forGetter(c -> c.canReplaceAir),
        Codec.BOOL.fieldOf("can_replace_water").orElse(false).forGetter(c -> c.canReplaceWater),
        Codec.BOOL.fieldOf("can_replace_surface_water").orElse(false).forGetter(c -> c.canReplaceSurfaceWater),
        Codec.BOOL.fieldOf("project").orElse(true).forGetter(c -> c.project),
        Codec.BOOL.fieldOf("project_to_ocean_floor").orElse(false).forGetter(c -> c.projectToOceanFloor)
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

    protected TFCRandomPatchConfig(BlockStateProvider stateProvider, BlockPlacer blockPlacer, List<BlockState> whitelist, List<BlockState> blacklist, int tries, boolean useDensity, int xSpread, int ySpread, int zSpread, boolean canReplaceAir, boolean canReplaceWater, boolean canReplaceSurfaceWater, boolean project, boolean projectToOceanFloor)
    {
        this.stateProvider = stateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = whitelist.stream().map(AbstractBlock.AbstractBlockState::getBlock).collect(Collectors.toSet());
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
    }
}
