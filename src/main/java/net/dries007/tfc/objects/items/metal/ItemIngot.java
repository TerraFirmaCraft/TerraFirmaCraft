/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEIngotPile;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class ItemIngot extends ItemMetal
{
    public ItemIngot(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking() && ConfigTFC.General.OVERRIDES.enableIngotPiles)
        {
            ItemStack stack = player.getHeldItem(hand);
            ItemIngot item = (ItemIngot) stack.getItem();
            // Placing an ingot pile erases data, and since I really don't want to rewrite all of this, let's be sufficient with this for now
            // todo: rewrite ingot piles. They should store inventory, allow multiple ingots per pile, and be placed on event handler.
            if (!ItemStack.areItemStacksEqual(new ItemStack(item, stack.getCount()), stack))
            {
                return EnumActionResult.FAIL;
            }
            if (worldIn.getBlockState(pos).getBlock() != BlocksTFC.INGOT_PILE)
            {
                if (facing == EnumFacing.UP && worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP))
                {
                    BlockPos up = pos.up();
                    if (worldIn.mayPlace(BlocksTFC.INGOT_PILE, up, false, EnumFacing.UP, null))
                    {
                        if (!worldIn.isRemote)
                        {
                            worldIn.setBlockState(up, BlocksTFC.INGOT_PILE.getDefaultState());
                            TEIngotPile te = Helpers.getTE(worldIn, up, TEIngotPile.class);
                            if (te != null)
                            {
                                te.setMetal(item.metal);
                                te.setCount(1);
                            }
                            worldIn.playSound(null, up, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                            stack.shrink(1);
                            player.setHeldItem(hand, stack);
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            else
            {
                // Place an ingot pile on top of the existing one
                BlockPos posTop = pos.down();
                IBlockState stateTop;
                do
                {
                    posTop = posTop.up();
                    stateTop = worldIn.getBlockState(posTop);
                    if (stateTop.getBlock() == BlocksTFC.INGOT_PILE)
                    {
                        TEIngotPile te = Helpers.getTE(worldIn, posTop, TEIngotPile.class);
                        if (te != null && te.getCount() < 64 && (te.getMetal() == item.metal) && worldIn.checkNoEntityCollision(new AxisAlignedBB(0, 0, 0, 1, (1 + te.getCount()) / 64d, 1).offset(posTop)))
                        {
                            te.setCount(te.getCount() + 1);
                            worldIn.playSound(null, posTop, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                            stack.shrink(1);
                            player.setHeldItem(hand, stack);
                            return EnumActionResult.SUCCESS;
                        }
                    }
                    else if (stateTop.getBlock().isReplaceable(worldIn, posTop) && worldIn.mayPlace(BlocksTFC.INGOT_PILE, posTop, false, EnumFacing.UP, null) && worldIn.getBlockState(posTop.down()).isSideSolid(worldIn, posTop.down(), EnumFacing.UP))
                    {
                        worldIn.setBlockState(posTop, BlocksTFC.INGOT_PILE.getDefaultState());
                        TEIngotPile te = Helpers.getTE(worldIn, posTop, TEIngotPile.class);
                        if (te != null)
                        {
                            te.setMetal(item.metal);
                            te.setCount(1);
                        }
                        worldIn.playSound(null, posTop, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                        stack.shrink(1);
                        player.setHeldItem(hand, stack);
                        return EnumActionResult.SUCCESS;
                    }
                    else
                    {
                        return EnumActionResult.FAIL;
                    }

                } while (posTop.getY() <= 256);
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }
}
