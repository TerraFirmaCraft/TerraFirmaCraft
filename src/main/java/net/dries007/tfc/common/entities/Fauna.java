/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.world.placement.ClimatePlacement;

/**
 * A data driven way to make spawning conditions for animals player configurable.
 */
public record Fauna(
    int chance,
    int distanceBelowSeaLevel,
    ClimatePlacement climate,
    boolean solidGround,
    int maxBrightness
) {
    public static final Codec<Fauna> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("chance", 1).forGetter(c -> c.chance),
        Codec.INT.optionalFieldOf("distance_below_sea_level", -1).forGetter(c -> c.distanceBelowSeaLevel),
        ClimatePlacement.CODEC.forGetter(c -> c.climate),
        Codec.BOOL.optionalFieldOf("solid_ground", false).forGetter(c -> c.solidGround),
        Codec.INT.optionalFieldOf("max_brightness", -1).forGetter(c -> c.maxBrightness)
    ).apply(i, Fauna::new));

    public static final DataManager<Fauna> MANAGER = new DataManager<>(Helpers.identifier("fauna"), CODEC);
}
