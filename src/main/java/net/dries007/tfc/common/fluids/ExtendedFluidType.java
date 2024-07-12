/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
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
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
            {
                return clientProperties.tintColorFunction().applyAsInt(getter, pos);
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
