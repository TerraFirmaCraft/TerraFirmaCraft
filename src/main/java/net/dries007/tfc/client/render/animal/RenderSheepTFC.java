/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelSheepBodyTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderSheepTFC extends RenderLiving<EntitySheepTFC>
{
    private static final ResourceLocation SHEEP_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/sheep.png");

    public RenderSheepTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelSheepBodyTFC(), 0.7F);
        this.addLayer(new LayerSheepWoolTFC(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntitySheepTFC entity)
    {
        return SHEEP_TEXTURES;
    }
}
