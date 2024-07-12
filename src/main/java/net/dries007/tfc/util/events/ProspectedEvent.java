/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;

import net.dries007.tfc.common.items.ProspectResult;

/**
 * Fired when a prospecting result is obtained.
 * This event is purely informational, it cannot change the result or displayed information in any way.
 * This event is fired on both sides
 */
public final class ProspectedEvent extends Event
{
    private final Player player;
    private final ProspectResult type;
    private final Block block;

    public ProspectedEvent(Player player, ProspectResult type, Block block)
    {
        this.player = player;
        this.type = type;
        this.block = block;
    }

    /**
     * @return The player doing the prospecting.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * @return The type of the result, to be displayed to the player.
     */
    public ProspectResult getType()
    {
        return type;
    }

    /**
     * If {@link #getType()} is not {@link ProspectResult#NOTHING}, then this will contain the block which was found.
     * Otherwise, this will be the block that was initially clicked.
     */
    public Block getBlock()
    {
        return block;
    }
}
