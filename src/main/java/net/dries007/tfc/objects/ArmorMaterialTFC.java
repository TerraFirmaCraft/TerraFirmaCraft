/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

import net.dries007.tfc.api.types.IArmorMaterialTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * This is an extension enum for the vanilla's ArmorMaterials.
 * We register a new material in vanilla and bind crushing, slashing and piercing resistances.
 */
public enum ArmorMaterialTFC implements IArmorMaterialTFC
{
    //todo tweak all these values
    //currently, modifiers = classic / 40. Should give about 45% resistance(damage = 55%) to red/blue steel before letting vanilla mechanic do the rest.
    //red/blue steel has the same base resistance(eg: the damage you take on generic/the "base" reduction using vanilla mechanics) as vanilla's diamond armor
    //black and normal steel has the same as vanilla's iron armor
    //wrought iron is equivalent to chain mail
    //copper is a little better than vanilla's leather and bronzes are in between wrought iron and copper.

    //LEATHER?
    COPPER(EnumHelper.addArmorMaterial("copper", MOD_ID + ":copper", 14, new int[] {1, 3, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 10, 10, 6.25f),
    BISMUTH_BRONZE(EnumHelper.addArmorMaterial("bismuth_bronze", MOD_ID + ":bismuth_bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 15, 10, 8.25f),
    BLACK_BRONZE(EnumHelper.addArmorMaterial("black_bronze", MOD_ID + ":black_bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 10, 15, 8.25f),
    BRONZE(EnumHelper.addArmorMaterial("bronze", MOD_ID + ":bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 12.5f, 12.5f, 8.25f),
    WROUGHT_IRON(EnumHelper.addArmorMaterial("wrought_iron", MOD_ID + ":wrought_iron", 33, new int[] {1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 20, 20, 13.2f),
    STEEL(EnumHelper.addArmorMaterial("steel", MOD_ID + ":steel", 40, new int[] {2, 5, 6, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 25, 30, 16.5f),
    BLACK_STEEL(EnumHelper.addArmorMaterial("black_steel", MOD_ID + ":black_steel", 50, new int[] {2, 5, 6, 2}, 17, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F), 50, 45, 33),
    BLUE_STEEL(EnumHelper.addArmorMaterial("blue_steel", MOD_ID + ":blue_steel", 68, new int[] {3, 6, 8, 3}, 23, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F), 62.5f, 50, 50),
    RED_STEEL(EnumHelper.addArmorMaterial("red_steel", MOD_ID + ":red_steel", 68, new int[] {3, 6, 8, 3}, 23, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F), 50, 62.5f, 50);

    private final float piercingRes, slashingRes, crushingRes;
    private final ArmorMaterial material;

    ArmorMaterialTFC(ArmorMaterial material, float piercingRes, float slashingRes, float crushingRes)
    {
        this.material = material;
        this.crushingRes = crushingRes;
        this.piercingRes = piercingRes;
        this.slashingRes = slashingRes;
    }

    @Override
    public float getCrushingModifier()
    {
        return crushingRes;
    }

    @Override
    public float getPiercingModifier()
    {
        return piercingRes;
    }

    @Override
    public float getSlashingModifier()
    {
        return slashingRes;
    }

    @Override
    public ArmorMaterial getMaterial()
    {
        return material;
    }
}
