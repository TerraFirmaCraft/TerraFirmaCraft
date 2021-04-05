package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.berrybush.WaterloggedBerryBushBlock;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class BerryBushFeature extends Feature<BlockStateFeatureConfig>
{
    private static final int REDUCTION_AMOUNT = -60 * ICalendar.TICKS_IN_DAY;

    public BerryBushFeature(Codec<BlockStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config)
    {
        BlockState bushState = config.state;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 15; i++)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, pos), rand.nextInt(10) - rand.nextInt(10), -1, rand.nextInt(10) - rand.nextInt(10));
            if (!world.isEmptyBlock(mutablePos)) continue;
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON)) continue;
            mutablePos.move(Direction.UP);

            if (bushState.hasProperty(WaterloggedBerryBushBlock.WILD))
                bushState = bushState.setValue(WaterloggedBerryBushBlock.WILD, true);
            world.setBlock(mutablePos, bushState.setValue(AbstractBerryBushBlock.LIFECYCLE, AbstractBerryBushBlock.Lifecycle.HEALTHY), 3);

            BerryBushTileEntity te = Helpers.getTileEntity(world, pos, BerryBushTileEntity.class);
            if (te != null)
            {
                te.reduceCounter(REDUCTION_AMOUNT);
            }
            return true;
        }
        return false;
    }
}
