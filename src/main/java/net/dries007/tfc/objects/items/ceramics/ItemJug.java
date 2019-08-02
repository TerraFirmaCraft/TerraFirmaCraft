/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.List;

import javax.annotation.Nonnull;

import net.dries007.tfc.Constants;
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
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ItemJug extends ItemFiredPottery implements ICapabilityProvider
{

	public static final int MAX_FLUID_AMOUNT = 1000;
    private static final int MAX_USE_DURATION = 32;
	private static final float BREAK_CHANCE = 0.02f;
	
	private NBTTagCompound getFluidTank(ItemStack stack)
	{
		return stack.getTagCompound().getCompoundTag("tank");
	}

    private boolean isFilled(ItemStack stack)
    {
        return getFluidTank(stack).getBoolean("Filled");
    }

    private void setFluid(ItemStack stack, Fluid fluidType)
    {
    	getFluidTank(stack).setString("Fluid", fluidType == null ? "None" : FluidRegistry.getFluidName(fluidType));
    	getFluidTank(stack).setBoolean("Filled", fluidType != null);
    }

    private Fluid getFluid(ItemStack stack)
    {
    	String fluidName = getFluidTank(stack).getString("Fluid");
    	return fluidName == "None" ? null : FluidRegistry.getFluid(fluidName);
    }

    private void setJustFilled(ItemStack stack, boolean justFilled)
    {
        getFluidTank(stack).setBoolean("JustFilled", justFilled);
    }

    private boolean isJustFilled(ItemStack stack)
    {
        return getFluidTank(stack).getBoolean("JustFilled");
    }

    @Override
    public void addSizeInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        super.addSizeInfo(stack, text);
        if (isFilled(stack))
        {
            text.add(String.format("Temperature: %sK", getFluid(stack).getTemperature()));
            text.add(String.format("Amount: %dmb", MAX_FLUID_AMOUNT));
        }
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return MAX_USE_DURATION;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        String name = super.getItemStackDisplayName(stack).replaceAll(" %s", "");
        if (!isFilled(stack)) return name;
        return String.format(
            "%s (%s)",
            name,
            new TextComponentTranslation(getFluid(stack).getUnlocalizedName()).getFormattedText()
        );
    }
    
	@Override
    @Nonnull
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			ItemStack stack = player.getHeldItem(hand);
			ItemJug jug = (ItemJug) stack.getItem();
			IBlockState fluidBlock = worldIn.getBlockState(pos.offset(facing));
			Fluid fluid = null;
			
			final Block block = worldIn.getBlockState(pos).getBlock();

			if(!jug.isFilled(stack))
			{
				if(block != Blocks.AIR && block.hasTileEntity(worldIn.getBlockState(pos)))
				{
					TileEntity te = worldIn.getTileEntity(pos);
					if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
                    {
                        IFluidHandler cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                        if (cap != null)
                        {
                            FluidStack fluidStack = cap.drain(MAX_FLUID_AMOUNT, false);
                            fluid = fluidStack == null ? null : fluidStack.getFluid();
                            if (fluid != null)
                            {
                                // Drain from the fluid storage into the jug
                                cap.drain(MAX_FLUID_AMOUNT, true);
                            }
                        }
					}
				}
                else if (fluidBlock.getBlock() instanceof BlockFluidBase)
				{
					fluid = ((BlockFluidBase) fluidBlock.getBlock()).getFluid();
				}
				
				if(fluid != null)
				{	
                    setFluid(stack, fluid);
                    setJustFilled(stack, true);
					System.out.println(String.format("Got fluid: %s", getFluid(stack)));
					return EnumActionResult.FAIL;
				}
			}
			else if(!block.hasTileEntity(worldIn.getBlockState(pos)) &&
					(fluid = jug.getFluid(stack)).canBePlacedInWorld())
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
				jug.setFluid(stack, null);
			}
			else if(block.hasTileEntity(worldIn.getBlockState(pos)))
			{
				TileEntity te = worldIn.getTileEntity(pos);
				if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
				{
                    IFluidHandler cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
					FluidStack fluidStack = new FluidStack(jug.getFluid(stack), MAX_FLUID_AMOUNT);
                    if (cap != null && cap.fill(fluidStack, false) == MAX_FLUID_AMOUNT)
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
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        final boolean filled = isFilled(stack);

        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer) entityLiving;

            FoodStats foodStats = entityplayer.getFoodStats();
            if (filled && foodStats instanceof FoodStatsTFC)
            {
                worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                FoodStatsTFC foodStackTFC = (FoodStatsTFC) foodStats;

                if (!worldIn.isRemote)
                {
                    foodStackTFC.addThirst(getFluid(stack) == FluidsTFC.FRESH_WATER ? 50 : 0);
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

    private void onDrink(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (Constants.RNG.nextFloat() < BREAK_CHANCE)
        {
            worldIn.playSound(player, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            stack.shrink(1);
        }
    }

    private void empty(ItemStack stack)
    {
        setFluid(stack, null);
        setJustFilled(stack, false);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        FoodStats foodStats = playerIn.getFoodStats();

        final ActionResult<ItemStack> FAILED = new ActionResult<>(EnumActionResult.FAIL, stack);

        if (!(foodStats instanceof FoodStatsTFC) || !isFilled(stack) || isJustFilled(stack))
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
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
    	ItemJug jug = (ItemJug) stack.getItem();
    	if(jug.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
    	{
	    	if(nbt == null)
	    	{
	    		nbt = new NBTTagCompound();
	    	}
	    	NBTTagCompound tank;
    		if(!nbt.hasKey("tank"))
    		{
    			nbt.setTag("tank", tank = new NBTTagCompound());
    		}
    		if(!(tank = nbt.getCompoundTag("tank")).hasKey("Filled"))
    		{
    			tank.setBoolean("Filled", false);
    		}
    		if(!tank.hasKey("Fluid"))
    		{
    			tank.setString("Fluid", "None");
    		}
    		if(!tank.hasKey("JustFilled"))
    		{
    			tank.setBoolean("JustFilled", false);
    		}
    		nbt.setTag("tank", tank);
            stack.setTagCompound(nbt);
    	}
        return super.initCapabilities(stack, nbt);
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
    	return null;
    }

}
