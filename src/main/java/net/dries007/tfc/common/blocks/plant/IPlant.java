/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.state.IntegerProperty;

import net.dries007.tfc.util.calendar.Month;

/**
 * Marker interface for {@link PlantBlock} subclasses.
 * See the various static create(IPlant, Properties) methods in these blocks to create plants.
 */
public interface IPlant
{
    /**
     * Get the current value of the stage property for the given month.
     */
    int stageFor(Month month);

    /**
     * Get the stage property used for the growth of this plant
     */
    IntegerProperty getStageProperty();
}
