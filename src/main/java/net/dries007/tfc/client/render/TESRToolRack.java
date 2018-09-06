/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.te.TEToolRack;

public class TESRToolRack extends TileEntitySpecialRenderer<TEToolRack>
{
    @Override
    public void render(TEToolRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        {
            int dir = te.getBlockMetadata();
            //EntityItem customitem = new EntityItem(field_147501_a.field_147550_f); //tileEntityRenderer.worldObj
            //customitem.hoverStart = 0f;
            float blockScale = .5f;
            //float timeD = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            for (int i = 0; i < 4; i++)
            {
                ItemStack stack = te.getItems().get(i);
                if (te.getItems().get(i) != ItemStack.EMPTY)
                {
                    float[] loc = getLocation(dir, i);
                    GlStateManager.pushMatrix(); //start
                    GlStateManager.translate(x + loc[0], y + loc[1], z + loc[2]);
                    /*if (RenderManager.instance.options.fancyGraphics)
                    {
                        GlStateManager.glRotatef(loc[3], 0.0F, 1.0F, 0.0F);
                    }*/
                    GlStateManager.scale(blockScale, blockScale, blockScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    /*customitem.setEntityItemStack(te.getStackInSlot(i));
                    itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);*/
                    GlStateManager.popMatrix(); //end
                }
            }
        }
    }


    public float[] getLocation(int dir, int slot)
    {
        float[] out = new float[4];
        if (dir == 0)
        {
            out[3] = 0f;
            if (slot == 0)
            {
                out[0] = 0.25f;
                out[1] = 0.5f;
                out[2] = 0.94f;
            }
            else if (slot == 1)
            {
                out[0] = 0.75f;
                out[1] = 0.5f;
                out[2] = 0.94f;
            }
            else if (slot == 2)
            {
                out[0] = 0.25f;
                out[1] = 0.1f;
                out[2] = 0.94f;
            }
            else if (slot == 3)
            {
                out[0] = 0.75f;
                out[1] = 0.1f;
                out[2] = 0.94f;
            }
        }
        else if (dir == 1)
        {
            out[3] = 270f;
            if (slot == 0)
            {
                out[0] = 0.06f;
                out[1] = 0.5f;
                out[2] = 0.25f;
            }
            else if (slot == 1)
            {
                out[0] = 0.06f;
                out[1] = 0.5f;
                out[2] = 0.75f;
            }
            else if (slot == 2)
            {
                out[0] = 0.06f;
                out[1] = 0.1f;
                out[2] = 0.25f;
            }
            else if (slot == 3)
            {
                out[0] = 0.06f;
                out[1] = 0.1f;
                out[2] = 0.75f;
            }
        }
        else if (dir == 2)
        {
            out[3] = 180f;
            if (slot == 0)
            {
                out[0] = 0.25f;
                out[1] = 0.5f;
                out[2] = 0.06f;
            }
            else if (slot == 1)
            {
                out[0] = 0.75f;
                out[1] = 0.5f;
                out[2] = 0.06f;
            }
            else if (slot == 2)
            {
                out[0] = 0.25f;
                out[1] = 0.1f;
                out[2] = 0.06f;
            }
            else if (slot == 3)
            {
                out[0] = 0.75f;
                out[1] = 0.1f;
                out[2] = 0.06f;
            }
        }
        else if (dir == 3)
        {
            out[3] = 90f;
            if (slot == 0)
            {
                out[0] = 0.94f;
                out[1] = 0.5f;
                out[2] = 0.25f;
            }
            else if (slot == 1)
            {
                out[0] = 0.94f;
                out[1] = 0.5f;
                out[2] = 0.75f;
            }
            else if (slot == 2)
            {
                out[0] = 0.94f;
                out[1] = 0.1f;
                out[2] = 0.25f;
            }
            else if (slot == 3)
            {
                out[0] = 0.94f;
                out[1] = 0.1f;
                out[2] = 0.75f;
            }
        }
        return out;
    }
}
