/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

public final class ToolMaterialsTFC
{
    // Damage here is for the sword.
    // Stone weapons have 75% the damage of a vanilla's wood sword while red/blue steel is like a diamond sword with sharpess V (3+ dmg)
    // All in-between weapons have an exponential growth (not much steep but still making it worth to upgrade)
    public static final Item.ToolMaterial IGNEOUS_INTRUSIVE = EnumHelper.addToolMaterial("tfc_igneous_intrusive", 1, 60, 7, 2.0f, 5); //Tier 0
    public static final Item.ToolMaterial SEDIMENTARY = EnumHelper.addToolMaterial("tfc_sedimentary", 1, 50, 7, 2.0f, 5);
    public static final Item.ToolMaterial IGNEOUS_EXTRUSIVE = EnumHelper.addToolMaterial("tfc_igneous_extrusive", 1, 70, 6, 2.0f, 5);
    public static final Item.ToolMaterial METAMORPHIC = EnumHelper.addToolMaterial("tfc_metamorphic", 1, 55, 6.5f, 2.0f, 5);
    public static final Item.ToolMaterial COPPER = EnumHelper.addToolMaterial("tfc_copper", 2, 600, 8, 3.5f, 8); //Tier 1
    public static final Item.ToolMaterial BRONZE = EnumHelper.addToolMaterial("tfc_bronze", 2, 1300, 11, 4.0f, 13); //Tier 2
    public static final Item.ToolMaterial BISMUTH_BRONZE = EnumHelper.addToolMaterial("tfc_bismuth_bronze", 2, 1200, 10, 4.0f, 10);
    public static final Item.ToolMaterial BLACK_BRONZE = EnumHelper.addToolMaterial("tfc_black_bronze", 2, 1460, 9, 4.25f, 10);
    public static final Item.ToolMaterial WROUGHT_IRON = EnumHelper.addToolMaterial("tfc_iron", 2, 2200, 12, 4.75f, 12); //Tier 3
    public static final Item.ToolMaterial STEEL = EnumHelper.addToolMaterial("tfc_steel", 2, 3300, 14, 5.75f, 12); //Tier 4
    public static final Item.ToolMaterial BLACK_STEEL = EnumHelper.addToolMaterial("tfc_black_steel", 3, 4200, 16, 7.0f, 17); //Tier 5
    public static final Item.ToolMaterial BLUE_STEEL = EnumHelper.addToolMaterial("tfc_blue_steel", 3, 6500, 18, 9.0f, 22); //Tier 6
    public static final Item.ToolMaterial RED_STEEL = EnumHelper.addToolMaterial("tfc_red_steel", 3, 6500, 18, 9.0f, 22);

    private ToolMaterialsTFC() {}
}
