/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import net.dries007.tfc.api.IArmorMaterialTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCArmorMaterial implements IArmorMaterial, IArmorMaterialTFC
{
    public static final IArmorMaterialTFC COPPER = new TFCArmorMaterial(MOD_ID + ":copper", 14, new int[] {1, 3, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 10, 10, 6.25f);
    public static final IArmorMaterialTFC BISMUTH_BRONZE = new TFCArmorMaterial(MOD_ID + ":bismuth_bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 15, 10, 8.25f);
    public static final IArmorMaterialTFC BLACK_BRONZE = new TFCArmorMaterial(MOD_ID + ":black_bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 10, 15, 8.25f);
    public static final IArmorMaterialTFC BRONZE = new TFCArmorMaterial(MOD_ID + ":bronze", 21, new int[] {1, 4, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 12.5f, 12.5f, 8.25f);
    public static final IArmorMaterialTFC WROUGHT_IRON = new TFCArmorMaterial(MOD_ID + ":wrought_iron", 33, new int[] {1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 20, 20, 13.2f);
    public static final IArmorMaterialTFC STEEL = new TFCArmorMaterial(MOD_ID + ":steel", 40, new int[] {2, 5, 6, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 25, 30, 16.5f);
    public static final IArmorMaterialTFC BLACK_STEEL = new TFCArmorMaterial(MOD_ID + ":black_steel", 50, new int[] {2, 5, 6, 2}, 17, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.EMPTY, 50, 45, 33);
    public static final IArmorMaterialTFC BLUE_STEEL = new TFCArmorMaterial(MOD_ID + ":blue_steel", 68, new int[] {3, 6, 8, 3}, 23, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, () -> Ingredient.EMPTY, 62.5f, 50, 50);
    public static final IArmorMaterialTFC RED_STEEL = new TFCArmorMaterial(MOD_ID + ":red_steel", 68, new int[] {3, 6, 8, 3}, 23, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, () -> Ingredient.EMPTY, 50, 62.5f, 50);

    protected static final int[] MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};

    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final Supplier<Ingredient> repairMaterialSupplier;
    private final float crushingModifier;
    private final float piercingModifier;
    private final float slashingModifier;

    public TFCArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, Supplier<Ingredient> repairMaterialSupplier, float crushingModifier, float piercingModifier, float slashingModifier)
    {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmounts = damageReductionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.repairMaterialSupplier = repairMaterialSupplier;
        this.crushingModifier = crushingModifier;
        this.piercingModifier = piercingModifier;
        this.slashingModifier = slashingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getCrushingModifier()
    {
        return crushingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getPiercingModifier()
    {
        return piercingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getSlashingModifier()
    {
        return slashingModifier;
    }

    @Override
    public int getDurability(EquipmentSlotType slotIn)
    {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * maxDamageFactor;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn)
    {
        return damageReductionAmounts[slotIn.getIndex()];
    }

    @Override
    public int getEnchantability()
    {
        return enchantability;
    }

    @Override
    public SoundEvent getSoundEvent()
    {
        return equipSound;
    }

    @Override
    public Ingredient getRepairMaterial()
    {
        return repairMaterialSupplier.get();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public float getToughness()
    {
        return toughness;
    }
}
