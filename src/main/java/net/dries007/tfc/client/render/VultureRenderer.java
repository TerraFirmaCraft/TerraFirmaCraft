package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.model.VultureModel;
import net.dries007.tfc.common.entities.VultureEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class VultureRenderer extends MobRenderer<VultureEntity, VultureModel>
{
    private static final ResourceLocation VULTURE_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/vulture.png");

    public VultureRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new VultureModel(), 0.75F);
    }

    @Override
    protected void scale(VultureEntity entity, MatrixStack matrixStackIn, float partialTickTime)
    {
        float scale = 1.0F + 0.15F * entity.getVultureSize(); // see PhantomRenderer
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.translate(0.0D, -0.3125D, 0.1875D);
    }

    @Override
    protected void setupRotations(VultureEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(entityLiving.xRot));
    }

    @Override
    public ResourceLocation getTextureLocation(VultureEntity entityIn)
    {
        return VULTURE_LOCATION;
    }
}
