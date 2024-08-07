/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.util.Metal;

/**
 * Interface used in registration to allow {@link Metal.BlockType}, {@link Metal.ItemType} to be used by addons.
 */
public interface RegistryMetal extends StringRepresentable
{
    LevelTier toolTier();

    Holder<ArmorMaterial> armorMaterial();

    int armorDurability(ArmorItem.Type type);

    Block getBlock(Metal.BlockType type);

    MapColor mapColor();

    Rarity rarity();

    default boolean weatheredParts()
    {
        return weatheringResistance() != -1;
    }

    /**
     * @return A weathering resistance, either in {@code [0, 1]} indicating a resistance to weathering, or {@code -1} to indicate
     * no weathering occurs for this block, and weathered block variants are not registered.
     */
    float weatheringResistance();
}
