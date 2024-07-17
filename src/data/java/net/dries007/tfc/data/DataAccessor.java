package net.dries007.tfc.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public record DataAccessor<T>(CompletableFuture<Map<ResourceLocation, T>> future)
{
    public T get(ResourceLocation id)
    {
        return future.getNow(Map.of()).get(id);
    }

    public Stream<T> all()
    {
        return future.getNow(Map.of()).values().stream();
    }
}
