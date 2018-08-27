/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.RockType;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockPlanksTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockStairsTFC extends BlockStairs
{
    private static final Map<Rock, EnumMap<RockType, BlockStairsTFC>> ROCK_TABLE = new HashMap<>();
    private static final Map<Tree, BlockStairsTFC> WOOD_MAP = new HashMap<>();

    public static BlockStairsTFC get(Rock rock, RockType type)
    {
        return ROCK_TABLE.get(rock).get(type);
    }

    public static BlockStairsTFC get(Tree wood)
    {
        return WOOD_MAP.get(wood);
    }

    public BlockStairsTFC(Rock rock, RockType type)
    {
        super(BlockRockVariant.get(rock, type).getDefaultState());

        if (!ROCK_TABLE.containsKey(rock))
            ROCK_TABLE.put(rock, new EnumMap<>(RockType.class));
        ROCK_TABLE.get(rock).put(type, this);

        Block c = BlockRockVariant.get(rock, type);
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
        OreDictionaryHelper.register(this, "stair");
        OreDictionaryHelper.registerRockType(this, type, rock, "stair");
    }

    public BlockStairsTFC(Tree wood)
    {
        super(BlockPlanksTFC.get(wood).getDefaultState());
        if (WOOD_MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        Block c = BlockPlanksTFC.get(wood);
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
        OreDictionaryHelper.register(this, "stair");
        OreDictionaryHelper.register(this, "stair", "wood");
        OreDictionaryHelper.register(this, "stair", "wood", wood);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
