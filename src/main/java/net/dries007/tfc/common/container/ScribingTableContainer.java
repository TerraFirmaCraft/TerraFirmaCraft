/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import org.apache.commons.lang3.StringUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.ScribingTableBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class ScribingTableContainer extends ItemCombinerMenu
{
    public static boolean isInkInput(ItemStack stack)
    {
        return !getInkFluid(stack).isEmpty() || Helpers.isItem(stack, TFCTags.Items.SCRIBING_INK);
    }

    public static FluidStack getInkFluid(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(ScribingTableContainer::getInkFluid).orElse(FluidStack.EMPTY);
    }

    public static FluidStack getInkFluid(IFluidHandlerItem handler)
    {
        for (int tank = 0; tank < handler.getTanks(); tank++)
        {
            FluidStack fluidStack = handler.getFluidInTank(tank);
            if (Helpers.isFluid(fluidStack.getFluid(), TFCTags.Fluids.SCRIBING_INK) && fluidStack.getAmount() >= FluidHelpers.BUCKET_VOLUME)
                return fluidStack;
        }
        return FluidStack.EMPTY;
    }

    private @Nullable String itemName;

    public ScribingTableContainer(Inventory playerInv, int windowId)
    {
        this(playerInv, windowId, ContainerLevelAccess.NULL);
    }

    public ScribingTableContainer(Inventory playerInv, int windowId, ContainerLevelAccess access)
    {
        super(TFCContainerTypes.SCRIBING_TABLE.get(), windowId, playerInv, access);
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasItem)
    {
        return player.getAbilities().instabuild || isInkInput(inputSlots.getItem(1));
    }

    @Override
    protected void onTake(Player player, ItemStack stack)
    {
        inputSlots.setItem(0, ItemStack.EMPTY);
        ItemStack dye = inputSlots.getItem(1);
        inputSlots.setItem(1, dye.getCapability(Capabilities.FLUID_ITEM).map(handler -> {
            handler.drain(new FluidStack(getInkFluid(handler), FluidHelpers.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
            return handler.getContainer();
        }).orElseGet(() -> {
            ItemStack result = dye.copy();
            result.shrink(1);
            return result;
        }));

        access.execute((level, pos) -> Helpers.playSound(level, pos, TFCSounds.SCRIBING_TABLE.get()));
    }

    @Override
    protected boolean isValidBlock(BlockState state)
    {
        return state.getBlock() instanceof ScribingTableBlock;
    }

    @Override
    public void createResult()
    {
        ItemStack input = this.inputSlots.getItem(0);
        ItemStack output = input.copy();

        if (StringUtils.isBlank(this.itemName))
        {
            if (input.hasCustomHoverName())
            {
                output.resetHoverName();
            }
            else
            {
                output = ItemStack.EMPTY;
            }
        }
        else if (!this.itemName.equals(input.getHoverName().getString()))
        {
            output.setHoverName(Helpers.literal(this.itemName));
        }
        else
        {
            output = ItemStack.EMPTY;
        }

        this.resultSlots.setItem(0, output);
        this.broadcastChanges();
    }

    public void setItemName(String text)
    {
        this.itemName = text;
        if (getSlot(2).hasItem())
        {
            ItemStack itemstack = getSlot(2).getItem();
            if (StringUtils.isBlank(text))
            {
                itemstack.resetHoverName();
            }
            else
            {
                itemstack.setHoverName(Helpers.literal(this.itemName));
            }
        }
        this.createResult();
    }

}
