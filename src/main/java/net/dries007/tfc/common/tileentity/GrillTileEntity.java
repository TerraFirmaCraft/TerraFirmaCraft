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

import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.common.types.FuelManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GrillTileEntity extends FirepitTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.grill");

    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    public GrillTileEntity()
    {
        this(TFCTileEntities.GRILL.get(), 9, NAME);
    }

    public GrillTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    public void onRemoveGrill()
    {
        BlockPos pos = worldPosition;
        if (level == null) return;
        for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
        {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, inventory.getStackInSlot(i)));
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
            //todo: grill restrictions
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
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        return new GrillContainer(this, p_createMenu_2_, p_createMenu_1_);
    }
}
