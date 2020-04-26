/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.IEgg;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class TENestBox extends TEInventory implements ITickable, IItemHandlerSidedCallback
{
    private static final int NUM_SLOTS = 4;
    private final IItemHandler inventoryWrapperExtract;

    public TENestBox()
    {
        super(NUM_SLOTS);
        this.inventoryWrapperExtract = new ItemHandlerSidedWrapper(this, inventory, EnumFacing.DOWN);
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    IEgg cap = stack.getCapability(CapabilityEgg.CAPABILITY, null);
                    if (cap != null && cap.getHatchDay() > 0 && cap.getHatchDay() <= CalendarTFC.PLAYER_TIME.getTotalDays())
                    {
                        Entity baby = cap.getEntity(this.world);
                        if (baby != null)
                        {
                            if (baby instanceof IAnimalTFC)
                            {
                                ((IAnimalTFC) baby).setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
                            }
                            baby.setLocationAndAngles(this.pos.getX(), this.pos.getY() + 0.5D, this.pos.getZ(), 0.0F, 0.0F);
                            world.spawnEntity(baby);
                            inventory.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() == Items.EGG;
    }

    public void insertEgg(ItemStack stack)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.insertItem(i, stack, false).isEmpty())
            {
                return;
            }
        }
    }

    public boolean hasFreeSlot()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.getStackInSlot(i).isEmpty())
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasBird()
    {
        return getBird() != null;
    }

    public void seatOnThis(EntityLiving bird)
    {
        Helpers.sitOnBlock(this.world, this.pos, bird, 0.0D);
    }

    @Nullable
    public Entity getBird()
    {
        return Helpers.getSittingEntity(this.world, this.pos);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) ?
            (T) inventoryWrapperExtract : super.getCapability(capability, facing);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
    }
}
