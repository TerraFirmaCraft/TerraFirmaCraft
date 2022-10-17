/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

public class ItemInMouthLayer<E extends LivingEntity, M extends EntityModel<E>> extends RenderLayer<E, M>
{
    private final RenderLayerParent<E, M> renderer;

    public ItemInMouthLayer(RenderLayerParent<E, M> renderer)
    {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTick, float tickCount, float yaw, float pitch)
    {
        if (renderer.getModel() instanceof MouthHolder mouth)
        {
            ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            poseStack.pushPose();

            mouth.translateToMouth(entity, poseStack, partialTick);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemstack, ItemTransforms.TransformType.GROUND, false, poseStack, buffer, packedLight);

            poseStack.popPose();
        }

    }
}
