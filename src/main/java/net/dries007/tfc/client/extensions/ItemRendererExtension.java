/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.extensions;

import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public record ItemRendererExtension(Supplier<BlockEntityWithoutLevelRenderer> renderer) implements IClientItemExtensions
{
    public static ItemRendererExtension cached(com.google.common.base.Supplier<BlockEntityWithoutLevelRenderer> renderer)
    {
        return new ItemRendererExtension(Suppliers.memoize(renderer));
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer()
    {
        return renderer.get();
    }
}
