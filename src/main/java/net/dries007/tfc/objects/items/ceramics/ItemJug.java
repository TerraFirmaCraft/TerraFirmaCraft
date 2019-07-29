/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.items.food.ItemFluidStoragePottery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

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
			final IBlockState up = worldIn.getBlockState(pos.up());
//			TerraFirmaCraft.getLog().info(String.format("Using item on block [%s] with type %s", up.getBlock().getRegistryName(), up.getBlock() instanceof BlockFluidBase));
//			TerraFirmaCraft.getLog().info(up.getClass());
			if(!filled && up.getBlock() instanceof BlockFluidBase)
			{
				final BlockFluidBase fluidBase = (BlockFluidBase) up.getBlock();
				TerraFirmaCraft.getLog().info(String.format("Filling jug with fluid of type: [%s]", fluidBase.getFluid().getUnlocalizedName()));
				fluidType = fluidBase.getFluid();
				filled = justFilled = true;
				return EnumActionResult.FAIL;
			}
		}
		
		return EnumActionResult.PASS;
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
