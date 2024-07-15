/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.Map;
import java.util.function.Function;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;


// todo 1.21, this whole thing looks concerning and broken and idk how to fix it
public record TrimmedItemModel(@Nullable ArmorTrim trim) implements IUnbakedGeometry<TrimmedItemModel>
{

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        /*
        final TextureAtlasSprite baseSprite = spriteGetter.apply(context.getMaterial("armor"));
        final TextureAtlasSprite overlaySprite = context.hasMaterial("overlay") ? spriteGetter.apply(context.getMaterial("overlay")) : null;
        final ResourceLocation trimLocation = context.getMaterial("trim").texture();
        final String color = trim != null ? trim.material().value().assetName() : null;
        final TextureAtlasSprite trimSprite = trim != null ? spriteGetter.apply(new Material(RenderHelpers.BLOCKS_ATLAS, trimLocation.withSuffix("_" + color))) : null;

        final var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);
        final var builder = CompositeModel.Baked.builder(itemContext, baseSprite, new TrimOverrideHandler(overrides, baker, itemContext, this), context.getTransforms());
        final var normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());

        addQuads(modelState, modelLocation, baseSprite, builder, normalRenderTypes, ContainedFluidModel.FLUID_TRANSFORM);

        if (overlaySprite != null)
        {
            addQuads(modelState, modelLocation, overlaySprite, builder, normalRenderTypes, ContainedFluidModel.FLUID_TRANSFORM);
        }

        if (trimSprite != null)
        {
            addQuads(modelState, modelLocation, trimSprite, builder, normalRenderTypes, ContainedFluidModel.COVER_TRANSFORM);
        }

        builder.setParticle(baseSprite);
        return builder.build();
        */
        return null;
    }

    private static void addQuads(ModelState modelState, ResourceLocation modelLocation, TextureAtlasSprite trimSprite, CompositeModel.Baked.Builder builder, RenderTypeGroup normalRenderTypes, @Nullable Transformation transformation)
    {
        /*var transformedState = transformation == null ? modelState : new SimpleModelState(modelState.getRotation().compose(transformation), modelState.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, trimSprite.contents());
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> trimSprite, transformedState, modelLocation);
        builder.addQuads(normalRenderTypes, quads);*/
    }

    public static class Loader implements IGeometryLoader<TrimmedItemModel>
    {
        @Override
        public TrimmedItemModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            return new TrimmedItemModel(null);
        }
    }

    private static final class TrimOverrideHandler extends ItemOverrides
    {
        private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;
        private final TrimmedItemModel parent;

        private TrimOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, TrimmedItemModel parent)
        {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
        {
            /*BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
            if (overridden != originalModel || level == null) return overridden;
            return ArmorTrim.getTrim(level.registryAccess(), stack).map(trim -> {
                final String name = trim.material().get().assetName();
                if (!cache.containsKey(name))
                {
                    TrimmedItemModel unbaked = new TrimmedItemModel(trim);
                    BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this, Helpers.resourceLocation("forge", "bucket_override"));
                    cache.put(name, bakedModel);
                    return bakedModel;
                }
                return cache.get(name);
            }).orElse(originalModel);*/
            return null;
        }
    }

}
