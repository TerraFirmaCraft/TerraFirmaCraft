package net.dries007.tfc.world.river;

import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public enum Flow implements StringRepresentable
{
    // The order of these is important, as we do ordinal based rotations and averages
    NNN("n", 0, -2), // 0
    NNE("nne", 1, -2),
    N_E("ne", 1, -1),
    NEE("nee", 2, -1),
    EEE("e", 2, 0), // 4
    SEE("see", 2, 1),
    S_E("se", 1, 1),
    SSE("sse", 1, 2),
    SSS("s", 0, 2), // 8
    SSW("ssw", -1, 2),
    S_W("sw", -1, 1),
    SWW("sww", -2, 1),
    WWW("w", -2, 0), // 12
    NWW("nww", -2, -1),
    N_W("nw", -1, -1),
    NNW("nnw", -1, -2), // 15
    NONE("none", 0, 0);

    private static final Flow[] VALUES = values();
    private static final int MODULUS = VALUES.length - 1; // Since when taking modulo, we want to skip NONE

    public static Flow valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : NONE;
    }

    public static Flow fromAngle(float angle)
    {
        // Polar coordinates - angle 0 = East, positive up to pi is -> north -> west, negative down to -pi is -> south -> west
        int ordinal = Math.round(4 - (8 * (angle / Mth.PI)));
        if (ordinal < 0)
        {
            ordinal += MODULUS;
        }
        return VALUES[ordinal];
    }

    /**
     * Averages four flows from the corners of a square, using two weights to describe the location within the square.
     */
    public static Flow combine(Flow flowNE, Flow flowSE, Flow flowNW, Flow flowSW, float weightE, float weightN)
    {
        Flow flowN = combine(flowNE, flowNW, weightE, flowSE != NONE && flowSW != NONE);
        Flow flowS = combine(flowSE, flowSW, weightE, flowNE != NONE && flowNW != NONE);
        return combine(flowN, flowS, weightN, false);
    }

    /**
     * Averages two flows with a weighted value.
     *
     * @param preventNone if true, this will default to not return none, unless both left and right are none
     */
    public static Flow combine(Flow left, Flow right, float weightLeft, boolean preventNone)
    {
        if (left == NONE)
        {
            return preventNone || weightLeft < 0.5 ? right : NONE;
        }
        else if (right == NONE)
        {
            return preventNone || weightLeft > 0.5 ? left : NONE;
        }
        else
        {
            int ordinalDistance = Math.abs(left.ordinal() - right.ordinal());
            if (ordinalDistance == 8)
            {
                // exact opposites
                return weightLeft > 0.5 ? left : right;
            }
            else if (ordinalDistance < 8)
            {
                // The center is the correct average
                int newOrdinal = (int) (left.ordinal() * weightLeft + right.ordinal() * (1 - weightLeft));
                return VALUES[newOrdinal];
            }
            else
            {
                // We need to average outside the center, by shifting the smaller one, averaging, and then taking a modulo
                int leftValue = left.ordinal(), rightValue = right.ordinal();
                if (leftValue < rightValue)
                {
                    leftValue += 16;
                }
                else
                {
                    rightValue += 16;
                }
                int newOrdinal = (int) (leftValue * weightLeft + rightValue * (1 - weightLeft));
                return VALUES[newOrdinal % MODULUS];
            }
        }
    }

    private final String name;
    private final int x, z;

    Flow(String name, int x, int z)
    {
        this.name = name;
        this.x = x;
        this.z = z;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }
}
