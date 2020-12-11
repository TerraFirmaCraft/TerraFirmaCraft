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

public class ClimateTargetDecorator extends Placement<ClimateTargetConfig>
{
    public ClimateTargetDecorator(Codec<ClimateTargetConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, ClimateTargetConfig config, BlockPos pos)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(((WorldDecoratingHelperAccessor) helper).accessor$getGenerator());
        final ChunkData data = provider.get(pos, ChunkData.Status.CLIMATE);
        if (random.nextFloat() < config.getChance(data.getAverageTemp(pos), data.getRainfall(pos), data.getForestType()))
        {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
