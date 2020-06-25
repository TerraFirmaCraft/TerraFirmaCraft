/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.world.layer.traits.ITypedAreaTransformer0;
import net.dries007.tfc.world.layer.traits.ITypedNoiseRandom;
import net.dries007.tfc.world.noise.Vec2;
import net.dries007.tfc.world.noise.VoronoiNoise2D;

public class PlateGenerationLayer implements ITypedAreaTransformer0<Plate>
{
    private static final float PI = (float) Math.PI;

    private final VoronoiNoise2D plateNoise;
    private final float plateSpread;
    private final int oceanPercent;

    public PlateGenerationLayer(VoronoiNoise2D plateNoise, float plateSpread, int oceanPercent)
    {
        this.plateNoise = plateNoise;
        this.plateSpread = plateSpread;
        this.oceanPercent = oceanPercent;
    }

    @Override
    public Plate apply(ITypedNoiseRandom<Plate> context, int x, int z)
    {
        Vec2 plateCenter = plateNoise.center(x * plateSpread, z * plateSpread);
        context.initRandom(Float.floatToRawIntBits(plateCenter.x), Float.floatToRawIntBits(plateCenter.y));
        boolean oceanic = context.nextRandom(100) < oceanPercent;
        float angle = 2 * PI * context.nextRandom(1000) / 1000f;
        float velocity = context.nextRandom(1000) / 1000f;
        float elevation = context.nextRandom(1000) / 1000f;
        float driftX = MathHelper.cos(angle) * velocity;
        float driftZ = MathHelper.sin(angle) * velocity;
        return new Plate(plateCenter.x / plateSpread, plateCenter.y / plateSpread, driftX, driftZ, elevation, oceanic);
    }
}
