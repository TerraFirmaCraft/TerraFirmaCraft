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
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.capability.ItemStickCapability;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEventHandler
{
    /**
     * Make leaves drop sticks
     */
    @SubscribeEvent
    public void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
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
            if (Constants.RNG.nextFloat() < chance)
                event.getDrops().add(new ItemStack(Items.STICK));
        }
    }

    /**
     * Place pit kiln block & add items
     * Note: `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     * We have this event already, might as well use it.
     */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

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
            if (IPlaceableItem.Impl.isPlaceable(mainStack))
            {
                event.setCanceled(true);
                return;
            }
        }

        // To add a new IPlaceableItem:
        // 1. Implement the interface on an item or
        // 2. Add to the Map<Predicate<ItemStack>, IPlaceableItem> in IPlaceableItem.Impl
        if (IPlaceableItem.Impl.isPlaceable(stack))
        {
            IPlaceableItem placeable = IPlaceableItem.Impl.getPlaceable(stack);
            if (placeable.placeItemInWorld(world, pos, stack, player, event.getFace(), event.getHitVec()))
            {
                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
            }
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    /**
     * This is an extra handler for items that also have an active effect when right clicked in the air
     */
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        if (event.getHand() == EnumHand.OFF_HAND)
        {
            ItemStack mainStack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (IPlaceableItem.Impl.isUsable(mainStack))
            {
                event.setCanceled(true);
                return;
            }
        }

        if (IPlaceableItem.Impl.isUsable(stack))
        {
            IPlaceableItem placeable = IPlaceableItem.Impl.getUsable(stack);
            if (placeable.placeItemInWorld(world, pos, stack, player, event.getFace(), null))
            {
                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, 1));
            }
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
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
            e.addCapability(ItemStickCapability.KEY, new ItemStickCapability(e.getObject().getTagCompound()));

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
