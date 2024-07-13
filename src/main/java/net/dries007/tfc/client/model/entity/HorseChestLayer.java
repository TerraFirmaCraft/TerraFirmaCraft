/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.util.Helpers;

public class HorseChestLayer<T extends TFCChestedHorse, M extends EntityModel<T>> extends RenderLayer<T, M>
{
    public static void registerChest(Item item, ResourceLocation location)
    {
        MAP.put(item, location);
    }

    private static final Map<Item, ResourceLocation> MAP = new HashMap<>();
    private static final ResourceLocation DEFAULT_CHEST_TEXTURE = Helpers.identifier("textures/entity/chest/horse/oak.png");

    private final M model;

    public HorseChestLayer(RenderLayerParent<T, M> parent, M model)
    {
        super(parent);
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float yaw, float pitch)
    {
        if (!entity.getChestItem().isEmpty())
        {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTexture(entity)));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }
    }

    private ResourceLocation getTexture(T entity)
    {
        final Item item = entity.getChestItem().getItem();
        if (MAP.containsKey(item))
        {
            return MAP.get(item);
        }
        return DEFAULT_CHEST_TEXTURE;
    }
}
