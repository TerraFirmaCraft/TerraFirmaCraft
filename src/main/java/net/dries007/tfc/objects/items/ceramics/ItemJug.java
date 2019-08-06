/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ItemJug extends ItemPottery
{

	private static final int MAX_JUG_CAPACITY = 1000;
	private static final int MAX_USE_DURATION = 32;

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = super.getItemStackDisplayName(stack);
		if(isEmpty(stack)) return name;
		return String.format(
			"%s %s", 
			new TextComponentTranslation(getFluid(stack).getUnlocalizedName()).getFormattedText(),
			name
		);
	}
	
	@Override
	public void addSizeInfo(ItemStack stack, List<String> text)
	{
		super.addSizeInfo(stack, text);
		if(!isEmpty(stack))
		{
			text.add(String.format("Amount: %smb", MAX_JUG_CAPACITY));
			text.add(String.format("Heat  : %sK", getFluid(stack).getTemperature()));
		}
	}

	private static FluidStack getFluidStackOf(Fluid fluid)
	{
		return fluid == null ? null : new FluidStack(fluid, MAX_JUG_CAPACITY);
	}

	private JugCapabilityProvider getJugProvider(ItemStack stack)
	{
		return (JugCapabilityProvider) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	}

	private boolean isEmpty(ItemStack stack)
	{
		return getJugProvider(stack).tank.getFluid() == null;
	}

	private void setFluid(ItemStack stack, @Nullable Fluid fluid)
	{
		if (canFill(stack, fluid))
		{
			getJugProvider(stack).fill(getFluidStackOf(fluid), true);
			setJustFilled(stack, true);
		}
	}
	
	public Fluid getFluid(ItemStack stack)
	{
		return isEmpty(stack) ? null : getJugProvider(stack).tank.getFluid().getFluid();
	}

	private boolean canFill(ItemStack stack, @Nonnull Fluid fluid)
	{
		return getJugProvider(stack).fill(getFluidStackOf(fluid), false) == MAX_JUG_CAPACITY;
	}

	private void setJustFilled(ItemStack stack, boolean justFilled)
	{
		this.getJugProvider(stack).setJustFilled(justFilled);
	}

	private boolean isJustFilled(ItemStack stack)
	{
		return getJugProvider(stack).isJustFilled();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		if(!worldIn.isRemote && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, facing))
		{
			IBlockState fluidBlock = worldIn.getBlockState(pos.offset(facing));
			Fluid fluid = null;
			final Block block = worldIn.getBlockState(pos).getBlock();

			if(isEmpty(stack))
			{
				// Block has entity
				if(block.hasTileEntity(worldIn.getBlockState(pos)) && worldIn.getTileEntity(pos).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
				{
		            IFluidHandler cap = worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
		            if (cap.drain(MAX_JUG_CAPACITY, false) != null)
					{
						Fluid fluidToFill = cap.drain(MAX_JUG_CAPACITY, true).getFluid();
						setFluid(stack, fluidToFill);
					}
				}
				// Fluid block available
		        else if (fluidBlock.getBlock() instanceof BlockFluidBase)
				{
					fluid = ((BlockFluidBase) fluidBlock.getBlock()).getFluid();
				}
				
				if(fluid != null)
				{	
					setFluid(stack, fluid);
		            setJustFilled(stack, true);
		            return EnumActionResult.FAIL;
				}
			}
			else if(!block.hasTileEntity(worldIn.getBlockState(pos)) &&
					(fluid = getFluid(stack)) != null &&
					fluid.canBePlacedInWorld())
			{
				BlockFluidBase bf = (BlockFluidBase) fluid.getBlock();				
				IBlockState state = bf.getDefaultState().withProperty(BlockFluidBase.LEVEL, 1);
				BlockPos targetPos = pos.offset(facing);
				worldIn.setBlockState(targetPos, state, 3);
				for(EnumFacing side : EnumFacing.HORIZONTALS)
				{
					if(worldIn.getBlockState(targetPos.offset(side)).getBlock() != Blocks.AIR) continue;
					worldIn.setBlockState(targetPos.offset(side), state.withProperty(BlockFluidBase.LEVEL, 2), 3);	
				}
				worldIn.scheduleBlockUpdate(targetPos, bf, 2, 1);
				getJugProvider(stack).empty();
			}
			else if(player.isSneaking() && block.hasTileEntity(worldIn.getBlockState(pos)))
			{
				TileEntity te = worldIn.getTileEntity(pos);
				if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
				{
		            IFluidHandler cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
					FluidStack fluidStack = new FluidStack(getFluid(stack), MAX_JUG_CAPACITY);
		            if (cap != null && cap.fill(fluidStack, false) == MAX_JUG_CAPACITY)
					{
						cap.fill(fluidStack, true);
						getJugProvider(stack).empty();
					}
				}
	            return EnumActionResult.FAIL;
			}
		}

		return EnumActionResult.PASS;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer) entityLiving;

            FoodStats foodStats = entityplayer.getFoodStats();
            if (!isEmpty(stack) && foodStats instanceof FoodStatsTFC)
            {
                worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                FoodStatsTFC foodStackTFC = (FoodStatsTFC) foodStats;

                if (!worldIn.isRemote)
                {
                    foodStackTFC.addThirst(getFluid(stack) == FluidsTFC.FRESH_WATER ? 50 : 0);
                }

                System.out.printf("Before: %s, Empty: %s\n", stack.getTagCompound(), isEmpty(stack));
                getJugProvider(stack).empty();
                setJustFilled(stack, false);
                System.out.printf("After: %s, Empty: %s\n", stack.getTagCompound(), isEmpty(stack));
                this.onDrink(stack, worldIn, entityplayer);
            }

            if (entityplayer instanceof EntityPlayerMP)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
            }
        }
		
		return stack;
	}
	
	private void onDrink(ItemStack stack, World world, EntityPlayer player) {
		
	}
	
	@Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        FoodStats foodStats = playerIn.getFoodStats();
        
        final ActionResult<ItemStack> FAILED = new ActionResult<>(EnumActionResult.PASS, stack);
        
        if (!(foodStats instanceof FoodStatsTFC) || isEmpty(stack) || isJustFilled(stack))
        {
            setJustFilled(stack, false);
            return FAILED;
        }

        FoodStatsTFC foodStatsTfc = (FoodStatsTFC) foodStats;
        if (!isJustFilled(stack) && foodStatsTfc.attemptDrink(getFluid(stack) == FluidsTFC.FRESH_WATER ? 50 : 0)) 
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return FAILED;
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
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new JugCapabilityProvider(stack, nbt);
	}

	private static final class JugCapabilityProvider implements ICapabilitySerializable<NBTTagCompound>, ICapabilityProvider, IFluidHandlerItem
	{
		private final ItemStack stack;
		private final FluidTank tank;
		private boolean justFilled;

		public JugCapabilityProvider(ItemStack stack, NBTTagCompound nbt)
		{
			this.stack = stack;
			this.tank = new FluidTank(MAX_JUG_CAPACITY);
			this.tank.setCanFill(true);

			if (nbt != null)
			{
				deserializeNBT(nbt);
			}
		}

		public void setJustFilled(boolean justFilled)
		{
			this.justFilled = justFilled;
		}

		public boolean isJustFilled()
		{
			return this.justFilled;
		}
		
		public void empty() {
			this.tank.fillInternal(getFluidStackOf(null), true);
			this.tank.setFluid(null);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill)
		{
			return this.tank.fill(resource, doFill);
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain)
		{
			return this.tank.drain(resource, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain)
		{
			return this.tank.drain(maxDrain, doDrain);
		}

		@Override
		public IFluidTankProperties[] getTankProperties()
		{
			return this.tank.getTankProperties();
		}

		@Override
		public ItemStack getContainer()
		{
			return this.stack;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt = this.tank.writeToNBT(nbt);
			nbt.setBoolean("JustFilled", this.justFilled);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			if (nbt != null)
			{
				this.tank.readFromNBT(nbt);
				this.justFilled = nbt.getBoolean("JustFilled");
			}
		}
	}

}
