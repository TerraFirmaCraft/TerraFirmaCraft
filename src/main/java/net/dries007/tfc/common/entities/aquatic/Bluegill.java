/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.items.TFCItems;

public class Bluegill extends TFCSalmon
{
    public Bluegill(EntityType<? extends TFCSalmon> type, Level level)
    {
        super(type, level);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.BLUEGILL_BUCKET.get());
    }
}
