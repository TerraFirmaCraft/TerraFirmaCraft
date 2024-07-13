/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.util.Helpers;

public class TFCBoatRenderer extends BoatRenderer
{
    public static ModelLayerLocation boatName(String name)
    {
        return RenderHelpers.layerId("boat/" + name);
    }

    private final Pair<ResourceLocation, ListModel<Boat>> location;

    public TFCBoatRenderer(EntityRendererProvider.Context context, String name)
    {
        this(context, Pair.of(Helpers.identifier("textures/entity/boat/" + name + ".png"), name.equals("palm") ? new RaftModel(context.bakeLayer(boatName(name))) : new BoatModel(context.bakeLayer(boatName(name)))));
    }

    public TFCBoatRenderer(EntityRendererProvider.Context context, Pair<ResourceLocation, ListModel<Boat>> pair)
    {
        super(context, false);
        this.location = pair;
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat)
    {
        return location;
    }
}
