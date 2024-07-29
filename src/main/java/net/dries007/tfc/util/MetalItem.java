/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * This is a cache of item -> metal textures used in rendering. It is automatically populated via the contents of the common tags for
 * ingots, double ingots, and sheets, and supports basic "get the metal for this item" queries. The main purpose is to allow automatic-ish
 * compatibility for arbitrary mod metals, so long as textures are provided, without having to co-opt any other structures for this purpose.
 */
public record MetalItem(
    String name,
    ResourceLocation textureId,
    ResourceLocation softTextureId
)
{
    private static final MetalPartCache CACHE = IndirectHashCollection.create(new MetalPartCache(new IdentityHashMap<>()));
    private static final MetalItem UNKNOWN = new MetalItem("unknown");
    private static final Logger LOGGER = LogUtils.getLogger();

    public static MetalItem unknown()
    {
        return UNKNOWN;
    }

    public static MetalItem getOrUnknown(ItemStack stack)
    {
        return CACHE.values.getOrDefault(stack.getItem(), UNKNOWN);
    }

    public MetalItem(String name)
    {
        this(
            name,
            Helpers.identifier(name).withPrefix("block/metal/block/"),
            Helpers.identifier(name).withPrefix("block/metal/smooth/")
        );
    }

    record MetalPartCache(Map<Item, MetalItem> values) implements IndirectHashCollection.Cache
    {
        @Override
        public void clear()
        {
            values.clear();
        }

        @Override
        public void reload(RecipeManager manager)
        {
            final Map<String, MetalItem> instances = new HashMap<>();
            final Set<String> excludedTags = Set.copyOf(TFCConfig.SERVER.excludedMetalTagNames.get());

            values.clear();

            BuiltInRegistries.ITEM.getTags().forEach(pair -> {
                final ResourceLocation id = pair.getFirst().location();
                if (id.getNamespace().equals("c"))
                {
                    final String[] path = id.getPath().split("/");
                    if (path.length == 2
                        && (path[0].equals("ingots") || path[0].equals("double_ingots") || path[0].equals("sheets"))
                        && !path[1].contains("/")
                        && !excludedTags.contains(id.getPath())
                    )
                    {
                        final String metalName = path[1];
                        final MetalItem metal = instances.computeIfAbsent(metalName, key -> new MetalItem(metalName));
                        pair.getSecond().forEach(holder -> values.compute(holder.value(), (key, old) -> {
                            if (old != null)
                            {
                                LOGGER.warn("Item {} was inferred to match multiple metals {} and {}, using former", key, old.name, metalName);
                                return old;
                            }
                            return metal;
                        }));
                    }
                }
            });
        }
    }
}
