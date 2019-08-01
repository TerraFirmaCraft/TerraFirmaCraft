/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.world.classic.fluids.FluidThirstConfig;
import net.dries007.tfc.world.classic.fluids.FluidThirstRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;

public abstract class ItemFluidStoragePottery extends ItemFiredPottery
{

	private static final Map<String, Fluid> STRING_TO_FLUIDS = new HashMap<>();
	private static final Map<Fluid, String> FLUIDS_TO_STRING = new HashMap<>();
	
	private static final int MAX_USE_DURATION = 32;

	private final int itemUseDuration;

	public ItemFluidStoragePottery(int itemUseDuration)
	{
		this.itemUseDuration = itemUseDuration;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		if(nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		NBTTagCompound fluidFill = nbt.hasKey("FluidFill") ? nbt.getCompoundTag("FluidFill") : new NBTTagCompound();
		if(!fluidFill.hasKey("Filled"))
		{
			fluidFill.setBoolean("Filled", false);
		}
		if(!fluidFill.hasKey("JustFilled"))
		{
			fluidFill.setBoolean("JustFilled", false);
		}
		if(!fluidFill.hasKey("Fluid"))
		{
			fluidFill.setString("Fluid", "Empty");
		}
		nbt.setTag("FluidFill", fluidFill);
		stack.setTagCompound(nbt);
		return super.initCapabilities(stack, nbt);
	}
	
	private NBTTagCompound getFluidFill(ItemStack stack)
	{
		return (NBTTagCompound) stack.getTagCompound().getTag("FluidFill");
	}
	
	protected boolean isFilled(ItemStack stack)
	{
		return getFluidFill(stack).getBoolean("Filled");
	}
	
	protected void setFluid(ItemStack stack, Fluid fluidType)
	{
		if(fluidType != null && !FLUIDS_TO_STRING.containsKey(fluidType))
		{
			FLUIDS_TO_STRING.put(fluidType, fluidType.getUnlocalizedName());
			STRING_TO_FLUIDS.put(fluidType.getUnlocalizedName(), fluidType);
		}
		getFluidFill(stack).setBoolean("Filled", fluidType != null);
		getFluidFill(stack).setString("Fluid", fluidType == null ? "None" : FLUIDS_TO_STRING.get(fluidType));
	}
	
	protected Fluid getFluid(ItemStack stack)
	{
		String fluidType = getFluidFill(stack).getString("Fluid");
		return fluidType == "None" ? null : STRING_TO_FLUIDS.get(fluidType);
	}
	
	protected void setJustFilled(ItemStack stack, boolean justFilled)
	{
		getFluidFill(stack).setBoolean("JustFilled", justFilled);
	}
	
	protected boolean justFilled(ItemStack stack)
	{
		return getFluidFill(stack).getBoolean("JustFilled");
	}

	public int getItemUseDuration()
	{
		return this.itemUseDuration;
	}
	
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		super.addSizeInfo(stack, tooltip);
		if(isFilled(stack))
		{
			tooltip.add(String.format("Temperature: %sK", getFluid(stack).getTemperature()));
		}
	}
	
	@Override
	public void addSizeInfo(ItemStack stack, List<String> text)
	{
		// Called in addInformation instead to change order
	}
	
	@Override
	public boolean canStack(ItemStack stack)
	{
		return false;
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
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = super.getItemStackDisplayName(stack).replaceAll(" %s", "");
		if(!isFilled(stack)) return name;
		return I18n.format(
			"%s (%s)",
			name, 
			new TextComponentTranslation(getFluidFill(stack).getString("Fluid")).getFormattedText()
		);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		final boolean filled = isFilled(stack);
		final Fluid fluidType = getFluid(stack);
		
		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;

			FoodStats foodStats = entityplayer.getFoodStats();
			if (filled && foodStats instanceof FoodStatsTFC)
			{
				worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
				FoodStatsTFC foodStackTFC = (FoodStatsTFC) foodStats;
				
				FluidThirstConfig config = FluidThirstRegistry.INSTANCE.getThirstConfig(fluidType.getUnlocalizedName());
				if(!worldIn.isRemote && config != null)
				{
					foodStackTFC.addThirst(config.getThirstAmount());
				}
				
				this.empty(stack);
				this.onDrink(stack, worldIn, entityplayer);
			}

			if (entityplayer instanceof EntityPlayerMP)
			{
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
			}
		}
		return stack;
	}
	
	private void empty(ItemStack stack)
	{
		setFluid(stack, null);
		setJustFilled(stack, false);
	}

	protected abstract void onDrink(ItemStack stack, World worldIn, EntityPlayer entityplayer);

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		FoodStats foodStats = playerIn.getFoodStats();

		final ActionResult<ItemStack> FAILED = new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		if (!(foodStats instanceof FoodStatsTFC) || !isFilled(stack) || justFilled(stack))
		{
			setJustFilled(stack, false);
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		FoodStatsTFC foodStatsTfc = (FoodStatsTFC) foodStats;

		if (!justFilled(stack) && foodStatsTfc.needWater())
		{
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}

		return FAILED;
	}

}
