/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.objects.fluids.capability.FluidHandlerSided;
import net.dries007.tfc.objects.fluids.capability.FluidTankCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidHandlerSidedCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidTankCallback;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.objects.items.itemblock.ItemBlockBarrel;
import net.dries007.tfc.util.FluidTransferHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarFormatted;
import net.dries007.tfc.util.calendar.ICalendarTickable;

import static net.dries007.tfc.objects.blocks.wood.BlockBarrel.SEALED;

@ParametersAreNonnullByDefault
public class TEBarrel extends TETickableInventory implements ITickable, ICalendarTickable, IItemHandlerSidedCallback, IFluidHandlerSidedCallback, IFluidTankCallback
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    public static final int BARREL_MAX_FLUID_TEMPERATURE = 500;

    private final FluidTank tank = new BarrelFluidTank(this, 0);
    private final Queue<ItemStack> surplus = new LinkedList<>(); // Surplus items from a recipe with output > stackSize
    private boolean sealed;
    private long sealedTick, sealedCalendarTick;
    private long lastPlayerTick; // Last player tick this barrel was ticked (for purposes of catching up)
    private BarrelRecipe recipe;
    private int tickCounter;
    private boolean checkInstantRecipe = false;

    public TEBarrel()
    {
        super(3);
    }

    /**
     * Save up item and fluid handler contents to a barrel's ItemStack
     *
     * @param stack the barrel's stack to save contents to
     */
    public void saveToItemStack(ItemStack stack)
    {
        IFluidHandler barrelCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (barrelCap instanceof ItemBlockBarrel.ItemBarrelFluidHandler)
        {
            NBTTagCompound inventoryTag = null;
            // Check if inventory has contents
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                if (!inventory.getStackInSlot(i).isEmpty())
                {
                    inventoryTag = inventory.serializeNBT();
                    break;
                }
            }
            NBTTagList surplusTag = null;
            // Check if there's remaining surplus from recipe
            if (!surplus.isEmpty())
            {
                surplusTag = new NBTTagList();
                for (ItemStack surplusStack : surplus)
                {
                    surplusTag.appendTag(surplusStack.serializeNBT());
                }
            }
            FluidStack storing = tank.getFluid();
            if (storing != null || inventoryTag != null || surplusTag != null)
            {
                ((ItemBlockBarrel.ItemBarrelFluidHandler) barrelCap).setBarrelContents(storing, inventoryTag, surplusTag, sealedTick, sealedCalendarTick);
            }
        }
    }

    /**
     * Load up item and fluid handler contents from a barrel's ItemStack
     *
     * @param stack the barrel's stack to load contents from
     */
    public void loadFromItemStack(ItemStack stack)
    {
        IFluidHandler barrelCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (barrelCap instanceof ItemBlockBarrel.ItemBarrelFluidHandler)
        {
            NBTTagCompound contents = ((ItemBlockBarrel.ItemBarrelFluidHandler) barrelCap).getBarrelContents();
            if (contents != null)
            {
                inventory.deserializeNBT(contents.getCompoundTag("inventory"));
                surplus.clear();
                NBTTagList surplusItems = contents.getTagList("surplus", Constants.NBT.TAG_COMPOUND);
                if (!surplusItems.isEmpty())
                {
                    for (int i = 0; i < surplusItems.tagCount(); i++)
                    {
                        surplus.add(new ItemStack(surplusItems.getCompoundTagAt(i)));
                    }
                }
                sealedTick = contents.getLong("sealedTick");
                sealedCalendarTick = contents.getLong("sealedCalendarTick");
                tank.fill(((ItemBlockBarrel.ItemBarrelFluidHandler) barrelCap).getFluid(), true);
                sealed = true;
                recipe = BarrelRecipe.get(inventory.getStackInSlot(SLOT_ITEM), tank.getFluid());
                markForSync();
            }
        }
    }

    /**
     * Called once per side when the TileEntity has finished loading.
     * On servers, this is the earliest point in time to safely access the TE's World object.
     */
    @Override
    public void onLoad()
    {
        if (!world.isRemote)
        {
            sealed = world.getBlockState(pos).getValue(SEALED);
            recipe = BarrelRecipe.get(inventory.getStackInSlot(SLOT_ITEM), tank.getFluid());
        }
    }

    @Nullable
    public BarrelRecipe getRecipe()
    {
        return recipe;
    }

    @Nonnull
    public String getSealedDate()
    {
        return ICalendarFormatted.getTimeAndDate(sealedCalendarTick, CalendarTFC.CALENDAR_TIME.getDaysInMonth());
    }

    @Override
    public void setAndUpdateFluidTank(int fluidTankID)
    {
        markForSync();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return !sealed && (isItemValid(slot, stack) || side == null && slot == SLOT_FLUID_CONTAINER_OUT);
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return !sealed && (side == null || slot != SLOT_FLUID_CONTAINER_IN);
    }

    @Override
    public boolean canFill(FluidStack resource, EnumFacing side)
    {
        return !sealed && (resource.getFluid() == null || resource.getFluid().getTemperature(resource) < BARREL_MAX_FLUID_TEMPERATURE);
    }

    @Override
    public boolean canDrain(EnumFacing side)
    {
        return !sealed;
    }

    public void onSealed()
    {
        sealedTick = CalendarTFC.PLAYER_TIME.getTicks();
        sealedCalendarTick = CalendarTFC.CALENDAR_TIME.getTicks();
        recipe = BarrelRecipe.get(inventory.getStackInSlot(SLOT_ITEM), tank.getFluid());
        if (recipe != null)
        {
            recipe.onBarrelSealed(tank.getFluid(), inventory.getStackInSlot(SLOT_ITEM));
        }
        sealed = true;
        markForSync();
    }

    public void onUnseal()
    {
        sealedTick = sealedCalendarTick = 0;
        if (recipe != null)
        {
            ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
            FluidStack inputFluid = tank.getFluid();
            if (recipe.isValidInput(inputFluid, inputStack))
            {
                tank.setFluid(recipe.getOutputFluidOnUnseal(inputFluid, inputStack));
                List<ItemStack> output = recipe.getOutputItemOnUnseal(inputFluid, inputStack);
                ItemStack first = output.get(0);
                output.remove(0);
                inventory.setStackInSlot(SLOT_ITEM, first);
                surplus.addAll(output);
            }
        }
        recipe = null;
        sealed = false;
        markForSync();
    }

    @Override
    public void update()
    {
        super.update();
        checkForCalendarUpdate();
        if (!world.isRemote)
        {
            tickCounter++;
            if (tickCounter == 10)
            {
                tickCounter = 0;

                ItemStack fluidContainerIn = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
                FluidActionResult result = FluidTransferHelper.emptyContainerIntoTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, ConfigTFC.Devices.BARREL.tank, world, pos);

                if (!result.isSuccess())
                {
                    result = FluidTransferHelper.fillContainerFromTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, ConfigTFC.Devices.BARREL.tank, world, pos);
                }

                if (result.isSuccess())
                {
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, result.getResult());
                }

                Fluid freshWater = FluidRegistry.getFluid("fresh_water");

                if (!sealed && world.isRainingAt(pos.up()) && (tank.getFluid() == null || tank.getFluid().getFluid() == freshWater))
                {
                    tank.fill(new FluidStack(freshWater, 10), true);
                }

                if (inventory.getStackInSlot(SLOT_ITEM) == ItemStack.EMPTY && !surplus.isEmpty())
                {
                    inventory.setStackInSlot(SLOT_ITEM, surplus.poll());
                }
            }

            // Check if recipe is complete (sealed recipes only)
            if (recipe != null && sealed)
            {
                int durationSealed = (int) (CalendarTFC.PLAYER_TIME.getTicks() - sealedTick);
                if (recipe.getDuration() > 0 && durationSealed > recipe.getDuration())
                {
                    ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
                    FluidStack inputFluid = tank.getFluid();
                    if (recipe.isValidInput(inputFluid, inputStack))
                    {
                        tank.setFluid(recipe.getOutputFluid(inputFluid, inputStack));
                        List<ItemStack> output = recipe.getOutputItem(inputFluid, inputStack);
                        ItemStack first = output.get(0);
                        output.remove(0);
                        inventory.setStackInSlot(SLOT_ITEM, first);
                        surplus.addAll(output);
                        markForSync();
                        onSealed(); //run the sealed check again in case we have a new valid recipe.
                    }
                    else
                    {
                        recipe = null;
                    }
                }
            }

            if (checkInstantRecipe)
            {
                ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
                FluidStack inputFluid = tank.getFluid();
                BarrelRecipe instantRecipe = BarrelRecipe.getInstant(inputStack, inputFluid);
                if (instantRecipe != null && inputFluid != null && instantRecipe.isValidInputInstant(inputStack, inputFluid))
                {
                    tank.setFluid(instantRecipe.getOutputFluid(inputFluid, inputStack));
                    List<ItemStack> output = instantRecipe.getOutputItem(inputFluid, inputStack);
                    ItemStack first = output.get(0);
                    output.remove(0);
                    inventory.setStackInSlot(SLOT_ITEM, first);
                    surplus.addAll(output);
                    instantRecipe.onRecipeComplete(world, pos);
                    markForSync();
                }
                else
                {
                    checkInstantRecipe = false;
                }
            }
        }
    }

    public boolean isSealed()
    {
        return sealed;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        checkInstantRecipe = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        tank.readFromNBT(nbt.getCompoundTag("tank"));
        if (tank.getFluidAmount() > tank.getCapacity())
        {
            // Fix config changes
            FluidStack fluidStack = tank.getFluid();
            if (fluidStack != null)
            {
                fluidStack.amount = tank.getCapacity();
            }
            tank.setFluid(fluidStack);
        }
        sealedTick = nbt.getLong("sealedTick");
        sealedCalendarTick = nbt.getLong("sealedCalendarTick");
        sealed = nbt.getBoolean("sealed");
        lastPlayerTick = nbt.getLong("lastPlayerTick");

        surplus.clear();
        if (nbt.hasKey("surplus"))
        {
            NBTTagList surplusItems = nbt.getTagList("surplus", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < surplusItems.tagCount(); i++)
            {
                surplus.add(new ItemStack(surplusItems.getCompoundTagAt(i)));
            }
        }

        recipe = BarrelRecipe.get(inventory.getStackInSlot(SLOT_ITEM), tank.getFluid());
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        nbt.setLong("sealedTick", sealedTick);
        nbt.setLong("sealedCalendarTick", sealedCalendarTick);
        nbt.setBoolean("sealed", sealed);
        nbt.setLong("lastPlayerTick", lastPlayerTick);

        if (!surplus.isEmpty())
        {
            NBTTagList surplusList = new NBTTagList();
            for (ItemStack stack : surplus)
            {
                surplusList.appendTag(stack.serializeNBT());
            }
            nbt.setTag("surplus", surplusList);
        }

        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) new ItemHandlerSidedWrapper(this, inventory, facing);
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) new FluidHandlerSided(this, tank, facing);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (state.getValue(SEALED))
        {
            ItemStack barrelStack = new ItemStack(state.getBlock());

            if (recipe != null)
            {
                // Drop the sealed barrel with inventory only if it's a valid recipe.
                saveToItemStack(barrelStack);
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), barrelStack);
            }
            else
            {
                // Drop the sealed barrel minus inventory if there is no recipe.
                int slotsToDrop = inventory.getSlots();
                for (int i = 0; i < slotsToDrop; i++)
                {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
                    inventory.setStackInSlot(i, new ItemStack(Items.AIR, 0));
                }
                if (!surplus.isEmpty())
                {
                    for (ItemStack surplusToDrop : surplus)
                    {
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), surplusToDrop);
                    }
                    surplus.clear();
                }
                saveToItemStack(barrelStack);
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), barrelStack);
            }
        }
        else
        {
            // Drop contents only, actual barrel will be dropped normally
            super.onBreakBlock(world, pos, state);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FLUID_CONTAINER_IN:
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            case SLOT_ITEM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCalendarUpdate(long deltaPlayerTicks)
    {
        while (deltaPlayerTicks > 0)
        {
            deltaPlayerTicks = 0;
            if (recipe != null && sealed && recipe.getDuration() > 0)
            {
                long tickFinish = sealedTick + recipe.getDuration();
                if (tickFinish <= CalendarTFC.PLAYER_TIME.getTicks())
                {
                    // Mark to run this transaction again in case this recipe produces valid output for another which could potentially finish in this time period.
                    deltaPlayerTicks = 1;
                    long offset = tickFinish - CalendarTFC.PLAYER_TIME.getTicks();

                    CalendarTFC.runTransaction(offset, offset, () -> {
                        ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
                        FluidStack inputFluid = tank.getFluid();
                        if (recipe.isValidInput(inputFluid, inputStack))
                        {
                            tank.setFluid(recipe.getOutputFluid(inputFluid, inputStack));
                            List<ItemStack> output = recipe.getOutputItem(inputFluid, inputStack);
                            ItemStack first = output.get(0);
                            output.remove(0);
                            inventory.setStackInSlot(SLOT_ITEM, first);
                            surplus.addAll(output);
                            markForSync();
                            onSealed(); //run the sealed check again in case we have a new valid recipe.
                        }
                        else
                        {
                            recipe = null;
                        }
                    });
                }
            }
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        this.lastPlayerTick = tick;
    }

    protected static class BarrelFluidTank extends FluidTankCallback
    {
        private final Set<Fluid> whitelist;

        public BarrelFluidTank(IFluidTankCallback callback, int fluidTankID)
        {
            super(callback, fluidTankID, ConfigTFC.Devices.BARREL.tank);
            whitelist = Arrays.stream(ConfigTFC.Devices.BARREL.fluidWhitelist).map(FluidRegistry::getFluid).filter(Objects::nonNull).collect(Collectors.toSet());
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            return fluid != null && (whitelist.contains(fluid.getFluid()) || BarrelRecipe.isBarrelFluid(fluid));
        }
    }
}
