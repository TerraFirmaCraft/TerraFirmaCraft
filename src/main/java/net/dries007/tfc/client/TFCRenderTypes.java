/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

public class TFCRenderTypes extends RenderType
{
    public static final RenderType GHOST_BLOCK = create("ghost_block", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(BLOCK_SHEET).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(true));

    private TFCRenderTypes(String name, VertexFormat fmt, VertexFormat.Mode glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable)
    {
        super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
        throw new IllegalStateException("This class must not be instantiated");
    }
}
