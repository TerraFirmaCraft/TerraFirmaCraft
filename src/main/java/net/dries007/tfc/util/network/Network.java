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
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.jetbrains.annotations.Nullable;

public class Network<T extends Node>
{
    // Prefer not doing direct modification to this map, rather, access through
    // the methods in NetworkManager, which allow for listening to updates
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

    public ObjectCollection<T> getNodes()
    {
        return nodes.values();
    }

    public final int size()
    {
        return nodes.size();
    }

    @Nullable
    final T getNode(long key)
    {
        return nodes.get(key);
    }

    final void removeNode(T node)
    {
        nodes.remove(node.key());
    }

    final void addNode(T node)
    {
        nodes.put(node.key(), node);
    }
}
