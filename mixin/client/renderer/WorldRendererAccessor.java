/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.shapes.VoxelShape;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor
{
    @Invoker("renderShape")
    static void invoke$renderShape(MatrixStack mStack, IVertexBuilder builder, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha)
    {
        throw new AssertionError("Mixin not applied");
    }
}
