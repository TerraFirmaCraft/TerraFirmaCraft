/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DelegateFoodHandler extends IFood
{
    IFood getFoodHandler();

    @Override
    default long getCreationDate()
    {
        return getFoodHandler().getCreationDate();
    }

    @Override
    default void setCreationDate(long creationDate)
    {
        getFoodHandler().setCreationDate(creationDate);
    }

    @Override
    default long getRottenDate()
    {
        return getFoodHandler().getRottenDate();
    }

    @Override
    default boolean isTransientNonDecaying()
    {
        return getFoodHandler().isTransientNonDecaying();
    }

    @Override
    default FoodData getData()
    {
        return getFoodHandler().getData();
    }

    @Override
    default float getDecayDateModifier()
    {
        return getFoodHandler().getDecayDateModifier();
    }

    @Override
    default void setNonDecaying()
    {
        getFoodHandler().setNonDecaying();
    }

    @Override
    default List<FoodTrait> getTraits()
    {
        return getFoodHandler().getTraits();
    }

    @Override
    default CompoundTag serializeNBT()
    {
        return getFoodHandler().serializeNBT();
    }

    @Override
    default void deserializeNBT(CompoundTag nbt)
    {
        getFoodHandler().deserializeNBT(nbt);
    }

    @Override
    default boolean isRotten()
    {
        return getFoodHandler().isRotten();
    }
}
