/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired when a chisel searches for a matching recipe.
 * The only parameter than can be modified is the output state.
 * The event must be cancelled in order the different output state to be placed.
 */
@Cancelable
public class ChiselResultEvent extends Event
{
    private final Player player;
    private final BlockState inputState;
    private final BlockHitResult hit;
    private BlockState outputState;

    public ChiselResultEvent(Player player, BlockState inputState, BlockHitResult hit, BlockState outputState)
    {
        this.player = player;
        this.inputState = inputState;
        this.hit = hit;
        this.outputState = outputState;
    }

    public Player getPlayer()
    {
        return player;
    }

    public BlockState getInputState()
    {
        return inputState;
    }

    public BlockHitResult getHit()
    {
        return hit;
    }

    public BlockState getOutputState()
    {
        return outputState;
    }

    public void setOutputState(BlockState outputState)
    {
        this.outputState = outputState;
    }
}
