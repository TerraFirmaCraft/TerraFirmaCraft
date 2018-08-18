/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects;

import java.util.function.BiFunction;

import net.minecraft.item.Item;

import net.dries007.tfc.api.types.MetalEnum;
import net.dries007.tfc.objects.items.metal.*;

public enum MetalType
{
    UNSHAPED(false, 100, null), // Special case, because it's a pottery item
    INGOT(false, 100, ItemIngot::new),
    DOUBLE_INGOT(false, 200),
    SCRAP(false, 100),
    DUST(false, 100),
    NUGGET(false, 10),
    SHEET(false, 200, ItemSheet::new),
    DOUBLE_SHEET(false, 400),
    LAMP(false, 100, ItemLamp::new),

    ANVIL(true, 1400, ItemAnvil::new),
    TUYERE(true, 400),

    PICK(true, 100, ItemMetalTool::new),
    PICK_HEAD(true, 100, true),
    SHOVEL(true, 100, ItemMetalTool::new),
    SHOVEL_HEAD(true, 100, true),
    AXE(true, 100, ItemMetalTool::new),
    AXE_HEAD(true, 100, true),
    HOE(true, 100, ItemMetalTool::new),
    HOE_HEAD(true, 100, true),
    CHISEL(true, 100, ItemMetalTool::new),
    CHISEL_HEAD(true, 100, true),
    SWORD(true, 200, ItemMetalTool::new),
    SWORD_BLADE(true, 200, true),
    MACE(true, 200, ItemMetalTool::new),
    MACE_HEAD(true, 200, true),
    SAW(true, 100, ItemMetalTool::new),
    SAW_BLADE(true, 100, true),
    JAVELIN(true, 100, ItemMetalTool::new), // todo: special class?
    JAVELIN_HEAD(true, 100, true),
    HAMMER(true, 100, ItemMetalTool::new),
    HAMMER_HEAD(true, 100, true),
    PROPICK(true, 100, ItemMetalTool::new),
    PROPICK_HEAD(true, 100, true),
    KNIFE(true, 100, ItemMetalTool::new),
    KNIFE_BLADE(true, 100, true),
    SCYTHE(true, 100, ItemMetalTool::new),
    SCYTHE_BLADE(true, 100, true),

    UNFINISHED_HELMET(true, 200),
    HELMET(true, 400, ItemMetalArmor::new),
    UNFINISHED_CHESTPLATE(true, 400),
    CHESTPLATE(true, 800, ItemMetalArmor::new),
    UNFINISHED_GREAVES(true, 400),
    GREAVES(true, 600, ItemMetalArmor::new),
    UNFINISHED_BOOTS(true, 200),
    BOOTS(true, 200, ItemMetalArmor::new);

    public final boolean toolItem;
    public final int smeltAmount;
    public final boolean hasMold;
    /**
     * Internal use only.
     */
    public final BiFunction<MetalEnum, MetalType, Item> supplier;

    MetalType(boolean toolItem, int smeltAmount, BiFunction<MetalEnum, MetalType, Item> supplier, boolean hasMold)
    {
        this.toolItem = toolItem;
        this.smeltAmount = smeltAmount;
        this.supplier = supplier;
        this.hasMold = hasMold;
    }

    MetalType(boolean toolItem, int smeltAmount, BiFunction<MetalEnum, MetalType, Item> supplier)
    {
        this(toolItem, smeltAmount, supplier, false);
    }

    MetalType(boolean toolItem, int smeltAmount, boolean hasMold)
    {
        this(toolItem, smeltAmount, ItemMetal::new, hasMold);
    }

    MetalType(boolean toolItem, int smeltAmount)
    {
        this(toolItem, smeltAmount, false);
    }
}
