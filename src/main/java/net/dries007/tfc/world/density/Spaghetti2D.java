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

public record Spaghetti2D(
    DensityFunction rarityModulator,
    DensityFunction thicknessModulator,
    DensityFunction elevationModulator,
    DensityFunction.NoiseHolder noise

) implements DensityFunction
{
    public static final KeyDispatchDataCodec<Spaghetti2D> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("rarity").forGetter(c -> c.rarityModulator),
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("thickness").forGetter(c -> c.thicknessModulator),
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("elevation").forGetter(c -> c.elevationModulator),
        DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(c -> c.noise)
    ).apply(instance, Spaghetti2D::new)));

    @Override
    public double compute(DensityFunction.FunctionContext context)
    {
        final double rarity = getRarity(rarityModulator.compute(context));
        final double rarityNoise = noise.getValue(context.blockX() / rarity, context.blockY() / rarity, context.blockZ() / rarity);
        final double thickness = thicknessModulator.compute(context);
        final double elevation = elevationModulator.compute(context);
        final double left = Math.abs(elevation - context.blockY() / 8.0) - thickness;
        final double right = Math.abs(rarity * rarityNoise) - 0.083D * thickness;
        return Mth.clamp(Math.max(left * left * left, right), -1.0, 1.0);
    }

    @Override
    public void fillArray(double[] array, ContextProvider context)
    {
        context.fillAllDirectly(array, this);
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
        return visitor.apply(new Spaghetti2D(
            rarityModulator.mapAll(visitor),
            thicknessModulator.mapAll(visitor),
            elevationModulator.mapAll(visitor),
            visitor.visitNoise(noise)
        ));
    }

    private double getRarity(double value)
    {
        if (value < -0.75)
        {
            return 0.5;
        }
        else if (value < -0.5)
        {
            return 0.75;
        }
        else if (value < 0.5)
        {
            return 1;
        }
        else
        {
            return value < 0.75 ? 2 : 3;
        }
    }
}
