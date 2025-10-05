/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.Map;

import com.google.common.collect.Maps;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.JellyfishModel;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.entities.aquatic.Jellyfish.Type;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class JellyfishRenderer extends SimpleMobRenderer<Jellyfish, JellyfishModel>
{
    private static final Map<Type, ResourceLocation> LOCATION_BY_TYPE = Util.make(Maps.newEnumMap(Type.class), (map) -> {
        map.put(Type.BLUE, RenderHelpers.animalTexture("jellyfish_blue"));
        map.put(Type.RED, RenderHelpers.animalTexture("jellyfish_red"));
        map.put(Type.YELLOW, RenderHelpers.animalTexture("jellyfish_yellow"));
        map.put(Type.PURPLE, RenderHelpers.animalTexture("jellyfish_purple"));
        map.put(Type.ORANGE, RenderHelpers.animalTexture("jellyfish_orange"));
    });

    public JellyfishRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new JellyfishModel(RenderHelpers.bakeSimple(ctx, "jellyfish")), "jellyfish", 0.3f, true, 1f, false, false, null);
    }

    @Override
    public ResourceLocation getTextureLocation(Jellyfish jellyfish)
    {
        return LOCATION_BY_TYPE.get(jellyfish.getVariant());
    }
}
