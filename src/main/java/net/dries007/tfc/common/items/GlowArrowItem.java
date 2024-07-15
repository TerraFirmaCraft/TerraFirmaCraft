/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.entities.misc.GlowArrow;

public class GlowArrowItem extends ArrowItem
{
    public GlowArrowItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon)
    {
        return new GlowArrow(level, shooter, weapon);
    }
}
