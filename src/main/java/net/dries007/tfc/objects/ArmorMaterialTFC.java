/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * This is an extension enum for the vanilla's ArmorMaterials.
 * We register a new material in vanilla and bind crushing, slashing and piercing resistances.
 */
public enum ArmorMaterialTFC
{
    //todo tweak all these values

    COPPER(EnumHelper.addArmorMaterial("copper", MOD_ID + ":copper", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    BISMUTH_BRONZE(EnumHelper.addArmorMaterial("bismuth_bronze", MOD_ID + ":bismuth_bronze", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    BLACK_BRONZE(EnumHelper.addArmorMaterial("black_bronze", MOD_ID + ":black_bronze", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    BRONZE(EnumHelper.addArmorMaterial("bronze", MOD_ID + ":bronze", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    WROUGHT_IRON(EnumHelper.addArmorMaterial("wrought_iron", MOD_ID + ":wrought_iron", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    STEEL(EnumHelper.addArmorMaterial("steel", MOD_ID + ":steel", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    BLACK_STEEL(EnumHelper.addArmorMaterial("black_steel", MOD_ID + ":black_steel", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10),
    BLUE_STEEL(EnumHelper.addArmorMaterial("blue_steel", MOD_ID + ":blue_steel", 15, new int[] {2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 50, 50, 50),
    RED_STEEL(EnumHelper.addArmorMaterial("red_steel", MOD_ID + ":red_steel", 21, new int[] {3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F), 10, 10, 10);

    private final float crushingRes, slashingRes, piercingRes;
    private final ArmorMaterial material;

    ArmorMaterialTFC(ArmorMaterial material, float crushingRes, float piercingRes, float slashingRes)
    {
        this.material = material;
        this.crushingRes = crushingRes;
        this.piercingRes = piercingRes;
        this.slashingRes = slashingRes;
    }

    public float getCrushingModifier()
    {
        return crushingRes;
    }

    public float getPiercingModifier()
    {
        return piercingRes;
    }

    public float getSlashingModifier()
    {
        return slashingRes;
    }

    public ArmorMaterial getMaterial()
    {
        return material;
    }
}
