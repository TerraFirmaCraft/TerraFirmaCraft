/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum Moth
{
    MOTH_SATURNIA(4f, 16f, 150f, 400f),
    MOTH_ARGEMA(15f, 30f, 300f, 500f),
    MOTH_ATTACUS(14f, 30f, 330f, 500f),
    MOTH_LUNA(4f, 30f, 100f, 500f),
    MOTH_DIAPHORA(6f, 16f, 150f, 400f),
    MOTH_TROSIA(14f, 30f, 330f, 500f);

    public static final Moth[] VALUES = Moth.values();

    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;

    Moth(float minTemp, float maxTemp, float minRain, float maxRain)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
    }

    @Nullable
    public static Moth getRandomMoth(float temp, float rain, RandomSource random)
    {
        final Moth moth = VALUES[random.nextInt(VALUES.length)];
        if (moth.minTemp < temp && temp < moth.maxTemp && moth.minRain < rain && rain < moth.maxRain)
        {
            return moth;
        }
        return null;
    }
}
