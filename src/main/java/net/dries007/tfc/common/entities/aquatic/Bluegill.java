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
