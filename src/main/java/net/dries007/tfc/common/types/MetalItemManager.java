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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class MetalItemManager
{
    public static final DataManager<MetalItem> MANAGER = new DataManager.Instance<>(MetalItem::new, "metal_items", "metal item", true);
    public static final IndirectHashCollection<Item, MetalItem> CACHE = new IndirectHashCollection<>(MetalItem::getValidItems);

    @Nullable
    public static MetalItem get(ItemStack stack)
    {
        for (MetalItem def : CACHE.getAll(stack.getItem()))
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
        MetalItem def = get(stack);
        if (def != null)
        {
            text.add(new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".tooltip.metal", def.getMetal().getDisplayName()));
            text.add(new TranslatableComponent(TerraFirmaCraft.MOD_ID + ".tooltip.units", def.getAmount()));
            text.add(def.getMetal().getTier().getDisplayName());
        }
    }
}