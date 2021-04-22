package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.model.OrcaModel;
import net.dries007.tfc.common.entities.aquatic.OrcaEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class OrcaRenderer extends MobRenderer<OrcaEntity, OrcaModel>
{
    private static final ResourceLocation ORCA_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/orca.png");

    public OrcaRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new OrcaModel(), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(OrcaEntity entityIn)
    {
        return ORCA_LOCATION;
    }

    @Override
    protected void setupRotations(OrcaEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180F));
    }
}
