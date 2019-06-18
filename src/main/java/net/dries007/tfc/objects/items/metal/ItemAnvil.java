/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nullable;
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
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;

import static net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC.AXIS;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemAnvil extends ItemMetal implements IPlaceableItem
{
    public ItemAnvil(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
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
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, @Nullable Vec3d hitVec)
    {
        if (!(stack.getItem() instanceof ItemAnvil))
        {
            return false;
        }
        if (facing != null)
        {
            IBlockState state = world.getBlockState(pos.offset(facing));
            if (state.getBlock().isReplaceable(world, pos.offset(facing)))
            {
                if (!world.isRemote)
                {
                    ItemAnvil anvil = (ItemAnvil) stack.getItem();
                    world.setBlockState(pos.offset(facing), BlockAnvilTFC.get(anvil.metal).getDefaultState().withProperty(AXIS, player.getHorizontalFacing()));

                    world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return true;
            }
        }
        return false;
    }
}
