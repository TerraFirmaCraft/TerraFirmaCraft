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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.objects.te.TEBarrel.BARREL_MAX_FLUID_TEMPERATURE;

/**
 * Item block for {@link BlockBarrel}
 * Only has NBT data if the barrel is sealed and has contents
 */
@ParametersAreNonnullByDefault
public class ItemBlockBarrel extends ItemBlockTFC
{
    public ItemBlockBarrel(Block block)
    {
        super(block);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem(hand).getTagCompound() == null)
        {
            pos = pos.offset(facing); //Since the clicked facing is the block bellow fluids
            IBlockState state = worldIn.getBlockState(pos);
            IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, facing);
            if (handler != null && handler.drain(Fluid.BUCKET_VOLUME, false) != null)
            {
                //noinspection ConstantConditions
                Fluid fluid = handler.drain(Fluid.BUCKET_VOLUME, false).getFluid();
                if (fluid.getTemperature() < BARREL_MAX_FLUID_TEMPERATURE)
                {
                    FluidTank tank = new FluidTank(TEBarrel.TANK_CAPACITY);
                    boolean canCreateSources = false; //default
                    if (state.getBlock() instanceof BlockFluidClassic)
                    {
                        BlockFluidClassic fluidblock = (BlockFluidClassic) worldIn.getBlockState(pos).getBlock();
                        canCreateSources = ObfuscationReflectionHelper.getPrivateValue(BlockFluidClassic.class, fluidblock, "canCreateSources");
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

                    nbt.setLong("sealedTick", CalendarTFC.PLAYER_TIME.getTicks());
                    nbt.setLong("sealedCalendarTick", CalendarTFC.CALENDAR_TIME.getTicks());
                    ItemStack stack = new ItemStack(player.getHeldItem(hand).getItem());
                    stack.setTagCompound(nbt);
                    player.getHeldItem(hand).shrink(1);
                    if (player.getHeldItem(hand).isEmpty())
                    {
                        player.setHeldItem(hand, stack);
                    }
                    else
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, stack);
                    }

                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @Nonnull
    public String getTranslationKey(@Nonnull ItemStack stack)
    {
        return stack.getTagCompound() != null ? super.getTranslationKey() + ".sealed" : super.getTranslationKey();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null)
        {
            FluidTank tank = new FluidTank(0).readFromNBT(nbt.getCompoundTag("tank"));
            ItemStackHandler stackHandler = new ItemStackHandler();
            stackHandler.deserializeNBT(nbt.getCompoundTag("inventory"));
            ItemStack inventory = stackHandler.getStackInSlot(TEBarrel.SLOT_ITEM);

            if (tank.getFluid() == null || tank.getFluidAmount() == 0)
            {
                if (inventory.isEmpty())
                {
                    tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.barrel_empty"));
                }
                else
                {
                    tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.barrel_item", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                }
            }
            else
            {
                tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.barrel_fluid", tank.getFluidAmount(), tank.getFluid().getLocalizedName()));

                if (!inventory.isEmpty())
                {
                    tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.barrel_item_in_fluid", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                }
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
    {
        if (player.getHeldItem(hand).getTagCompound() != null)
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
                        canCreateSources = ObfuscationReflectionHelper.getPrivateValue(BlockFluidClassic.class, fluidblock, "canCreateSources");
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

                    nbt.setLong("sealedTick", CalendarTFC.PLAYER_TIME.getTicks());
                    nbt.setLong("sealedCalendarTick", CalendarTFC.CALENDAR_TIME.getTicks());
                    ItemStack stack = new ItemStack(player.getHeldItem(hand).getItem());
                    stack.setTagCompound(nbt);
                    player.setHeldItem(hand, stack);
                    return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
                }
            }
        }
        return super.onItemRightClick(worldIn, player, hand);
    }
}
