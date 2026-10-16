/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.TFCParrotModel;
import net.dries007.tfc.common.entities.prey.TFCParrot;
import net.dries007.tfc.common.entities.prey.TFCParrot.Species;

public class TFCParrotRenderer extends MobRenderer<Parrot, ParrotModel>
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
        super(context, new TFCParrotModel(context.bakeLayer(ModelLayers.PARROT)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot parrot)
    {
        return parrot instanceof TFCParrot tfcParrot ? getTexture(tfcParrot.getSpecies()) : ParrotRenderer.getVariantTexture(parrot.getVariant());
    }

    @Override
    protected float getBob(Parrot parrot, float partialTick)
    {
        // Same as vanilla: the wing flap, which ParrotModel reads as ageInTicks
        final float flap = Mth.lerp(partialTick, parrot.oFlap, parrot.flap);
        final float flapSpeed = Mth.lerp(partialTick, parrot.oFlapSpeed, parrot.flapSpeed);
        return (Mth.sin(flap) + 1f) * flapSpeed;
    }
}