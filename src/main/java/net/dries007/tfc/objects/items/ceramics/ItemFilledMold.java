/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.MetalType;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemFilledMold extends ItemFiredPottery implements IMetalObject
{
    private static final Map<Metal, EnumMap<MetalType, ItemFilledMold>> TABLE = new HashMap<>();

    public static ItemFilledMold get(Metal metal, MetalType type)
    {
        return TABLE.get(metal).get(type);
    }

    public final Metal metal;
    public final MetalType type;

    public ItemFilledMold(MetalType type, Metal metal)
    {
        this.type = type;
        this.metal = metal;

        if (!TABLE.containsKey(metal))
            TABLE.put(metal, new EnumMap<>(MetalType.class));
        TABLE.get(metal).put(type, this);

        setNoRepair();
        OreDictionaryHelper.register(this, "mold", type);
        OreDictionaryHelper.register(this, "mold", type, metal);
        setMaxDamage(type.smeltAmount);
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
