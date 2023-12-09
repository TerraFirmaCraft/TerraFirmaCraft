/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class AxleBlock extends AbstractShaftAxleBlock
{
    private final Supplier<? extends WindmillBlock> windmill;
    private final ResourceLocation textureLocation;

    public AxleBlock(ExtendedProperties properties, Supplier<? extends WindmillBlock> windmill, ResourceLocation textureLocation)
    {
        super(properties);

        this.windmill = windmill;
        this.textureLocation = textureLocation;
    }

    @Override
    public ResourceLocation getAxleTextureLocation()
    {
        return textureLocation;
    }

    @Override
    public AxleBlock getAxle()
    {
        return this;
    }

    public WindmillBlock getWindmill()
    {
        return windmill.get();
    }

}
