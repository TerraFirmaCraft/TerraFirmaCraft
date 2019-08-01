/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.forge.ForgeableHandler;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.items.ItemArmorTFC;

public class ItemMetalArmor extends ItemArmorTFC implements IMetalObject, IItemSize
{
    private static final Map<Metal, EnumMap<Metal.ItemType, ItemMetalArmor>> TABLE = new HashMap<>();

    public static ItemMetalArmor get(Metal metal, Metal.ItemType type)
    {
        return TABLE.get(metal).get(type);
    }

    private final Metal metal;
    private final Metal.ItemType type;

    public ItemMetalArmor(Metal metal, Metal.ItemType type)
    {
        //noinspection ConstantConditions
        super(metal.getArmorMetal(), type.getArmorSlot(), type.getEquipmentSlot());
        this.metal = metal;
        this.type = type;
        if (!TABLE.containsKey(metal))
            TABLE.put(metal, new EnumMap<>(Metal.ItemType.class));
        TABLE.get(metal).put(type, this);
    }

    @Nullable
    @Override
    public Metal getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return type.getSmeltAmount();
    }

    @Override
    @Nonnull
    public EnumRarity getRarity(ItemStack stack)
    {
        switch (metal.getTier())
        {
            case TIER_I:
            case TIER_II:
                return EnumRarity.COMMON;
            case TIER_III:
                return EnumRarity.UNCOMMON;
            case TIER_IV:
                return EnumRarity.RARE;
            case TIER_V:
                return EnumRarity.EPIC;
        }
        return super.getRarity(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ForgeableHandler(nbt, metal.getSpecificHeat(), metal.getMeltTemp());
    }
}
