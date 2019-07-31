/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.items.food.ItemFluidStoragePottery;
import net.dries007.tfc.objects.te.TEBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ItemJug extends ItemFluidStoragePottery
{

	private static final float BREAK_CHANCE = 0.02f;
	private static final int USE_DURATION = 32;

	public ItemJug()
	{
		super(USE_DURATION);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			IBlockState fluidBlock = worldIn.getBlockState(pos.up());
			Fluid fluid = null;
			
			final Block block = worldIn.getBlockState(pos).getBlock();
			if(block != Blocks.AIR && block.hasTileEntity(worldIn.getBlockState(pos)))
			{
				TileEntity te = worldIn.getTileEntity(pos);
				System.out.printf("Has tank tag: %s\n", te.getTileData());
				if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
				{
					if(te instanceof TEBarrel)
					{
						FluidTank tank = new FluidTank(0).readFromNBT(((TEBarrel) te).getTileData().getCompoundTag("tank"));
						fluid = (tank == null || tank.getFluid() == null) ? null : tank.getFluid().getFluid();
						System.out.printf("Got fluid: %s\n", tank.getFluid());
					}
				}
			}
			else if(fluidBlock != null && fluidBlock.getBlock() instanceof BlockFluidBase)
			{
				fluid = ((BlockFluidBase) fluidBlock.getBlock()).getFluid();
			}
			
			if(fluid != null)
			{
				ItemStack stack = player.getHeldItem(hand);
				ItemJug jug = (ItemJug) stack.getItem();
				
				if(!jug.isFilled(stack) && fluidBlock.getBlock() instanceof BlockFluidBase)
				{
					final BlockFluidBase fluidBase = (BlockFluidBase) fluidBlock.getBlock();
					jug.setFluid(stack, fluidBase.getFluid());
					jug.setJustFilled(stack, true);
					return EnumActionResult.FAIL;
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
