/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TypedSourceLayer;
import net.dries007.tfc.world.noise.Cellular2D;

public class PlateGenerationLayer implements TypedSourceLayer<Plate>
{
    private static final float PI = (float) Math.PI;

    private final Cellular2D plateNoise;
    private final float oceanPercent;

    public PlateGenerationLayer(Cellular2D plateNoise, int oceanPercent)
    {
        this.plateNoise = plateNoise;
        this.oceanPercent = oceanPercent * 0.01f;
    }

    @Override
    public Plate apply(AreaContext context, int x, int z)
    {
        final Cellular2D.Cell cell = plateNoise.cell(x, z);
        final float centerX = cell.x();
        final float centerZ = cell.y();

        context.setSeed((long) centerX, (long) centerZ);

        final boolean oceanic = context.random().nextFloat() < oceanPercent;
        final float angle = 2 * PI * context.random().nextFloat();
        final float velocity = context.random().nextFloat();
        final float elevation = context.random().nextFloat();
        final float driftX = Mth.cos(angle) * velocity;
        final float driftZ = Mth.sin(angle) * velocity;

        return new Plate(centerX, centerZ, driftX, driftZ, elevation, oceanic);
    }
}
