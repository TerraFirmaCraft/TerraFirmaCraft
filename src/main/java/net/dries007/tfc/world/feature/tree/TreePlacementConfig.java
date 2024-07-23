/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Locale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

public record TreePlacementConfig(int width, int height, GroundType groundType)
{
    public static final Codec<TreePlacementConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("width").forGetter(c -> c.width),
        Codec.INT.fieldOf("height").forGetter(c -> c.height),
        GroundType.CODEC.optionalFieldOf("ground_type", GroundType.NORMAL).forGetter(c -> c.groundType)
    ).apply(instance, TreePlacementConfig::new));

    public boolean mayPlaceInWater()
    {
        return groundType == GroundType.SHALLOW_WATER || groundType == GroundType.SUBMERGED || groundType == GroundType.SUBMERGED_ALLOW_SALTWATER;
    }

    public boolean mayPlaceUnderwater()
    {
        return groundType == GroundType.SUBMERGED || groundType == GroundType.SUBMERGED_ALLOW_SALTWATER;
    }

    public boolean requiresFreshwater()
    {
        return groundType != GroundType.SUBMERGED_ALLOW_SALTWATER && groundType != GroundType.SHALLOW_ALLOW_SALTWATER;
    }

    public enum GroundType implements StringRepresentable
    {
        NORMAL,
        SAND,
        SHALLOW_WATER,
        SUBMERGED,
        SHALLOW_ALLOW_SALTWATER,
        SUBMERGED_ALLOW_SALTWATER,
        FLOATING;

        public static final Codec<GroundType> CODEC = StringRepresentable.fromEnum(GroundType::values);

        private final String serializedName = name().toLowerCase(Locale.ROOT);

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}