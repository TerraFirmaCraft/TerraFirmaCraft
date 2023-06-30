/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class ExtendedFluidType extends FluidType
{
    private final FluidTypeClientProperties clientProperties;

    public ExtendedFluidType(Properties properties, FluidTypeClientProperties clientProperties)
    {
        super(properties);

        this.clientProperties = clientProperties;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor()
            {
                return clientProperties.tintColor();
            }

            @Override
            public ResourceLocation getStillTexture()
            {
                return clientProperties.stillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return clientProperties.flowingTexture();
            }

            @Override
            @Nullable
            public ResourceLocation getOverlayTexture()
            {
                return clientProperties.overlayTexture();
            }

            @Override
            @Nullable
            public ResourceLocation getRenderOverlayTexture(Minecraft minecraft)
            {
                return clientProperties.renderOverlayTexture();
            }
        });
    }
}
