/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.Tiered;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.forge.*;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.AnvilPlanContainer;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnvilBlockEntity extends InventoryBlockEntity<AnvilBlockEntity.AnvilInventory> implements ISlotCallback
{
    public static final int SLOT_INPUT_MAIN = 0;
    public static final int SLOT_INPUT_SECOND = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_CATALYST = 3;

    public static final int[] SLOTS_BY_HAND_EXTRACT = new int[] {SLOT_INPUT_MAIN, SLOT_INPUT_SECOND};
    public static final int[] SLOTS_BY_HAND_INSERT = new int[] {SLOT_CATALYST, SLOT_INPUT_MAIN, SLOT_INPUT_SECOND};

    private static final Component NAME = Helpers.translatable("tfc.block_entity.anvil");

    public AnvilBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.ANVIL.get(), pos, state, AnvilInventory::new, NAME);
    }

    @Nullable
    public Forging getMainInputForging()
    {
        return inventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN)
            .getCapability(ForgingCapability.CAPABILITY)
            .resolve()
            .orElse(null);
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
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        if (slot == SLOT_INPUT_MAIN)
        {
            stack.getCapability(ForgingCapability.CAPABILITY).ifPresent(Forging::clearRecipeIfNotWorked);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        if (!stack.isEmpty())
        {
            final Forging forge = ForgingCapability.get(stack);
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
                        if (!level.isClientSide)
                        {
                            forge.setRecipe(recipe, inventory);
                        }
                    }
                }
            }
        }
        setChanged();
    }

    @Override
    public void ejectInventory()
    {
        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        if (!stack.isEmpty())
        {
            stack.getCapability(ForgingCapability.CAPABILITY).ifPresent(Forging::clearRecipeIfNotWorked);
        }
        super.ejectInventory();
    }

    public void chooseRecipe(@Nullable AnvilRecipe recipe)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        if (!stack.isEmpty())
        {
            final Forging forge = ForgingCapability.get(stack);
            if (forge != null)
            {
                forge.setRecipe(recipe, inventory);
            }
        }
    }

    /**
     * Sends feedback to the chat, as the action bar is obscured by the anvil gui
     */
    public InteractionResult work(ServerPlayer player, ForgeStep step)
    {
        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        final Forging forge = ForgingCapability.get(stack);
        if (forge != null)
        {
            // Check that we have a hammer, either in the anvil or in the player inventory
            ItemStack hammer = inventory.getStackInSlot(SLOT_HAMMER);
            @Nullable InteractionHand hammerSlot = null;
            if (hammer.isEmpty())
            {
                hammer = player.getMainHandItem();
                hammerSlot = InteractionHand.MAIN_HAND;
            }
            if (hammer.isEmpty())
            {
                hammer = player.getOffhandItem();
                hammerSlot = InteractionHand.OFF_HAND;
            }
            if (hammer.isEmpty() || !Helpers.isItem(hammer, TFCTags.Items.HAMMERS))
            {
                player.displayClientMessage(Helpers.translatable("tfc.tooltip.hammer_required_to_work"), false);
                return InteractionResult.FAIL;
            }

            // Prevent the player from immediately destroying the item by overworking
            if (!forge.getSteps().any() && forge.getWork() == 0 && step.step() < 0)
            {
                return InteractionResult.FAIL;
            }

            final AnvilRecipe recipe = forge.getRecipe(level);
            if (recipe != null)
            {
                if (!recipe.matches(inventory, level))
                {
                    player.displayClientMessage(Helpers.translatable("tfc.tooltip.anvil_is_too_low_tier_to_work"), false);
                    return InteractionResult.FAIL;
                }

                final LazyOptional<IHeat> heat = stack.getCapability(HeatCapability.CAPABILITY);
                if (heat.map(h -> h.getWorkingTemperature() > h.getTemperature()).orElse(false))
                {
                    player.displayClientMessage(Helpers.translatable("tfc.tooltip.not_hot_enough_to_work"), false);
                    return InteractionResult.FAIL;
                }

                // Proceed with working
                forge.addStep(step);

                // Damage the hammer
                final InteractionHand breakingHand = hammerSlot;
                hammer.hurtAndBreak(1, player, e -> {
                    if (breakingHand != null)
                    {
                        e.broadcastBreakEvent(breakingHand);
                    }
                });

                if (forge.getWork() < 0 || forge.getWork() > ForgeStep.LIMIT)
                {
                    // Destroy the input
                    inventory.setStackInSlot(SLOT_INPUT_MAIN, ItemStack.EMPTY);
                    level.playSound(null, worldPosition, SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 0.4f, 1.0f);
                    return InteractionResult.FAIL;
                }
                level.playSound(null, worldPosition, TFCSounds.ANVIL_HIT.get(), SoundSource.PLAYERS, 0.4f, 1.0f);

                // Re-check anvil recipe completion
                if (recipe.checkComplete(inventory))
                {
                    // Recipe completed, so consume inputs and add outputs
                    final ItemStack outputStack = recipe.assemble(inventory);

                    // Always preserve heat of the input
                    outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(outputHeat ->
                        outputHeat.setTemperatureIfWarmer(heat.map(IHeat::getTemperature).orElse(0f)));

                    // And apply the forging bonus, if the recipe says to do so
                    if (recipe.shouldApplyForgingBonus())
                    {
                        final float ratio = (float) forge.getSteps().total() / ForgeRule.calculateOptimalStepsToTarget(recipe.computeTarget(inventory), recipe.getRules());
                        final ForgingBonus bonus = ForgingBonus.byRatio(ratio);
                        ForgingBonus.set(outputStack, bonus);
                    }

                    inventory.setStackInSlot(SLOT_INPUT_MAIN, outputStack);
                }

                markForSync();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * Sends feedback to the action bar, as the anvil gui will be closed
     */
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
            if (getTier() < recipe.getTier())
            {
                player.displayClientMessage(Helpers.translatable("tfc.tooltip.anvil_is_too_low_tier_to_weld"), true);
                return InteractionResult.FAIL;
            }

            final LazyOptional<IHeat> leftHeat = left.getCapability(HeatCapability.CAPABILITY);
            final LazyOptional<IHeat> rightHeat = right.getCapability(HeatCapability.CAPABILITY);

            if (leftHeat.map(h -> h.getWeldingTemperature() > h.getTemperature()).orElse(false) || rightHeat.map(h -> h.getWeldingTemperature() > h.getTemperature()).orElse(false))
            {
                player.displayClientMessage(Helpers.translatable("tfc.tooltip.not_hot_enough_to_weld"), true);
                return InteractionResult.FAIL;
            }

            if (inventory.getStackInSlot(SLOT_CATALYST).isEmpty())
            {
                player.displayClientMessage(Helpers.translatable("tfc.tooltip.no_flux_to_weld"), true);
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

    /**
     * Sets the inventory for a block entity that is not placed in the world
     */
    public void setInventoryFromOutsideWorld(ItemStack main, ItemStack hammer, ItemStack flux)
    {
        final NonNullList<ItemStack> internalStacks = inventory.getInternalStacks();
        internalStacks.set(SLOT_INPUT_MAIN, main);
        internalStacks.set(SLOT_HAMMER, hammer);
        internalStacks.set(SLOT_CATALYST, flux);
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
            Helpers.warnWhenCalledFromClientThread();
            return anvil.getLevel() instanceof ServerLevel level ? level.getSeed() : 0;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            final ItemStack stack = super.extractItem(slot, amount, simulate);
            stack.getCapability(ForgingCapability.CAPABILITY).ifPresent(Forging::clearRecipeIfNotWorked);
            return stack;
        }
    }
}
