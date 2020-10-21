package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.feature.WorldDecoratingHelperAccessor;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class ClimateDecorator extends Placement<ClimateConfig>
{
    public ClimateDecorator(Codec<ClimateConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, ClimateConfig config, BlockPos pos)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(((WorldDecoratingHelperAccessor) helper).accessor$getGenerator());
        final ChunkData data = provider.get(pos, ChunkData.Status.CLIMATE);
        if (config.isValid(data.getAverageTemp(pos), data.getRainfall(pos)))
        {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
