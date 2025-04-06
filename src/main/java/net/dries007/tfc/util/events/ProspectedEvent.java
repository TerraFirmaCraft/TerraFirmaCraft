/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;

import net.dries007.tfc.common.items.ProspectResult;

/**
 * Fired when a prospecting result is obtained.
 * This event is purely informational, it cannot change the result or displayed information in any way.
 * This event is fired on both sides, on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */
public final class ProspectedEvent extends Event
{
    private final Player player;
    private final ProspectResult type;
    private final int dist;
    private final Block block;

    public ProspectedEvent(Player player, ProspectResult type, int dist, Block block)
    {
        this.player = player;
        this.type = type;
        this.dist = dist;
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
     * @return The distance from the prospect pos the block was found
     */
    public int getDist() { return dist; }

    /**
     * If {@link #getType()} is not {@link ProspectResult#NOTHING}, then this will contain the block which was found.
     * Otherwise, this will be the block that was initially clicked.
     */
    public Block getBlock()
    {
        return block;
    }
}
