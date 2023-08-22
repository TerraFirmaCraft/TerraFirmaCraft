/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.item.Item;

import net.dries007.tfc.common.capabilities.glass.GlassOperation;

public class GlassworkingItem extends Item
{
    private final GlassOperation operation;

    public GlassworkingItem(Properties properties, GlassOperation operation)
    {
        super(properties);
        this.operation = operation;
    }

    public GlassOperation getOperation()
    {
        return operation;
    }
}
