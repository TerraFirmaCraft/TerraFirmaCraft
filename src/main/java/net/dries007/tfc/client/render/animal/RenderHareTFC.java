/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelHareTFC;
import net.dries007.tfc.objects.entity.animal.EntityHareTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderHareTFC extends RenderLiving<EntityHareTFC>
{
    private static final ResourceLocation BROWN = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/hare/brown.png");
    private static final ResourceLocation SPOTTED = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/hare/spotted.png");
    private static final ResourceLocation BLACK = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/hare/black.png");
    private static final ResourceLocation CREAM = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/hare/cream.png");


    public RenderHareTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelHareTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityHareTFC hare, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.15f + hare.getPercentToAdulthood() * 0.15f);
        super.doRender(hare, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHareTFC entity)
    {
        switch (entity.getHareType())
        {
            case 0:
            default:
                return BROWN;
            case 1:
                return SPOTTED;
            case 2:
                return BLACK;
            case 3:
                return CREAM;
        }
    }
}