/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;

/**
 * Simple implementation of a {@link BakedModel} which delegates to a {@link StaticModelData} provided by the {@link net.minecraftforge.client.model.data.ModelData} mechanism.
 */
public interface IStaticBakedModel extends IDynamicBakedModel
{
    @Override
    @NotNull
    default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType renderType)
    {
        final StaticModelData property = data.get(StaticModelData.PROPERTY);
        if (property != null && side == null)
        {
            return property.quads();
        }
        return List.of();
    }

    @Override
    default TextureAtlasSprite getParticleIcon(ModelData data)
    {
        final StaticModelData property = data.get(StaticModelData.PROPERTY);
        if (property != null)
        {
            return property.particleIcon();
        }
        return RenderHelpers.missingTexture();
    }

    @Override
    default TextureAtlasSprite getParticleIcon()
    {
        return RenderHelpers.missingTexture();
    }

    @Override
    default boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    default boolean isGui3d()
    {
        return false;
    }

    @Override
    default boolean usesBlockLight()
    {
        return true;
    }

    @Override
    default boolean isCustomRenderer()
    {
        return false;
    }

    @Override
    default ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

    record StaticModelData(List<BakedQuad> quads, TextureAtlasSprite particleIcon)
    {
        public static final ModelProperty<StaticModelData> PROPERTY = new ModelProperty<>();
    }
}
