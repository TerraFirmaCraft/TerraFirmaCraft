/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemMetal extends ItemTFC implements IMetalObject
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
    public int getSmeltAmount(ItemStack stack)
    {
        if (!isDamageable() || !stack.isItemDamaged()) return type.smeltAmount;
        double d = (stack.getMaxDamage() - stack.getItemDamage()) / (double) stack.getMaxDamage() - .10;
        return d < 0 ? 0 : MathHelper.floor(type.smeltAmount * d);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        switch (type)
        {
            case HAMMER:
            case INGOT:
            case SCRAP:
            case LAMP:
            case TUYERE:
            case PICK_HEAD:
            case SHOVEL_HEAD:
            case AXE_HEAD:
            case HOE_HEAD:
            case CHISEL:
            case CHISEL_HEAD:
            case SWORD_BLADE:
            case MACE_HEAD:
            case SAW_BLADE:
            case JAVELIN_HEAD:
            case HAMMER_HEAD:
            case PROPICK:
            case PROPICK_HEAD:
            case KNIFE:
            case KNIFE_BLADE:
            case SCYTHE:
                return Size.SMALL;
            case SAW:
            case SHEET:
            case DOUBLE_SHEET:
                return Size.NORMAL;
            case ANVIL:
                return Size.HUGE;
            case DUST:
                return Size.VERY_SMALL;
            case NUGGET:
                return Size.TINY;
            default:
                return Size.LARGE;
        }
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        switch (type)
        {
            case DOUBLE_SHEET:
            case ANVIL:
            case HELMET:
            case GREAVES:
            case CHESTPLATE:
            case BOOTS:
                return Weight.HEAVY;
            case HOE:
            case DUST:
            case NUGGET:
            case LAMP:
            case TUYERE:
            case UNFINISHED_CHESTPLATE:
            case UNFINISHED_GREAVES:
            case UNFINISHED_HELMET:
            case UNFINISHED_BOOTS:
                return Weight.LIGHT;
            default:
                return Weight.MEDIUM;
        }
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        switch (type)
        {
            case TUYERE:
                return false;
            default:
                return true;
        }
    }
}
