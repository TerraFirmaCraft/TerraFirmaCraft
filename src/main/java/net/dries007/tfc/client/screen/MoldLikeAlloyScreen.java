package net.dries007.tfc.client.screen;

import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.container.MoldLikeAlloyContainer;
import net.dries007.tfc.util.Metal;

public class MoldLikeAlloyScreen extends TFCContainerScreen<MoldLikeAlloyContainer>
{
    @Nullable private final MoldLike mold;

    public MoldLikeAlloyScreen(MoldLikeAlloyContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_1x1);

        mold = MoldLike.get(container.getTargetStack());
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        // Metal and contents tooltip
        if (mold != null)
        {
            final FluidStack fluid = mold.getFluidInTank(0);
            final Metal metal = Metal.MANAGER.getMetal(fluid.getFluid());
            if (metal != null)
            {
                drawCenteredLine(stack, I18n.get(metal.getTranslationKey()), 14);
                drawCenteredLine(stack, I18n.get("tfc.tooltip.mold_like.alloy_units", fluid.getAmount()), 23);
            }
        }
    }
}
