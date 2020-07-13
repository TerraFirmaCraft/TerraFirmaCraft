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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;

import static net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC.AXIS;

@ParametersAreNonnullByDefault
public class ItemAnvil extends ItemMetal
{
    public ItemAnvil(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE; // Can't be stored and causes overburden
    }

    @Override
    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (facing != null)
        {
            ItemStack stack = player.getHeldItem(hand);
            BlockPos placedPos = pos.offset(facing);
            BlockPos supportPos = placedPos.down();
            IBlockState state = worldIn.getBlockState(placedPos);
            IBlockState stateSupport = worldIn.getBlockState(supportPos);
            if (state.getBlock().isReplaceable(worldIn, placedPos) &&
                stateSupport.isSideSolid(worldIn, supportPos, EnumFacing.UP)) //forge says to do it this way, IBlockProperties#isTopSolid
            {
                if (!worldIn.isRemote)
                {
                    ItemAnvil anvil = (ItemAnvil) stack.getItem();
                    worldIn.setBlockState(placedPos, BlockAnvilTFC.get(anvil.metal).getDefaultState().withProperty(AXIS, player.getHorizontalFacing()));
                    worldIn.playSound(null, placedPos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    stack.shrink(1);
                    player.setHeldItem(hand, stack);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }
}
