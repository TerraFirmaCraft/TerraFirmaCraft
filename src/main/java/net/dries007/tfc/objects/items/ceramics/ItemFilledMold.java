package net.dries007.tfc.objects.items.ceramics;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemFilledMold extends ItemFiredPottery implements IMetalObject
{
    private static final InsertOnlyEnumTable<Metal, Metal.ItemType, ItemFilledMold> TABLE = new InsertOnlyEnumTable<>(Metal.class, Metal.ItemType.class);

    public static ItemFilledMold get(Metal metal, Metal.ItemType type)
    {
        return TABLE.get(metal, type);
    }

    public final Metal metal;
    public final Metal.ItemType type;

    public ItemFilledMold(Metal.ItemType type, Metal metal)
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
