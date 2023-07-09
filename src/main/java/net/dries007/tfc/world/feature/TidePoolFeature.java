package net.dries007.tfc.world.feature;

import java.util.function.Predicate;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.settings.RockSettings;

public class TidePoolFeature extends Feature<NoneFeatureConfiguration>
{
    public TidePoolFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        boolean placedAny = false;
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final BlockPos origin = context.origin();
        final boolean hasRim = random.nextDouble() < 0.9D;
        final int rimOffsetX = hasRim ? Mth.nextInt(random, 0, 2) : 0;
        final int rimOffsetZ = hasRim ? Mth.nextInt(random, 0, 2) : 0;
        final boolean willPlaceRim = hasRim && rimOffsetX != 0 && rimOffsetZ != 0;
        final int xSize = Mth.nextInt(random, 3, 7);
        final int zSize = Mth.nextInt(random, 3, 7);
        final int maxLength = Math.max(xSize, zSize);

        final ChunkDataProvider provider = ChunkDataProvider.get(context.chunkGenerator());
        final RockSettings rock = provider.get(context.level(), origin).getRockData().getRock(origin);
        final BlockState cobble = rock.cobble().defaultBlockState();
        final BlockState raw = rock.raw().defaultBlockState();
        final BlockState water = TFCBlocks.SALT_WATER.get().defaultBlockState();
        final Predicate<BlockState> test = state -> state.getBlock() == cobble.getBlock() || water.getBlock() == state.getBlock();

        for (BlockPos pos : BlockPos.withinManhattan(origin, xSize, 0, zSize))
        {
            if (pos.distManhattan(origin) > maxLength)
            {
                break;
            }

            if (isClear(level, pos, test))
            {
                if (willPlaceRim)
                {
                    placedAny = true;
                    this.setBlock(level, pos, raw);
                }

                BlockPos offsetPos = pos.offset(rimOffsetX, 0, rimOffsetZ);
                if (isClear(level, offsetPos, test))
                {
                    placedAny = true;
                    this.setBlock(level, offsetPos, random.nextBoolean() ? water : cobble);
                }
            }
        }

        return placedAny;
    }

    private static boolean isClear(LevelAccessor level, BlockPos pos, Predicate<BlockState> contentsTest)
    {
        if (contentsTest.test(level.getBlockState(pos)))
        {
            return false;
        }
        else
        {
            for (Direction direction : Helpers.DIRECTIONS)
            {
                final boolean airAbove = level.getBlockState(pos.relative(direction)).isAir();
                if (airAbove && direction != Direction.UP || !airAbove && direction == Direction.UP)
                {
                    return false;
                }
            }
            return true;
        }
    }
}
