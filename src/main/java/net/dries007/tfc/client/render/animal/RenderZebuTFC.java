/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelZebuTFC;
import net.dries007.tfc.objects.entity.animal.EntityZebuTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderZebuTFC extends RenderAnimalTFC<EntityZebuTFC>
{
    private static final ResourceLocation ZEBU_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/zebu_young.png");
    private static final ResourceLocation ZEBU_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/zebu_old.png");

    public RenderZebuTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelZebuTFC(), 0.7F, ZEBU_YOUNG, ZEBU_OLD);
    }

    protected void preRenderCallback(EntityZebuTFC zebuTFC, float par2)
    {
        GlStateManager.scale(0.9f, 0.9f, 0.9f);
    }
}
