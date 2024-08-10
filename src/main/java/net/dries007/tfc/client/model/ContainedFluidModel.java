/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

/**
 * Copy pasta of {@link DynamicFluidContainerModel} that's had excess / unused parts removed, and one key modification: we need to implement the
 * fluid query in a way that avoids trying to simulate drain a container, because of containers like molds, which contain a fluid but don't
 * allow it to be drained when solid.
 */
public record ContainedFluidModel(Fluid fluid) implements IUnbakedGeometry<ContainedFluidModel>
{
    // Depth offsets to prevent Z-fighting
    // Make public since we use them elsewhere
    public static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());
    public static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.004f), new Quaternionf());

    public static final ItemColor COLOR = (stack, tintIndex) -> {
        if (tintIndex != 1) return 0xFFFFFFFF;
        final FluidStack fluid = FluidHelpers.getContainedFluidInTank(stack);
        if (fluid.isEmpty()) return 0xFFFFFFFF;
        return IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
    };

    public ContainedFluidModel withFluid(Fluid newFluid)
    {
        return new ContainedFluidModel(newFluid);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        Material particleLocation = context.hasMaterial("particle") ? context.getMaterial("particle") : null;
        Material baseLocation = context.hasMaterial("base") ? context.getMaterial("base") : null;
        Material fluidMaskLocation = context.hasMaterial("fluid") ? context.getMaterial("fluid") : null;
        Material coverLocation = context.hasMaterial("cover") ? context.getMaterial("cover") : null;

        TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(new Material(RenderHelpers.BLOCKS_ATLAS, IClientFluidTypeExtensions.of(fluid).getStillTexture())) : null;
        TextureAtlasSprite coverSprite = coverLocation != null && baseLocation != null ? spriteGetter.apply(coverLocation) : null;

        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;
        if (particleSprite == null)
        {
            particleSprite = fluidSprite == null ? spriteGetter.apply(new Material(RenderHelpers.BLOCKS_ATLAS, Helpers.identifier("block/empty"))) : fluidSprite;
        }

        // We need to disable GUI 3D and block lighting for this to render properly
        var itemContext = StandaloneGeometryBakingContext.builder(context)
            .withGui3d(false)
            .withUseBlockLight(false)
            .build(ResourceLocation.fromNamespaceAndPath("neoforge", "dynamic_fluid_container"));
        var modelBuilder = CompositeModel.Baked.builder(itemContext, particleSprite, new ContainedFluidOverrideHandler(overrides, baker, itemContext, this), context.getTransforms());
        var normalRenderTypes = DynamicFluidContainerModel.getLayerRenderTypes(false);

        if (baseLocation != null)
        {
            // Base texture
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelState);
            modelBuilder.addQuads(normalRenderTypes, quads);
        }

        if (fluidMaskLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null)
            {
                // Fluid layer
                var transformedState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
                var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite); // Use template as mask
                var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState); // Bake with fluid texture

                var emissive = fluid.getFluidType().getLightLevel() > 0;
                var renderTypes = DynamicFluidContainerModel.getLayerRenderTypes(emissive);
                if (emissive) QuadTransformers.settingMaxEmissivity().processInPlace(quads);

                modelBuilder.addQuads(renderTypes, quads);
            }
        }

        if (coverSprite != null)
        {
            // Cover/overlay
            var transformedState = new SimpleModelState(modelState.getRotation().compose(COVER_TRANSFORM), modelState.isUvLocked());
            var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite); // Use cover as mask
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, transformedState); // Bake with selected texture
            modelBuilder.addQuads(normalRenderTypes, quads);
        }

        modelBuilder.setParticle(particleSprite);

        return modelBuilder.build();
    }

    public static class Loader implements IGeometryLoader<ContainedFluidModel>
    {
        @Override
        public ContainedFluidModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            return new ContainedFluidModel(Fluids.EMPTY);
        }
    }

    private static final class ContainedFluidOverrideHandler extends ItemOverrides
    {
        private final Map<Fluid, BakedModel> cache = new Reference2ObjectOpenHashMap<>(); // contains all the baked models since they'll never change
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;
        private final ContainedFluidModel parent;

        private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, ContainedFluidModel parent)
        {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
        {
            BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
            if (overridden != originalModel) return overridden;
            final FluidStack fluidStack = FluidHelpers.getContainedFluidInTank(stack);
            if (fluidStack.isEmpty()) return originalModel;

            Fluid fluid = fluidStack.getFluid();

            if (!cache.containsKey(fluid))
            {
                ContainedFluidModel unbaked = this.parent.withFluid(fluid);
                BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
                cache.put(fluid, bakedModel);
                return bakedModel;
            }

            return cache.get(fluid);
        }
    }
}
