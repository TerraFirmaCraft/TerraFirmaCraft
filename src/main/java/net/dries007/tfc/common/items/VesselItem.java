/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatHandler;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VesselItem extends Item
{
    public static final int SLOTS = 4;

    public VesselItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !level.isClientSide() && player instanceof ServerPlayer serverPlayer)
        {
            final VesselLike vessel = VesselLike.get(stack);
            if (vessel != null)
            {
                if (vessel.mode() == VesselLike.Mode.INVENTORY)
                {
                    if (vessel.getTemperature() > 0)
                    {
                        player.displayClientMessage(Helpers.translatable("tfc.tooltip.small_vessel.inventory_too_hot"), true);
                    }
                    else
                    {
                        NetworkHooks.openGui(serverPlayer, TFCContainerProviders.SMALL_VESSEL.of(stack, hand), ItemStackContainerProvider.write(hand));
                    }
                }
                else if (vessel.mode() == VesselLike.Mode.MOLTEN_ALLOY)
                {
                    NetworkHooks.openGui(serverPlayer, TFCContainerProviders.MOLD_LIKE_ALLOY.of(stack, hand), ItemStackContainerProvider.write(hand));
                }
                else
                {
                    player.displayClientMessage(Helpers.translatable("tfc.tooltip.small_vessel.alloy_solid"), true);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new VesselCapability(stack);
    }

    static class VesselCapability implements VesselLike, ICapabilityProvider, DelegateItemHandler, DelegateHeatHandler, SimpleFluidHandler
    {
        private final ItemStack stack;
        private final LazyOptional<VesselCapability> capability;

        private final ItemStackHandler inventory;
        private final Alloy alloy;
        private final HeatHandler heat; // Since we cannot heat individual items (no tick() method), we only use a heat value for the container

        private final HeatingRecipe[] cachedRecipes; // Recipes for each of the four slots in the inventory

        VesselCapability(ItemStack stack)
        {
            this.stack = stack;
            this.capability = LazyOptional.of(() -> this);

            this.inventory = new InventoryItemHandler(this, SLOTS);
            this.alloy = new Alloy(TFCConfig.SERVER.smallVesselCapacity.get());
            this.heat = new HeatHandler(1, 0, 0);

            this.cachedRecipes = new HeatingRecipe[SLOTS];

            load();
        }

        @Override
        public IHeat getHeatHandler()
        {
            return heat;
        }

        @Override
        public int getSlotStackLimit(int slot)
        {
            return 16;
        }

        @Override
        public void setAndUpdateSlots(int slot)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);
            cachedRecipes[slot] = stack.isEmpty() ? null : HeatingRecipe.getRecipe(stack); // Update cached recipe for slot
            updateHeatCapacity(); // Update heat capacity as average of inventory slots
            save(); // Update the stack tag on an inventory change
        }

        @Override
        public void onSlotTake(Player player, int slot, ItemStack stack)
        {
            FoodCapability.removeTrait(stack, FoodTraits.PRESERVED);
        }

        @Override
        public Mode mode()
        {
            if (alloy.isEmpty())
            {
                return Mode.INVENTORY;
            }
            else
            {
                // Since the temperature here is not cached, we cannot cache the mode, and instead have to calculate it on demand
                // The alloy result here is cached internally, and the temperature should be quick (since it queries the alloy heat handler)
                final Metal result = alloy.getResult();
                return getTemperature() >= result.getMeltTemperature() ? Mode.MOLTEN_ALLOY : Mode.SOLID_ALLOY;
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(TFCConfig.SERVER.smallVesselMaximumItemSize.get());
        }

        @Override
        public void setTemperature(float temperature)
        {
            heat.setTemperature(temperature);
            updateInventoryMelting();
            save();
        }

        @Override
        public void addTooltipInfo(ItemStack stack, List<Component> text)
        {
            heat.addTooltipInfo(stack, text);
            if (!Helpers.isEmpty(inventory) || !alloy.isEmpty()) // Only show the 'contents' label if we actually have contents
            {
                text.add(Helpers.translatable("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));

                final Mode mode = mode();
                switch (mode)
                {
                    case INVENTORY -> Helpers.addInventoryTooltipInfo(inventory, text);
                    case MOLTEN_ALLOY, SOLID_ALLOY -> {
                        text.add(alloy.getResult().getDisplayName()
                            .append(" ")
                            .append(Helpers.translatable("tfc.tooltip.fluid_units", alloy.getAmount()))
                            .append(" ")
                            .append(Helpers.translatable(mode == Mode.SOLID_ALLOY ? "tfc.tooltip.small_vessel.solid" : "tfc.tooltip.small_vessel.molten")));
                        if (!Helpers.isEmpty(inventory))
                        {
                            text.add(Helpers.translatable("tfc.tooltip.small_vessel.still_has_unmelted_items").withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
        }

        @NotNull
        @Override
        public ItemStack getContainer()
        {
            return stack;
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
        {
            if (cap == HeatCapability.CAPABILITY || cap == Capabilities.ITEM || cap == Capabilities.FLUID || cap == Capabilities.FLUID_ITEM)
            {
                return capability.cast();
            }
            return LazyOptional.empty();
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            return alloy.getResultAsFluidStack();
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return alloy.getMaxUnits();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack)
        {
            return Metal.get(stack.getFluid()) != null;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            final Metal metal = Metal.get(resource.getFluid());
            if (metal != null)
            {
                return alloy.add(metal, resource.getAmount(), action.simulate());
            }
            return 0;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            return mode() == Mode.MOLTEN_ALLOY ? drainIgnoringTemperature(maxDrain, action) : FluidStack.EMPTY;
        }

        @Override
        public FluidStack drainIgnoringTemperature(int maxDrain, FluidAction action)
        {
            final Mode mode = mode();
            if (mode == Mode.MOLTEN_ALLOY || mode == Mode.SOLID_ALLOY)
            {
                final Metal result = alloy.getResult();
                final int amount = alloy.removeAlloy(maxDrain, action.simulate());
                if (action.execute())
                {
                    updateHeatCapacity();
                    save();
                }
                return new FluidStack(result.getFluid(), amount);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack)
        {
            FoodCapability.applyTrait(stack, FoodTraits.PRESERVED);
            inventory.setStackInSlot(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            FoodCapability.applyTrait(stack, FoodTraits.PRESERVED);
            final ItemStack result = inventory.insertItem(slot, stack, simulate);
            if (simulate)
            {
                FoodCapability.removeTrait(result, FoodTraits.PRESERVED); // Un-do preservation for simulated actions
            }
            return result;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            final ItemStack result = inventory.extractItem(slot, amount, simulate);
            FoodCapability.removeTrait(result, FoodTraits.PRESERVED);
            return result;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return new CompoundTag(); // Unused since we serialize directly to stack tag
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {}

        /**
         * Updates the heat capacity.
         * When in inventory mode, the heat capacity will be the weighted average of the contents, based on stack size.
         * When in alloy mode, the heat capacity will be the capacity of the result metal.
         * As a result, this needs to be called whenever a change is made either to the inventory, or the alloy (but not a heat change).
         */
        private void updateHeatCapacity()
        {
            float value = 0;
            if (mode() == Mode.INVENTORY)
            {
                int count = 0;
                for (ItemStack stack : Helpers.iterate(inventory))
                {
                    final IHeat heat = stack.getCapability(HeatCapability.CAPABILITY).resolve().orElse(null);
                    if (heat != null)
                    {
                        count += stack.getCount();
                        value += heat.getHeatCapacity();
                    }
                }
                value = count > 0 ? value / count : 1;
            }
            else
            {
                value = alloy.getResult().getHeatCapacity();
            }
            if (value != heat.getHeatCapacity())
            {
                heat.setHeatCapacity(value);
                save(); // Save, since we've changed the heat capacity, and possibly the temperature
            }
        }

        /**
         * Called when a change to the inventory, or heat, indicates that the recipes should be re-checked for completion
         * Note: this does not update the recipes themselves.
         */
        private void updateInventoryMelting()
        {
            boolean updatedAlloy = false;
            final ItemStackInventory wrapper = new ItemStackInventory();
            for (int i = 0; i < SLOTS; i++)
            {
                final ItemStack stack = inventory.getStackInSlot(i);
                wrapper.setStack(stack);
                if (cachedRecipes[i] != null)
                {
                    final HeatingRecipe recipe = cachedRecipes[i];
                    if (recipe.isValidTemperature(heat.getTemperature()))
                    {
                        // Melt item, add the contents to the alloy. Excess solids are placed into the inventory, more than can fit is voided.
                        final ItemStack outputStack = recipe.assemble(wrapper);
                        final FluidStack outputFluid = recipe.getOutputFluid();

                        if (!outputStack.isEmpty())
                        {
                            // Multiply the contents by the inventory count, since heat recipes only apply to single stack sizes
                            outputStack.setCount(Math.min(
                                outputStack.getCount() * stack.getCount(),
                                Math.min(outputStack.getMaxStackSize(), getSlotStackLimit(i))
                            ));
                        }

                        if (!outputFluid.isEmpty())
                        {
                            outputFluid.setAmount(outputFluid.getAmount() * stack.getCount());
                        }

                        // Apply item output
                        inventory.setStackInSlot(i, outputStack);

                        // Apply fluid output
                        Metal metal = Metal.get(outputFluid.getFluid());
                        if (metal != null)
                        {
                            alloy.add(metal, outputFluid.getAmount(), false);
                            updatedAlloy = true;
                        }
                    }
                }
            }
            if (updatedAlloy)
            {
                updateHeatCapacity();
            }
        }

        private void load()
        {
            final CompoundTag tag = stack.getOrCreateTag();
            inventory.deserializeNBT(tag.getCompound("inventory"));
            alloy.deserializeNBT(tag.getCompound("alloy"));

            // Deserialize heat capacity before we deserialize heat
            // Since setting heat capacity indirectly modifies the temperature, we need to make sure we get all three values correct when we receive a sync from server
            // This may be out of sync because the current value of Calendars.get().getTicks() can be != to the last update tick stored here.
            heat.setHeatCapacity(tag.getFloat("heat_capacity"));
            heat.deserializeNBT(tag.getCompound("heat"));

            // Additionally, we need to update the contents of our cached recipes. Since we can experience modification (copy) which will invalidate our cache, that would not trigger setAndUpdateSlots
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                final ItemStack stack = inventory.getStackInSlot(i);
                cachedRecipes[i] = stack.isEmpty() ? null : HeatingRecipe.getRecipe(stack);
            }

            updateHeatCapacity();
        }

        private void save()
        {
            final CompoundTag tag = stack.getOrCreateTag();
            tag.put("inventory", inventory.serializeNBT());
            tag.put("alloy", alloy.serializeNBT());
            tag.put("heat", heat.serializeNBT());
            tag.putFloat("heat_capacity", heat.getHeatCapacity());
        }
    }
}
