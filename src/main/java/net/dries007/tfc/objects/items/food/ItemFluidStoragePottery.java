/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.food;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemFiredPottery;
import net.dries007.tfc.world.classic.fluids.FluidThirstConfig;
import net.dries007.tfc.world.classic.fluids.FluidThirstRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public abstract class ItemFluidStoragePottery extends ItemFiredPottery
{

	private static final int MAX_USE_DURATION = 32;

	private final int itemUseDuration;
	protected Fluid fluidType;
	protected boolean filled, justFilled;

	public ItemFluidStoragePottery(int itemUseDuration)
	{
		this.itemUseDuration = itemUseDuration;
		this.empty();
	}

	/**
	 * @return the itemUseDuration
	 */
	public int getItemUseDuration()
	{
		return this.itemUseDuration;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return MAX_USE_DURATION;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	private void empty()
	{
		this.filled = this.justFilled = false;
		this.fluidType = null;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;

			FoodStats foodStats = entityplayer.getFoodStats();
			if (filled && foodStats instanceof FoodStatsTFC)
			{
				worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
				FoodStatsTFC foodStackTFC = (FoodStatsTFC) foodStats;
				
				TerraFirmaCraft.getLog().info(fluidType.getUnlocalizedName());
				FluidThirstConfig config = FluidThirstRegistry.INSTANCE.getThirstConfig(fluidType.getUnlocalizedName());
				if(!worldIn.isRemote && config != null)
				{
					foodStackTFC.addThirst(config.getThirstAmount());
				}
				
				this.empty();
				this.onDrink(stack, worldIn, entityplayer);
			}

			if (entityplayer instanceof EntityPlayerMP)
			{
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
			}
		}
		return stack;
	}

	protected abstract void onDrink(ItemStack stack, World worldIn, EntityPlayer entityplayer);

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		FoodStats foodStats = playerIn.getFoodStats();

		final ActionResult<ItemStack> FAILED = new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);

		if (!(foodStats instanceof FoodStatsTFC) || !this.filled)
			return FAILED;

		if (this.justFilled)
		{
			this.justFilled = false;
			return FAILED;
		}

		FoodStatsTFC foodStatsTfc = (FoodStatsTFC) foodStats;

		if (foodStatsTfc.needWater())
		{
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}

		return FAILED;
	}

}
