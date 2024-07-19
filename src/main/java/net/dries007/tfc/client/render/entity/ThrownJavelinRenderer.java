/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.JavelinModel;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.entities.misc.ThrownJavelin;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class ThrownJavelinRenderer extends EntityRenderer<ThrownJavelin>
{
    public static final ResourceLocation DEFAULT_TEXTURE = Helpers.identifier("textures/entity/projectiles/stone_javelin.png");
    public static final Map<Item, ResourceLocation> JAVELIN_TEXTURES = Util.make(new HashMap<>(), map -> {
        TFCItems.ROCK_TOOLS.forEach((k, v) -> map.put(v.get(RockCategory.ItemType.JAVELIN).get(), Helpers.identifier("textures/entity/projectiles/stone_javelin.png")));
        TFCItems.METAL_ITEMS.entrySet()
            .stream()
            .filter(e -> e.getValue().containsKey(Metal.ItemType.JAVELIN))
            .forEach(e -> map.put(e.getValue().get(Metal.ItemType.JAVELIN).get(), Helpers.identifier("textures/entity/projectiles/" + e.getKey().getSerializedName() + "_javelin.png")));
    });

    private final JavelinModel model;

    public ThrownJavelinRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new JavelinModel(context.bakeLayer(RenderHelpers.layerId("javelin")));
    }

    @Override
    public void render(ThrownJavelin javelin, float ageInTicks, float pitch, PoseStack poseStack, MultiBufferSource buffers, int packedLight)
    {
        poseStack.pushPose();

        float degrees = Mth.lerp(pitch, javelin.yRotO, javelin.getYRot()) - 90.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(degrees));
        float degrees1 = Mth.lerp(pitch, javelin.xRotO, javelin.getXRot()) + 90.0F;
        poseStack.mulPose(Axis.ZP.rotationDegrees(degrees1));

        final VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(getTextureLocation(javelin)), false, javelin.isEnchantGlowing());
        this.model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
        super.render(javelin, ageInTicks, pitch, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownJavelin entity)
    {
        return JAVELIN_TEXTURES.getOrDefault(entity.getItem().getItem(), DEFAULT_TEXTURE);
    }
}
