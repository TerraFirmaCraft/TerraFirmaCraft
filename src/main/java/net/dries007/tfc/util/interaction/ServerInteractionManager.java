/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.interaction;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Util class for handling right click actions with more precision than {@link net.minecraftforge.event.entity.player.PlayerInteractEvent} gives us
 *
 * @see net.minecraft.server.management.PlayerInteractionManager
 */
@ParametersAreNonnullByDefault
final class ServerInteractionManager
{
    /**
     * @see net.minecraft.server.management.PlayerInteractionManager#processRightClickBlock(EntityPlayer, World, ItemStack, EnumHand, BlockPos, EnumFacing, float, float, float)
     */
    @Nonnull
    static EnumActionResult processRightClickBlock(PlayerInteractEvent.RightClickBlock event, IRightClickBlockAction itemUseAction)
    {
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        EnumFacing facing = event.getFace();
        if (facing == null)
        {
            // Should never happen
            facing = EnumFacing.UP;
        }
        EnumHand hand = event.getHand();
        ItemStack stack = event.getItemStack();

        float hitX = 0, hitY = 0, hitZ = 0;
        Vec3d hitVec = event.getHitVec();
        if (hitVec != null)
        {
            hitX = ((float) (hitVec.x - pos.getX()));
            hitY = ((float) (hitVec.y - pos.getY()));
            hitZ = ((float) (hitVec.z - pos.getZ()));
        }

        EnumActionResult result = EnumActionResult.PASS;
        if (event.getUseItem() != Event.Result.DENY)
        {
            result = stack.onItemUseFirst(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            if (result != EnumActionResult.PASS) return result;
        }

        boolean bypass = player.getHeldItemMainhand().doesSneakBypassUse(worldIn, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(worldIn, pos, player);

        if (!player.isSneaking() || bypass || event.getUseBlock() == Event.Result.ALLOW)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            if (event.getUseBlock() != Event.Result.DENY)
                if (iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, facing, hitX, hitY, hitZ))
                {
                    result = EnumActionResult.SUCCESS;
                }
        }

        if (stack.isEmpty())
        {
            return EnumActionResult.PASS;
        }
        else if (player.getCooldownTracker().hasCooldown(stack.getItem()))
        {
            return EnumActionResult.PASS;
        }
        else
        {
            if (stack.getItem() instanceof ItemBlock && !player.canUseCommandBlock())
            {
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                {
                    return EnumActionResult.FAIL;
                }
            }

            if (player.interactionManager.isCreative())
            {
                int j = stack.getMetadata();
                int i = stack.getCount();
                if (result != EnumActionResult.SUCCESS && event.getUseItem() != Event.Result.DENY
                    || result == EnumActionResult.SUCCESS && event.getUseItem() == Event.Result.ALLOW)
                {
                    // Fire the alternative item use action
                    EnumActionResult enumactionresult = itemUseAction.onRightClickBlock(stack, player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    if (enumactionresult == EnumActionResult.PASS)
                    {
                        enumactionresult = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    }
                    stack.setItemDamage(j);
                    stack.setCount(i);
                    return enumactionresult;
                }
                else return result;
            }
            else
            {
                if (result != EnumActionResult.SUCCESS && event.getUseItem() != Event.Result.DENY
                    || result == EnumActionResult.SUCCESS && event.getUseItem() == Event.Result.ALLOW)
                {
                    ItemStack copyBeforeUse = stack.copy();
                    // Fire the alternative item use action
                    result = itemUseAction.onRightClickBlock(copyBeforeUse, player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    if (result == EnumActionResult.PASS)
                    {
                        result = copyBeforeUse.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    }
                    if (stack.isEmpty()) ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                }
                return result;
            }
        }
    }

    /**
     * @see net.minecraft.server.management.PlayerInteractionManager#processRightClick(EntityPlayer, World, ItemStack, EnumHand)
     */
    @Nonnull
    static EnumActionResult processRightClickItem(PlayerInteractEvent.RightClickItem event, IRightClickItemAction action)
    {
        // No special logic required, just fire the right click and return the result
        return action.onRightClickItem(event.getWorld(), event.getEntityPlayer(), event.getHand());
    }
}
