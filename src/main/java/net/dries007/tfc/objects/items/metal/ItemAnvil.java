/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;
import net.dries007.tfc.util.IPlacableItem;

import static net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC.AXIS;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemAnvil extends ItemMetal implements IPlacableItem
{
    public ItemAnvil(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing facing, Vec3d hitVec)
    {
        if (!(stack.getItem() instanceof ItemAnvil))
        {
            return false;
        }
        IBlockState state = world.getBlockState(pos.offset(facing));
        if (state.getBlock().isReplaceable(world, pos.offset(facing)))
        {
            if (!world.isRemote)
            {
                ItemAnvil anvil = (ItemAnvil) stack.getItem();
                world.setBlockState(pos.offset(facing), BlockAnvilTFC.get(anvil.metal).getDefaultState().withProperty(AXIS, player.getHorizontalFacing().getAxis() == EnumFacing.Axis.X));

                world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }
}
