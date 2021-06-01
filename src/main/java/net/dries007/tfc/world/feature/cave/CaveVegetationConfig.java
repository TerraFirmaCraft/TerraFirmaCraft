/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class CaveVegetationConfig implements IFeatureConfig
{
    @SuppressWarnings("deprecation")
    public static final MapCodec<CaveVegetationConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.mapKeyListCodec(Codec.mapPair(
            Registry.BLOCK.listOf().fieldOf("stone"),
            Codecs.weightedCodec(Codecs.LENIENT_BLOCKSTATE, "block").fieldOf("ore")
        ).codec()).fieldOf("blocks").forGetter(c -> c.states)
    ).apply(instance, CaveVegetationConfig::new));

    public static final Codec<CaveVegetationConfig> CODEC = MAP_CODEC.codec();

    private final Map<Block, IWeighted<BlockState>> states;

    public CaveVegetationConfig(Map<Block, IWeighted<BlockState>> states)
	{
		this.states = states;
	}

    @Nullable
    public BlockState getStateToGenerate(BlockState stoneState, Random random)
    {
        final IWeighted<BlockState> weighted = states.get(stoneState.getBlock());
        if (weighted != null)
        {
            return weighted.get(random);
        }
        return null;
    }
}
