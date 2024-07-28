/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.mold.Vessel;
import net.dries007.tfc.common.component.mold.VesselComponent;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;
import net.dries007.tfc.util.data.FluidHeat;

public class VesselItem extends Item
{
    public static final int SLOTS = 4;

    private final Vessel.ContainerInfo containerInfo = new Vessel.ContainerInfo() {
        @Override
        public boolean canContainFluid(Fluid input)
        {
            return FluidHeat.get(input) != null;
        }

        @Override
        public int fluidCapacity()
        {
            return TFCConfig.SERVER.smallVesselCapacity.get();
        }

        @Override
        public boolean canContainItem(ItemStack stack)
        {
            return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(TFCConfig.SERVER.smallVesselMaximumItemSize.get());
        }

        @Override
        public int slotCapacity()
        {
            return 16;
        }
    };

    public VesselItem(Properties properties)
    {
        super(properties
            .component(TFCComponents.VESSEL, VesselComponent.EMPTY)
            .component(TFCComponents.HEAT, HeatComponent.of(HeatCapability.POTTERY_HEAT_CAPACITY)));
    }

    public Vessel.ContainerInfo containerInfo()
    {
        return containerInfo;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player)
    {
        final Vessel vessel = Vessel.get(stack);
        if (vessel != null &&
            vessel.isInventory() &&
            TFCConfig.SERVER.enableSmallVesselInventoryInteraction.get() &&
            !player.isCreative() &&
            action == ClickAction.SECONDARY)
        {
            for (int i = SLOTS - 1; i >= 0; i--)
            {
                final ItemStack simulate = vessel.extractItem(i, 64, true);
                if (!simulate.isEmpty())
                {
                    final ItemStack extracted = vessel.extractItem(i, 64, false);
                    final ItemStack leftover = slot.safeInsert(extracted);
                    if (!leftover.isEmpty())
                    {
                        // We can't simulate the `safeInsert` above, so we have to revert whatever leftover was obtained here
                        // Insert should be safe, because the previous extract extracted a full stack, and so should leave the slot empty
                        vessel.insertItem(i, leftover, false);

                        // Update slots, if we're in a crafting menu, to update output slots. See #2378
                        player.containerMenu.slotsChanged(slot.container);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot)
    {
        final Vessel vessel = Vessel.get(stack);
        if (vessel != null &&
            vessel.isInventory() &&
            TFCConfig.SERVER.enableSmallVesselInventoryInteraction.get() &&
            !player.isCreative() &&
            action == ClickAction.SECONDARY &&
            slot.allowModification(player))
        {
            if (!carried.isEmpty())
            {
                boolean slotsChanged = false;
                final ItemStack oldCarried = carried.copy();
                for (int i = 0; i < SLOTS; i++)
                {
                    final ItemStack leftover = vessel.insertItem(i, carried, false);
                    if (leftover.getCount() != oldCarried.getCount() || slotsChanged)
                    {
                        slotsChanged = true;
                        carriedSlot.set(leftover);
                        carried = leftover;
                    }
                    if (carried.isEmpty())
                    {
                        break;
                    }
                }
                if (slotsChanged)
                {
                    // Update slots, if we're in a crafting menu, to update output slots. See #2378
                    player.containerMenu.slotsChanged(slot.container);
                    return true;
                }
            }
            else
            {
                for (int i = SLOTS - 1; i >= 0; i--)
                {
                    final ItemStack current = vessel.getStackInSlot(i);
                    if (!current.isEmpty())
                    {
                        carriedSlot.set(vessel.extractItem(i, 64, false));

                        // Update slots, if we're in a crafting menu, to update output slots. See #2378
                        player.containerMenu.slotsChanged(slot.container);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !level.isClientSide() && player instanceof ServerPlayer serverPlayer)
        {
            final Vessel vessel = Vessel.get(stack);
            if (vessel != null)
            {
                if (vessel.isInventory())
                {
                    if (vessel.getTemperature() > 0)
                    {
                        player.displayClientMessage(Component.translatable("tfc.tooltip.small_vessel.inventory_too_hot"), true);
                    }
                    else
                    {
                        TFCContainerProviders.SMALL_VESSEL.openScreen(serverPlayer, hand);
                    }
                }
                else if (vessel.isMolten())
                {
                    TFCContainerProviders.MOLD_LIKE_ALLOY.openScreen(serverPlayer, hand);
                }
                else
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.small_vessel.alloy_solid"), true);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        final @Nullable Vessel vessel = Vessel.get(stack);
        if (vessel != null && vessel.isEmpty()) // Only show the 'contents' label if we actually have contents
        {
            if (vessel.isInventory())
            {
                if (!TFCConfig.CLIENT.displayItemContentsAsImages.get())
                {
                    tooltip.add(Component.translatable("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
                    Helpers.addInventoryTooltipInfo(vessel.contents(), tooltip);
                }
            }
            else
            {
                tooltip.add(Component.translatable("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
                tooltip.add(Tooltips.fluidUnitsAndCapacityOf(vessel.getFluidInTank(0).getHoverName(), vessel.getFluidInTank(0).getAmount(), containerInfo.fluidCapacity())
                    .append(Tooltips.moltenOrSolid(vessel.isMolten())));
                if (!Helpers.isEmpty(vessel.contents()))
                {
                    tooltip.add(Component.translatable("tfc.tooltip.small_vessel.still_has_unmelted_items").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (TFCConfig.CLIENT.displayItemContentsAsImages.get())
        {
            final Vessel vessel = Vessel.get(stack);
            if (vessel != null && vessel.isInventory())
            {
                return Helpers.getTooltipImage(vessel, 2, 2, 0, VesselItem.SLOTS - 1);
            }
        }
        return super.getTooltipImage(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return 1;
    }
}
