package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.fruittree.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class FruitTreeFeature extends Feature<BlockStateFeatureConfig>
{
    public FruitTreeFeature(Codec<BlockStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int i = 0; i < 12; i++)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, pos), rand.nextInt(10) - rand.nextInt(10), -1, rand.nextInt(10) - rand.nextInt(10));
            if (world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                boolean blocked = false;
                for (int j = 1; j <= 10; j++)
                {
                    mutablePos.move(Direction.UP);
                    if (!world.isEmptyBlock(mutablePos))
                    {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked)
                {
                    mutablePos.move(Direction.DOWN, 9);
                    int saplings = MathHelper.clamp(rand.nextInt(5) + 1, 2, 4);
                    BlockState branch = config.state.getBlock().defaultBlockState().setValue(GrowingFruitTreeBranchBlock.SAPLINGS, saplings);
                    setBlock(world, mutablePos, branch);
                    TickCounterTileEntity te = Helpers.getTileEntity(world, mutablePos, TickCounterTileEntity.class);
                    if (te != null)
                    {
                        te.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * 300);
                    }
                    world.getBlockTicks().scheduleTick(mutablePos, branch.getBlock(), 1);
                    return true;
                }
            }
        }
        return false;
    }
}
