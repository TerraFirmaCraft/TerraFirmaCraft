/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.BellowsBlockEntity;

/**
 * Implemented on blocks which can accept air from a bellows.
 * In order to determine what blocks the bellows can affect, offsets from the bellows' position are registered in {@link #registerOffset(int, int, int)}.
 */
public interface IBellowsConsumer
{
    Set<Offset> OFFSETS = new HashSet<>();

    static void registerDefaultOffsets()
    {
        registerOffset(1, 0, 0); // Blast Furnace
        registerOffset(1, 0, -1); // Charcoal Forge
    }

    /**
     * Register an offset to be checked by the bellows for the purpose of consuming air.
     *
     * @param out  A number of blocks directly straight from the bellows output face
     * @param side A number of blocks to the side, 90 degrees clockwise (looking down) from the bellows output face
     * @param up   A number of blocks up from the bellows position.
     */
    static void registerOffset(int out, int side, int up)
    {
        OFFSETS.add(new Offset(out, side, up));
    }

    static Collection<Offset> offsets()
    {
        return OFFSETS;
    }

    /**
     * @param level The world.
     * @param pos   The position of the bellows consumer.
     * @param state The block state of the bellows consumer.
     * @return {@code true} if the block is allowed to accept air.
     */
    default boolean canAcceptAir(Level level, BlockPos pos, BlockState state)
    {
        return true;
    }

    /**
     * @param level  The world.
     * @param pos    The position of the bellows consumer.
     * @param state  The block state of the bellows consumer.
     * @param amount An amount of air provided this tick by the bellows. The TFC bellows always provides {@link BellowsBlockEntity#BELLOWS_AIR} amount to this.
     */
    void intakeAir(Level level, BlockPos pos, BlockState state, int amount);

    record Offset(int out, int side, int up) {}
}
