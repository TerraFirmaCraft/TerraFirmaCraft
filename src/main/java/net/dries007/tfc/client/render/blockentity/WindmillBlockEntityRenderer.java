/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientRotationNetworkHandler;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WindmillBladeLatticeModel;
import net.dries007.tfc.client.model.entity.WindmillBladeModel;
import net.dries007.tfc.client.model.entity.WindmillBladeRusticModel;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.WindmillBladeItem;
import net.dries007.tfc.util.Helpers;

public class WindmillBlockEntityRenderer implements BlockEntityRenderer<WindmillBlockEntity>
{
    public static final Map<Item, Provider<Function<BlockEntityRendererProvider.Context, WindmillBladeModel>>> BLADE_MODELS = RenderHelpers.mapOf(map -> {
        final ResourceLocation defaultTexture = Helpers.identifier("textures/entity/misc/windmill_blade.png");
        final Function<BlockEntityRendererProvider.Context, WindmillBladeModel> defaultModel = defaultModelFactory();

        TFCItems.WINDMILL_BLADES.forEach((color, item) -> map.accept(item, new Provider<>(defaultTexture, color, defaultModel)));
        map.accept(TFCItems.LATTICE_WINDMILL_BLADE, new Provider<>(
            Helpers.identifier("textures/entity/misc/windmill_blade_lattice.png"),
            DyeColor.WHITE,
            context -> new WindmillBladeLatticeModel(context.bakeLayer(RenderHelpers.layerId("windmill_blade_lattice")))
        ));
        map.accept(TFCItems.RUSTIC_WINDMILL_BLADE, new Provider<>(
            Helpers.identifier("textures/entity/misc/windmill_blade_rustic.png"),
            DyeColor.WHITE,
            context -> new WindmillBladeRusticModel(context.bakeLayer(RenderHelpers.layerId("windmill_blade_rustic")))
        ));
    });

    private static Function<BlockEntityRendererProvider.Context, WindmillBladeModel> defaultModelFactory()
    {
        // This fanciness is to avoid creating models for each color of windmill when we only need one
        // And have a proper invalidate-able cache that doesn't leak memory in hypothetical situations
        final Mutable<Pair<BlockEntityRendererProvider.Context, WindmillBladeModel>> cache = new MutableObject<>(null);
        return context -> {
            if (cache.getValue() != null && cache.getValue().getFirst() != context) cache.setValue(null);
            if (cache.getValue() == null) cache.setValue(Pair.of(context, new WindmillBladeModel(context.bakeLayer(RenderHelpers.layerId("windmill_blade")))));
            return cache.getValue().getSecond();
        };
    }

    private final Map<Item, Provider<WindmillBladeModel>> bladeModels;

    public WindmillBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.bladeModels = Helpers.mapValue(BLADE_MODELS, v -> new Provider<>(v.texture, v.color, v.model.apply(context)));
    }

    @Override
    public void render(WindmillBlockEntity windmill, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final Level level = windmill.getLevel();
        final BlockState state = windmill.getBlockState();

        if (!(state.getBlock() instanceof WindmillBlock windmillBlock) || level == null)
        {
            return;
        }

        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);
        final int bladeCount = state.getValue(WindmillBlock.COUNT);
        final float angle = ClientRotationNetworkHandler.getRotationAngle(windmill, partialTick);

        AxleBlockEntityRenderer.renderAxle(stack, bufferSource, windmillBlock, axis, packedLight, packedOverlay, -angle);

        stack.pushPose();

        final boolean axisX = state.getValue(WindmillBlock.AXIS) == Direction.Axis.X;

        if (!axisX)
        {
            stack.mulPose(Axis.YN.rotationDegrees(90f));
        }

        stack.translate(0.5f, -1, axisX ? 0.5f : -0.5f);

        // First, figure out if we have all identical models
        boolean hasFullIdenticalSet = bladeCount == 5;
        if (hasFullIdenticalSet)
        {
            // If we have five blades, we have to check each model
            @Nullable WindmillBladeModel model = null;
            for (int i = 0; i < bladeCount; i++)
            {
                final ItemStack item = windmill.getInventory().getStackInSlot(i);
                final var provider = bladeModels.get(item.getItem());
                if (item.isEmpty() || provider == null || (model != null && model != provider.model))
                {
                    hasFullIdenticalSet = false;
                    break;
                }
                model = provider.model;
            }
        }

        final float offsetAngle = Mth.TWO_PI / bladeCount;
        for (int i = 0; i < bladeCount; i++)
        {
            final ItemStack item = windmill.getInventory().getStackInSlot(i);
            final var provider = bladeModels.get(item.getItem());
            if (provider == null)
            {
                continue;
            }

            final int color = provider.color == DyeColor.WHITE ? -1 : provider.color.getTextureDiffuseColor();
            final WindmillBladeModel bladeModel = provider.model;
            final ResourceLocation bladeTexture = provider.texture;

            stack.pushPose();

            // nudge to avoid Z-fighting
            stack.translate(0.0001f * i, 0.0001f * i,0.0001f * i);
            bladeModel.setupAnim(windmill, partialTick, offsetAngle * i);
            bladeModel.renderToBuffer(stack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(bladeTexture)), packedLight, packedOverlay, color);

            if (hasFullIdenticalSet)
            {
                bladeModel.renderWindmillExtras(stack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(bladeTexture)), packedLight, packedOverlay, color);
            }

            stack.popPose();
        }

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(WindmillBlockEntity windmill)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(WindmillBlockEntity blockEntity)
    {
        return AABB.INFINITE;
    }

    /**
     * Provides a model, or a factory for a model, along with other rendering info on a per-item basis.
     */
    public record Provider<T>(
        ResourceLocation texture,
        DyeColor color,
        T model
    ) {}
}
