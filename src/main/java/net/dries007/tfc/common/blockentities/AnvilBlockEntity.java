/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.Tiered;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.IForging;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.AnvilPlanContainer;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import org.jetbrains.annotations.Nullable;

public class AnvilBlockEntity extends InventoryBlockEntity<AnvilBlockEntity.AnvilInventory> implements ISlotCallback
{
    public static final int SLOT_INPUT_MAIN = 0;
    public static final int SLOT_INPUT_SECOND = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_CATALYST = 3;

    public static final int[] SLOTS_BY_HAND_EXTRACT = new int[] {SLOT_INPUT_MAIN, SLOT_INPUT_SECOND};
    public static final int[] SLOTS_BY_HAND_INSERT = new int[] {SLOT_INPUT_MAIN, SLOT_INPUT_SECOND, SLOT_CATALYST};

    private static final Component NAME = new TranslatableComponent("tfc.block_entity.anvil");

    private final ContainerData syncableData;

    private int workTarget; // The target to work, only for client purposes
    private int workValue; // The current work progress of the item, only for client display purposes
    @Nullable private AnvilRecipe cachedRecipe;

    public AnvilBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.ANVIL.get(), pos, state, AnvilInventory::new, NAME);

        syncableData = new IntArrayBuilder()
            .add(() -> workTarget, value -> workTarget = value)
            .add(() -> workValue, value -> workValue = value);
    }

    public int getWorkTarget()
    {
        return workTarget;
    }

    public int getWorkValue()
    {
        return workValue;
    }

    @Nullable
    public AnvilRecipe getRecipe()
    {
        return cachedRecipe;
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    /**
     * @return the provider for opening the anvil plan screen
     */
    public MenuProvider planProvider()
    {
        return new SimpleMenuProvider(this::createPlanContainer, getDisplayName());
    }

    /**
     * @return the provider for opening the main anvil working screen
     */
    public MenuProvider anvilProvider()
    {
        return this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return AnvilContainer.create(this, player.getInventory(), containerId);
    }

    @Nullable
    public AbstractContainerMenu createPlanContainer(int containerId, Inventory inventory, Player player)
    {
        return AnvilPlanContainer.create(this, player.getInventory(), containerId);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot)
            {
                case SLOT_INPUT_MAIN, SLOT_INPUT_SECOND -> true;
                case SLOT_HAMMER -> Helpers.isItem(stack, TFCTags.Items.HAMMERS);
                case SLOT_CATALYST -> Helpers.isItem(stack, TFCTags.Items.FLUX);
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
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        if (!stack.isEmpty())
        {
            final IForging forge = ForgingCapability.get(stack);
            if (forge != null)
            {
                AnvilRecipe recipe = forge.getRecipe(level);
                if (recipe == null)
                {
                    // Select a default recipe if we only find a single recipe for this item
                    final Collection<AnvilRecipe> all = AnvilRecipe.getAll(level, stack, getTier());
                    if (all.size() == 1)
                    {
                        // Update the recipe held by the forging item
                        recipe = all.iterator().next();
                        forge.setRecipe(recipe);
                    }
                }

                // Update cached fields
                cachedRecipe = recipe;
                workTarget = recipe != null ? recipe.computeTarget(inventory) : 0;
                workValue = forge.getWork();
            }
        }

        setChanged();
    }

    public void chooseRecipe(@Nullable AnvilRecipe recipe)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        final IForging forge = ForgingCapability.get(stack);
        if (!stack.isEmpty() && forge != null)
        {
            // Set the recipe on the stack, and also update the recipe stored here and other recipe properties
            forge.setRecipe(recipe);

        }
    }

    public InteractionResult work(ServerPlayer player, ForgeStep step)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        final IForging forge = ForgingCapability.get(stack);
        if (forge != null)
        {
            // Prevent the player from immediately destroying the item by overworking
            if (!forge.getSteps().any() && forge.getWork() == 0 && step.step() < 0)
            {
                return InteractionResult.FAIL;
            }

            final AnvilRecipe recipe = forge.getRecipe(level);
            if (recipe != null)
            {
                final LazyOptional<IHeat> heat = stack.getCapability(HeatCapability.CAPABILITY);
                if (heat.map(h -> h.getWorkingTemperature() > h.getTemperature(false)).orElse(false))
                {
                    player.displayClientMessage(new TranslatableComponent("tfc.tooltip.not_hot_enough_to_work"), true);
                    return InteractionResult.FAIL;
                }

                // Proceed with working
                forge.addStep(step);
                if (forge.getWork() < 0 || forge.getWork() > ForgeStep.LIMIT)
                {
                    // todo: sound or other indicator of breaking?
                    return InteractionResult.FAIL;
                }

                // Re-check anvil recipe completion
                if (recipe.checkComplete(inventory))
                {
                    // Recipe completed, so consume inputs and add outputs
                    // Always preserve heat
                    final ItemStack outputStack = recipe.assemble(inventory);

                    outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(outputHeat ->
                        outputHeat.setTemperatureIfWarmer(heat.map(h -> h.getTemperature(false)).orElse(0f)));

                    inventory.setStackInSlot(SLOT_INPUT_MAIN, outputStack);
                }
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult weld(Player player)
    {
        final ItemStack left = inventory.getLeft(), right = inventory.getRight();
        if (left.isEmpty() && right.isEmpty())
        {
            return InteractionResult.PASS;
        }

        assert level != null;

        final WeldingRecipe recipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.WELDING.get(), inventory, level).orElse(null);
        if (recipe != null)
        {
            if (recipe.getTier() < getTier())
            {
                player.displayClientMessage(new TranslatableComponent("tfc.tooltip.anvil_is_too_low_tier_to_weld"), true);
                return InteractionResult.FAIL;
            }

            final LazyOptional<IHeat> leftHeat = left.getCapability(HeatCapability.CAPABILITY);
            final LazyOptional<IHeat> rightHeat = right.getCapability(HeatCapability.CAPABILITY);

            if (leftHeat.map(h -> h.getWeldingTemperature() <= h.getTemperature(false)).orElse(false) || rightHeat.map(h -> h.getWeldingTemperature() <= h.getTemperature(false)).orElse(false))
            {
                player.displayClientMessage(new TranslatableComponent("tfc.tooltip.not_hot_enough_to_weld"), true);
                return InteractionResult.FAIL;
            }

            if (inventory.getStackInSlot(SLOT_CATALYST).isEmpty())
            {
                player.displayClientMessage(new TranslatableComponent("tfc.tooltip.no_flux_to_weld"), true);
                return InteractionResult.FAIL;
            }

            final ItemStack result = recipe.assemble(inventory);

            inventory.setStackInSlot(SLOT_INPUT_MAIN, result);
            inventory.setStackInSlot(SLOT_INPUT_SECOND, ItemStack.EMPTY);
            inventory.getStackInSlot(SLOT_CATALYST).shrink(1);

            // Always copy heat from inputs since we have two
            result.getCapability(HeatCapability.CAPABILITY).ifPresent(resultHeat -> resultHeat.setTemperatureIfWarmer(Math.max(
                leftHeat.map(IHeat::getTemperature).orElse(0f),
                rightHeat.map(IHeat::getTemperature).orElse(0f)
            )));

            markForSync();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
