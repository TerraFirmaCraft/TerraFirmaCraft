/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.entity.JellyfishModel;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;

public class JellyfishRenderer extends MobRenderer<Jellyfish, JellyfishModel>
{
    public JellyfishRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new JellyfishModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("jellyfish"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Jellyfish jellyfish)
    {
        return jellyfish.getTextureLocation();
    }
}
