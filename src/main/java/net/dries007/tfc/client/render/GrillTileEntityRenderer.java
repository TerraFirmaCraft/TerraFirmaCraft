package net.dries007.tfc.client.render;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.tileentity.GrillTileEntity;

import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_START;

public class GrillTileEntityRenderer extends TileEntityRenderer<GrillTileEntity>
{
    public GrillTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(GrillTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
            {
                ItemStack item = cap.getStackInSlot(i);
                if (!item.isEmpty())
                {
                    float yOffset = 0.625f;
                    matrixStack.pushPose();
                    matrixStack.translate(0.3, 0.003125D + yOffset, 0.28);
                    matrixStack.scale(0.3f, 0.3f, 0.3f);
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

                    float translateAmount = -1.4F;
                    int ordinal = i - SLOT_EXTRA_INPUT_START;
                    if (ordinal == 1 || ordinal == 3)
                    {
                        matrixStack.translate(translateAmount, 0, 0);
                    }
                    if (ordinal == 2 || ordinal == 3)
                    {
                        matrixStack.translate(0, translateAmount, 0);
                    }
                    if (ordinal == 4)
                    {
                        matrixStack.translate(translateAmount / 2, translateAmount / 2, 0);
                    }

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                    matrixStack.popPose();
                }
            }
        });
    }
}
