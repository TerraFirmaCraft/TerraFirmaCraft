/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.Block;

public class ExtendedBlock extends Block implements IForgeBlockExtension
{
    private final ExtendedProperties extendedProperties;

    public ExtendedBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.extendedProperties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return extendedProperties;
    }
}
