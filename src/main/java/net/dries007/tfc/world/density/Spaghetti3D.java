/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.density;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public record Spaghetti3D(
    DensityFunction rarityModulator,
    DensityFunction thicknessModulator,
    DensityFunction.NoiseHolder noise1,
    DensityFunction.NoiseHolder noise2
) implements DensityFunction
{
    public static final KeyDispatchDataCodec<Spaghetti3D> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("rarity").forGetter(c -> c.rarityModulator),
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("thickness").forGetter(c -> c.thicknessModulator),
        DensityFunction.NoiseHolder.CODEC.fieldOf("noise1").forGetter(c -> c.noise1),
        DensityFunction.NoiseHolder.CODEC.fieldOf("noise2").forGetter(c -> c.noise2)
    ).apply(instance, Spaghetti3D::new)));

    @Override
    public double compute(DensityFunction.FunctionContext context)
    {
        final double rarity = getRarity(rarityModulator.compute(context));
        final double thickness = thicknessModulator.compute(context);
        final double value1 = noise1.getValue(context.blockX() / rarity, context.blockY() / rarity, context.blockZ() / rarity);
        final double value2 = noise2.getValue(context.blockX() / rarity, context.blockY() / rarity, context.blockZ() / rarity);
        return Mth.clamp(Math.max(Math.abs(rarity * value1), Math.abs(rarity * value2)) - thickness, -1.0, 1.0);
    }

    @Override
    public void fillArray(double[] array, ContextProvider provider)
    {
        provider.fillAllDirectly(array, this);
    }

    @Override
    public double minValue()
    {
        return -1;
    }

    @Override
    public double maxValue()
    {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec()
    {
        return CODEC;
    }

    @Override
    public DensityFunction mapAll(DensityFunction.Visitor visitor)
    {
        return visitor.apply(new Spaghetti3D(
            rarityModulator.mapAll(visitor),
            thicknessModulator.mapAll(visitor),
            visitor.visitNoise(noise1),
            visitor.visitNoise(noise2)
        ));
    }

    private double getRarity(double value)
    {
        if (value < -0.5)
        {
            return 0.75;
        }
        else if (value < 0)
        {
            return 1;
        }
        else
        {
            return value < 0.5 ? 1.5 : 2;
        }
    }
}
