package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.crop.WildDoubleCropBlock;
import net.dries007.tfc.world.Codecs;

public class TallWildCropFeature extends Feature<BlockStateConfiguration>
{
    public static final Codec<BlockStateConfiguration> CODEC = Codecs.blockStateConfigCodec(b -> b instanceof WildDoubleCropBlock, "Must be a WildDoubleCropBlock");

    public TallWildCropFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        ((WildDoubleCropBlock) context.config().state.getBlock()).placeTwoHalves(context.level(), context.origin(), 2);
        return true;
    }
}
