/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.List;

import net.dries007.tfc.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ItemJug extends ItemFluidStoragePottery
{

	private static final int MAX_FLUID_AMOUNT = 1000;
	private static final float BREAK_CHANCE = 0.02f;
	private static final int USE_DURATION = 32;

	public ItemJug()
	{
		super(USE_DURATION);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(isFilled(stack))
		{
			tooltip.add(String.format("Amount: %dmb", MAX_FLUID_AMOUNT));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			ItemStack stack = player.getHeldItem(hand);
			ItemJug jug = (ItemJug) stack.getItem();
			IBlockState fluidBlock = worldIn.getBlockState(pos.up());
			Fluid fluid = null;
			
			final Block block = worldIn.getBlockState(pos).getBlock();

			if(!jug.isFilled(stack))
			{
				if(block != Blocks.AIR && block.hasTileEntity(worldIn.getBlockState(pos)))
				{
					TileEntity te = worldIn.getTileEntity(pos);
					if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
					{
						IFluidHandler cap = (IFluidHandler) te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
						FluidStack fluidStack = cap.drain(MAX_FLUID_AMOUNT, false);
						fluid = fluidStack == null ? null : fluidStack.getFluid();
						if(fluid != null)
						{
							// Drain from the fluid storage into the jug
							cap.drain(MAX_FLUID_AMOUNT, true);
						}
					}
				}
				else if(fluidBlock != null && fluidBlock.getBlock() instanceof BlockFluidBase)
				{
					fluid = ((BlockFluidBase) fluidBlock.getBlock()).getFluid();
				}
				
				if(fluid != null)
				{	
					jug.setFluid(stack, fluid);
					jug.setJustFilled(stack, true);
					return EnumActionResult.FAIL;
				}
			}
			else if(block.hasTileEntity(worldIn.getBlockState(pos)))
			{
				TileEntity te = worldIn.getTileEntity(pos);
				if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
				{
					IFluidHandler cap = (IFluidHandler) te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
					FluidStack fluidStack = new FluidStack(jug.getFluid(stack), MAX_FLUID_AMOUNT);
					if(cap.fill(fluidStack, false) == MAX_FLUID_AMOUNT)
					{
						// Empty from the jug into the fluid storage
						cap.fill(fluidStack, true);
						jug.setFluid(stack, null);
					}
				}
			}
		}
		
		return EnumActionResult.PASS;
	}
	
	@Override
	public String getTranslationKey() {
		return super.getTranslationKey();
	}

	@Override
	protected void onDrink(ItemStack stack, World worldIn, EntityPlayer player)
	{
		if (Constants.RNG.nextFloat() < BREAK_CHANCE)
		{
			worldIn.playSound(player, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			stack.shrink(1);
		}
	}

}
