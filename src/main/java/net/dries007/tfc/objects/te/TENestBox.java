package net.dries007.tfc.objects.te;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.IEgg;
import net.dries007.tfc.objects.entity.animal.EntityAnimalOviparous;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class TENestBox extends TEInventory implements ITickable
{
    private static final int NUM_SLOTS = 4;

    public TENestBox()
    {
        super(NUM_SLOTS);
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
                    if (cap != null && cap.getHatchDay() > 0 && cap.getHatchDay() <= CalendarTFC.INSTANCE.getTotalDays())
                    {
                        Entity baby = cap.getEntity(this.world);
                        if (baby instanceof EntityAnimalOviparous)
                        {
                            ((EntityAnimalOviparous) baby).setBirthDay((int) cap.getHatchDay());
                        }
                        baby.setLocationAndAngles(this.pos.getX(), this.pos.getY() + 0.5D, this.pos.getZ(), 0.0F, 0.0F);
                        this.world.spawnEntity(baby);
                        inventory.setStackInSlot(i, ItemStack.EMPTY);
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
        return stack.getItem() == ItemsTFC.EGG;
    }

    public boolean insertEgg(ItemStack stack)
    {
        stack.setCount(1);
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.insertItem(i, stack, false).isEmpty())
            {
                return true;
            }
        }
        return false;
    }
}
