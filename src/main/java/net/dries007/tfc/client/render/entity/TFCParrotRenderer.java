/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.prey.TFCParrot;
import net.dries007.tfc.common.entities.prey.TFCParrot.Species;

public class TFCParrotRenderer extends ParrotRenderer
{
    private static final Map<Species, ResourceLocation> TEXTURES = Util.make(Maps.newEnumMap(Species.class), map -> {
        for (Species species : Species.VALUES)
        {
            map.put(species, RenderHelpers.animalTexture("parrot/" + species.getSerializedName()));
        }
        map.put(Species.SCARLET_MACAW, ParrotRenderer.getVariantTexture(Parrot.Variant.RED_BLUE));
        map.put(Species.HYACINTH_MACAW, ParrotRenderer.getVariantTexture(Parrot.Variant.BLUE));
        map.put(Species.MILITARY_MACAW, ParrotRenderer.getVariantTexture(Parrot.Variant.GREEN));
        map.put(Species.BLUE_AND_YELLOW_MACAW, ParrotRenderer.getVariantTexture(Parrot.Variant.YELLOW_BLUE));
        map.put(Species.COCKATIEL, ParrotRenderer.getVariantTexture(Parrot.Variant.GRAY));
    });

    public static ResourceLocation getTexture(Species species)
    {
        return TEXTURES.get(species);
    }

    public TFCParrotRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot parrot)
    {
        return parrot instanceof TFCParrot tfcParrot ? getTexture(tfcParrot.getSpecies()) : super.getTextureLocation(parrot);
    }
}