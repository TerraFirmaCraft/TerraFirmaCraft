/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.plant.PlantBlock;

/**
 * Properties to create a {@link PlantBlock} subclass.
 */
public interface RegistryPlant
{
    /**
     * Whether this plant starts blooming on the wettest day of the year (true) or the hottest (false)
     */
    boolean isWetSeasonBlooming();

    /**
     * What time of day this plant starts blooming. Set equal to end time to always bloom.
     */
    int getStartTime();

    /**
     * What time of day this plant stops blooming. Set equal to start time to always bloom.
     */
    int getEndTime();

    /**
     * The year fraction after the hottest/wettest day of the year to start displaying blooming model
     */
    float getBloomOffset();

    /**
     * The year fraction after the start of blooming to stop displaying blooming model and start displaying seeding model
     */
    float getBloomingEnd();

    /**
     * The year fraction after the start of blooming to stop displaying seeding model and start displaying dying model
     */
    float getSeedingEnd();

    /**
     * The year fraction after the start of blooming to stop displaying dying model and start displaying dormant model
     */
    float getDyingEnd();

    /**
     * The year fraction after the start of blooming to stop displaying dormant model and start displaying sprouting model
     */
    float getDormantEnd();

    /**
     * The year fraction after the start of blooming to stop displaying sprouting model and start displaying budding model
     */
    float getSproutingEnd();

    @Nullable
    IntegerProperty getAgeProperty();
}
