package net.dries007.tfc.test.blocks;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.Capabilities;

@GameTestHolder
public class LootTableTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(structure = "3x3_platform")
    public void testChestLootGenerates(GameTestHelper helper)
    {
        testLootGenerates(helper, TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.CHEST).get(), () -> helper.useBlock(new BlockPos(1, 1, 1)));
    }

    @MyTest(structure = "3x3_platform")
    public void testChestLootDoesNotGenerate(GameTestHelper helper)
    {
        testLootDoesNotGenerate(helper, TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.CHEST).get(), () -> {});
    }

    @MyTest(structure = "3x3_platform")
    public void testChestLootGeneratesWithCapability(GameTestHelper helper)
    {
        testLootGenerates(helper, TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.CHEST).get(), () -> accessItemHandler(helper, new BlockPos(1, 1, 1)));
    }

    @MyTest(structure = "3x3_platform")
    public void testVesselLootGeneratesWithCapability(GameTestHelper helper)
    {
        testLootGenerates(helper, TFCBlocks.LARGE_VESSEL.get(), () -> accessItemHandler(helper, new BlockPos(1, 1, 1)));
    }

    @MyTest(structure = "3x3_platform")
    public void testVesselLootGenerates(GameTestHelper helper)
    {
        testLootGenerates(helper, TFCBlocks.LARGE_VESSEL.get(), () -> helper.useBlock(new BlockPos(1, 1, 1)));
    }

    @MyTest(structure = "3x3_platform")
    public void testVesselLootDoesNotGenerate(GameTestHelper helper)
    {
        testLootDoesNotGenerate(helper, TFCBlocks.LARGE_VESSEL.get(), () -> {});
    }

    private void accessItemHandler(GameTestHelper helper, BlockPos pos)
    {
        final BlockEntity be = helper.getBlockEntity(pos);
        if (be == null || !be.getCapability(Capabilities.ITEM).isPresent())
        {
            helper.fail("Block entity missing item handler capability");
        }
    }

    private void testLootGenerates(GameTestHelper helper, Block block, Runnable testAction)
    {
        final BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, block);
        setLootTable(helper, pos);
        testAction.run();
        if (isTagGenerated(helper, pos, block instanceof ChestBlock ? "Items" : "inventory"))
        {
            helper.succeed();
        }
        else
        {
            helper.fail("Expected loot to generate, but it was not generated.");
        }
    }

    private void testLootDoesNotGenerate(GameTestHelper helper, Block block, Runnable testAction)
    {
        final BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, block);
        setLootTable(helper, pos);
        testAction.run();
        if (isTagGenerated(helper, pos, block instanceof ChestBlock ? "Items" : "inventory"))
        {
            helper.fail("Expected no loot to generate, but it was generated.");
        }
        else
        {
            helper.succeed();
        }
    }

    private boolean isTagGenerated(GameTestHelper helper, BlockPos pos, String key)
    {
        final BlockEntity be = helper.getBlockEntity(pos);
        if (be != null)
        {
            final CompoundTag tag = be.saveWithoutMetadata();
            return tag.contains(key);
        }
        else
        {
            helper.fail("No block entity found", pos);
            return false;
        }
    }

    private void setLootTable(GameTestHelper helper, BlockPos pos)
    {
        final BlockEntity be = helper.getBlockEntity(pos);
        if (be != null)
        {
            final CompoundTag tag = be.saveWithoutMetadata();
            tag.putString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, "minecraft:chests/simple_dungeon");
            be.load(tag);
            be.setChanged();
        }
        else
        {
            helper.fail("No block entity found", pos);
        }
    }

}
