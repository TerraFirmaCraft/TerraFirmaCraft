/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.container.MoldLikeAlloyContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Tooltips;

public class MoldLikeAlloyScreen extends TFCContainerScreen<MoldLikeAlloyContainer>
{
    public MoldLikeAlloyScreen(MoldLikeAlloyContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_1x1);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        // Metal and contents tooltip
        final MoldLike mold = MoldLike.get(menu.getTargetStack());
        if (mold != null)
        {
            final FluidStack fluid = mold.getFluidInTank(0);
            final Metal metal = Metal.get(fluid.getFluid());
            if (metal != null)
            {
                drawCenteredLine(stack, Helpers.translatable(metal.getTranslationKey()), 14);
                drawCenteredLine(stack, Tooltips.fluidUnits(fluid.getAmount()), 23);

                final float temperature = mold.getTemperature();
                final MutableComponent tooltip = TFCConfig.CLIENT.heatTooltipStyle.get().format(temperature);
                if (tooltip != null)
                {
                    drawCenteredLine(stack, tooltip, 56);
                }
            }
        }
    }
}
