/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.rock;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RawRockVariantBlock extends RockVariantBlock// implements ICollapsableBlock
{
    public RawRockVariantBlock(Properties properties)
    {
        super(properties);
    }

    //@Override
    //public BlockRockVariantFallable getFallingVariant()
    //{
    //    return (BlockRockVariantFallable) BlockRockVariant.get(rock, Rock.Type.COBBLE);
    //}

    //@Override
    //public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    //{
    //    // Trigger the collapsing mechanic!
    //    checkCollapsingArea(worldIn, pos);
    //}

    //@SuppressWarnings("deprecation")
    //@Override
    //public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    //{
    //    super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    //    for (EnumFacing face : EnumFacing.values())
    //    {
    //        if (!worldIn.getBlockState(pos.offset(face)).getBlock().isReplaceable(worldIn, pos.offset(face)))
    //        {
    //            return;
    //        }
    //    }
//
    //    // No supporting solid blocks, so pop off as an item
    //    worldIn.setBlockToAir(pos);
    //    Helpers.spawnItemStack(worldIn, pos, new ItemStack(state.getBlock(), 1));
    //}

    //@Override
    //public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    //{
    //    ItemStack stack = playerIn.getHeldItem(hand);
    //    if (OreDictionaryHelper.doesStackMatchOre(stack, "hammer"))
    //    {
    //        if (!worldIn.isRemote)
    //        {
    //            // Create a rock anvil
    //            BlockStoneAnvil block = BlockStoneAnvil.get(this.rock);
    //            if (block != null)
    //            {
    //                worldIn.setBlockState(pos, block.getDefaultState());
    //                TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.ANVIL);
    //            }
    //        }
    //        return true;
    //    }
    //    return false;
    //}
}