/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

/**
 * Interface for a block model which implements both unbaked and baked geometry. This means the model requires no individual baking and a single instance is sufficient.
 * Individual model data can be stored via the {@link net.neoforged.neoforge.client.model.data.ModelData} mechanism.
 */
public interface IBakedGeometry<T extends IBakedGeometry<T>> extends IUnbakedGeometry<T>, IDynamicBakedModel, IGeometryLoader<T>
{
    @Override
    default BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    default T read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
    {
        return (T) this;
    }
}
