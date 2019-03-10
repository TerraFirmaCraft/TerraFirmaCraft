/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import net.dries007.tfc.api.capability.ItemStickCapability;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.network.PacketCalendarUpdate;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CommonEventHandler
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
            if (Constants.RNG.nextFloat() < chance)
                event.getDrops().add(new ItemStack(Items.STICK));
        }
    }

    /**
     * Handler for {@link IPlaceableItem}
     * To add a new placeable item effect, eiether implement {@link IPlaceableItem} or see {@link IPlaceableItem.Impl} for vanilla item usages
     *
     * Notes:
     * 1) `onBlockActivate` doesn't get called when the player is sneaking, unless doesSneakBypassUse returns true.
     * 2) This event handler is fired first with the main hand as event.getStack()
     * If nothing happens (as per vanilla behavior, even if this event causes something to happen),
     * The event will fire AGAIN with the offhand and offhand stack.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

        if (IPlaceableItem.Impl.isPlaceable(stack))
        {
            IPlaceableItem placeable = IPlaceableItem.Impl.getPlaceable(stack);
            if (placeable.placeItemInWorld(world, pos, stack, player, event.getFace(), event.getHitVec()))
            {
                player.setHeldItem(event.getHand(), Helpers.consumeItem(stack, player, placeable.consumeAmount()));

                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    /**
     * This is an extra handler for items that also have an active effect when right clicked in the air
     * Note: If you have an item that needs an active effect, use onItemRightClick(), or attach this via {@link IPlaceableItem.Impl}
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = event.getEntityPlayer();

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
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> e)
    {
        ItemStack stack = e.getObject();
        // Skip items with existing capabilities
        if (CapabilityItemSize.getIItemSize(stack) != null) return;

        Item item = stack.getItem();
        boolean canStack = stack.getMaxStackSize() > 1; // This is necessary so it isn't accidentally overridden by a default implementation

        // todo: Add more items here
        if (item == Items.COAL)
            CapabilityItemSize.add(e, Items.COAL, Size.SMALL, Weight.MEDIUM, canStack);
        else if (item == Items.STICK)
            e.addCapability(ItemStickCapability.KEY, new ItemStickCapability(e.getObject().getTagCompound()));
        else if (item == Items.CLAY_BALL)
            CapabilityItemSize.add(e, item, Size.SMALL, Weight.MEDIUM, canStack);

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

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Capability Sync Handler
            final EntityPlayerMP player = (EntityPlayerMP) event.player;
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));

            // World Data (Calendar) Sync Handler
            TerraFirmaCraft.getNetwork().sendTo(new PacketCalendarUpdate(), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
        // Capability Sync Handler
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            final EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            player.inventoryContainer.addListener(new CapabilityContainerListener(player));
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        // Capability Sync Handler
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            final EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            event.getContainer().addListener(new CapabilityContainerListener(player));
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        // Calendar Sync / Initialization
        final World world = event.getWorld();
        if (world.provider.getDimension() == 0 && !world.isRemote)
        {
            CalendarTFC.CalendarWorldData.onLoad(event.getWorld());
        }
    }
}
