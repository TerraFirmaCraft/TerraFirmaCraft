package net.dries007.tfc.util;

import java.util.Locale;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public final class JsonHelpers extends GsonHelper
{
    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(JsonObject json, String key, IForgeRegistry<T> registry)
    {
        return getRegistryEntry(GsonHelper.getAsString(json, key), registry);
    }

    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(JsonElement json, IForgeRegistry<T> registry)
    {
        return getRegistryEntry(GsonHelper.convertToString(json, "entry"), registry);
    }

    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(String key, IForgeRegistry<T> registry)
    {
        final ResourceLocation res = new ResourceLocation(key);
        final T obj = registry.getValue(res);
        if (obj == null)
        {
            throw new JsonParseException("Unknown entry from " + registry.getRegistryName().getPath() + ": " + key);
        }
        return obj;
    }

    public static <T> Tag<T> getTag(JsonObject json, String key, TagCollection<T> tags)
    {
        return getTag(GsonHelper.getAsString(json, key), tags);
    }

    public static <T> Tag<T> getTag(String key, TagCollection<T> tags)
    {
        final ResourceLocation res = new ResourceLocation(key);
        final Tag<T> tag = tags.getTag(res);
        if (tag == null)
        {
            throw new JsonParseException("Invalid tag name: " + key);
        }
        return tag;
    }

    @SuppressWarnings("ConstantConditions")
    public static <E extends Enum<E>> E getEnum(JsonObject obj, String key, Class<E> enumClass, @Nullable E defaultValue)
    {
        final String enumName = GsonHelper.getAsString(obj, key, null);
        if (enumName != null)
        {
            try
            {
                Enum.valueOf(enumClass, enumName.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException e)
            {
                throw new JsonParseException("No " + enumClass.getSimpleName() + " named: " + enumName);
            }
        }
        if (defaultValue != null)
        {
            return defaultValue;
        }
        throw new JsonParseException("Missing " + key + ", expected to find a string " + enumClass.getSimpleName());
    }

    public static <T> T getFrom(JsonObject json, String key, DataManager<T> manager)
    {
        final ResourceLocation id = new ResourceLocation(getAsString(json, key));
        final T obj = manager.get(id);
        if (obj == null)
        {
            throw new JsonParseException("No " + manager.typeName + " of name " + id);
        }
        return obj;
    }

    public static ItemStack getItemStack(JsonObject json, String key)
    {
        return CraftingHelper.getItemStack(getAsJsonObject(json, key), true);
    }

    public static JsonElement get(JsonObject json, String key)
    {
        if (!json.has(key))
        {
            throw new JsonParseException("Missing required key: " + key);
        }
        return json.get(key);
    }

    public static FluidStack getFluidStack(JsonObject json)
    {
        final int amount = GsonHelper.getAsInt(json, "amount", -1);
        final Fluid fluid = getRegistryEntry(json, "fluid", ForgeRegistries.FLUIDS);
        return new FluidStack(fluid, amount);
    }

    public static BlockState getBlockState(String block)
    {
        final StringReader reader = new StringReader(block);
        try
        {
            final BlockStateParser parser = new BlockStateParser(reader, false).parse(false);
            if (parser.getState() != null)
            {
                return parser.getState();
            }
            throw new JsonParseException("Weird result, valid parse but not a block state: " + block);
        }
        catch (CommandSyntaxException e)
        {
            throw new JsonParseException(e.getMessage());
        }
    }
}
