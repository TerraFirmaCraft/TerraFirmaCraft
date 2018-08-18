/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.MetalEnum;
import net.dries007.tfc.objects.MetalType;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemFilledMold extends ItemFiredPottery implements IMetalObject
{
    private static final InsertOnlyEnumTable<MetalEnum, MetalType, ItemFilledMold> TABLE = new InsertOnlyEnumTable<>(MetalEnum.class, MetalType.class);

    public static ItemFilledMold get(MetalEnum metal, MetalType type)
    {
        return TABLE.get(metal, type);
    }

    public final MetalEnum metal;
    public final MetalType type;

    public ItemFilledMold(MetalType type, MetalEnum metal)
    {
        this.type = type;
        this.metal = metal;
        TABLE.put(metal, type, this);
        setNoRepair();
        OreDictionaryHelper.register(this, "mold", type);
        OreDictionaryHelper.register(this, "mold", type, metal);
        setMaxDamage(type.smeltAmount);
    }

    @Override
    public MetalEnum getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
