package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.model.BluegillModel;
import net.dries007.tfc.common.entities.aquatic.BluegillEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BluegillRenderer extends MobRenderer<BluegillEntity, BluegillModel>
{
    private static final ResourceLocation BLUEGILL_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/bluegill.png");

    public BluegillRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new BluegillModel(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(BluegillEntity entityIn)
    {
        return BLUEGILL_LOCATION;
    }

    @Override
    protected void setupRotations(BluegillEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180F));
    }
}
