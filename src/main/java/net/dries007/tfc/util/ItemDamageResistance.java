/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ItemDamageResistance extends PhysicalDamageTypeData
{
    public static final DataManager<ItemDamageResistance> MANAGER = new DataManager<>(Helpers.identifier("item_damage_resistances"), "item_damage_resistances", ItemDamageResistance::new);

    @Nullable
    public static ItemDamageResistance get(ItemStack item)
    {
        for (ItemDamageResistance resist : MANAGER.getValues())
        {
            if (resist.matches(item))
            {
                return resist;
            }
        }
        return null;
    }

    private final Ingredient ingredient;

    public ItemDamageResistance(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        this.ingredient = Ingredient.fromJson(json.get("ingredient"));
    }

    public boolean matches(ItemStack item)
    {
        return ingredient.test(item);
    }
}
