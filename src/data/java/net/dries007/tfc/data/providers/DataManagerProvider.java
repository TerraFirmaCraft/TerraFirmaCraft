package net.dries007.tfc.data.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    private final Map<ResourceLocation, T> elements;
    private final PackOutput.PathProvider path;
    private final CompletableFuture<Void> contentDone;

    protected DataManagerProvider(DataManager<T> manager, PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        this.manager = manager;
        this.lookup = lookup;
        this.elements = new HashMap<>();
        this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, TerraFirmaCraft.MOD_ID + "/" + manager.getName());
        this.contentDone = new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        return beforeRun().thenCompose(provider -> {
            addData();
            contentDone.complete(null);
            return CompletableFuture.allOf(elements.entrySet()
                .stream()
                .map(e -> DataProvider.saveStable(output, provider, manager.codec(), e.getValue(), path.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
        });
    }

    public final DataAccessor<T> output()
    {
        return new DataAccessor<>(contentDone.thenApply(v -> elements));
    }

    @Override
    public final String getName()
    {
        return "Data Manager (" + manager.getName() + ")";
    }

    protected final void add(String name, T value)
    {
        if (elements.put(Helpers.identifier(name), value) != null) throw new IllegalStateException("Duplicate registration of " + name);
    }

    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return lookup;
    }

    protected abstract void addData();
}
