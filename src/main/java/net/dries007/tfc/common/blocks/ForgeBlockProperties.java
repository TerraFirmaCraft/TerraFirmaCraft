/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ForgeBlockProperties
{
    private final BlockBehaviour.Properties properties;

    @Nullable
    private Supplier<? extends BlockEntity> tileEntityFactory;
    private int flammability;
    private int fireSpreadSpeed;

    public ForgeBlockProperties(BlockBehaviour.Properties properties)
    {
        this.properties = properties;

        tileEntityFactory = null;
        flammability = 0;
        fireSpreadSpeed = 0;
    }

    public ForgeBlockProperties tileEntity(Supplier<? extends BlockEntity> tileEntityFactory)
    {
        this.tileEntityFactory = tileEntityFactory;
        return this;
    }

    public ForgeBlockProperties flammable(int flammability, int fireSpreadSpeed)
    {
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
        return this;
    }

    public BlockBehaviour.Properties properties()
    {
        return properties;
    }

    boolean hasTileEntity()
    {
        return tileEntityFactory != null;
    }

    @Nullable
    BlockEntity createTileEntity()
    {
        return tileEntityFactory != null ? tileEntityFactory.get() : null;
    }

    int getFlammability()
    {
        return flammability;
    }

    int getFireSpreadSpeed()
    {
        return fireSpreadSpeed;
    }
}
