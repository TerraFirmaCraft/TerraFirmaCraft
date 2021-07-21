/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.common.ItemDefinition;
import net.dries007.tfc.util.Helpers;

public class ItemSizeDefinition extends ItemDefinition implements IItemSize
{
    private final Size size;
    private final Weight weight;

    public ItemSizeDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        this.size = Helpers.getEnumFromJson(json, "size", Size.class, Size.NORMAL);
        this.weight = Helpers.getEnumFromJson(json, "weight", Weight.class, Weight.MEDIUM);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return weight;
    }
}
