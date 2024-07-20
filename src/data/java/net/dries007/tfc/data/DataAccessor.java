package net.dries007.tfc.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public interface DataAccessor<T>
{
    default T get(ResourceLocation id)
    {
        return future().getNow(Map.of()).get(id);
    }

    default Stream<T> all()
    {
        return future().getNow(Map.of()).values().stream();
    }

    CompletableFuture<Map<ResourceLocation, T>> future();

    record Future<T>(CompletableFuture<Map<ResourceLocation, T>> future) implements DataAccessor<T> {}
}
