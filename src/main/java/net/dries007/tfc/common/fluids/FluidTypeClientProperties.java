package net.dries007.tfc.common.fluids;

import java.util.function.ToIntBiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

public record FluidTypeClientProperties(int tintColor, ToIntBiFunction<BlockAndTintGetter, BlockPos> tintColorFunction, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture)
{
    public FluidTypeClientProperties(int tintColor, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture)
    {
        this(tintColor, (level, pos) -> tintColor, stillTexture, flowingTexture, overlayTexture, renderOverlayTexture);
    }
}
