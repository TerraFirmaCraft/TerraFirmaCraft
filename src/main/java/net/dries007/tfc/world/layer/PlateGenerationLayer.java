/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TypedSourceLayer;
import net.dries007.tfc.world.noise.Cellular2D;

public class PlateGenerationLayer implements TypedSourceLayer<Plate>
{
    private static final float PI = (float) Math.PI;

    private final Cellular2D plateNoise;
    private final int oceanPercent;

    public PlateGenerationLayer(Cellular2D plateNoise, int oceanPercent)
    {
        this.plateNoise = plateNoise;
        this.oceanPercent = oceanPercent;
    }

    @Override
    public Plate apply(AreaContext context, int x, int z)
    {
        plateNoise.noise(x, z);
        float centerX = plateNoise.getCenterX();
        float centerZ = plateNoise.getCenterY();
        context.initSeed(Float.floatToRawIntBits(centerX), Float.floatToRawIntBits(centerZ));
        for (int j = 0; j < 10; j++) context.nextInt(1);
        boolean oceanic = context.nextInt(100) < oceanPercent;
        float angle = 2 * PI * context.nextInt(100) / 100f;
        float velocity = context.nextInt(100) / 100f;
        float elevation = context.nextInt(100) / 100f;
        float driftX = MathHelper.cos(angle) * velocity;
        float driftZ = MathHelper.sin(angle) * velocity;
        return new Plate(centerX, centerZ, driftX, driftZ, elevation, oceanic);
    }
}
