/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TreePlacementConfig(int width, int height, boolean allowSubmerged, boolean allowDeeplySubmerged)
{
    public static final Codec<TreePlacementConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("width").forGetter(c -> c.width),
        Codec.INT.fieldOf("height").forGetter(c -> c.height),
        Codec.BOOL.optionalFieldOf("allow_submerged", false).forGetter(c -> c.allowSubmerged),
        Codec.BOOL.optionalFieldOf("allow_deeply_submerged", false).forGetter(c -> c.allowDeeplySubmerged)
        ).apply(instance, TreePlacementConfig::new));
}