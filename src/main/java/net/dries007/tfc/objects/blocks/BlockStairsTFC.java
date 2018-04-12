package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

import java.util.EnumMap;

public class BlockStairsTFC extends BlockStairs
{
    private static final InsertOnlyEnumTable<Rock, Rock.Type, BlockStairsTFC> ROCK_TABLE = new InsertOnlyEnumTable<>(Rock.class, Rock.Type.class);
    private static final EnumMap<Wood, BlockStairsTFC> WOOD_MAP = new EnumMap<>(Wood.class);

    public static BlockStairsTFC get(Rock rock, Rock.Type type)
    {
        return ROCK_TABLE.get(rock, type);
    }

    public static BlockStairsTFC get(Wood wood)
    {
        return WOOD_MAP.get(wood);
    }

    public BlockStairsTFC(Rock rock, Rock.Type type)
    {
        super(BlockRockVariant.get(rock, type).getDefaultState());
        ROCK_TABLE.put(rock, type, this);
        Block c = BlockRockVariant.get(rock, type);
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
    }

    public BlockStairsTFC(Wood wood)
    {
        super(BlockPlanksTFC.get(wood).getDefaultState());
        if (WOOD_MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        Block c = BlockPlanksTFC.get(wood);
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
    }
}
