package net.dries007.tfc.data;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.ICalendar;

public interface Accessors
{
    default Ingredient ingredientOf(Metal metal, Metal.ItemType type)
    {
        return type.isDefault()
            ? Ingredient.of(tagOf(metal, type))
            : Ingredient.of(TFCItems.METAL_ITEMS.get(metal).get(type).get());
    }

    default Ingredient ingredientOf(Metal metal, Metal.BlockType type)
    {
        return type == Metal.BlockType.BLOCK
            ? Ingredient.of(tagOf(Registries.ITEM, "storage_blocks/" + metal.name()))
            : Ingredient.of(TFCBlocks.METALS.get(metal).get(type).get());
    }

    default TagKey<Item> tagOf(Metal metal, Metal.ItemType type)
    {
        assert type.isDefault() : "Non-typical use of tag for " + metal.getSerializedName() + " / " + type.name().toLowerCase(Locale.ROOT);
        return tagOf(Registries.ITEM, type.name() + "s/" + metal.name());
    }

    default <T> TagKey<T> tagOf(ResourceKey<Registry<T>> key, String name)
    {
        return TagKey.create(key, ResourceLocation.fromNamespaceAndPath("c", name.toLowerCase(Locale.ROOT)));
    }

    default Item itemOf(ResourceLocation name)
    {
        assert BuiltInRegistries.ITEM.containsKey(name) : "No item '" + name + "'";
        return BuiltInRegistries.ITEM.get(name);
    }

    default Fluid fluidOf(DyeColor color)
    {
        return TFCFluids.COLORED_FLUIDS.get(color).getSource();
    }

    default Fluid fluidOf(SimpleFluid fluid)
    {
        return TFCFluids.SIMPLE_FLUIDS.get(fluid).getSource();
    }

    default Fluid fluidOf(Metal metal)
    {
        return TFCFluids.METALS.get(metal).getSource();
    }

    default ResourceLocation nameOf(ItemLike item)
    {
        assert item.asItem() != Items.AIR;
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    default int units(Metal.ItemType type)
    {
        return switch (type)
        {
            case ROD -> 50;
            default -> 100;
            case DOUBLE_INGOT, SHEET, FISH_HOOK, FISHING_ROD, SWORD, SWORD_BLADE, MACE, MACE_HEAD, SHEARS, UNFINISHED_BOOTS -> 200;
            case DOUBLE_SHEET, TUYERE, UNFINISHED_HELMET, UNFINISHED_CHESTPLATE, UNFINISHED_GREAVES, SHIELD, BOOTS -> 400;
            case HELMET, GREAVES -> 600;
            case CHESTPLATE -> 800;
            case HORSE_ARMOR -> 1200;
        };
    }

    default int units(Metal.BlockType type)
    {
        return switch (type)
        {
            case ANVIL -> 1400;
            case BLOCK, LAMP -> 100;
            case BLOCK_SLAB -> 50;
            case BLOCK_STAIRS -> 75;
            case BARS -> 25;
            case CHAIN -> 6;
            case TRAPDOOR -> 200;
        };
    }

    default int hours(int hours)
    {
        return hours * ICalendar.TICKS_IN_HOUR;
    }

    default <T1, T2, V> Map<T1, V> pivot(Map<T1, Map<T2, V>> map, T2 key)
    {
        return map.entrySet()
            .stream()
            .filter(e -> e.getValue().containsKey(key))
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(key)));
    }
}
