/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.world.layer.traits.ITypedAreaTransformer0;
import net.dries007.tfc.world.layer.traits.ITypedNoiseRandom;
import net.dries007.tfc.world.noise.Cellular2D;

public class PlateGenerationLayer implements ITypedAreaTransformer0<Plate>
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
    public Plate apply(ITypedNoiseRandom<Plate> context, int x, int z)
    {
        plateNoise.noise(x, z);
        float centerX = plateNoise.getCenterX();
        float centerZ = plateNoise.getCenterY();
        context.initRandom(Float.floatToRawIntBits(centerX), Float.floatToRawIntBits(centerZ));
        for (int j = 0; j < 10; j++) context.nextRandom(1);
        boolean oceanic = context.nextRandom(100) < oceanPercent;
        float angle = 2 * PI * context.nextRandom(100) / 100f;
        float velocity = context.nextRandom(100) / 100f;
        float elevation = context.nextRandom(100) / 100f;
        float driftX = MathHelper.cos(angle) * velocity;
        float driftZ = MathHelper.sin(angle) * velocity;
        return new Plate(centerX, centerZ, driftX, driftZ, elevation, oceanic);
    }
}
