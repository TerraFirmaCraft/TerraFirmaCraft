/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.CharcoalForgeContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.data.Fuel;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class CharcoalForgeBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable, MenuProvider
{
    public static final int SLOT_FUEL_MIN = 0;
    public static final int SLOT_FUEL_MAX = 4;
    public static final int SLOT_INPUT_MIN = 5;
    public static final int SLOT_INPUT_MAX = 9;
    public static final int SLOT_EXTRA_MIN = 10;
    public static final int SLOT_EXTRA_MAX = 13;

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.charcoal_forge");

    public static void createFromCharcoalPile(Level level, BlockPos pos)
    {
        level.setBlockAndUpdate(pos, TFCBlocks.CHARCOAL_FORGE.get().defaultBlockState().setValue(CharcoalForgeBlock.HEAT, 2));
        level.getBlockEntity(pos, TFCBlockEntities.CHARCOAL_FORGE.get()).ifPresent(CharcoalForgeBlockEntity::onFirstCreation);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CharcoalForgeBlockEntity forge)
    {
        forge.checkForLastTickSync();
        forge.checkForCalendarUpdate();

        if (forge.needsRecipeUpdate)
        {
            forge.needsRecipeUpdate = false;
            forge.updateCachedRecipes();
        }

        if (level.getGameTime() % 20 == 0)
        {
            // Slurp in charcoal or other fuel.
            final AABB bounds = new AABB(pos.getX() - 0.2, pos.getY() + 0.875, pos.getZ() - 0.2, pos.getX() + 1.2, pos.getY() + 1.25, pos.getZ() + 1.2);
            Helpers.gatherAndConsumeItems(level, bounds, forge.inventory, SLOT_FUEL_MIN, SLOT_FUEL_MAX);
        }

        boolean isRaining = level.isRainingAt(pos);
        if (state.getValue(CharcoalForgeBlock.HEAT) > 0)
        {
            if (isRaining && level.random.nextFloat() < 0.15F)
            {
                Helpers.playSound(level, pos, TFCSounds.ITEM_COOL.get());
            }
            int heatLevel = Mth.clamp((int) (forge.temperature / Heat.maxVisibleTemperature() * 6) + 1, 1, 7); // scaled 1 through 7
            if (heatLevel != state.getValue(CharcoalForgeBlock.HEAT))
            {
                level.setBlockAndUpdate(pos, state.setValue(CharcoalForgeBlock.HEAT, heatLevel));
                forge.markForSync();
            }

            // Update fuel
            if (forge.burnTicks > 0)
            {
                forge.burnTicks -= forge.airTicks > 0 || isRaining ? 2 : 1; // Fuel burns twice as fast using bellows, or in the rain
            }
            if (forge.burnTicks <= 0 && !forge.consumeFuel())
            {
                forge.extinguish(state);
            }
        }
        else if (forge.burnTemperature > 0)
        {
            forge.extinguish(state);
        }
        if (forge.airTicks > 0)
        {
            forge.airTicks--;
        }

        // Always update temperature / cooking, until the fire pit is not hot anymore
        if (forge.temperature > 0 || forge.burnTemperature > 0)
        {
            forge.temperature = HeatCapability.adjustDeviceTemp(forge.temperature, forge.burnTemperature, forge.airTicks, isRaining);

            HeatCapability.provideHeatTo(level, pos.above(), forge.temperature);

            for (int slot = SLOT_INPUT_MIN; slot <= SLOT_INPUT_MAX; slot++)
            {
                final ItemStack stack = forge.inventory.getStackInSlot(slot);
                final @Nullable IHeat heat = HeatCapability.get(stack);

                if (heat != null)
                {
                    // Update temperature of item
                    HeatCapability.addTemp(heat, forge.temperature);

                    // Handle possible melting, or conversion (if reach 1599 = pit kiln temperature)
                    forge.handleInputMelting(stack, slot);
                }
            }
            forge.markForSync();
        }

        // This is here to avoid duplication glitches
        if (forge.needsSlotUpdate)
        {
            forge.cascadeFuelSlots();
        }
    }

    protected final ContainerData syncableData;
    private final HeatingRecipe[] cachedRecipes = new HeatingRecipe[5];
    private boolean needsSlotUpdate = false;
    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int airTicks; // Ticks of air provided by bellows
    private long lastPlayerTick; // Last player tick this forge was ticked (for purposes of catching up)
    private boolean needsRecipeUpdate; // Set to indicate on tick, the cached recipes need to be re-updated

    public CharcoalForgeBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CHARCOAL_FORGE.get(), pos, state, defaultInventory(14), NAME);

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        airTicks = 0;
        lastPlayerTick = Integer.MIN_VALUE;
        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);

        if (TFCConfig.SERVER.charcoalForgeEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(SLOT_FUEL_MIN, 1, 2, 3, SLOT_FUEL_MAX), Direction.UP)
                .on(new PartialItemHandler(inventory).insert(SLOT_INPUT_MIN, 6, 7, 8, SLOT_INPUT_MAX), Direction.Plane.HORIZONTAL)
                .on(new PartialItemHandler(inventory).extract(SLOT_INPUT_MIN, 6, 7, 8, SLOT_INPUT_MAX), Direction.DOWN);
        }

        Arrays.fill(cachedRecipes, null);
    }

    public void intakeAir(int amount)
    {
        airTicks += amount;
        if (airTicks > BellowsBlockEntity.MAX_DEVICE_AIR_TICKS)
        {
            airTicks = BellowsBlockEntity.MAX_DEVICE_AIR_TICKS;
        }
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(CharcoalForgeBlock.HEAT) != 0)
        {
            HeatCapability.Remainder remainder = HeatCapability.consumeFuelForTicks(ticks, inventory, burnTicks, burnTemperature, SLOT_FUEL_MIN, SLOT_FUEL_MAX);

            burnTicks = remainder.burnTicks();
            burnTemperature = remainder.burnTemperature();
            needsSlotUpdate = true;

            if (remainder.ticks() > 0)
            {
                // Consumed all fuel, so extinguish and cool instantly
                extinguish(state);
                for (int i = SLOT_INPUT_MIN; i <= SLOT_INPUT_MAX; i++)
                {
                    final ItemStack stack = inventory.getStackInSlot(i);
                    HeatCapability.setTemperature(stack, 0);
                }
            }
        }
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public int getAirTicks()
    {
        return airTicks;
    }

    public void onFirstCreation()
    {
        burnTicks = 200;
        burnTemperature = 500;
        markForSync();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player)
    {
        return CharcoalForgeContainer.create(this, playerInv, windowID);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        super.saveAdditional(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        updateCachedRecipes();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot <= SLOT_FUEL_MAX)
        {
            return Helpers.isItem(stack.getItem(), TFCTags.Items.FORGE_FUEL);
        }
        else if (slot <= SLOT_INPUT_MAX)
        {
            return HeatCapability.maybeHas(stack);
        }
        else
        {
            return Helpers.mightHaveCapability(stack, Capabilities.FLUID_ITEM) && HeatCapability.maybeHas(stack);
        }
    }

    /**
     * Attempts to light the forge. Use over just setting the block state HEAT, as if there is no fuel, that will light the forge for one tick which looks strange
     *
     * @param state The current firepit block state
     * @return {@code true} if the firepit was lit.
     */
    public boolean light(BlockState state)
    {
        assert level != null;
        if (consumeFuel())
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(CharcoalForgeBlock.HEAT, 2));
            return true;
        }
        return false;
    }

    /**
     * Attempts to consume one piece of fuel. Returns if the fire pit consumed any fuel (and so, ended up lit)
     */
    private boolean consumeFuel()
    {
        final ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL_MIN);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            inventory.setStackInSlot(SLOT_FUEL_MIN, ItemStack.EMPTY);
            needsSlotUpdate = true;
            Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.duration();
                burnTemperature = fuel.temperature();
            }
            markForSync();
        }
        return burnTicks > 0;
    }

    public void extinguish(BlockState state)
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, state.setValue(CharcoalForgeBlock.HEAT, 0));
        burnTicks = 0;
        burnTemperature = 0;
        markForSync();
    }

    private void handleInputMelting(ItemStack stack, int startIndex)
    {
        assert level != null;

        final HeatingRecipe recipe = cachedRecipes[startIndex - SLOT_INPUT_MIN];
        final @Nullable IHeat cap = HeatCapability.get(stack);

        if (cap != null && recipe != null && recipe.isValidTemperature(cap.getTemperature()))
        {
            assert level != null;

            // Handle possible metal output
            FluidStack fluidStack = recipe.assembleFluid(stack);
            ItemStack outputStack = recipe.assembleItem(stack);
            float itemTemperature = cap.getTemperature();

            // Loop through all input slots
            for (int slot = SLOT_EXTRA_MIN; slot <= SLOT_EXTRA_MAX; slot++)
            {
                fluidStack = Helpers.mergeOutputFluidIntoSlot(this.inventory, fluidStack, itemTemperature, slot);
                if (fluidStack.isEmpty()) break;
            }

            FoodCapability.applyTrait(outputStack, FoodTraits.CHARCOAL_GRILLED);
            this.inventory.setStackInSlot(startIndex, outputStack);
        }
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i <= SLOT_FUEL_MAX; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest available slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
        needsSlotUpdate = false;
    }

    private void updateCachedRecipes()
    {
        // cache heat recipes for each input
        assert level != null;
        for (int i = SLOT_INPUT_MIN; i <= SLOT_INPUT_MAX; i++)
        {
            cachedRecipes[i - SLOT_INPUT_MIN] = null;
            ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                cachedRecipes[i - SLOT_INPUT_MIN] = HeatingRecipe.getRecipe(inputStack);
            }
        }
    }
}
