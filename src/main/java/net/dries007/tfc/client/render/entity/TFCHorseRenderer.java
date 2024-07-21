/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

import net.dries007.tfc.util.Helpers;

public class TFCHorseRenderer extends AbstractHorseRenderer<Horse, HorseModel<Horse>>
{
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(Variant.class), (map) -> {
        map.put(Variant.WHITE, Helpers.identifierMC("textures/entity/horse/horse_white.png"));
        map.put(Variant.CREAMY, Helpers.identifierMC("textures/entity/horse/horse_creamy.png"));
        map.put(Variant.CHESTNUT, Helpers.identifierMC("textures/entity/horse/horse_chestnut.png"));
        map.put(Variant.BROWN, Helpers.identifierMC("textures/entity/horse/horse_brown.png"));
        map.put(Variant.BLACK, Helpers.identifierMC("textures/entity/horse/horse_black.png"));
        map.put(Variant.GRAY, Helpers.identifierMC("textures/entity/horse/horse_gray.png"));
        map.put(Variant.DARK_BROWN, Helpers.identifierMC("textures/entity/horse/horse_darkbrown.png"));
    });

    public TFCHorseRenderer(EntityRendererProvider.Context context)
    {
        super(context, new HorseModel<>(context.bakeLayer(ModelLayers.HORSE)), 1.1F);
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(new HorseArmorLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Horse horse)
    {
        return LOCATION_BY_VARIANT.get(horse.getVariant());
    }

}
