package net.dries007.tfc.common.capabilities.power;

import net.minecraft.core.Direction;

public interface IRotationProvider
{
    boolean isPowered();

    /**
     * @return {@code true if powering succeeded}
     */
    boolean setPowered(boolean powered);

    /**
     * @return {@code true} if machines should attempt to cause this to rotate (ie, an axle could cause this to receive power)
     */
    default boolean canBeDriven()
    {
        return true;
    }

    /**
     * @return {@code true} if this can rotate other devices (ie, this could cause an axle to get rotated)
     */
    default boolean canDriveOthers()
    {
        return true;
    }

    default boolean terminatesPowerTrain()
    {
        return false;
    }
}
