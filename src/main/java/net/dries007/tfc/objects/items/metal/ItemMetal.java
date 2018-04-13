package net.dries007.tfc.objects.items.metal;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemMetal extends Item implements IMetalObject
{
    private static final InsertOnlyEnumTable<Metal, Metal.ItemType, ItemMetal> TABLE = new InsertOnlyEnumTable<>(Metal.class, Metal.ItemType.class);

    public static ItemMetal get(Metal metal, Metal.ItemType type)
    {
        return TABLE.get(metal, type);
    }

    public final Metal metal;
    public final Metal.ItemType type;

    public ItemMetal(Metal metal, Metal.ItemType type)
    {
        this.metal = metal;
        this.type = type;
        TABLE.put(metal, type, this);
        setNoRepair();
        OreDictionaryHelper.register(this, type);
        OreDictionaryHelper.register(this, type, metal);
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public boolean isSmeltable(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        if (!isDamageable() || !stack.isItemDamaged()) return type.smeltAmount;
        double d = (stack.getMaxDamage() - stack.getItemDamage()) / (double)stack.getMaxDamage() - .10;
        return d < 0 ? 0 : MathHelper.floor(type.smeltAmount * d);
    }
}
