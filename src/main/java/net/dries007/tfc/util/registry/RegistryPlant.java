/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.util.calendar.Month;
import org.jetbrains.annotations.Nullable;

/**
 * Properties to create a {@link PlantBlock} subclass. The {@link #getStageProperty()} method is required in order to implement block dependent property initialization, as setting a property in the constructor is too late.
 */
public interface RegistryPlant
{
    /**
     * Get the current value of the stage property for the given month.
     */
    int stageFor(Month month);

    /**
     * Get the stage property used for the growth of this plant
     */
    @Nullable
    IntegerProperty getStageProperty();
}
