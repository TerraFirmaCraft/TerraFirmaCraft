package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.land.TFCAnimalProperties;
import net.dries007.tfc.common.entities.land.TFCPig;

public class TFCPigModel extends PigModel<TFCPig>
{
    public static LayerDefinition createTFCBodyLayer(CubeDeformation def)
    {
        MeshDefinition meshdefinition = QuadrupedModel.createBodyMesh(6, def);
        PartDefinition root = meshdefinition.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, def)
                .texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, def),
            PartPose.offset(0.0F, 12.0F, -6.0F));

        head.addOrReplaceChild("tusk1", CubeListBuilder.create()
                .texOffs(32, 0).addBox(0F, 0F, 0F, 1, 2, 1),
            PartPose.offsetAndRotation(-3F, 0.5F, -9.0F, Mth.PI / 12, 0F, 0F)
        );
        head.addOrReplaceChild("tusk2", CubeListBuilder.create()
                .texOffs(32, 0).addBox(0F, 0F, 0F, 1, 2, 1),
            PartPose.offsetAndRotation(2.0F, 0.5F, -9.0F, Mth.PI / 12, 0F, 0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 32);
    }
    private final ModelPart tusk1;
    private final ModelPart tusk2;

    public TFCPigModel(ModelPart root)
    {
        super(root);
        tusk1 = root.getChild("head").getChild("tusk1");
        tusk2 = root.getChild("head").getChild("tusk2");
    }

    @Override
    public void setupAnim(TFCPig entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        tusk1.visible = tusk2.visible = entity.getGender() != TFCAnimalProperties.Gender.FEMALE;
    }
}