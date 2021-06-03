/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.ManateeModel;
import net.dries007.tfc.common.entities.aquatic.ManateeEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ManateeRenderer extends MobRenderer<ManateeEntity, ManateeModel>
{
    private static final ResourceLocation MANATEE_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/manatee.png");

    public ManateeRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new ManateeModel(), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ManateeEntity entityIn)
    {
        return MANATEE_LOCATION;
    }
}
