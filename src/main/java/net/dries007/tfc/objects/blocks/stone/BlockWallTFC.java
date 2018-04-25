package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

//todo: actually by-pass the variant? or would it be worth adding a mossy texture for nice looking walls
public class BlockWallTFC extends BlockWall
{
    private static final InsertOnlyEnumTable<Rock, Rock.Type, BlockWallTFC> TABLE = new InsertOnlyEnumTable<>(Rock.class, Rock.Type.class);

    public static BlockWallTFC get(Rock rock, Rock.Type type)
    {
        return TABLE.get(rock, type);
    }

    public final BlockRockVariant parent;

    public BlockWallTFC(BlockRockVariant modelBlock)
    {
        super(modelBlock);
        TABLE.put(modelBlock.rock, modelBlock.type, this);
        parent = modelBlock;
        OreDictionaryHelper.register(this, "wall");
        OreDictionaryHelper.registerRockType(this, modelBlock.type, modelBlock.rock, "wall");
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
