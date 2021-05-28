package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.model.PenguinModel;
import net.dries007.tfc.common.entities.aquatic.PenguinEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PenguinRenderer extends MobRenderer<PenguinEntity, PenguinModel>
{
    private static final ResourceLocation PENGUIN_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/penguin.png");
    private static final ResourceLocation BABY_PENGUIN_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/penguin_baby.png");

    public PenguinRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new PenguinModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(PenguinEntity entityIn)
    {
        return entityIn.isBaby() ? BABY_PENGUIN_LOCATION : PENGUIN_LOCATION;
    }

    @Override
    protected void setupRotations(PenguinEntity entity, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(entity, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        if (entity.isInWater())
        {
            matrixStackIn.translate(0.0D, 0.4D, 0.0D);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(270.0F));
            matrixStackIn.translate(0.0D, -0.4D, 0.0D);
        }
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
    }
}
