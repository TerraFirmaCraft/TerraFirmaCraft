/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.dries007.tfc.common.container.NestBoxContainer;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.common.entities.misc.Seat;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class NestBoxBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 4;

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
                        Helpers.playSound(level, pos, SoundEvents.CHICKEN_EGG);
                        if (Helpers.insertOne(nest, bird.makeEgg()))
                        {
                            bird.setFertilized(false);
                            bird.setProductsCooldown();
                            bird.stopRiding();
                            nest.markForSync();
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
                if (egg != null && egg.canHatch())
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
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return NestBoxContainer.create(this, inv, windowID);
    }
}
