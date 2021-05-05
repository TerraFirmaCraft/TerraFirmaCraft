package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.LobsterModel;
import net.dries007.tfc.common.entities.aquatic.SeafloorCritterEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LobsterRenderer extends MobRenderer<SeafloorCritterEntity, LobsterModel>
{
    private static final ResourceLocation LOBSTER_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/animal/lobster.png");

    public LobsterRenderer(EntityRendererManager manager)
    {
        super(manager, new LobsterModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(SeafloorCritterEntity entityIn)
    {
        return LOBSTER_LOCATION;
    }
}
