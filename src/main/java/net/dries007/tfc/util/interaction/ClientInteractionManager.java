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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Util class for handling right click actions with more precision than {@link net.minecraftforge.event.entity.player.PlayerInteractEvent} gives us
 *
 * @see net.minecraft.client.multiplayer.PlayerControllerMP
 */
@ParametersAreNonnullByDefault
final class ClientInteractionManager
{
    /**
     * @see PlayerControllerMP#processRightClickBlock(EntityPlayerSP, WorldClient, BlockPos, EnumFacing, Vec3d, EnumHand)
     */
    @Nonnull
    static EnumActionResult processRightClickBlock(PlayerInteractEvent.RightClickBlock event, IRightClickBlockAction itemUseAction)
    {
        EnumActionResult result = EnumActionResult.PASS;

        EntityPlayerSP player = (EntityPlayerSP) event.getEntityPlayer();
        ItemStack itemstack = event.getItemStack();
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        EnumHand hand = event.getHand();
        EnumFacing direction = event.getFace();
        if (direction == null)
        {
            direction = EnumFacing.UP;
        }
        Vec3d hitVec = event.getHitVec();
        float hitX = 0, hitY = 0, hitZ = 0;
        if (hitVec != null)
        {
            hitX = ((float) (hitVec.x - pos.getX()));
            hitY = ((float) (hitVec.y - pos.getY()));
            hitZ = ((float) (hitVec.z - pos.getZ()));
        }
        boolean flag = false;
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;

        if (controller.getCurrentGameType() != GameType.SPECTATOR)
        {
            EnumActionResult ret = itemstack.onItemUseFirst(player, worldIn, pos, hand, direction, hitX, hitY, hitZ);
            if (ret != EnumActionResult.PASS)
            {
                return ret;
            }

            IBlockState iblockstate = worldIn.getBlockState(pos);
            boolean bypass = player.getHeldItemMainhand().doesSneakBypassUse(worldIn, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(worldIn, pos, player);

            if ((!player.isSneaking() || bypass || event.getUseBlock() == Event.Result.ALLOW))
            {
                if (event.getUseBlock() != Event.Result.DENY)
                    flag = iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, direction, hitX, hitY, hitZ);
                if (flag) result = EnumActionResult.SUCCESS;
            }

            if (!flag && itemstack.getItem() instanceof ItemBlock)
            {
                ItemBlock itemblock = (ItemBlock) itemstack.getItem();

                if (!itemblock.canPlaceBlockOnSide(worldIn, pos, direction, player, itemstack))
                {
                    return EnumActionResult.FAIL;
                }
            }
        }

        if (!flag && controller.getCurrentGameType() != GameType.SPECTATOR || event.getUseItem() == Event.Result.ALLOW)
        {
            if (itemstack.isEmpty())
            {
                return EnumActionResult.PASS;
            }
            else if (player.getCooldownTracker().hasCooldown(itemstack.getItem()))
            {
                return EnumActionResult.PASS;
            }
            else
            {
                if (itemstack.getItem() instanceof ItemBlock && !player.canUseCommandBlock())
                {
                    Block block = ((ItemBlock) itemstack.getItem()).getBlock();

                    if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                    {
                        return EnumActionResult.FAIL;
                    }
                }

                if (controller.getCurrentGameType().isCreative())
                {
                    int i = itemstack.getMetadata();
                    int j = itemstack.getCount();
                    if (event.getUseItem() != Event.Result.DENY)
                    {
                        EnumActionResult enumactionresult;
                        // Fire the alternative item use action
                        enumactionresult = itemUseAction.onRightClickBlock(itemstack, player, worldIn, pos, hand, direction, hitX, hitY, hitZ);
                        if (enumactionresult == EnumActionResult.PASS)
                        {
                            // fire the normal one as well
                            enumactionresult = itemstack.onItemUse(player, worldIn, pos, hand, direction, hitX, hitY, hitZ);
                        }
                        itemstack.setItemDamage(i);
                        itemstack.setCount(j);
                        return enumactionresult;
                    }
                    else
                    {
                        return result;
                    }
                }
                else
                {
                    ItemStack copyForUse = itemstack.copy();
                    if (event.getUseItem() != Event.Result.DENY)
                    {
                        // Fire the alternative item use action
                        result = itemUseAction.onRightClickBlock(copyForUse, player, worldIn, pos, hand, direction, hitX, hitY, hitZ);
                        if (result == EnumActionResult.PASS)
                        {
                            // fire the normal one as well
                            result = copyForUse.onItemUse(player, worldIn, pos, hand, direction, hitX, hitY, hitZ);
                        }
                    }
                    if (itemstack.isEmpty()) ForgeEventFactory.onPlayerDestroyItem(player, copyForUse, hand);
                    return result;
                }
            }
        }
        else
        {
            return EnumActionResult.SUCCESS;
        }
    }

    /**
     * @see PlayerControllerMP#processRightClick(EntityPlayer, World, EnumHand)
     */
    @Nonnull
    static EnumActionResult processRightClickItem(PlayerInteractEvent.RightClickItem event, IRightClickItemAction action)
    {
        // No special logic required, just fire the right click and return the result
        return action.onRightClickItem(event.getWorld(), event.getEntityPlayer(), event.getHand());
    }

}
