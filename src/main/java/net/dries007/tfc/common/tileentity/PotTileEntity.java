package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotTileEntity extends FirepitTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.pot");

    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    public PotTileEntity()
    {
        this(TFCTileEntities.POT.get(), 9, NAME);
    }

    public PotTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected void handleCooking()
    {

    }

    public void onRemovePot()
    {
        if (level == null) return;
        for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
        {
            Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i), 0.7D);
        }
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_FUEL_INPUT)
        {
            return FuelManager.isItemFuel(stack);
        }
        else
        {
            //todo: soup restrictions
            return true;
        }
    }

    @Override
    public void clearContent()
    {
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_EXTRA_INPUT_END; i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new PotContainer(this, playerInv, windowID);
    }
}
