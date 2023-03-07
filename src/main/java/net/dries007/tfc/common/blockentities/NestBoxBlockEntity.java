/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.egg.IEgg;
import net.dries007.tfc.common.container.NestBoxContainer;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class NestBoxBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
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
                        Helpers.insertOne(level, pos, TFCBlockEntities.NEST_BOX.get(), bird.makeEgg());
                        bird.setFertilized(false);
                        bird.setProductsCooldown();
                        bird.stopRiding();
                        nest.markForSync();
                    }
                }
                else
                {
                    bird.stopRiding();
                }
            }
            for (int i = 0; i < nest.inventory.getSlots(); i++)
            {
                final int slot = i;
                ItemStack stack = nest.inventory.getStackInSlot(slot);
                stack.getCapability(EggCapability.CAPABILITY).filter(IEgg::isFertilized).ifPresent(egg -> {
                    if (egg.getHatchDay() > 0 && egg.getHatchDay() <= Calendars.SERVER.getTotalDays())
                    {
                        egg.getEntity(level).ifPresent(entity -> {
                            entity.moveTo(pos, 0f, 0f);
                            level.addFreshEntity(entity);
                        });
                        nest.inventory.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                });
            }
        }
    }

    public static final int SLOTS = 4;
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.nest_box");

    public NestBoxBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.NEST_BOX.get(), pos, state, defaultInventory(SLOTS), NAME);

        if (TFCConfig.SERVER.nestBoxEnableAutomation.get())
        {
            sidedInventory.on(new PartialItemHandler(inventory).extract(), Direction.DOWN);
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
        return Helpers.mightHaveCapability(stack, EggCapability.CAPABILITY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return NestBoxContainer.create(this, inv, windowID);
    }
}
