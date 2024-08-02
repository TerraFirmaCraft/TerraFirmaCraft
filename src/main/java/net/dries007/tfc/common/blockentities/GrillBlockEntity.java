/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class GrillBlockEntity extends AbstractFirepitBlockEntity<ItemStackHandler>
{
    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    private final HeatingRecipe[] cachedRecipes;

    public GrillBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.GRILL.get(), pos, state, defaultInventory(9));

        cachedRecipes = new HeatingRecipe[5];

        if (TFCConfig.SERVER.firePitEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(SLOT_FUEL_INPUT).extract(4, 5, 6, 7, 8), Direction.Plane.HORIZONTAL)
                .on(new PartialItemHandler(inventory).insert(4, 5, 6, 7, 8), Direction.UP);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player)
    {
        return GrillContainer.create(this, playerInv, windowID);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END)
        {
            return HeatCapability.has(stack);
        }
        return super.isItemValid(slot, stack);
    }

    @Override
    protected void handleCooking()
    {
        assert level != null;
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
        {
            final ItemStack inputStack = inventory.getStackInSlot(slot);
            final @Nullable IHeat inputHeat = HeatCapability.get(inputStack);
            if (inputHeat != null)
            {
                HeatCapability.addTemp(inputHeat, temperature);
                HeatingRecipe recipe = cachedRecipes[slot - SLOT_EXTRA_INPUT_START];
                if (recipe != null && recipe.isValidTemperature(inputHeat.getTemperature()))
                {
                    ItemStack output = recipe.assembleItem(inputStack);
                    FoodCapability.applyTrait(output, FoodTraits.WOOD_GRILLED);
                    inventory.setStackInSlot(slot, output);
                    markForSync();
                }
            }
        }
    }

    @Override
    protected void coolInstantly()
    {
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            HeatCapability.setTemperature(stack, 0);
        }
    }

    @Override
    protected void updateCachedRecipe()
    {
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);
            cachedRecipes[slot - SLOT_EXTRA_INPUT_START] = stack.isEmpty() ? null : HeatingRecipe.getRecipe(stack);
        }
    }

}
