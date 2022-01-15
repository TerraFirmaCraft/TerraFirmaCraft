package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.dries007.tfc.common.blocks.rock.Rock;

public enum OreDeposit
{
    NATIVE_COPPER,
    NATIVE_SILVER,
    NATIVE_GOLD,
    CASSITERITE;

    private static final OreDeposit[] VALUES = values();

    private static final Object2IntMap<Block> ROCK_CACHE = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<Block> ORE_CACHE = new Object2IntOpenHashMap<>();

    public static int oreValue(BlockState state)
    {
        return ORE_CACHE.getInt(state.getBlock());
    }

    public static int rockValue(BlockState state)
    {
        return ROCK_CACHE.getInt(state.getBlock());
    }

    public static void computeCache()
    {
        for (Rock rock : Rock.VALUES)
        {
            for (OreDeposit ore : OreDeposit.VALUES)
            {
                ORE_CACHE.put(TFCBlocks.ORE_DEPOSITS.get(rock).get(ore).get(), ore.ordinal());
                ROCK_CACHE.put(TFCBlocks.ORE_DEPOSITS.get(rock).get(ore).get(), rock.ordinal());
            }
        }
    }
}
