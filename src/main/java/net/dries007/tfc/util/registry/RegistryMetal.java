/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;

import net.dries007.tfc.util.Metal;

/**
 * Interface used in registration to allow {@link Metal.BlockType}, {@link Metal.ItemType} to be used by addons.
 */
public interface RegistryMetal extends StringRepresentable
{
    Tier toolTier();

    ArmorMaterial armorTier();

    Metal.Tier metalTier();
}
