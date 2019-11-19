/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.Helpers;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class ItemGlassBottleTFC extends ItemGlassBottle
{
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        RayTraceResult rayTraceResult = Helpers.rayTrace(worldIn, playerIn, true);

        if (rayTraceResult != null && isNotVanillaFluid(worldIn.getBlockState(rayTraceResult.getBlockPos())))
        {
            ItemStack heldItem = playerIn.getHeldItem(handIn);
            FluidActionResult fluidActionResult = tryFill(playerIn, heldItem);
            return fluidActionResult.isSuccess()
                ? ActionResult.newResult(EnumActionResult.SUCCESS, fluidActionResult.getResult())
                : ActionResult.newResult(EnumActionResult.FAIL, heldItem);
        }
        else
        {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
    }

    private static FluidActionResult tryFill(EntityPlayer playerIn, ItemStack heldItem)
    {
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(heldItem);
        IItemHandler itemHandler = playerIn.getCapability(ITEM_HANDLER_CAPABILITY, null);
        if (fluidHandler == null || itemHandler == null)
        {
            return FluidActionResult.FAILURE;
        }

        int capacity = getCapacity(fluidHandler);
        return FluidUtil.tryFillContainerAndStow(heldItem, fluidHandler, itemHandler, capacity, playerIn, true);
    }

    private static Integer getCapacity(IFluidHandler fluidHandler)
    {
        return Optional.ofNullable(fluidHandler.getTankProperties())
            .map(properties -> properties[0])
            .map(IFluidTankProperties::getCapacity)
            .orElse(0);
    }

    private static boolean isNotVanillaFluid(IBlockState blockState)
    {
        return blockState.getMaterial() == Material.WATER && blockState.getBlock() != Blocks.WATER;
    }
}
