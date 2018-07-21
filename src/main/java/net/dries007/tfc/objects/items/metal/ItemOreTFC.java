/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.EnumMap;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.util.Size;
import net.dries007.tfc.api.util.Weight;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.OreEnum;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemOreTFC extends ItemTFC implements IMetalObject
{
    private static final EnumMap<OreEnum, ItemOreTFC> MAP = new EnumMap<>(OreEnum.class);

    public static ItemOreTFC get(OreEnum ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(OreEnum ore, OreEnum.Grade grade, int amount)
    {
        return new ItemStack(MAP.get(ore), amount, ore.graded ? grade.getMeta() : 0);
    }

    public static ItemStack get(OreEnum ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final OreEnum ore;

    public ItemOreTFC(OreEnum ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        if (ore.metal != null)
        {
            setHasSubtypes(true);
            OreDictionaryHelper.register(this, "ore");
            OreDictionaryHelper.register(this, "ore", ore);
            for (OreEnum.Grade grade : OreEnum.Grade.values())
            {
                OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", grade);
                OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", ore, grade);
            }
        }
        else // Mineral
        {
            OreDictionaryHelper.register(this, "gem", ore);
            switch (ore)
            {
                case LAPIS_LAZULI:
                    OreDictionaryHelper.register(this, "gem", "lapis");
                    break;
                case BITUMINOUS_COAL:
                    OreDictionaryHelper.register(this, "gem", "coal");
                    break;
            }
        }
    }

    public OreEnum.Grade getGradeFromStack(ItemStack stack)
    {
        return OreEnum.Grade.byMetadata(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        OreEnum.Grade grade = getGradeFromStack(stack);
        if (grade == OreEnum.Grade.NORMAL) return super.getUnlocalizedName(stack);
        return super.getUnlocalizedName(stack) + "." + grade.getName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;
        if (ore.graded)
            for (OreEnum.Grade grade : OreEnum.Grade.values())
                items.add(new ItemStack(this, 1, grade.getMeta()));
        else
            items.add(new ItemStack(this));
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ore.metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return getGradeFromStack(stack).smeltAmount;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
