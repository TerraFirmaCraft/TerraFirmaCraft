/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Locale;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.common.fluids.FluidHelpers;

public final class JsonHelpers extends GsonHelper
{
    /**
     * @deprecated Use {@link #getRegistryEntry(JsonObject, String, Registry)} instead. Remove in 1.21.
     */
    @Deprecated
    public static <T> T getRegistryEntry(JsonObject json, String key, IForgeRegistry<T> registry)
    {
        return getRegistryEntry(GsonHelper.getAsString(json, key), registry);
    }

    /**
     * @deprecated Use {@link #getRegistryEntry(JsonElement, Registry)} instead. Remove in 1.21.
     */
    @Deprecated
    public static <T> T getRegistryEntry(JsonElement json, IForgeRegistry<T> registry)
    {
        return getRegistryEntry(GsonHelper.convertToString(json, "entry"), registry);
    }

    /**
     * @deprecated Use {@link #getRegistryEntry(String, Registry)} instead. Remove in 1.21.
     */
    @Deprecated
    public static <T> T getRegistryEntry(String key, IForgeRegistry<T> registry)
    {
        final ResourceLocation res = Helpers.resourceLocation(key);
        final T obj = registry.getValue(res);
        if (obj == null || !registry.containsKey(res))
        {
            throw new JsonParseException("Unknown " + registry.getRegistryName().getPath() + ": " + key);
        }
        return obj;
    }

    public static <T> T getRegistryEntry(JsonObject json, String key, Registry<T> registry)
    {
        return getRegistryEntry(GsonHelper.getAsString(json, key), registry);
    }

    public static <T> T getRegistryEntry(JsonElement json, Registry<T> registry)
    {
        return getRegistryEntry(GsonHelper.convertToString(json, "entry"), registry);
    }

    public static <T> T getRegistryEntry(String key, Registry<T> registry)
    {
        final ResourceLocation res = Helpers.resourceLocation(key);
        final T obj = registry.get(res);
        if (obj == null || !registry.containsKey(res))
        {
            throw new JsonParseException("Unknown " + registry.key().location().getPath() + ": " + key);
        }
        return obj;
    }

    public static <T> TagKey<T> getTag(JsonObject json, String key, ResourceKey<? extends Registry<T>> registry)
    {
        return getTag(GsonHelper.getAsString(json, key), registry);
    }

    public static <T> TagKey<T> getTag(String key, ResourceKey<? extends Registry<T>> registry)
    {
        final ResourceLocation res = Helpers.resourceLocation(key);
        return TagKey.create(registry, res);
    }

    public static <E extends Enum<E>> E getEnum(JsonObject obj, String key, Class<E> enumClass, E defaultValue)
    {
        if (obj.has(key))
        {
            return getEnum(obj.get(key), enumClass);
        }
        return defaultValue;
    }

    public static <E extends Enum<E>> E getEnum(JsonElement json, Class<E> enumClass)
    {
        final String enumName = JsonHelpers.convertToString(json, enumClass.getSimpleName());
        try
        {
            return Enum.valueOf(enumClass, enumName.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e)
        {
            throw new JsonParseException("No " + enumClass.getSimpleName() + " named: " + enumName);
        }
    }

    public static CraftingBookCategory getCraftingCategory(JsonObject json)
    {
        return CraftingBookCategory.CODEC.byName(getAsString(json, "category", null), CraftingBookCategory.MISC);
    }

    public static <T> DataManager.Reference<T> getReference(JsonObject json, String key, DataManager<T> manager)
    {
        return manager.getReference(Helpers.resourceLocation(getAsString(json, key)));
    }

    public static ResourceLocation getResourceLocation(JsonObject json, String key)
    {
        return Helpers.resourceLocation(getAsString(json, key));
    }

    public static ItemStack getItemStack(JsonObject json)
    {
        return CraftingHelper.getItemStack(json, true);
    }

    public static ItemStack getItemStack(JsonObject json, String key)
    {
        return getItemStack(getAsJsonObject(json, key));
    }

    public static JsonElement get(JsonObject json, String key)
    {
        if (!json.has(key))
        {
            throw new JsonParseException("Missing required key: " + key);
        }
        return json.get(key);
    }

    public static FluidStack getFluidStack(JsonObject json, String key)
    {
        return getFluidStack(getAsJsonObject(json, key));
    }

    public static FluidStack getFluidStack(JsonObject json)
    {
        final int amount = GsonHelper.getAsInt(json, "amount", FluidHelpers.BUCKET_VOLUME);
        final Fluid fluid = getRegistryEntry(json, "fluid", BuiltInRegistries.FLUID);
        return new FluidStack(fluid, amount);
    }

    @SuppressWarnings("deprecation")
    public static BlockState getBlockState(String block)
    {
        final StringReader reader = new StringReader(block);
        try
        {
            return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, false).blockState();
        }
        catch (CommandSyntaxException e)
        {
            throw new JsonParseException(e.getMessage());
        }
    }

    public static DyeColor getDyeColor(JsonObject json, String key)
    {
        final String name = getAsString(json, key);
        final DyeColor color = DyeColor.byName(name, null);
        if (color == null)
        {
            throw new JsonParseException("No dye color named '" + name + "', must be one of " + Arrays.toString(DyeColor.values()));
        }
        return color;
    }

    public static <T> T decodeCodecDefaulting(JsonObject json, Codec<T> codec, String key, T defaultValue)
    {
        if (!json.has(key))
        {
            return defaultValue;
        }
        return decodeCodec(json, codec, key);
    }

    public static <T> T decodeCodec(JsonObject json, Codec<T> codec, String key)
    {
        T result = codec.decode(JsonOps.INSTANCE, json.get(key)).getOrThrow(false, e -> {}).getFirst();
        if (result == null)
        {
            throw new JsonParseException("Unable to parse fauna json: " + json);
        }
        return result;
    }
}
