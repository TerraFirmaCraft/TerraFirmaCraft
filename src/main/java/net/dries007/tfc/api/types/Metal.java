/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.items.metal.*;

/**
 * todo: document API
 */
public class Metal extends IForgeRegistryEntry.Impl<Metal>
{
    @GameRegistry.ObjectHolder("tfc:unknown")
    public static final Metal UNKNOWN = null;

    private final Tier tier;
    private final float specificHeat;
    private final int meltTemp;
    private final boolean usable;
    private final int color;

    private final Item.ToolMaterial toolMetal;

    /**
     * This is a registry object that will create a number of things.
     *
     * Use the provided Builder to create your own metals
     *
     * @param name        the registry name of the object. The path must also be unique
     * @param tier        the tier of the metal
     * @param usable      is the metal usable to create basic metal items? (not tools)
     * @param sh          specific heat capacity. Higher = harder to heat up / cool down. Most IRL metals are between 0.3 - 0.7
     * @param melt        melting point. See @link Heat for temperature scale. Similar to IRL melting point in celcius.
     * @param color       color of the metal when in fluid form. Used to autogenerate a fluid texture
     * @param toolMetal   The tool material. Null if metal is not able to create tools
     */
    public Metal(@Nonnull ResourceLocation name, Tier tier, boolean usable, float sh, int melt, int color, @Nullable Item.ToolMaterial toolMetal)
    {
        this.usable = usable;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.color = color;

        this.toolMetal = toolMetal;

        setRegistryName(name);
    }

    @Nullable
    public Item.ToolMaterial getToolMetal()
    {
        return toolMetal;
    }

    public boolean isToolMetal()
    {
        return getToolMetal() != null;
    }

    public Tier getTier()
    {
        return tier;
    }

    public float getSpecificHeat()
    {
        return specificHeat;
    }

    public int getMeltTemp()
    {
        return meltTemp;
    }

    public int getColor()
    {
        return color;
    }

    @Override
    public String toString()
    {
        return String.valueOf(getRegistryName());
    }

    public enum Tier
    {
        TIER_I,
        TIER_II, // Not implemented, but presumed to be a more advanced, more capable version of the pit kiln.
        TIER_III,
        TIER_IV,
        TIER_V
    }

    public enum ItemType
    {
        UNSHAPED(false, 100, null), // Special case, because it's a pottery item
        INGOT(false, 100, ItemIngot::new, true),
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

        public static Item create(Metal metal, ItemType type)
        {
            return type.supplier.apply(metal, type);
        }

        public final boolean toolItem;
        public final int smeltAmount;
        public final boolean hasMold;
        public final BiFunction<Metal, ItemType, Item> supplier;

        ItemType(boolean toolItem, int smeltAmount, BiFunction<Metal, ItemType, Item> supplier, boolean hasMold)
        {
            this.toolItem = toolItem;
            this.smeltAmount = smeltAmount;
            this.supplier = supplier;
            this.hasMold = hasMold;
        }

        ItemType(boolean toolItem, int smeltAmount, boolean hasMold)
        {
            this(toolItem, smeltAmount, ItemMetal::new, hasMold);
        }

        ItemType(boolean toolItem, int smeltAmount)
        {
            this(toolItem, smeltAmount, false);
        }

        ItemType(boolean toolItem, int smeltAmount, BiFunction<Metal, ItemType, Item> supplier)
        {
            this(toolItem, smeltAmount, supplier, false);
        }

        public boolean hasType(Metal metal)
        {
            if (supplier == null)
                return false;
            if (!metal.usable)
                return this == ItemType.INGOT || this == ItemType.UNSHAPED;
            return !this.toolItem || metal.getToolMetal() != null;
        }
    }
}
