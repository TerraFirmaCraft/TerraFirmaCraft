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
