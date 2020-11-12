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
    public void breakSupportingBlock(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 6, 0));
        helper.assertAirAt(new BlockPos(0, 5, 0), "Spike should break when not supported above");
    }

    @IntegrationTest("top")
    public void breakTop(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 5, 0));
        helper.assertAirAt(new BlockPos(0, 4, 0), "Spike should break when base is broken");
    }

    @IntegrationTest("top")
    public void breakMiddle(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 4, 0));
        helper.assertBlockAt(new BlockPos(0, 5, 0), this::isSpike, "Base should still be supported");
        helper.assertAirAt(new BlockPos(0, 3, 0), "Spike should break when middle is broken");
    }

    @IntegrationTest("top")
    public void breakTip(IntegrationTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(0, 3, 0));
        helper.assertBlockAt(new BlockPos(0, 4, 0), this::isSpike, "Middle should still be supported");
    }

    private boolean isSpike(BlockState state)
    {
        return state.getBlock() instanceof RockSpikeBlock;
    }
}
