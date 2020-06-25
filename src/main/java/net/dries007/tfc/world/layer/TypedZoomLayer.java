/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;

import net.dries007.tfc.world.layer.traits.ITypedAreaTransformer1;
import net.dries007.tfc.world.layer.traits.ITypedNoiseRandom;
import net.dries007.tfc.world.layer.traits.LazyTypedArea;

/**
 * Modified version of {@link net.minecraft.world.gen.layer.ZoomLayer} for {@link LazyTypedArea}
 */
public abstract class TypedZoomLayer<A> implements ITypedAreaTransformer1<A>
{
    public static <A> TypedZoomLayer<A> normal()
    {
        return new TypedZoomLayer<A>()
        {
            @Override
            protected A pick(INoiseRandom context, A first, A second, A third, A fourth)
            {
                if (second.equals(third) && third.equals(fourth))
                {
                    return second;
                }
                else if (first.equals(second) && first.equals(third))
                {
                    return first;
                }
                else if (first.equals(second) && first.equals(fourth))
                {
                    return first;
                }
                else if (first.equals(third) && first.equals(fourth))
                {
                    return first;
                }
                else if (first.equals(second) && !third.equals(fourth))
                {
                    return first;
                }
                else if (first.equals(third) && !second.equals(fourth))
                {
                    return first;
                }
                else if (first.equals(fourth) && !second.equals(third))
                {
                    return first;
                }
                else if (second.equals(third) && !first.equals(fourth))
                {
                    return second;
                }
                else if (second.equals(fourth) && !first.equals(third))
                {
                    return second;
                }
                else
                {
                    return third.equals(fourth) && !first.equals(second) ? third : TypedZoomLayer.pickRandom(context, first, second, third, fourth);
                }
            }
        };
    }

    public static <A> TypedZoomLayer<A> fuzzy()
    {
        return new TypedZoomLayer<A>()
        {
            @Override
            protected A pick(INoiseRandom context, A first, A second, A third, A fourth)
            {
                return TypedZoomLayer.pickRandom(context, first, second, third, fourth);
            }
        };
    }

    private static <A> A pickRandom(INoiseRandom context, A first, A second)
    {
        return context.nextRandom(2) == 0 ? first : second;
    }

    private static <A> A pickRandom(INoiseRandom context, A first, A second, A third, A fourth)
    {
        int choice = context.nextRandom(4);
        switch (choice)
        {
            case 0:
                return first;
            case 1:
                return second;
            case 2:
                return third;
            case 3:
            default:
                return fourth;
        }
    }

    @Override
    public int getParentX(int x)
    {
        return x >> 1;
    }

    @Override
    public int getParentY(int z)
    {
        return z >> 1;
    }

    @Override
    public A apply(ITypedNoiseRandom<A> context, LazyTypedArea<A> area, int x, int z)
    {
        A baseValue = area.get(getParentX(x), getParentY(z));
        context.initRandom((x >> 1) << 1, (z >> 1) << 1);
        int xOffset = x & 1;
        int zOffset = z & 1;
        if (xOffset == 0 && zOffset == 0)
        {
            return baseValue;
        }
        else
        {
            A valuePlusZ = area.get(getParentX(x), getParentY(z + 1));
            if (xOffset == 0)
            {
                return pickRandom(context, baseValue, valuePlusZ);
            }
            else
            {
                A valuePlusX = area.get(getParentX(x + 1), getParentY(z));
                if (zOffset == 0)
                {
                    return pickRandom(context, baseValue, valuePlusX);
                }
                else
                {
                    A valuePlusBoth = area.get(getParentX(x + 1), getParentY(z + 1));
                    return pick(context, baseValue, valuePlusX, valuePlusZ, valuePlusBoth);
                }
            }
        }
    }

    protected abstract A pick(INoiseRandom context, A first, A second, A third, A fourth);
}
