package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.misc.TFCChestBoat;
import net.dries007.tfc.common.items.ChestBlockItem;

public class TFCChestBoatRenderer extends TFCBoatRenderer
{
    public static ModelLayerLocation chestBoatName(String name)
    {
        return RenderHelpers.modelIdentifier("boat/" + name);
    }

    private final BoatModel model;

    public TFCChestBoatRenderer(EntityRendererProvider.Context context, String name)
    {
        super(context, name);
        this.model = new ChestBoatModel(context.bakeLayer(chestBoatName(name)));
    }

    public TFCChestBoatRenderer(EntityRendererProvider.Context context, Pair<ResourceLocation, ListModel<Boat>> originalPair, BoatModel model)
    {
        super(context, originalPair);
        this.model = model;
    }

    @Override
    public void render(Boat boat, float ageInTicks, float pitch, PoseStack poseStack, MultiBufferSource buffers, int packedLight)
    {
        if (boat instanceof TFCChestBoat chest)
        {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.375F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - ageInTicks));
            float f = (float) boat.getHurtTime() - pitch;
            float f1 = boat.getDamage() - pitch;
            if (f1 < 0.0F)
            {
                f1 = 0.0F;
            }
            if (f > 0.0F)
            {
                poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) boat.getHurtDir()));
            }
            float f2 = boat.getBubbleAngle(pitch);
            if (!Mth.equal(f2, 0.0F))
            {
                poseStack.mulPose((new Quaternionf()).setAngleAxis(boat.getBubbleAngle(pitch) * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
            }

            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            model.setupAnim(chest, pitch, 0.0F, -0.1F, 0.0F, 0.0F);
            VertexConsumer vertexconsumer = buffers.getBuffer(model.renderType(getChestTexture(chest)));
            model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            poseStack.popPose();
        }

        super.render(boat, ageInTicks, pitch, poseStack, buffers, packedLight);
    }

    protected ResourceLocation getChestTexture(TFCChestBoat chest)
    {
        return ((ChestBlockItem) chest.getChestItem().getItem()).getBoatTexture();
    }
}
