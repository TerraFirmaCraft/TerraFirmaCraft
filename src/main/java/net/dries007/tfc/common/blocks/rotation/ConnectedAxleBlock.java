/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import net.minecraft.resources.ResourceLocation;

/**
 * Implement on blocks which can derive an axle, which is then rendered attached to a rotational sink block.
 * <p>
 * <strong>Example:</strong>The Quern needs to render a short section of an axle to connect properly. It renders this using the adjacent block's axle, which it obtains through this interface.
 */
public interface ConnectedAxleBlock
{
    AxleBlock getAxle();

    default ResourceLocation getAxleTextureLocation()
    {
        return getAxle().getAxleTextureLocation();
    }
}
