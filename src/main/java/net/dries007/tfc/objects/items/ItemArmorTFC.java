/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.damage.IDamageResistance;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.IArmorMaterialTFC;

public class ItemArmorTFC extends ItemArmor implements IItemSize, IDamageResistance
{
    private final IArmorMaterialTFC materialTFC;

    public ItemArmorTFC(IArmorMaterialTFC materialTFC, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
    {
        super(materialTFC.getMaterial(), renderIndexIn, equipmentSlotIn);
        this.materialTFC = materialTFC;
        setNoRepair();
    }

    @Override
    public float getCrushingModifier()
    {
        return materialTFC.getCrushingModifier();
    }

    @Override
    public float getPiercingModifier()
    {
        return materialTFC.getPiercingModifier();
    }

    @Override
    public float getSlashingModifier()
    {
        return materialTFC.getSlashingModifier();
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Stored in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY; // Stacksize is already restricted to 1
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return false;
    }
}
