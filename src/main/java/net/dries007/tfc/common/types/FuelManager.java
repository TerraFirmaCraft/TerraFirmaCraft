/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class FuelManager
{
    public static final DataManager<Fuel> MANAGER = new DataManager.Instance<>(Fuel::new, "fuels", "fuel", true);
    public static final IndirectHashCollection<Item, Fuel> CACHE = new IndirectHashCollection<>(Fuel::getValidItems);

    @Nullable
    public static Fuel get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        final Fuel fuel = get(stack);
        if (fuel != null)
        {
            // todo: color and convert temperature and duration to words
            text.add(new TranslatableComponent("tfc.tooltip.fuel", fuel.getDuration(), fuel.getTemperature()));
        }
    }
}
