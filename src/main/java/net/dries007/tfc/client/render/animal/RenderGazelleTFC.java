/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelGazelleTFC;
import net.dries007.tfc.objects.entity.animal.EntityGazelleTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderGazelleTFC extends RenderLiving<EntityGazelleTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/gazelle.png");

    public RenderGazelleTFC(RenderManager manager)
    {
        super(manager, new ModelGazelleTFC(), 0.7F);
    }

    @Override
    protected float handleRotationFloat(EntityGazelleTFC gazelle, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityGazelleTFC gazelleTFC, float par2)
    {
        GlStateManager.scale(0.9f, 0.9f, 0.9f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGazelleTFC entity)
    {
        return TEXTURE;
    }
}
