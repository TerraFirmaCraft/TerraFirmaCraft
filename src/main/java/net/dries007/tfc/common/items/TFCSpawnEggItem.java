/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

import net.dries007.tfc.mixin.item.SpawnEggItemAccessor;

public class TFCSpawnEggItem extends SpawnEggItem
{
    public static final List<TFCSpawnEggItem> EGGS = new ArrayList<>();

    public static void setup()
    {
        //Map<EntityType<?>, SpawnEggItem> map = SpawnEggItemAccessor.accessor$getIdMap();
        EGGS.forEach(egg -> SpawnEggItemAccessor.accessor$getIdMap().put(egg.defaultType.get(), egg));
    }

    protected final Supplier<? extends EntityType<?>> defaultType;

    public TFCSpawnEggItem(Supplier<? extends EntityType<?>> entity, int color1, int color2, Item.Properties properties)
    {
        super(null, color1, color2, properties);
        defaultType = entity;
        EGGS.add(this);
        SpawnEggItemAccessor.accessor$getIdMap().remove(null); // populate with null key, and then delete it
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT nbt)
    {
        if (nbt != null && nbt.contains("EntityTag", 10))
        {
            CompoundNBT compoundnbt = nbt.getCompound("EntityTag");
            if (compoundnbt.contains("id", 8))
            {
                return EntityType.byString(compoundnbt.getString("id")).orElse(defaultType.get());
            }
        }
        return defaultType.get();
    }


}
