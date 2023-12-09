/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class BladedAxleBlock extends AbstractShaftAxleBlock
{
    private final Supplier<? extends AxleBlock> axle;

    public BladedAxleBlock(ExtendedProperties properties, Supplier<? extends AxleBlock> axle)
    {
        super(properties);
        this.axle = axle;
    }

    @Override
    public AxleBlock getAxle()
    {
        return axle.get();
    }
}
