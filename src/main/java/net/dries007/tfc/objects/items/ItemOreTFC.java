package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.blocks.BlockOreTFC;
import net.dries007.tfc.util.IMetalObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.EnumMap;

public class ItemOreTFC extends Item implements IMetalObject
{
    private static final EnumMap<Ore, ItemOreTFC> MAP = new EnumMap<>(Ore.class);

    public static ItemOreTFC get(Ore ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Ore ore, BlockOreTFC.Grade grade, int amount)
    {
        return new ItemStack(MAP.get(ore), amount, ore.graded ? grade.getMeta() : 0);
    }

    public static ItemStack get(Ore ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Ore ore;

    public ItemOreTFC(Ore ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        if (ore.graded) setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;
        if (ore.graded)
            for (BlockOreTFC.Grade grade : BlockOreTFC.Grade.values())
                items.add(new ItemStack(this, 1, grade.getMeta()));
        else
            items.add(new ItemStack(this));
    }

    public BlockOreTFC.Grade getGradeFromStack(ItemStack stack)
    {
        return BlockOreTFC.Grade.byMetadata(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        BlockOreTFC.Grade grade = getGradeFromStack(stack);
        if (grade == BlockOreTFC.Grade.NORMAL) return super.getUnlocalizedName(stack);
        return super.getUnlocalizedName(stack) + "." + grade.getName();
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ore.metal;
    }

    @Override
    public boolean isSmeltable(ItemStack stack)
    {
        return ore.metal != null;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return getGradeFromStack(stack).smeltAmount;
    }
}
