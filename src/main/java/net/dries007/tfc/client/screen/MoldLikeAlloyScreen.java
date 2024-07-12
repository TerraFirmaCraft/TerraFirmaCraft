/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.container.MoldLikeAlloyContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Tooltips;
import net.dries007.tfc.util.data.Metal;

public class MoldLikeAlloyScreen extends TFCContainerScreen<MoldLikeAlloyContainer>
{
    public MoldLikeAlloyScreen(MoldLikeAlloyContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_1x1);
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int mouseX, int mouseY)
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
                drawCenteredLine(stack, metal.getDisplayName(), 14);
                drawCenteredLine(stack, Tooltips.fluidUnits(fluid.getAmount()), 23);

                final float temperature = mold.getTemperature();
                final MutableComponent tooltip = TFCConfig.CLIENT.heatTooltipStyle.get().format(temperature);
                if (tooltip != null)
                {
                    drawCenteredLine(stack, tooltip, 56);
                }

                final ItemStack outputStack = this.menu.getInventory().getStackInSlot(0);
                final @Nullable IFluidHandlerItem outputFluidHandler = outputStack.getCapability(Capabilities.FluidHandler.ITEM);

                if (outputFluidHandler != null && !outputFluidHandler.isFluidValid(0, fluid))
                {
                    drawCenteredLine(stack, Component.translatable("tfc.tooltip.mold.fluid_incompatible"), 65);
                }
            }
        }
    }
}
