/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import net.dries007.tfc.common.entities.aquatic.SeafloorCritterEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class HorseshoeCrabModel extends SegmentedModel<SeafloorCritterEntity>
{
    private final ModelRenderer crab;

    public HorseshoeCrabModel()
    {
        texHeight = 16;
        texWidth = 16;

        crab = new ModelRenderer(this);
        crab.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(crab, 0.0F, 1.5708F, 0.0F);
        crab.texOffs(0, 0).addBox(5.0F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        crab.texOffs(0, 9).addBox(1.0F, -1.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        crab.texOffs(0, 0).addBox(1.0F, -2.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        crab.texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        crab.texOffs(0, 7).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        crab.texOffs(8, 7).addBox(-3.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        crab.texOffs(0, 0).addBox(-8.0F, -0.2F, -0.5F, 5.0F, 0.0F, 1.0F, 0.0F, false);
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(crab);
    }

    @Override
    public void setupAnim(SeafloorCritterEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_)
    {

    }
}
