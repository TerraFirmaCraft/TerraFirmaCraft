/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum Butterfly
{
    GOLDEN_BIRDWING(23f, 30f, 300f, 500f),
    PAPILIO_RUMANZOVIA(20f, 30f,400f, 500f),
    PAPILIO_PALINURUS(21f, 30f, 300f, 500f),
    PEACOCK(10f, 30f, 120f, 410f),
    SERICINUS(15f, 18f, 150f, 400f),
    PAPILIO_BLUMEI(18f, 24f, 300f, 500f),
    ADONIS_BLUE(10f, 20f, 150f, 400f),
    SILVERWASHED_FRITILLARY(9f, 15f, 150f, 250f);

    public static final Butterfly[] VALUES = Butterfly.values();

    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;

    Butterfly(float minTemp, float maxTemp, float minRain, float maxRain)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
    }

    @Nullable
    public static Butterfly getRandomButterfly(float temp, float rain, RandomSource random)
    {
        final Butterfly fly = VALUES[random.nextInt(VALUES.length)];
        if (fly.minTemp < temp && temp < fly.maxTemp && fly.minRain < rain && rain < fly.maxRain)
        {
            return fly;
        }
        return null;
    }
}
