package net.dries007.tfc.util.mechanical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.power.IRotator;

public final class MechanicalUniverse
{
    private static final AtomicInteger CURRENT = new AtomicInteger(0);
    private static final Map<LevelAccessor, Map<Long, MechanicalNetwork>> NETWORKS = new HashMap<>();

    public static void onLevelLoaded(LevelAccessor level)
    {
        NETWORKS.put(level, new HashMap<>());
    }

    public static void onLevelUnloaded(LevelAccessor level)
    {
        NETWORKS.remove(level);
    }

    @Nullable
    public static MechanicalNetwork get(IRotator rotator)
    {
        if (rotator.isClientSide()) return null;
        return NETWORKS.get(rotator.levelOrThrow()).get(rotator.getId());
    }

    public static void delete(IRotator rotator)
    {
        if (rotator.isClientSide()) return;
        NETWORKS.get(rotator.levelOrThrow()).remove(rotator.getId());
    }

    @Nullable
    public static MechanicalNetwork getOrCreate(IRotator rotator)
    {
        if (rotator.isClientSide()) return null;
        final var map = NETWORKS.computeIfAbsent(rotator.levelOrThrow(), l -> new HashMap<>());
        final long id = rotator.getBlockPos().asLong();
        MechanicalNetwork network;
        if (!map.containsKey(id))
        {
            network = new MechanicalNetwork(rotator);
            map.put(id, network);
        }
        else
        {
            network = map.get(id);
        }
        return network;
    }

    public static void tick(ServerLevel level)
    {
        if (level.getGameTime() % 20 == 0)
        {
            final var map = NETWORKS.get(level);
            final var values = new ArrayList<>(map.values());
            if (!values.isEmpty())
            {
                int idx = CURRENT.getAndIncrement();
                if (idx >= values.size())
                {
                    idx = 0;
                    CURRENT.set(0);
                }
                NetworkTracker.tickNetwork(values.get(idx));
            }

        }

    }
}
