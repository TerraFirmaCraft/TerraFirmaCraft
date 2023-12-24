package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.wood.SewingTableBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.IntArrayBuilder;

public class SewingTableContainer extends Container implements ISlotCallback, ButtonHandlerContainer
{
    public static SewingTableContainer create(Inventory playerInventory, int windowId, ContainerLevelAccess access)
    {
        return new SewingTableContainer(playerInventory, windowId, access).init(playerInventory, 30);
    }

    public static final int NUM_SLOTS = 3;
    public static final int SLOT_YARN = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_RESULT = 2;
    public static final int BURLAP_ID = 0;
    public static final int WOOL_ID = 1;
    public static final int REMOVE_ID = 2;
    public static final int NEEDLE_ID = 3;

    public static final int PLACED_SLOTS = 32;
    public static final int PLACED_SLOTS_OFFSET = 100;

    private final ItemStackHandler inventory;
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    private final DataSlot activeMaterialData = DataSlot.standalone();
    private final ContainerData placedMaterialData = new SimpleContainerData(PLACED_SLOTS);

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
        addDataSlot(activeMaterialData).set(-1);
        for (int i = 0; i < PLACED_SLOTS; i++)
            placedMaterialData.set(i, -1);
        addDataSlots(placedMaterialData);
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
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        int activeMaterial = getActiveMaterial();
        if (buttonID == REMOVE_ID)
        {
            activeMaterial = -1;
        }
        else if (buttonID == BURLAP_ID || buttonID == NEEDLE_ID || buttonID == WOOL_ID)
        {
            activeMaterial = activeMaterial == buttonID ? -1 : buttonID;
        }
        else if (buttonID - PLACED_SLOTS_OFFSET >= 0 && buttonID - PLACED_SLOTS_OFFSET < PLACED_SLOTS && (activeMaterial == BURLAP_ID || activeMaterial == WOOL_ID || activeMaterial == -1))
        {
            placedMaterialData.set(buttonID - PLACED_SLOTS_OFFSET, activeMaterial);
        }
        activeMaterialData.set(activeMaterial);
        broadcastChanges();
    }

    public int getActiveMaterial()
    {
        return activeMaterialData.get();
    }

    public int getPlacedMaterial(int slot)
    {
        return placedMaterialData.get(slot);
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
        addSlot(new CallbackSlot(this, inventory, SLOT_RESULT, 152, 83));
    }
}
