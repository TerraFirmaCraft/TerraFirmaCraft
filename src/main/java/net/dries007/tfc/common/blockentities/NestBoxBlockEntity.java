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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.common.entities.land.OviparousAnimal;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class NestBoxBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, NestBoxBlockEntity nest)
    {
        if (level.getGameTime() % 30 == 0)
        {
            Entity sitter = Seat.getSittingEntity(level, pos);
            if (sitter instanceof OviparousAnimal bird && bird.isReadyForAnimalProduct() && bird.getRandom().nextInt(10) == 0)
            {
                Helpers.playSound(level, pos, SoundEvents.CHICKEN_EGG);
                Helpers.insertOne(level, pos, TFCBlockEntities.NEST_BOX.get(), bird.makeEgg());
                bird.setFertilized(false);
                bird.setProductsCooldown();
                bird.stopRiding();
                nest.markForSync();
            }
        }
    }

    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.nest_box");

    public NestBoxBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.NEST_BOX.get(), pos, state, defaultInventory(4), NAME);

        sidedInventory.on(new PartialItemHandler(inventory).extract(), Direction.DOWN);
    }
}
