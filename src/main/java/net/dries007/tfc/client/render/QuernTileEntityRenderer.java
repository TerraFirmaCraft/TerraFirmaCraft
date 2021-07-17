package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.tileentity.QuernTileEntity;

public class QuernTileEntityRenderer extends TileEntityRenderer<QuernTileEntity>
{
    public QuernTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(QuernTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.getLevel() == null) return;
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            ItemStack input = cap.getStackInSlot(QuernTileEntity.SLOT_INPUT);
            ItemStack output = cap.getStackInSlot(QuernTileEntity.SLOT_OUTPUT);
            ItemStack handstone = cap.getStackInSlot(QuernTileEntity.SLOT_HANDSTONE);

            if (!output.isEmpty())
            {
                for (int i = 0; i < output.getCount(); i++)
                {
                    double yPos = 0.625D;
                    matrixStack.pushPose();
                    switch (Math.floorDiv(i, 16))
                    {
                        case 0:
                        {
                            matrixStack.translate(0.125D, yPos, 0.125D + (0.046875D * i));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                            break;
                        }
                        case 1:
                        {
                            matrixStack.translate(0.125D + (0.046875D * (i - 16)), yPos, 0.875D);
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90F));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                            break;
                        }
                        case 2:
                        {
                            matrixStack.translate(0.875D, yPos, 0.875D - (0.046875D * (i - 32)));
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                            break;
                        }
                        case 3:
                        {
                            matrixStack.translate(0.875D - (0.046875D * (i - 48)), yPos, 0.125D);
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees(270F));
                            matrixStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                            break;
                        }
                        default:
                        {
                            matrixStack.translate(0.5D, 1.0D, 0.5D);
                            matrixStack.mulPose(Vector3f.YP.rotationDegrees((te.getLevel().getGameTime() + partialTicks) * 4F));
                        }
                    }

                    matrixStack.scale(0.125F, 0.125F, 0.125F);
                    Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);

                    matrixStack.popPose();
                }
            }

            if (!handstone.isEmpty())
            {
                int rotationTicks = te.getRotationTimer();
                double center = rotationTicks > 0 ? 0.497D + (te.getLevel().random.nextDouble() * 0.006D) : 0.5D;

                matrixStack.pushPose();
                matrixStack.translate(center, 0.705D, center);

                if (rotationTicks > 0)
                {
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees((rotationTicks - partialTicks) * 4F));
                }

                matrixStack.scale(1.25F, 1.25F, 1.25F);
                Minecraft.getInstance().getItemRenderer().renderStatic(handstone, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                matrixStack.popPose();
            }

            if (!input.isEmpty())
            {
                double height = (handstone.isEmpty()) ? 0.75D : 0.875D;
                int rotationTicks = te.getRotationTimer();
                double center = rotationTicks > 0 ? 0.497D + (te.getLevel().random.nextDouble() * 0.006D) : 0.5D;

                matrixStack.pushPose();
                matrixStack.translate(center, height, center);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(45F));
                matrixStack.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);

                matrixStack.popPose();
            }
        });
    }
}
