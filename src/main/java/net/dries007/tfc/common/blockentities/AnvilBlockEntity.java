/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.devices.Tiered;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.IForging;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;

public class AnvilBlockEntity extends TickableInventoryBlockEntity<AnvilBlockEntity.AnvilInventory> implements ISlotCallback
{
    public static final int SLOT_INPUT_MAIN = 0;
    public static final int SLOT_INPUT_SECOND = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_CATALYST = 3;

    public static final int DATA_SLOT_TARGET = 0;

    private static final Component NAME = new TranslatableComponent("tfc.block_entity.crucible");

    private final ContainerData syncableData;
    private int workTarget; // The target to work, only for client purposes
    private int workValue; // The current work progress of the item, only for client display purposes

    public AnvilBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.ANVIL.get(), pos, state, AnvilInventory::new, NAME);

        syncableData = new IntArrayBuilder()
            .add(() -> workTarget, value -> workTarget = value)
            .add(() -> workValue, value -> workValue = value);
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        // todo: valid item checks
        return switch (slot) {
            case SLOT_INPUT_MAIN, SLOT_INPUT_SECOND -> true;
            case SLOT_HAMMER -> true;
            case SLOT_CATALYST -> true;
            default -> false;
        };
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return slot == SLOT_CATALYST ? 64 : 1;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
    }

    public void work(ServerPlayer player, ForgeStep step)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        final IForging forge = ForgingCapability.get(stack);
        if (forge != null)
        {
            // Prevent the player from immediately destroying the item by overworking
            if (!forge.getSteps().any() && forge.getWork() == 0 && step.step() < 0)
            {
                return;
            }

            final AnvilRecipe recipe = forge.getRecipe(level);
            if (recipe != null)
            {
                // todo: temp check

                // Proceed with working
                forge.addStep(step);
                if (forge.overworked())
                {

                    return;
                }

                // Re-check anvil recipe completion
                final AnvilRecipe.Result post = recipe.checkComplete(inventory);
                if (post == AnvilRecipe.Result.SUCCESS)
                {
                    // Recipe completed, so consume inputs and add outputs
                    // Always preserve heat
                    final ItemStack outputStack = recipe.assemble(inventory);

                    inventory.setStackInSlot(SLOT_INPUT_MAIN, outputStack);
                }
            }
        }
    }

    public boolean weld(ServerPlayer player)
    {
        final ItemStack left = inventory.getLeft(), right = inventory.getRight();
        if (left.isEmpty() && right.isEmpty())
        {
            return false;
        }

        assert level != null;

        final WeldingRecipe recipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.WELDING.get(), inventory, level).orElse(null);
        if (recipe != null)
        {
            final ItemStack result = recipe.assemble(inventory);

            inventory.setStackInSlot(SLOT_INPUT_MAIN, result);
            inventory.setStackInSlot(SLOT_INPUT_SECOND, ItemStack.EMPTY);
            inventory.getStackInSlot(SLOT_CATALYST).shrink(1);

            // Always copy heat from inputs since we have two
            result.getCapability(HeatCapability.CAPABILITY).ifPresent(resultHeat -> resultHeat.setTemperatureIfWarmer(Math.max(
                HeatCapability.getTemperature(left),
                HeatCapability.getTemperature(right)
            )));

            markForSync();
            return true;
        }
        return false;
    }

    public int getTier()
    {
        return getBlockState().getBlock() instanceof Tiered tiered ? tiered.getTier() : 0;
    }

    public static class AnvilInventory extends InventoryItemHandler implements AnvilRecipe.Inventory, WeldingRecipe.Inventory
    {
        private final AnvilBlockEntity anvil;

        public AnvilInventory(InventoryBlockEntity<AnvilInventory> anvil)
        {
            super(anvil, 4);
            this.anvil = (AnvilBlockEntity) anvil;
        }

        @Override
        public ItemStack getItem()
        {
            return getStackInSlot(SLOT_INPUT_MAIN);
        }

        @Override
        public ItemStack getLeft()
        {
            return getStackInSlot(SLOT_INPUT_MAIN);
        }

        @Override
        public ItemStack getRight()
        {
            return getStackInSlot(SLOT_INPUT_SECOND);
        }

        @Override
        public ItemStack getCatalyst()
        {
            return getStackInSlot(SLOT_CATALYST);
        }

        @Override
        public int getTier()
        {
            return anvil.getTier();
        }

        @Override
        public long getSeed()
        {
            return anvil.getLevel() instanceof ServerLevel level ? level.getSeed() : 0;
        }
    }
}
