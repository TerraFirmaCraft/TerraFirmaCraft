/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

@SuppressWarnings("WeakerAccess")
public final class ToolMaterialsTFC
{
    public static final Item.ToolMaterial IG_IN = EnumHelper.addToolMaterial("IgIn", 1, 60, 7, 2.0f, 5); //Tier 0
    public static final Item.ToolMaterial SED = EnumHelper.addToolMaterial("Sed", 1, 70, 7, 2.0f, 5);
    public static final Item.ToolMaterial IG_EX = EnumHelper.addToolMaterial("IgEx", 1, 50, 6, 2.0f, 5);
    public static final Item.ToolMaterial M_M = EnumHelper.addToolMaterial("MM", 1, 55, 6.5f, 2.0f, 5);
    public static final Item.ToolMaterial COPPER = EnumHelper.addToolMaterial("Copper", 2, 600, 8, 3.25f, 8); //Tier 1
    public static final Item.ToolMaterial BRONZE = EnumHelper.addToolMaterial("Bronze", 2, 1300, 11, 5.0f, 13); //Tier 2
    public static final Item.ToolMaterial BISMUTH_BRONZE = EnumHelper.addToolMaterial("BismuthBronze", 2, 1200, 10, 4.5f, 10);
    public static final Item.ToolMaterial BLACK_BRONZE = EnumHelper.addToolMaterial("BlackBronze", 2, 1460, 9, 4.75f, 10);
    public static final Item.ToolMaterial IRON = EnumHelper.addToolMaterial("Iron", 2, 2200, 12, 6.75f, 10); //Tier 3
    public static final Item.ToolMaterial STEEL = EnumHelper.addToolMaterial("Steel", 2, 3300, 14, 8.5f, 10); //Tier 4
    public static final Item.ToolMaterial BLACK_STEEL = EnumHelper.addToolMaterial("BlackSteel", 3, 4200, 16, 10.25f, 12); //Tier 5
    public static final Item.ToolMaterial BLUE_STEEL = EnumHelper.addToolMaterial("BlueSteel", 3, 6500, 18, 12.0f, 22); //Tier 6
    public static final Item.ToolMaterial RED_STEEL = EnumHelper.addToolMaterial("RedSteel", 3, 6500, 18, 12.0f, 22);

    private ToolMaterialsTFC() {}
}
