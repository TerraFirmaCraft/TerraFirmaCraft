/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.power;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.mechanical.MechanicalNetwork;
import net.dries007.tfc.util.mechanical.MechanicalUniverse;

public interface IRotator
{
    default BlockEntity getBlockEntity()
    {
        return (BlockEntity) this;
    }

    default boolean isClientSide()
    {
        return levelOrThrow().isClientSide();
    }

    default Level levelOrThrow()
    {
        return Objects.requireNonNull(getBlockEntity().getLevel());
    }

    default BlockPos getBlockPos()
    {
        return getBlockEntity().getBlockPos();
    }

    default boolean isSource()
    {
        return false;
    }

    @Nullable
    default MechanicalNetwork getExistingNetwork()
    {
        return MechanicalUniverse.get(this);
    }

    boolean hasShaft(LevelAccessor level, BlockPos pos, Direction facing);

    int getSignal();

    void setSignal(int signal);

    long getId();

    void setId(long id);

}
