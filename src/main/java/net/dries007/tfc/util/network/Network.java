/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.Comparator;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Network<T extends Node>
{
    final Long2ObjectMap<T> nodes = new Long2ObjectOpenHashMap<>();
    final long networkId;

    public Network(long networkId)
    {
        this.networkId = networkId;
    }

    @Override
    public String toString()
    {
        return "[network=%d]\n%s".formatted(networkId, nodes.values()
            .stream()
            .sorted(Comparator.comparing(Node::pos))
            .map(e -> e + "\n")
            .collect(Collectors.joining()));
    }
}
