/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

import static net.minecraft.world.level.block.Block.*;

public class VaneBlock extends ExtendedBlock implements EntityBlockExtension, IForgeBlockExtension
{
    private final ExtendedProperties properties;
    public static BooleanProperty ATTACHED_WIND_DEVICES = TFCBlockStateProperties.ATTACHED_WIND_DEVICES;
    private static final VoxelShape SHAPE = box(6D, 0.0D, 6D, 10D, 12.0D, 10D);

    public VaneBlock(ExtendedProperties properties)
    {
        super(properties);
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return null;
    }

}
