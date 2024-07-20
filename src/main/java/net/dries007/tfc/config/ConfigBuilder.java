/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

/**
 * Thin wrapper around the Forge config builder, which fixes some issues that have annoyed me for ages.
 * In theory this could use a different underlying builder in the case that becomes desired.
 * <p>
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
    private final String translationKeyPrefix;
    private boolean emptyLineAdded;

    public ConfigBuilder(ModConfigSpec.Builder builder, String translationKeyPrefix)
    {
        this.builder = builder;
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

    public BooleanValue define(String path, boolean value) { return begin(path).define(path, value); }
    public IntValue define(String path, int value, int min, int max) { return begin(path).defineInRange(path, value, min, max); }
    public DoubleValue define(String path, double value, double min, double max) { return begin(path).defineInRange(path, value, min, max); }
    public ConfigValue<String> define(String path, String value) { return begin(path).define(path, value); }
    public <E extends Enum<E>> EnumValue<E> define(String path, E value) { return begin(path).defineEnum(path, value); }
    public ConfigValue<List<? extends String>> define(String path, List<? extends String> value, Predicate<String> predicate) { return begin(path).defineListAllowEmpty(path, new ArrayList<>(value), String::new, o -> o instanceof String s && predicate.test(s)); }

    private ModConfigSpec.Builder begin(String path)
    {
        builder.translation("tfc.config." + translationKeyPrefix + "." + path);
        emptyLineAdded = false;
        return builder;
    }
}
