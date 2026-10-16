/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.TFCParrotModel;
import net.dries007.tfc.common.entities.prey.TFCParrot;
import net.dries007.tfc.common.entities.prey.TFCParrot.Species;

public class TFCParrotRenderer extends ParrotRenderer
{
    private static final Map<Species, ResourceLocation> TEXTURES = Util.make(Maps.newEnumMap(Species.class), map -> {
        for (Species species : Species.VALUES)
        {
            map.put(species, RenderHelpers.animalTexture("parrot/" + species.getSerializedName()));
        }
        map.put(Species.SCARLET_MACAW, getVariantTexture(Parrot.Variant.RED_BLUE));
        map.put(Species.HYACINTH_MACAW, getVariantTexture(Parrot.Variant.BLUE));
        map.put(Species.MILITARY_MACAW, getVariantTexture(Parrot.Variant.GREEN));
        map.put(Species.BLUE_AND_YELLOW_MACAW, getVariantTexture(Parrot.Variant.YELLOW_BLUE));
        map.put(Species.COCKATIEL, getVariantTexture(Parrot.Variant.GRAY));
    });

    public static ResourceLocation getTexture(Species species)
    {
        return TEXTURES.get(species);
    }

    public TFCParrotRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        model = new TFCParrotModel(context.bakeLayer(ModelLayers.PARROT));
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot parrot)
    {
        return parrot instanceof TFCParrot tfcParrot ? getTexture(tfcParrot.getSpecies()) : super.getTextureLocation(parrot);
    }
}