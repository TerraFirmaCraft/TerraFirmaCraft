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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.Tiered;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingCapability;
import net.dries007.tfc.common.component.forge.ForgingComponent;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.AnvilPlanContainer;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class AnvilBlockEntity extends InventoryBlockEntity<AnvilBlockEntity.AnvilInventory> implements ISlotCallback
{
    public static final int SLOT_INPUT_MAIN = 0;
    public static final int SLOT_INPUT_SECOND = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_CATALYST = 3;

    public static final int[] SLOTS_BY_HAND_EXTRACT = new int[] {SLOT_INPUT_MAIN, SLOT_INPUT_SECOND};
    public static final int[] SLOTS_BY_HAND_INSERT = new int[] {SLOT_CATALYST, SLOT_INPUT_MAIN, SLOT_INPUT_SECOND};

    public AnvilBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.ANVIL.get(), pos, state, AnvilInventory::new);
    }

    public AnvilBlockEntity(BlockEntityType<? extends AnvilBlockEntity> type, BlockPos pos, BlockState state, InventoryFactory<AnvilInventory> inventoryFactory)
    {
        super(type, pos, state, inventoryFactory);
    }

    public Forging getMainInputForging()
    {
        return ForgingCapability.get(inventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN));
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
                case SLOT_HAMMER -> Helpers.isItem(stack, TFCTags.Items.TOOLS_HAMMER);
                case SLOT_CATALYST -> Helpers.isItem(stack, TFCTags.Items.WELDING_FLUX);
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
            ForgingCapability.clearRecipeIfNotWorked(stack);
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
            final @Nullable AnvilRecipe recipe = forge.getRecipe();
            if (recipe == null)
            {
                // Select a default recipe if we only find a single recipe for this item
                final Collection<RecipeHolder<AnvilRecipe>> all = AnvilRecipe.getAll(level, stack, getTier());
                if (all.size() == 1 && !level.isClientSide)
                {
                    // Update the recipe held by the forging item
                    forge.setRecipe(all.iterator().next(), inventory);
                }
            }
        }
        setChanged();
    }

    @Override
    public void ejectInventory()
    {
        ForgingCapability.clearRecipeIfNotWorked(inventory.getStackInSlot(SLOT_INPUT_MAIN));
        super.ejectInventory();
    }

    public void chooseRecipe(ResourceLocation recipeId)
    {
        assert level != null;

        final @Nullable AnvilRecipe recipe = AnvilRecipe.byId(recipeId);
        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        if (!stack.isEmpty() && recipe != null)
        {
            ForgingCapability.get(stack).setRecipe(new RecipeHolder<>(recipeId, recipe), inventory);
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

        // Check that we have a hammer, either in the anvil or in the player inventory
        ItemStack hammer = inventory.getStackInSlot(SLOT_HAMMER);
        InteractionHand hammerSlot = null;
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
        if (hammerSlot == null || hammer.isEmpty() || !Helpers.isItem(hammer, TFCTags.Items.TOOLS_HAMMER))
        {
            player.displayClientMessage(Component.translatable("tfc.tooltip.hammer_required_to_work"), false);
            return InteractionResult.FAIL;
        }

        // Prevent the player from immediately destroying the item by overworking
        if (!forge.isWorked() && forge.work() == 0 && step.step() < 0)
        {
            return InteractionResult.FAIL;
        }

        final AnvilRecipe recipe = forge.getRecipe();
        if (recipe != null)
        {
            if (!recipe.matches(inventory, level))
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.anvil_is_too_low_tier_to_work"), false);
                return InteractionResult.FAIL;
            }

            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null && !heat.canWork())
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.not_hot_enough_to_work"), false);
                return InteractionResult.FAIL;
            }

            // Proceed with working
            forge.addStep(step);

            // Damage the hammer
            Helpers.damageItem(hammer, player, hammerSlot);

            if (forge.work() < 0 || forge.work() > ForgeStep.LIMIT)
            {
                // Destroy the input
                inventory.setStackInSlot(SLOT_INPUT_MAIN, ItemStack.EMPTY);
                level.playSound(null, worldPosition, SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 0.4f, 1.0f);
                return InteractionResult.FAIL;
            }
            createForgingEffects();

            // Re-check anvil recipe completion
            if (recipe.checkComplete(inventory))
            {
                // Recipe completed, so consume inputs and add outputs
                final ItemStack outputStack = recipe.assemble(inventory, level.registryAccess());
                final @Nullable IHeat outputHeat = HeatCapability.get(outputStack);

                // Always preserve heat of the input
                if (outputHeat != null)
                {
                    outputHeat.setTemperatureIfWarmer(heat);
                }

                // And apply the forging bonus, if the recipe says to do so
                if (recipe.shouldApplyForgingBonus())
                {
                    final float ratio = (float) forge.totalWorked() / ForgeRule.calculateOptimalStepsToTarget(recipe.computeTarget(inventory), recipe.getRules());
                    final ForgingBonus bonus = ForgingBonus.byRatio(ratio);
                    ForgingBonus.set(outputStack, bonus);

                    if (bonus == ForgingBonus.PERFECT)
                    {
                        TFCAdvancements.PERFECTLY_FORGED.trigger(player);
                    }
                }

                inventory.setStackInSlot(SLOT_INPUT_MAIN, outputStack);
            }

            markForSync();
        }
        return InteractionResult.SUCCESS;
    }

    public boolean workRemotely(ForgeStep step, int movement, boolean forceCompletion)
    {
        assert level != null;

        if (level.isClientSide)
        {
            return false;
        }

        final ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_MAIN);
        final Forging forge = ForgingCapability.get(stack);

        // Prevent the player from immediately destroying the item by overworking
        if (!forge.isWorked() && forge.work() == 0 && movement < 0)
        {
            return false;
        }

        final AnvilRecipe recipe = forge.getRecipe();
        if (recipe != null)
        {
            if (!recipe.matches(inventory, level))
            {
                return false;
            }

            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null && !heat.canWork())
            {
                return false;
            }

            // Proceed with working
            if (forceCompletion)
            {
                final int target = recipe.computeTarget(inventory);
                final int cursor = forge.work();
                if ((movement > 0 && cursor > target) || (movement < 0 && cursor < target))
                {
                    movement = -movement;
                }
                if ((movement > 0 && cursor + movement > target) || (movement < 0 && cursor + movement < target))
                {
                    movement = target - cursor;
                }
            }
            forge.addStep(step, movement);

            if (forge.work() < 0 || forge.work() > ForgeStep.LIMIT)
            {
                // Destroy the input
                inventory.setStackInSlot(SLOT_INPUT_MAIN, ItemStack.EMPTY);
                level.playSound(null, worldPosition, SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 0.4f, 1.0f);
                return true;
            }

            createForgingEffects();

            // Re-check anvil recipe completion
            if (recipe.checkComplete(inventory))
            {
                // Recipe completed, so consume inputs and add outputs
                final ItemStack outputStack = recipe.assemble(inventory, level.registryAccess());
                final @Nullable IHeat outputHeat = HeatCapability.get(outputStack);

                // Always preserve heat of the input
                if (outputHeat != null)
                {
                    outputHeat.setTemperatureIfWarmer(heat);
                }

                inventory.setStackInSlot(SLOT_INPUT_MAIN, outputStack);
            }

            markForSync();
        }
        return true;
    }

    private void createForgingEffects()
    {
        assert level != null;
        level.playSound(null, worldPosition, TFCSounds.ANVIL_HIT.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
        if (level instanceof ServerLevel server)
        {
            final double x = worldPosition.getX() + Mth.nextDouble(level.random, 0.2, 0.8);
            final double z = worldPosition.getZ() + Mth.nextDouble(level.random, 0.2, 0.8);
            final double y = worldPosition.getY() + Mth.nextDouble(level.random, 0.8, 1.0);
            server.sendParticles(TFCParticles.SPARK.get(), x, y, z, 5, 0, 0, 0, 0.2f);
        }
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

        final WeldingRecipe recipe = RecipeHelpers.unbox(RecipeHelpers.getHolder(level, TFCRecipeTypes.WELDING, inventory));
        if (recipe != null)
        {
            if (!recipe.isCorrectTier(getTier()))
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.anvil_is_too_low_tier_to_weld"), true);
                return InteractionResult.FAIL;
            }

            final @Nullable IHeat leftHeat = HeatCapability.get(left);
            final @Nullable IHeat rightHeat = HeatCapability.get(right);

            if ((leftHeat != null && !leftHeat.canWeld()) || (rightHeat != null && !rightHeat.canWeld()))
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.not_hot_enough_to_weld"), true);
                return InteractionResult.FAIL;
            }

            if (inventory.getStackInSlot(SLOT_CATALYST).isEmpty())
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.no_flux_to_weld"), true);
                return InteractionResult.FAIL;
            }

            final ItemStack result = recipe.assemble(inventory);
            final @Nullable IHeat resultHeat = HeatCapability.get(result);

            inventory.setStackInSlot(SLOT_INPUT_MAIN, result);
            inventory.setStackInSlot(SLOT_INPUT_SECOND, ItemStack.EMPTY);
            inventory.getStackInSlot(SLOT_CATALYST).shrink(1);

            // Always copy heat from inputs since we have two
            if (resultHeat != null)
            {
                resultHeat.setTemperatureIfWarmer(leftHeat);
                resultHeat.setTemperatureIfWarmer(rightHeat);
            }

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
        // todo: does this need to use `getInternalStacks` ? Can it not just use `setStackInSlot` ?
        final NonNullList<ItemStack> internalStacks = inventory.getInternalStacks();
        internalStacks.set(SLOT_INPUT_MAIN, main);
        internalStacks.set(SLOT_HAMMER, hammer);
        internalStacks.set(SLOT_CATALYST, flux);
    }

    public static class AnvilInventory extends InventoryItemHandler implements AnvilRecipe.Inventory, WeldingRecipe.Inventory, NonEmptyInput
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
            ForgingCapability.clearRecipeIfNotWorked(stack);
            return stack;
        }
    }
}
