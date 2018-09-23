/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.te.TEToolRack;

import static net.dries007.tfc.Constants.META_TO_ANGLE;

public class TESRToolRack extends TileEntitySpecialRenderer<TEToolRack>
{
    //direction, slot, axis, that's the order.
    static final float[][][] location = new float[4][4][3];

    static
    {
        //Config values for moving the item placement
        final float heightLow = .28125f;
        final float heightHigh = .78125f;
        final float column1 = .25f;
        final float column2 = .75f;
        final float offset = .09375f;

        final float offsetInv = 1f - offset;

        //Hardcoding values so render doesn't have to calculate
        for (int dir = 0; dir < 4; dir++)
        {
            for (int slot = 0; slot < 4; slot++)
            {
                if (slot < 2)
                    location[dir][slot][1] = heightHigh;
                else
                    location[dir][slot][1] = heightLow;

                if (dir % 2 == 0)
                {
                    if (slot % 2 == 0)
                        location[dir][slot][0] = column1;
                    else
                        location[dir][slot][0] = column2;
                }
                else
                {
                    if (slot % 2 == 0)
                        location[dir][slot][2] = column1;
                    else
                        location[dir][slot][2] = column2;
                }

                if (dir == 0)
                {
                    location[dir][slot][2] = offset;
                }
                else if (dir == 1)
                {
                    location[dir][slot][0] = offsetInv;
                }
                else if (dir == 2)
                {
                    location[dir][slot][2] = offsetInv;
                }
                else //if (dir == 3)
                {
                    location[dir][slot][0] = offset;
                }
            }
        }
    }

    @Override
    public void render(TEToolRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        {
            int dir = te.getBlockMetadata();
            float blockScale = .5f;
            for (int i = 0; i < 4; i++)
            {
                ItemStack stack = te.getItems().get(i);
                if (te.getItems().get(i) != ItemStack.EMPTY)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + location[dir][i][0], y + location[dir][i][1], z + location[dir][i][2]);
                    GlStateManager.rotate(META_TO_ANGLE[dir], 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(blockScale, blockScale, blockScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    GlStateManager.popMatrix();
                }
            }
        }
    }
}
