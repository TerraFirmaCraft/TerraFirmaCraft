package net.dries007.tfc.data.providers;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.DataManager;

public abstract class DataManagerProvider<T> implements DataProvider
{
    /**
     * For use in a test environment, sets up all data generated values of a given data manager, via the provided data provider,
     * to initialize the runtime data manager. Requires registries to be setup already.
     */
    public static <T> void setup(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends DataManagerProvider<T>> factory)
    {
        final RegistryAccess.Frozen lookup = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        final var provider = factory.apply(new PackOutput(Path.of("")), CompletableFuture.completedFuture(lookup));

        provider.addData(lookup);
        provider.manager.bindValues(provider.elements.buildOrThrow());
    }

    private final DataManager<T> manager;
    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final ImmutableMap.Builder<ResourceLocation, T> elements;
    private final PackOutput.PathProvider path;
    protected final CompletableFuture<?> contentDone;

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
            manager.bindValues(map);
            contentDone.complete(null);
            return CompletableFuture.allOf(map.entrySet()
                .stream()
                .map(e -> DataProvider.saveStable(output, provider, manager.codec(), e.getValue(), path.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
        });
    }

    public CompletableFuture<?> output()
    {
        return contentDone;
    }

    @Override
    public final String getName()
    {
        return "Data Manager (" + manager.getName() + ")";
    }

    protected final void add(String name, T value)
    {
        add(Helpers.identifier(name.toLowerCase(Locale.ROOT)), value);
    }

    protected final void add(ResourceLocation name, T value)
    {
        elements.put(name, value);
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
