/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class ItemBlockBarrel extends ItemBlockTFC
{
    public ItemBlockBarrel(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    @Nonnull
    public String getTranslationKey(@Nonnull ItemStack stack)
    {
        if (stack.getMetadata() == 1)
        {
            return super.getTranslationKey() + ".sealed";
        }

        return super.getTranslationKey();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getMetadata() == 1)
        {
            NBTTagCompound compound = stack.getTagCompound();

            if (compound != null)
            {
                FluidTank tank = new FluidTank(0).readFromNBT(compound.getCompoundTag("tank"));
                ItemStackHandler stackHandler = new ItemStackHandler();
                stackHandler.deserializeNBT(compound.getCompoundTag("inventory"));
                ItemStack inventory = stackHandler.getStackInSlot(TEBarrel.SLOT_ITEM);

                if (tank.getFluid() == null || tank.getFluidAmount() == 0)
                {
                    if (inventory.isEmpty())
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_empty"));
                    }
                    else
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_item", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                    }
                }
                else
                {
                    tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_fluid", tank.getFluidAmount(), tank.getFluid().getLocalizedName()));

                    if (!inventory.isEmpty())
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_item_in_fluid", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem(hand).getMetadata() == 0)
        {
            pos = pos.offset(facing); //Since the clicked facing is the block bellow fluids
            IBlockState state = worldIn.getBlockState(pos);
            IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, facing);
            if (handler != null && handler.drain(Fluid.BUCKET_VOLUME, false) != null)
            {
                FluidTank tank = new FluidTank(TEBarrel.TANK_CAPACITY);
                boolean canCreateSources = false; //default
                if (state.getBlock() instanceof BlockFluidClassic)
                {
                    BlockFluidClassic fluidblock = (BlockFluidClassic) worldIn.getBlockState(pos).getBlock();
                    canCreateSources = ReflectionHelper.getPrivateValue(BlockFluidClassic.class, fluidblock, "canCreateSources");
                }
                else if (state.getBlock() instanceof BlockLiquid)
                {
                    //Fire the event so other mods that prevent infinite water disable this
                    canCreateSources = ForgeEventFactory.canCreateFluidSource(worldIn, pos, state, state.getMaterial() == Material.WATER);
                }
                FluidStack fluidStack = handler.drain(Fluid.BUCKET_VOLUME, true);
                if (canCreateSources && fluidStack != null)
                {
                    fluidStack.amount = TEBarrel.TANK_CAPACITY;
                }
                tank.fill(fluidStack, true);

                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
                nbt.setTag("inventory", new ItemStackHandler(3).serializeNBT());

                nbt.setLong("sealedTick", CalendarTFC.TOTAL_TIME.getTicks());
                nbt.setLong("sealedCalendarTick", CalendarTFC.CALENDAR_TIME.getTicks());
                ItemStack stack = new ItemStack(player.getHeldItem(hand).getItem(), 1, 1);
                stack.setTagCompound(nbt);
                player.setHeldItem(hand, stack);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
    {
        if (player.getHeldItem(hand).getMetadata() == 0)
        {
            RayTraceResult rayTrace = rayTrace(worldIn, player, true);
            //noinspection ConstantConditions - ray trace can be null
            if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = rayTrace.getBlockPos();
                IBlockState state = worldIn.getBlockState(pos);
                IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, rayTrace.sideHit);
                if (handler != null && handler.drain(Fluid.BUCKET_VOLUME, false) != null)
                {
                    FluidTank tank = new FluidTank(TEBarrel.TANK_CAPACITY);
                    boolean canCreateSources = false; //default
                    if (state.getBlock() instanceof BlockFluidClassic)
                    {
                        BlockFluidClassic fluidblock = (BlockFluidClassic) worldIn.getBlockState(pos).getBlock();
                        canCreateSources = ReflectionHelper.getPrivateValue(BlockFluidClassic.class, fluidblock, "canCreateSources");
                    }
                    else if (state.getBlock() instanceof BlockLiquid)
                    {
                        //Fire the event so other mods that prevent infinite water disable this
                        canCreateSources = ForgeEventFactory.canCreateFluidSource(worldIn, pos, state, state.getMaterial() == Material.WATER);
                    }
                    FluidStack fluidStack = handler.drain(Fluid.BUCKET_VOLUME, true);
                    if (canCreateSources && fluidStack != null)
                    {
                        fluidStack.amount = TEBarrel.TANK_CAPACITY;
                    }
                    tank.fill(fluidStack, true);

                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
                    nbt.setTag("inventory", new ItemStackHandler(3).serializeNBT());

                    nbt.setLong("sealedTick", CalendarTFC.TOTAL_TIME.getTicks());
                    nbt.setLong("sealedCalendarTick", CalendarTFC.CALENDAR_TIME.getTicks());
                    ItemStack stack = new ItemStack(player.getHeldItem(hand).getItem(), 1, 1);
                    stack.setTagCompound(nbt);
                    player.setHeldItem(hand, stack);
                    return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
                }
            }
        }
        return super.onItemRightClick(worldIn, player, hand);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.HUGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return stack.getMetadata() == 0;
    }
}
