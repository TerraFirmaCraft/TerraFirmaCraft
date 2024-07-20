/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.extensions;

import java.util.function.ToIntBiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

public record FluidRendererExtension(
    int tintColor,
    ToIntBiFunction<BlockAndTintGetter, BlockPos> tintColorFunction,
    ResourceLocation stillTexture,
    ResourceLocation flowingTexture,
    @Nullable ResourceLocation overlayTexture,
    @Nullable ResourceLocation renderOverlayTexture
) implements IClientFluidTypeExtensions
{
    public FluidRendererExtension(int tintColor, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture)
    {
        this(tintColor, (level, pos) -> tintColor, stillTexture, flowingTexture, overlayTexture, renderOverlayTexture);
    }

    @Override
    public int getTintColor()
    {
        return tintColor;
    }

    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return tintColorFunction.applyAsInt(getter, pos);
    }

    @Override
    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    @Override
    @Nullable
    public ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    @Override
    @Nullable
    public ResourceLocation getRenderOverlayTexture(Minecraft minecraft)
    {
        return renderOverlayTexture;
    }
}
