package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.wood.SewingTableBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.items.TFCItems;

public class SewingTableContainer extends Container implements ISlotCallback
{
    public static SewingTableContainer create(Inventory playerInventory, int windowId, ContainerLevelAccess access)
    {
        return new SewingTableContainer(playerInventory, windowId, access).init(playerInventory, 30);
    }

    public static final int NUM_SLOTS = 3;
    public static final int SLOT_YARN = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_RESULT = 2;

    private final ItemStackHandler inventory;
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;

    public SewingTableContainer(Inventory playerInventory, int windowId)
    {
        this(playerInventory, windowId, ContainerLevelAccess.NULL);
    }

    public SewingTableContainer(Inventory playerInventory, int windowId, ContainerLevelAccess access)
    {
        super(TFCContainerTypes.SEWING_TABLE.get(), windowId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.inventory = new InventoryItemHandler(this, NUM_SLOTS);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_YARN)
        {
            return stack.getItem() == TFCItems.WOOL_YARN.get();
        }
        if (slot == SLOT_RESULT)
        {
            return false;
        }
        return true;
    }

    @Override
    public void removed(Player player)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            final ItemStack stack = inventory.getStackInSlot(i);
            giveItemStackToPlayerOrDrop(player, stack);
        }
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof SewingTableBlock && player.position().distanceToSqr(pos.getCenter()) < 64, true);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch(typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, SLOT_YARN, NUM_SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        super.addContainerSlots();
        addSlot(new CallbackSlot(this, inventory, SLOT_YARN, 11, 83));
        addSlot(new CallbackSlot(this, inventory, SLOT_TOOL, 36, 83));
        addSlot(new CallbackSlot(this, inventory, SLOT_RESULT, 152, 44));
    }
}
