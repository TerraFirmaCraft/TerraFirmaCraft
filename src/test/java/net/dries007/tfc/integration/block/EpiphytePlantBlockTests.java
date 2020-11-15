package net.dries007.tfc.integration.block;

import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.mcjunitlib.framework.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.EpiphytePlantBlock;
import net.dries007.tfc.common.blocks.plant.Plant;

@IntegrationTestClass("block/epiphyte_plant")
public class EpiphytePlantBlockTests
{
    @IntegrationTest("placed")
    public void testBreakSupportingBlock(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(1, 1, 1));
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            helper.assertAirAt(new BlockPos(1, 1, 1).relative(direction), "Expected epiphyte plants to break when supporting block broke");
        }
    }

    @IntegrationTest("placed_wide")
    public void testPlacementReplaces(IntegrationTestHelper helper)
    {
        BlockPos pos = new BlockPos(2, 1, 2);
        Block stone = Blocks.STONE;
        IntegrationTestHelper.ScheduleHelper scheduler = helper.scheduler();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            scheduler = scheduler.thenRun(10, () -> helper.placeBlock(pos.relative(direction), direction, stone));
            helper.assertBlockAt(pos.relative(direction), stone, "Expected epiphyte plants to be replaced by stone");
        }
    }

    @IntegrationTest("placed_wide")
    public void testBreakNearbyBlocks(IntegrationTestHelper helper)
    {
        BlockPos pos = new BlockPos(2, 1, 2);
        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            helper.runAfter(10, () -> helper.setBlockState(pos.relative(direction, 2), log))
                .thenRun(10, () -> helper.destroyBlock(pos.relative(direction, 2)))
                .thenRun(10, () -> helper.setBlockState(pos.relative(direction).relative(direction.getClockWise()), log))
                .thenRun(10, () -> helper.destroyBlock(pos.relative(direction).relative(direction.getClockWise())))
                .thenRun(10, () -> {});
            helper.assertBlockAt(pos.relative(direction), this::isEpiphyte, "Expected epiphyte to not break");
        }
    }

    @IntegrationTestFactory("empty")
    public Stream<DynamicIntegrationTest> testPlaceOnSupportingBlock()
    {
        return TFCBlocks.PLANTS.entrySet().stream()
            .filter(entry -> entry.getKey().getType() == Plant.BlockType.EPIPHYTE)
            .map(entry -> DynamicIntegrationTest.create(entry.getKey().name().toLowerCase(), helper -> {
                IntegrationTestHelper.ScheduleHelper scheduler = helper.scheduler();
                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    scheduler = scheduler.thenRun(10, () -> helper.placeBlock(new BlockPos(1, 1, 1), direction, entry.getValue().get()));
                    helper.assertBlockAt(new BlockPos(1, 1, 1).relative(direction), entry.getValue().get(), "Expected epiphyte to be placed");
                }
                scheduler.thenRun(10, () -> {});
            }));
    }

    private boolean isEpiphyte(BlockState state)
    {
        return state.getBlock() instanceof EpiphytePlantBlock;
    }
}
