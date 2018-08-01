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
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.util.Size;
import net.dries007.tfc.api.util.Weight;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.CapabilityItemSize;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.IPlacableItem;

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
        ItemStack stack = event.getItemStack();
        EntityPlayer player = event.getEntityPlayer();

        /*
         Note: This event handler is fired first with the main hand as event.getStack()
         If nothing happens (as per vanilla behavior, even if this event causes something to happen),
         The event will fire AGAIN with the offhand and offhand stack.

         This is to prevent that second firing of the event from causing duplicate actions
         i.e. if you hold charcoal with main hand and a block of dirt with offhand, the dirt will try and place because
         vanilla behavior doesn't do anything with charcoal in the main hand
        */
        if (event.getHand() == EnumHand.OFF_HAND)
        {
            ItemStack mainStack = player.getHeldItem(EnumHand.MAIN_HAND);
            if ((mainStack.getItem() == Items.COAL && mainStack.getMetadata() == 1) ||
                (Helpers.doesStackMatchOre(mainStack, "logWood") && player.isSneaking()) ||
                mainStack.getItem() instanceof IPlacableItem)
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

                    if (world.getBlockState(pos).getBlock() instanceof BlockCharcoalPile)
                    {
                        if (world.getBlockState(pos).getValue(LAYERS) != 8)
                        {
                            // Adding layers is handled in BlockCharcoalPile
                            return;
                        }
                    }
                    if (!world.isRemote)
                    {
                        // noinspection ConstantConditions
                        world.setBlockState(pos.offset(facing), BlocksTFC.CHARCOAL_PILE.getDefaultState());

                        if (!player.isCreative())
                        {
                            player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, 1));
                        }
                        world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0F, 0.5F);
                        return;
                    }

                }
            }
        }
        if (Helpers.doesStackMatchOre(stack, "logWood") && player.isSneaking())
        {
            EnumFacing facing = event.getFace();
            if (facing != null)
            {
                //noinspection ConstantConditions
                if (world.getBlockState(pos).getBlock() == BlocksTFC.LOG_PILE)
                {
                    if (!world.isRemote)
                    {
                        TELogPile te = Helpers.getTE(world, pos, TELogPile.class);
                        if (te != null)
                        {
                            if (te.insertLog(stack.copy()))
                            {
                                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
                                world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                            else
                            {
                                // Insert log didn't work, see if trying to place another log pile
                                if (facing == EnumFacing.UP && te.countLogs() == 16 || (facing != EnumFacing.UP && world.getBlockState(pos.down().offset(facing)).isNormalCube()
                                    && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing))))
                                {
                                    // noinspection ConstantConditions
                                    world.setBlockState(pos.offset(facing), BlocksTFC.LOG_PILE.getStateForPlacement(world, pos, facing, 0, 0, 0, 0, player));

                                    TELogPile te2 = Helpers.getTE(world, pos.offset(facing), TELogPile.class);
                                    if (te2 != null)
                                    {
                                        te2.insertLog(stack.copy());
                                    }

                                    player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
                                    world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (world.getBlockState(pos.down().offset(facing)).isNormalCube()
                        && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing)) &&
                        player.isSneaking())
                    {
                        // Place log pile
                        if (!world.isRemote)
                        {
                            // noinspection ConstantConditions
                            world.setBlockState(pos.offset(facing), BlocksTFC.LOG_PILE.getStateForPlacement(world, pos, facing, 0, 0, 0, 0, player));

                            TELogPile te = Helpers.getTE(world, pos.offset(facing), TELogPile.class);
                            if (te != null)
                            {
                                te.insertLog(stack.copy());
                            }

                            player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
                            world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
            }
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // All items that can should use this implementation instead of the other exceptions (which are for items that can't implement IPlacableItem)
        if (stack.getItem() instanceof IPlacableItem)
        {
            IPlacableItem item = (IPlacableItem) stack.getItem();
            if (item.placeItemInWorld(world, pos, stack, player, event.getFace(), event.getHitVec()))
            {
                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));

                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    //Used for IItemSize capability. You can either implement the interface or use the capability
    @SubscribeEvent
    public void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> e)
    {
        ItemStack stack = e.getObject();
        // Skip items with existing capabilities
        if (CapabilityItemSize.getIItemSize(stack) != null) return;

        Item item = stack.getItem();
        boolean canStack = stack.getMaxStackSize() > 1; // This is nessecary so it isn't accidentally overriden by a default implementation

        // todo: Add more items here
        if (item == Items.COAL)
            CapabilityItemSize.add(e, Items.COAL, Size.SMALL, Weight.MEDIUM, canStack);
        else if (item == Items.STICK)
            CapabilityItemSize.add(e, Items.STICK, Size.SMALL, Weight.LIGHT, canStack);

            // Final checks for general item types
        else if (item instanceof ItemTool)
            CapabilityItemSize.add(e, item, Size.LARGE, Weight.MEDIUM, canStack);
        else if (item instanceof ItemArmor)
            CapabilityItemSize.add(e, item, Size.LARGE, Weight.HEAVY, canStack);
        else if (item instanceof ItemBlock)
            CapabilityItemSize.add(e, item, Size.SMALL, Weight.MEDIUM, canStack);
        else
            CapabilityItemSize.add(e, item, Size.VERY_SMALL, Weight.LIGHT, canStack);
    }
}
