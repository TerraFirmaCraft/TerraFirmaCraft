/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IFireable;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEventHandler
{
    /**
     * Make leaves drop sticks
     */
    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final EntityPlayer harvester = event.getHarvester();
        final ItemStack heldItem = harvester == null ? ItemStack.EMPTY : harvester.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (!event.isSilkTouching() && block instanceof BlockLeaves)
        {
            // Done via event so it applies to all leaves.
            double chance = ConfigTFC.GENERAL.leafStickDropChance;
            if (!heldItem.isEmpty() && Helpers.containsAnyOfCaseInsensitive(heldItem.getItem().getToolClasses(heldItem), ConfigTFC.GENERAL.leafStickDropChanceBonusClasses))
                chance = ConfigTFC.GENERAL.leafStickDropChanceBonus;
            if (event.getWorld().rand.nextFloat() < chance)
                event.getDrops().add(new ItemStack(Items.STICK));
        }
    }

    /**
     * Place pit kiln block & add items
     * Note: `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     * We have this event already, might as well use it.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = event.getItemStack();
        EntityPlayer player = event.getEntityPlayer();

        /*
         Note: This event handler is fired first with the main hand as event.getStack()
         If nothing happens (as per vanilla behavior, even if this event causes something to happen),
         The event will fire AGAIN with the offhand.
         This is to prevent that second firing of the event from causing duplicate actions
         i.e. if you hold charcoal with main hand and a block of dirt with offhand, the dirt will try and place because
         vanilla behavior doesn't do anything with charcoal in the main hand
        */
        if (event.getHand() == EnumHand.OFF_HAND)
        {
            ItemStack mainStack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (mainStack.getItem() == Items.COAL && mainStack.getMetadata() == 1)
            {
                event.setCanceled(true);
                return;
            }
            IFireable fireable = IFireable.fromItem(event.getItemStack().getItem());
            if (fireable != null && event.getEntityPlayer().isSneaking() && event.getFace() == EnumFacing.UP)
            {
                event.setCanceled(true);
                return;
            }
        }

        if (stack.getItem() == Items.COAL && stack.getMetadata() == 1)
        {
            EnumFacing facing = event.getFace();
            if (facing != null)
            {
                if (world.getBlockState(pos.down().offset(facing)).isNormalCube()
                    && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing)))
                {

                    if (world.getBlockState(pos).getBlock() instanceof BlockCharcoalPile || world.getBlockState(pos).getBlock() instanceof BlockCharcoalPile)
                    {
                        if (world.getBlockState(pos).getValue(LAYERS) != 8)
                        {
                            return;
                        }
                    }
                    if (!world.isRemote)
                    {
                        world.setBlockState(pos.offset(facing), BlocksTFC.CHARCOAL_PILE.getDefaultState());

                        if (!player.isCreative())
                        {
                            player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, 1));
                        }
                        world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0F, 0.5F);
                        event.setCanceled(true);
                    }

                }
            }
        }
        // TODO: Log Piles
        // TODO: IPlaceableItem instances Call getBlockForPlacement or something like that
        // Kiln Pottery
        IFireable fireable = IFireable.fromItem(event.getItemStack().getItem());
        if (fireable != null && event.getEntityPlayer().isSneaking() && event.getFace() == EnumFacing.UP)
        {

            if (fireable.getFireableTiers().contains(Metal.Tier.TIER_I))
            {
                //noinspection ConstantConditions
                if (world.getBlockState(pos).getBlock() != BlocksTFC.PIT_KILN)
                {
                    if (!world.isSideSolid(pos, EnumFacing.UP)) return;
                    pos = pos.add(0, 1, 0); // also important for TE fetch
                    if (!world.getBlockState(pos).getMaterial().isReplaceable()) return; // can't put down the block
                    //noinspection ConstantConditions
                    world.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
                }

                TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
                if (te == null) return;
                te.onRightClick(event.getEntityPlayer(), event.getItemStack(), (event.getHitVec().x % 1) < .5, (event.getHitVec().z % 1) < .5);
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
