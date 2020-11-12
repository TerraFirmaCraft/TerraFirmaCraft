package net.dries007.tfc.integration;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.mcjunitlib.framework.IntegrationTest;
import com.alcatrazescapee.mcjunitlib.framework.IntegrationTestClass;
import com.alcatrazescapee.mcjunitlib.framework.IntegrationTestHelper;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;

@IntegrationTestClass("rock_spike_tests")
public class RockSpikeTests
{
    @IntegrationTest("top")
    public void topBreakAbove(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 6, 0));
        helper.assertAirAt(new BlockPos(0, 5, 0), "Spike should break when not supported above");
    }

    @IntegrationTest("top")
    public void topBreakBase(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 5, 0));
        helper.assertAirAt(new BlockPos(0, 4, 0), "Spike should break when base is broken");
    }

    @IntegrationTest("top")
    public void topBreakMiddle(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 4, 0));
        helper.assertBlockAt(new BlockPos(0, 5, 0), this::isSpike, "Base should still be supported");
        helper.assertAirAt(new BlockPos(0, 3, 0), "Spike should break when middle is broken");
    }

    @IntegrationTest("top")
    public void topBreakTip(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 3, 0));
        helper.assertBlockAt(new BlockPos(0, 4, 0), this::isSpike, "Middle should still be supported");
    }

    @IntegrationTest("bottom")
    public void bottomBreakBelow(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 0, 0));
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should break when not supported below");
        helper.assertAirAt(new BlockPos(0, 0, 0), "Spike should shatter on impact");
    }

    @IntegrationTest("bottom")
    public void bottomBreakBase(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 1, 0));
        helper.assertAirAt(new BlockPos(0, 2, 0), "Spike should break when base is broken");
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should shatter on impact");
    }

    @IntegrationTest("bottom")
    public void bottomBreakMiddle(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 2, 0));
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should break when middle is broken");
    }

    @IntegrationTest("bottom")
    public void bottomBreakTip(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 3, 0));
        helper.assertBlockAt(new BlockPos(0, 2, 0), this::isSpike, "Middle should still be supported");
    }

    @IntegrationTest("column")
    public void columnBreakBelow(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 0, 0));
        helper.assertAirAt(new BlockPos(0, 0, 0), "Spike should shatter on impact");
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should break when supported by smaller spike above");
        helper.assertBlockAt(new BlockPos(0, 3, 0), this::isSpike, "Tip should still be supported from above");
    }

    @IntegrationTest("column")
    public void columnBreakLowerBase(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 1, 0));
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should shatter on impact");
        helper.assertAirAt(new BlockPos(0, 2, 0), "Spike should break when supported by smaller spike above");
        helper.assertBlockAt(new BlockPos(0, 3, 0), this::isSpike, "Tip should still be supported from above");
    }

    @IntegrationTest("column")
    public void columnBreakLowerMiddle(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 2, 0));
        helper.assertBlockAt(new BlockPos(0, 1, 0), this::isSpike, "Bottom should still be supported");
        helper.assertBlockAt(new BlockPos(0, 3, 0), this::isSpike, "Tip should still be supported from above");
    }

    @IntegrationTest("column")
    public void columnBreakTip(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 3, 0));
        helper.assertBlockAt(new BlockPos(0, 2, 0), this::isSpike, "Upper spike should still be supported");
        helper.assertBlockAt(new BlockPos(0, 4, 0), this::isSpike, "Lower spike should still be supported");
    }

    @IntegrationTest("column")
    public void columnBreakUpperMiddle(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 4, 0));
        helper.assertBlockAt(new BlockPos(0, 3, 0), this::isSpike, "Upper spike should still be supported");
        helper.assertBlockAt(new BlockPos(0, 5, 0), this::isSpike, "Lower spike should still be supported");
    }

    @IntegrationTest("column")
    public void columnBreakUpperBase(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 5, 0));
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should collapse from larger part on top");
    }

    @IntegrationTest("column")
    public void columnBreakAbove(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 6, 0));
        helper.assertAirAt(new BlockPos(0, 1, 0), "Spike should collapse from larger part on top");
    }

    private boolean isSpike(BlockState state)
    {
        return state.getBlock() instanceof RockSpikeBlock;
    }
}
