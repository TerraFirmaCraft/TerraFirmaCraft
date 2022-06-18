/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.PotRecipe;

import static net.dries007.tfc.common.blockentities.PotBlockEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.blockentities.PotBlockEntity.SLOT_EXTRA_INPUT_START;

public class PotBlockEntityRenderer implements BlockEntityRenderer<PotBlockEntity>
{
    @Override
    public void render(PotBlockEntity pot, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (pot.getLevel() == null) return;

        final PotRecipe.Output output = pot.getOutput();
        final boolean useDefaultFluid = output != null && output.renderDefaultFluid();
        final FluidStack fluidStack = pot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            .map(cap -> cap.getFluidInTank(0))
            .filter(f -> !f.isEmpty())
            .orElseGet(() -> useDefaultFluid ? new FluidStack(Fluids.WATER, FluidHelpers.BUCKET_VOLUME) : FluidStack.EMPTY);
        if (!fluidStack.isEmpty())
        {
            int color = useDefaultFluid ? TFCFluids.ALPHA_MASK | 0xA64214 : RenderHelpers.getFluidColor(fluidStack);
            RenderHelpers.renderFluidFace(poseStack, fluidStack, buffer, color, 0.3125F, 0.3125F, 0.6875F, 0.6875F, 0.625F, combinedOverlay, combinedLight);
        }

        pot.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            int ordinal = 0;
            for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
            {
                ItemStack item = cap.getStackInSlot(slot);
                if (!item.isEmpty())
                {
                    float yOffset = 0.46f;
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.003125D + yOffset, 0.5);
                    poseStack.scale(0.3f, 0.3f, 0.3f);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

                    ordinal++;
                    poseStack.translate(0, 0, -0.12F * ordinal);

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                    poseStack.popPose();
                }
            }
        });
    }
}
