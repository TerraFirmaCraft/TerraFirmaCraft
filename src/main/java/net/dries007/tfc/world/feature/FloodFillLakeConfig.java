package net.dries007.tfc.world.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class FloodFillLakeConfig implements IFeatureConfig
{
    public static final Codec<FloodFillLakeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.fieldOf("state").forGetter(FloodFillLakeConfig::getState),
        Codec.BOOL.optionalFieldOf("overfill", false).forGetter(FloodFillLakeConfig::shouldOverfill)
    ).apply(instance, FloodFillLakeConfig::new));

    private final BlockState state;
    private final boolean overfill;

    public FloodFillLakeConfig(BlockState state, boolean overfill)
    {
        this.state = state;
        this.overfill = overfill;
    }

    public boolean shouldOverfill()
    {
        return overfill;
    }

    public BlockState getState()
    {
        return state;
    }
}
