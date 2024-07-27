/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/**
 * Thin wrapper around the Forge config builder, which fixes some issues that have annoyed me for ages.
 * In theory this could use a different underlying builder in the case that becomes desired.
 * <ul>
 *     <li>Improves the formatting of comments (what we can control)</li>
 *     <li>Automatically handles translation keys (if that ever becomes a need)</li>
 *     <li>No footguns like allowing `define()` for lists or enums</li>
 * </ul>
 * This doesn't cover every use case, but we don't need the majority of them.
 */
public class ConfigBuilder
{
    private final ModConfigSpec.Builder builder;
    private final Factory factory;
    private final String translationKeyPrefix;
    private boolean emptyLineAdded;

    public ConfigBuilder(ModConfigSpec.Builder builder, Factory factory, String translationKeyPrefix)
    {
        this.builder = builder;
        this.factory = factory;
        this.translationKeyPrefix = translationKeyPrefix;
        this.emptyLineAdded = false;
    }

    public ConfigBuilder push(String path) { builder.push(path); return this; }
    public ConfigBuilder swap(String path) { builder.pop().push(path); return this; }
    public ConfigBuilder pop() { builder.pop(); return this; }
    public ConfigBuilder pop(int n)
    {
        for (int i = 0; i < n; i++) pop();
        return this;
    }

    public ConfigBuilder comment(String... path)
    {
        // NightConfig's Toml config formatting is AWFUL
        // - Insert a blank comment as the first comment line
        // - Insert a space before the comment body
        if (!emptyLineAdded)
        {
            builder.comment("");
            emptyLineAdded = true;
        }
        for (String line : path)
        {
            builder.comment(" " + line);
        }
        return this;
    }

    // A note on config values and loaded values:
    //
    // For ages (since this became an exception) we have had issues now, with trying to access config values before they are loaded, in
    // totally innocuous situations. Most of which, indicate we either (1) can't involve a config in that situation, or (2) have to accept
    // some inconsistency.
    //
    // As a result, I've made a decision here to use a raw `Supplier<T>` as the type of the underlying value, and do an "is loaded, else
    // return default" check before ALL config values. The effects of this are twofold:
    //
    // 1) We lose access to i.e., type-specific suppliers, however, the usability of those is in practice, entirely useless (from a performance
    //    pov). The default implementation is still doing unboxing, just hiding it behind another method. If we desire type-specific suppliers,
    //    we can coerce them if needed
    //
    // 2) We clean up all situations where we had to forcibly accept "get if loaded else default" behavior before, by making it consistent
    //    across all situations. The potential bug caused by not validating the config was loaded has not seemed to occur, and most situations
    //    it has been more salient just to let the default value return. Our config organization has been largely consistent w.r.t. when config
    //    values actually get invoked.
    //
    // Thus, the choice here to wrap everything in a special record which does the "get if loaded else default", and the choice to type the return
    // value very loosely.

    public Supplier<Boolean> define(String path, boolean value)
    {
        return factory.create(begin(path).define(path, value));
    }

    public Supplier<Integer> define(String path, int value, int min, int max)
    {
        return factory.create(begin(path).defineInRange(path, value, min, max));
    }

    public Supplier<Integer> define(String path, int value)
    {
        return factory.create(begin(path).define(path, value));
    }

    public Supplier<Double> define(String path, double value, double min, double max)
    {
        return factory.create(begin(path).defineInRange(path, value, min, max));
    }

    public Supplier<String> define(String path, String value)
    {
        return factory.create(begin(path).define(path, value));
    }

    public <E extends Enum<E>> Supplier<E> define(String path, E value)
    {
        return factory.create(begin(path).defineEnum(path, value));
    }

    // List<? extends String> is stupid, just force the types here to work
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Supplier<List<String>> define(String path, List<String> value, Predicate<String> predicate)
    {
        return (Supplier) factory.create(begin(path).defineListAllowEmpty(path, new ArrayList<>(value), String::new, o -> o instanceof String s && predicate.test(s)));
    }

    private ModConfigSpec.Builder begin(String path)
    {
        builder.translation("tfc.config." + translationKeyPrefix + "." + path);
        emptyLineAdded = false;
        return builder;
    }

    interface Factory
    {
        <T, V extends ConfigValue<T>> Supplier<T> create(V value);
    }

    record CommonValue<T>(ConfigValue<T> value) implements Supplier<T>
    {
        @Override
        public T get()
        {
            return TFCConfig.COMMON.spec().isLoaded() ? value.get() : value.getDefault();
        }
    }

    record ClientValue<T>(ConfigValue<T> value) implements Supplier<T>
    {
        @Override
        public T get()
        {
            return TFCConfig.CLIENT.spec().isLoaded() ? value.get() : value.getDefault();
        }
    }

    record ServerValue<T>(ConfigValue<T> value) implements Supplier<T>
    {
        @Override
        public T get()
        {
            return TFCConfig.SERVER.spec().isLoaded() ? value.get() : value.getDefault();
        }
    }
}
