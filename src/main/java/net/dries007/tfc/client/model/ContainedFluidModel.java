/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.*;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

/**
 * Copy pasta of {@link net.minecraftforge.client.model.DynamicBucketModel} with
 *
 * - most useless junk removed
 * - a better implementation of "does this item contain said fluid" for simple containers that doesn't require that the item can actually *drain* said fluid.
 */
public class ContainedFluidModel implements IModelGeometry<ContainedFluidModel>
{
    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_COVER = 7.496f / 16f;
    private static final float SOUTH_Z_COVER = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    private final Fluid fluid;

    public ContainedFluidModel(Fluid fluid)
    {
        this.fluid = fluid;
    }

    public ContainedFluidModel withFluid(Fluid newFluid)
    {
        return new ContainedFluidModel(newFluid);
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        Material particleLocation = owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : null;
        Material baseLocation = owner.isTexturePresent("base") ? owner.resolveTexture("base") : null;
        Material fluidMaskLocation = owner.isTexturePresent("fluid") ? owner.resolveTexture("fluid") : null;
        Material coverLocation = owner.isTexturePresent("cover") ? owner.resolveTexture("cover") : null;

        ModelState transformsFromModel = owner.getCombinedTransform();

        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(fluid.getAttributes().getStillTexture())) : null;
        TextureAtlasSprite coverSprite = coverLocation != null && baseLocation != null ? spriteGetter.apply(coverLocation) : null;

        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap =
            PerspectiveMapWrapper.getTransforms(new CompositeModelState(transformsFromModel, modelTransform));

        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;
        if (particleSprite == null)
        {
            particleSprite = fluidSprite == null ? spriteGetter.apply(new Material(RenderHelpers.BLOCKS_ATLAS, Helpers.identifier("block/empty"))) : fluidSprite;
        }

        Transformation transform = modelTransform.getRotation();
        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, Objects.requireNonNull(particleSprite), new ContainedFluidItemOverride(overrides, bakery, owner, this), transformMap);

        if (baseLocation != null)
        {
            // build base (insidest)
            builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseLocation), transform, spriteGetter));
        }

        if (fluidMaskLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null)
            {
                // build liquid layer (inside)
                int luminosity = fluid.getAttributes().getLuminosity();
                int color = fluid.getAttributes().getColor();
                builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, 1, luminosity));
                builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1, luminosity));
            }
        }

        if (coverSprite != null)
        {
            TextureAtlasSprite baseSprite = spriteGetter.apply(baseLocation);
            builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, NORTH_Z_COVER, Direction.NORTH, 0xFFFFFFFF, 2));
            builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, SOUTH_Z_COVER, Direction.SOUTH, 0xFFFFFFFF, 2));
        }

        builder.setParticle(particleSprite);

        return builder.build();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        final Set<Material> textures = new HashSet<>();

        if (owner.isTexturePresent("particle")) textures.add(owner.resolveTexture("particle"));
        if (owner.isTexturePresent("base")) textures.add(owner.resolveTexture("base"));
        if (owner.isTexturePresent("fluid")) textures.add(owner.resolveTexture("fluid"));
        if (owner.isTexturePresent("cover")) textures.add(owner.resolveTexture("cover"));

        return textures;
    }

    public static class Loader implements IModelLoader<ContainedFluidModel>
    {
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {}

        @Override
        public ContainedFluidModel read(JsonDeserializationContext context, JsonObject json)
        {
            return new ContainedFluidModel(Fluids.EMPTY);
        }
    }

    private static final class ContainedFluidItemOverride extends ItemOverrides
    {
        private final Map<Fluid, BakedModel> cache;
        private final ItemOverrides nested;
        private final ModelBakery bakery;
        private final IModelConfiguration owner;
        private final ContainedFluidModel parent;

        private ContainedFluidItemOverride(ItemOverrides nested, ModelBakery bakery, IModelConfiguration owner, ContainedFluidModel parent)
        {
            this.cache = new HashMap<>();
            this.nested = nested;
            this.bakery = bakery;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed)
        {
            final BakedModel overriden = nested.resolve(originalModel, stack, world, entity, seed);
            if (overriden != originalModel)
            {
                return overriden;
            }
            return stack.getCapability(Capabilities.FLUID)
                .map(cap -> {
                    // Forge: don't try and DRAIN THE STACK to see if it contains fluid...
                    final Fluid fluid = cap.getFluidInTank(0).getFluid();
                    return cache.computeIfAbsent(fluid, key -> {
                        final ContainedFluidModel unbaked = parent.withFluid(key);
                        return unbaked.bake(owner, bakery, ForgeModelBakery.defaultTextureGetter(), BlockModelRotation.X0_Y0, this, new ResourceLocation("forge:bucket_override"));
                    });
                })
                .orElse(originalModel);
        }
    }
}
