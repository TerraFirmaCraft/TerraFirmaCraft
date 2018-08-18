/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;

import net.dries007.tfc.objects.MetalType;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.api.types.MetalEnum.Tier.*;

@Deprecated
public enum MetalEnum
{
    BISMUTH(TIER_I, 0.14, 270),
    BISMUTH_BRONZE(TIER_I, 0.35, 985, ToolMaterialsTFC.BISMUTH_BRONZE),
    BLACK_BRONZE(TIER_I, 0.35, 1070, ToolMaterialsTFC.BLACK_BRONZE),
    BRASS(TIER_I, 0.35, 930),
    BRONZE(TIER_I, 0.35, 950, ToolMaterialsTFC.BRONZE),
    COPPER(TIER_I, 0.35, 1080, ToolMaterialsTFC.COPPER),
    GOLD(TIER_I, 0.6, 1060),
    LEAD(TIER_I, 0.22, 328),
    NICKEL(TIER_I, 0.48, 1453),
    ROSE_GOLD(TIER_I, 0.35, 960),
    SILVER(TIER_I, 0.48, 961),
    TIN(TIER_I, 0.14, 230),
    ZINC(TIER_I, 0.21, 420),
    STERLING_SILVER(TIER_I, 0.35, 900),

    WROUGHT_IRON(TIER_III, 0.35, 1535, ToolMaterialsTFC.IRON),
    PIG_IRON(TIER_IV, 0.35, 1535), // bloom: 0.35, 1500

    STEEL(TIER_IV, 0.35, 1540, ToolMaterialsTFC.STEEL),

    PLATINUM(TIER_V, 0.35, 1730),

    BLACK_STEEL(TIER_V, 0.35, 1485, ToolMaterialsTFC.BLACK_STEEL),
    BLUE_STEEL(TIER_V, 0.35, 1540, ToolMaterialsTFC.BLUE_STEEL),
    RED_STEEL(TIER_V, 0.35, 1540, ToolMaterialsTFC.RED_STEEL),

    WEAK_STEEL(false, TIER_V, 0.35, 1540),
    WEAK_BLUE_STEEL(false, TIER_V, 0.35, 1540),
    WEAK_RED_STEEL(false, TIER_V, 0.35, 1540),

    HIGH_CARBON_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_BLUE_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_RED_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_BLACK_STEEL(false, TIER_V, 0.35, 1540),

    UNKNOWN(false, TIER_I, 0.5, 1250);

    public final boolean usable;
    public final Tier tier;
    public final double specificHeat;
    public final int meltTemp;
    public final Item.ToolMaterial toolMetal;

    MetalEnum(Tier tier, double sh, int melt)
    {
        this(true, tier, sh, melt, null);
    }

    MetalEnum(Tier tier, double sh, int melt, @Nonnull Item.ToolMaterial toolMetal)
    {
        this(true, tier, sh, melt, toolMetal);
    }

    MetalEnum(boolean usable, Tier tier, double sh, int melt)
    {
        this(usable, tier, sh, melt, null);
    }

    MetalEnum(boolean usable, Tier tier, double sh, int melt, @Nonnull Item.ToolMaterial toolMetal)
    {
        this.usable = usable;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.toolMetal = toolMetal;
    }

    public boolean hasType(MetalType type)
    {
        if (!usable) return type == MetalType.INGOT || type == MetalType.UNSHAPED;
        return !type.toolItem || this.toolMetal != null;
    }

    public enum Tier
    {
        TIER_I,
        TIER_II, // Not implemented, but presumed to be a more advanced, more capable version of the pit kiln.
        TIER_III,
        TIER_IV,
        TIER_V
    }

}
