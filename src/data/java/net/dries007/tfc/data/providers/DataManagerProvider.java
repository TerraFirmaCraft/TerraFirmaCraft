package net.dries007.tfc.data.providers;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.data.DataAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.DataManager;

public abstract class DataManagerProvider<T> implements DataProvider
{
    private final DataManager<T> manager;
    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final ImmutableMap.Builder<ResourceLocation, T> elements;
    private final PackOutput.PathProvider path;
    protected final CompletableFuture<Map<ResourceLocation, T>> contentDone;

    protected DataManagerProvider(DataManager<T> manager, PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        this.manager = manager;
        this.lookup = lookup;
        this.elements = ImmutableMap.builder();
        this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, TerraFirmaCraft.MOD_ID + "/" + manager.getName());
        this.contentDone = new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        return beforeRun().thenCompose(provider -> {
            addData(provider);
            final Map<ResourceLocation, T> map = elements.buildOrThrow();
            contentDone.complete(map);
            return CompletableFuture.allOf(map.entrySet()
                .stream()
                .map(e -> DataProvider.saveStable(output, provider, manager.codec(), e.getValue(), path.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
        });
    }

    public DataAccessor<T> output()
    {
        return new DataAccessor.Future<>(contentDone);
    }

    @Override
    public final String getName()
    {
        return "Data Manager (" + manager.getName() + ")";
    }

    protected final void add(String name, T value)
    {
        elements.put(Helpers.identifier(name.toLowerCase(Locale.ROOT)), value);
    }

    protected final void add(DataManager.Reference<T> reference, T value)
    {
        elements.put(reference.id(), value);
    }

    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return lookup;
    }

    protected abstract void addData(HolderLookup.Provider provider);
}
