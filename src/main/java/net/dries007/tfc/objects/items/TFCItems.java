/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import net.dries007.tfc.api.*;
import net.dries007.tfc.objects.TFCItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.Map;
import net.dries007.tfc.api.Metal;
import net.dries007.tfc.api.RockCategory;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.TFCItemGroup.ROCK_BLOCKS;

/**
 * Collection of all TFC items.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCItems
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

    public static final Map<Gem.Default, Map<Gem.Grade, RegistryObject<Item>>> GEMS = Util.make(new EnumMap<>(Gem.Default.class), map -> {
        for (Gem.Default gem : Gem.Default.values())
        {
            Map<Gem.Grade, RegistryObject<Item>> inner = new EnumMap<>(Gem.Grade.class);
            for(Gem.Grade grade : Gem.Grade.values()) {
                String name = ("gem/" + grade.name().toLowerCase() +"/"+ gem.name()).toLowerCase();
                RegistryObject<Item> item = ITEMS.register(name, () -> new Item(new Item.Properties().group(TFCItemGroup.GEM)));
                inner.put(grade, item);
            }
            map.put(gem, inner);
        }
    });
    public static final Map<Metal.Default, Map<Metal.ItemType, RegistryObject<Item>>> METAL_ITEMS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.ItemType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), () -> type.create(metal))
        )
    );

    public static final Map<RockCategory, Map<RockCategory.ItemType, RegistryObject<Item>>> ROCK_TOOLS = Helpers.mapOfKeys(RockCategory.class, category ->
        Helpers.mapOfKeys(RockCategory.ItemType.class, type ->
            register(("stone/" + type.name() + "/" + category.name()).toLowerCase(), () -> type.create(category))
        )
    );

    public static final Map<Rock.Default, Map<RockCategory.RockItems, RegistryObject<Item>>> ROCK_ITEMS = Util.make(new EnumMap<>(Rock.Default.class), map -> {
        for (Rock.Default rock : Rock.Default.values())
        {
            Map<RockCategory.RockItems, RegistryObject<Item>> inner = new EnumMap<>(RockCategory.RockItems.class);
            for(RockCategory.RockItems rockItems : RockCategory.RockItems.values()) {
                String name = ("rock/" + rockItems.name().toLowerCase() +"/"+ rock.name()).toLowerCase();
                RegistryObject<Item> item = ITEMS.register(name, () -> new Item(new Item.Properties().group(TFCItemGroup.MISC)));
                inner.put(rockItems, item);
            }
            map.put(rock, inner);
        }
    });

    private static RegistryObject<Item> register(String name, ItemGroup group)
    {
        return register(name, () -> new Item(new Item.Properties().group(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name, item);
    }
}
