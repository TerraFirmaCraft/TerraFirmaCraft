package net.dries007.tfc.client.model.entity;

import java.util.function.Function;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ThrownJavelinModel extends Model
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -16.0F, 1.0F, 1.0F, 32.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-1.5F, -1.5F, -18.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4).addBox(-1.0F, -1.0F, -20.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 4).addBox(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -20.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart root;

    public ThrownJavelinModel(ModelPart root)
    {
        super(RenderType::entitySolid);
        this.root = root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
