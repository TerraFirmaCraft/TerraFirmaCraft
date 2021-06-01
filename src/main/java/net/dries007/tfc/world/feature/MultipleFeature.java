package net.dries007.tfc.world.feature;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class MultipleFeature extends Feature<MultipleConfig>
{
	public MultipleFeature(Codec<MultipleConfig> codec)
	{
		super(codec);
	}

	@Override
	public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, MultipleConfig config)
	{
		boolean result = false;
		for (Supplier<ConfiguredFeature<?, ?>> feature : config.features)
		{
			result |= feature.get().place(reader, generator, rand, pos);
		}
		return result;
	}
}
