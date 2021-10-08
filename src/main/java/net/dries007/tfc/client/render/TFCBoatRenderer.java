package net.dries007.tfc.client.render;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.TFCBoat;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBoatRenderer extends BoatRenderer
{
    public static ModelLayerLocation boatName(Wood wood)
    {
        return Helpers.modelIdentifier("boat/" + wood.name().toLowerCase(Locale.ROOT));
    }

    private final Map<Wood, Pair<ResourceLocation, BoatModel>> resources;

    public TFCBoatRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.resources = Stream.of(Wood.VALUES).collect(ImmutableMap.toImmutableMap(wood -> wood, wood -> Pair.of(new ResourceLocation(MOD_ID, "textures/entity/boat/" + wood.name().toLowerCase(Locale.ROOT) + ".png"), new BoatModel(context.bakeLayer(boatName(wood))))));
    }

    @Override
    public Pair<ResourceLocation, BoatModel> getModelWithLocation(Boat boat)
    {
        if (boat instanceof TFCBoat tfcBoat)
        {
            return resources.get(Wood.VALUES[tfcBoat.getEntityData().get(TFCBoat.TFC_WOOD_ID)]);
        }
        return super.getModelWithLocation(boat);
    }
}
