package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.common.types.FuelManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GrillTileEntity extends FirepitTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.grill");

    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    private final HeatingRecipe[] cachedGrillRecipes;

    public GrillTileEntity()
    {
        this(TFCTileEntities.GRILL.get(), 9, NAME);
    }

    public GrillTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
        cachedGrillRecipes = new HeatingRecipe[5];
    }

    @Override
    protected void handleCooking()
    {
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
        {
            ItemStack inputStack = inventory.getStackInSlot(slot);
            int finalSlot = slot;
            inputStack.getCapability(HeatCapability.CAPABILITY, null).ifPresent(cap -> {
                float itemTemp = cap.getTemperature();
                if (temperature > itemTemp)
                    HeatCapability.addTemp(cap);
                HeatingRecipe recipe = cachedGrillRecipes[finalSlot - SLOT_EXTRA_INPUT_START];
                if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                {
                    ItemStack output = recipe.assemble(new ItemStackRecipeWrapper(inputStack));
                    //todo: apply trait WOOD_GRILLED
                    inventory.setStackInSlot(finalSlot, output);
                    markForSync();
                }
            });
        }
    }

    @Override
    protected void handleQuenching()
    {
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
            inventory.getStackInSlot(slot).getCapability(HeatCapability.CAPABILITY, null).ifPresent(cap -> cap.setTemperature(0f));
    }

    @Override
    public void updateCache()
    {
        if (level == null) return;
        for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
        {
            cachedGrillRecipes[i - SLOT_EXTRA_INPUT_START] = HeatingRecipe.getRecipe(level, new ItemStackRecipeWrapper(inventory.getStackInSlot(i)));
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
        //todo: grill input restrictions?
        if (slot == SLOT_FUEL_INPUT)
        {
            return FuelManager.get(stack).isPresent();
        }
        return slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END;
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new GrillContainer(this, playerInv, windowID);
    }
}
