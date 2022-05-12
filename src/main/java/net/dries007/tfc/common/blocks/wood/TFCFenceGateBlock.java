/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.world.level.block.FenceGateBlock;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class TFCFenceGateBlock extends FenceGateBlock implements IForgeBlockExtension
{
    private final ExtendedProperties properties;

    public TFCFenceGateBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
