/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.EggComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.container.NestBoxContainer;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.common.entities.misc.Seat;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class NestBoxBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 4;
    private static final String INCUBATING_EGGS = "incubatingEggs";

    private int incubatingEggs;

    public static void serverTick(Level level, BlockPos pos, BlockState state, NestBoxBlockEntity nest)
    {
        nest.checkForLastTickSync();
        if (level.getGameTime() % 30 == 0)
        {
            Entity sitter = Seat.getSittingEntity(level, pos);
            if (sitter instanceof OviparousAnimal bird)
            {
                if (bird.isReadyForAnimalProduct())
                {
                    if (bird.getRandom().nextInt(7) == 0)
                    {
                        if (Helpers.hasOpenSlot(nest))
                        {
                            Helpers.playSound(level, pos, SoundEvents.CHICKEN_EGG);
                            ItemStack egg = bird.makeEgg();
                            if (!Helpers.insertOne(nest, egg))
                            {
                                // Nest somehow got full or the produce item is not valid for insertion into the nest?
                                // Just spawn an egg so that the bird's wear and tear isn't completely wasted
                                Helpers.spawnItem(level, pos, egg, 0.3f);
                            }
                            bird.setFertilized(false);
                            bird.setProductsCooldown();
                            bird.stopRiding();
                            nest.markForSync();
                        }
                        else
                        {
                            // Nest is full, try another to lay in
                            bird.stopRiding();
                        }
                    }
                }
                else
                {
                    bird.stopRiding();
                }
            }

            for (int slot = 0; slot < nest.inventory.getSlots(); slot++)
            {
                final ItemStack stack = nest.inventory.getStackInSlot(slot);
                final @Nullable EggComponent egg = stack.get(TFCComponents.EGG);
                if (nest.isIncubating(slot) && egg != null && egg.canHatch())
                {
                    egg.hatch(level).ifPresent(entity -> {
                        entity.moveTo(pos, 0f, 0f);
                        level.addFreshEntity(entity);
                    });
                    nest.inventory.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    public NestBoxBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.NEST_BOX.get(), pos, state, defaultInventory(SLOTS));

        if (TFCConfig.SERVER.nestBoxEnableAutomation.get())
        {
            sidedInventory.on(new PartialItemHandler(inventory).extractAll(), Direction.DOWN);
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
        return stack.has(TFCComponents.EGG);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        final ItemStack stack = inventory.getStackInSlot(slot);
        final @Nullable EggComponent egg = stack.get(TFCComponents.EGG);
        setIncubating(slot, egg != null && egg.fertilized() && !FoodCapability.isRotten(stack));
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        if (nbt.contains(INCUBATING_EGGS, CompoundTag.TAG_ANY_NUMERIC))
        {
            incubatingEggs = nbt.getInt(INCUBATING_EGGS);
        }
        else
        {
            incubatingEggs = 0;
            for (int slot = 0; slot < inventory.getSlots(); slot++)
            {
                final @Nullable EggComponent egg = inventory.getStackInSlot(slot).get(TFCComponents.EGG);
                setIncubating(slot, egg != null && egg.fertilized());
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.putInt(INCUBATING_EGGS, incubatingEggs);
    }

    private boolean isIncubating(int slot)
    {
        return (incubatingEggs & 1 << slot) != 0;
    }

    private void setIncubating(int slot, boolean incubating)
    {
        if (incubating)
        {
            incubatingEggs |= 1 << slot;
        }
        else
        {
            incubatingEggs &= ~(1 << slot);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return NestBoxContainer.create(this, inv, windowID);
    }
}
