package net.dries007.tfc.common.capabilities.power;

public interface IRotationProvider
{
    boolean isPowered();

    void setPowered(boolean powered);
}
