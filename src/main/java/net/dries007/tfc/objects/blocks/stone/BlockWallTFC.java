/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

//todo: actually by-pass the variant? or would it be worth adding a mossy texture for nice looking walls
public class BlockWallTFC extends BlockWall
{
    private static final Map<Rock, EnumMap<Rock.Type, BlockWallTFC>> TABLE = new HashMap<>();

    public static BlockWallTFC get(Rock rock, Rock.Type type)
    {
        return TABLE.get(rock).get(type);
    }

    public final BlockRockVariant parent;

    public BlockWallTFC(BlockRockVariant modelBlock)
    {
        super(modelBlock);

        if (!TABLE.containsKey(modelBlock.rock))
            TABLE.put(modelBlock.rock, new EnumMap<>(Rock.Type.class));
        TABLE.get(modelBlock.rock).put(modelBlock.type, this);

        parent = modelBlock;
        OreDictionaryHelper.register(this, "wall");
        OreDictionaryHelper.registerRockType(this, modelBlock.type, "wall");
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
}
