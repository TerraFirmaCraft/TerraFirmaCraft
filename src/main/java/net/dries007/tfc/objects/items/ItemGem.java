package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.EnumMap;

public class ItemGem extends Item
{
    private static final EnumMap<Gem, ItemGem> MAP = new EnumMap<>(Gem.class);

    public static ItemGem get(Gem gem)
    {
        return MAP.get(gem);
    }

    public static ItemStack get(Gem ore, Gem.Grade grade, int amount)
    {
        return new ItemStack(MAP.get(ore), amount, grade.getMeta());
    }

    public final Gem gem;

    public ItemGem(Gem gem)
    {
        this.gem = gem;
        if (MAP.put(gem, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        setHasSubtypes(true);
        OreDictionaryHelper.register(this, "gem");
        OreDictionaryHelper.register(this, "gem", gem);
        for (Gem.Grade grade : Gem.Grade.values())
        {
            OreDictionaryHelper.registerMeta(this, grade.getMeta(), "gem", gem, grade);
            OreDictionaryHelper.registerMeta(this, grade.getMeta(), "gem", grade);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;

        for (Gem.Grade grade : Gem.Grade.values())
            items.add(new ItemStack(this, 1, grade.getMeta()));
    }

    public Gem.Grade getGradeFromStack(ItemStack stack)
    {
        return Gem.Grade.fromMeta(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        Gem.Grade grade = getGradeFromStack(stack);
        return super.getUnlocalizedName(stack) + "." + grade.name().toLowerCase();
    }
}
