/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;
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

import net.dries007.tfc.objects.fluids.FluidsTFC;

/**
 * todo: as per comment in ItemsTFC, turn this into a proper fluid handler item?
 */
@ParametersAreNonnullByDefault
public class ItemGlassBottleTFC extends ItemGlassBottle
{
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        RayTraceResult result = rayTrace(worldIn, playerIn, true);

        //noinspection ConstantConditions
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            IBlockState targetState = worldIn.getBlockState(result.getBlockPos());
            if (targetState.getMaterial() == Material.WATER && targetState.getBlock() != Blocks.WATER && targetState.getBlock() != FluidsTFC.FRESH_WATER.get().getBlock())
            {
                return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
