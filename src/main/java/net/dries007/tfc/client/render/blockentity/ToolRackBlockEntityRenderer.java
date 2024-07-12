/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.ToolRackBlockEntity;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;

public class ToolRackBlockEntityRenderer implements BlockEntityRenderer<ToolRackBlockEntity>
{
    //direction, slot, axis, that's the order.
    private static final float[][][] ITEM_LOCATION = new float[4][4][3];
    private static final float[] META_TO_ANGLE = new float[] {180f, 90f, 0f, 270f};
    private static final float ITEM_SCALE = .5f;

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
                    ITEM_LOCATION[dir][slot][1] = heightHigh;
                else
                    ITEM_LOCATION[dir][slot][1] = heightLow;

                if (dir % 2 == 0)
                {
                    if (slot % 2 == 0)
                        ITEM_LOCATION[dir][slot][0] = column1;
                    else
                        ITEM_LOCATION[dir][slot][0] = column2;
                }
                else
                {
                    if (slot % 2 == 0)
                        ITEM_LOCATION[dir][slot][2] = column1;
                    else
                        ITEM_LOCATION[dir][slot][2] = column2;
                }

                if (dir == 0)
                {
                    ITEM_LOCATION[dir][slot][2] = offset;
                }
                else if (dir == 1)
                {
                    ITEM_LOCATION[dir][slot][0] = offsetInv;
                }
                else if (dir == 2)
                {
                    ITEM_LOCATION[dir][slot][2] = offsetInv;
                }
                else
                {
                    ITEM_LOCATION[dir][slot][0] = offset;
                }
            }
        }
    }

    @Override
    public void render(ToolRackBlockEntity toolRack, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (toolRack.getLevel() == null) return;
        int meta = toolRack.getBlockState().getValue(ToolRackBlock.FACING).get2DDataValue();

        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = toolRack.getInventory().getStackInSlot(i);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                poseStack.translate(ITEM_LOCATION[meta][i][0], ITEM_LOCATION[meta][i][1], ITEM_LOCATION[meta][i][2]);
                poseStack.mulPose(Axis.YP.rotationDegrees(META_TO_ANGLE[meta]));
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, toolRack.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }
}
