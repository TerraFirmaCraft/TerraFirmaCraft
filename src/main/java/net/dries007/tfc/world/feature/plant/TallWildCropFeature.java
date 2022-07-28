/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.crop.WildDoubleCropBlock;
import net.dries007.tfc.world.feature.BlockConfig;

public class TallWildCropFeature extends Feature<BlockConfig<WildDoubleCropBlock>>
{
    public static final Codec<BlockConfig<WildDoubleCropBlock>> CODEC = BlockConfig.codec(b -> b instanceof WildDoubleCropBlock t ? t : null, "Must be a " + WildDoubleCropBlock.class.getSimpleName());

    public TallWildCropFeature(Codec<BlockConfig<WildDoubleCropBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<WildDoubleCropBlock>> context)
    {
        context.config().block().placeTwoHalves(context.level(), context.origin(), 2);
        return true;
    }
}
