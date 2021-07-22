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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class FuelManager extends DataManager<Fuel>
{
    public static final FuelManager INSTANCE = new FuelManager();

    private static final IndirectHashCollection<Item, Fuel> CACHE = new IndirectHashCollection<>(Fuel::getValidItems);
    private static final String TOOLTIP_KEY = TerraFirmaCraft.MOD_ID + ".tooltip.fuel";

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

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        final Fuel fuel = get(stack);
        if (fuel != null)
        {
            // todo: color and convert temperature to words
            text.add(new TranslationTextComponent(TOOLTIP_KEY, fuel.getDuration(), fuel.getTemperature()));
        }
    }

    private FuelManager()
    {
        super(new GsonBuilder().create(), "fuels", "fuel", true);
    }

    @Override
    protected Fuel read(ResourceLocation id, JsonObject obj)
    {
        return new Fuel(id, obj);
    }
}
