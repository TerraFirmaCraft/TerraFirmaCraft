/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.HorseshoeCrabModel;
import net.dries007.tfc.common.entities.aquatic.SeafloorCritterEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class HorseshoeCrabRenderer extends MobRenderer<SeafloorCritterEntity, HorseshoeCrabModel>
{
    private static final ResourceLocation HORSESHOE_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/horseshoe_crab.png");

    public HorseshoeCrabRenderer(EntityRendererManager manager)
    {
        super(manager, new HorseshoeCrabModel(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(SeafloorCritterEntity p_110775_1_)
    {
        return HORSESHOE_LOCATION;
    }
}
