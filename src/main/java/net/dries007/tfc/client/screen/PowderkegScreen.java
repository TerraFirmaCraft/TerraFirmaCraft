/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PowderkegSealButton;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.container.PowderkegContainer;
import net.dries007.tfc.util.Helpers;

public class PowderkegScreen extends BlockEntityScreen<PowderkegBlockEntity, PowderkegContainer>
{
    private static final Component SEAL = Helpers.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = Helpers.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");

    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/powderkeg.png");

    public PowderkegScreen(PowderkegContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new PowderkegSealButton(blockEntity, getGuiLeft(), getGuiTop(), new Button.OnTooltip()
        {
            @Override
            public void onTooltip(Button button, PoseStack poseStack, int x, int y)
            {
                renderTooltip(poseStack, isSealed() ? UNSEAL : SEAL, x, y);
            }

            @Override
            public void narrateTooltip(Consumer<Component> consumer)
            {
                consumer.accept(isSealed() ? UNSEAL : SEAL);
            }
        }));
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(PowderkegBlock.SEALED);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (isSealed())
        {
            drawDisabled(poseStack, 0, PowderkegBlockEntity.SLOTS - 1);
        }
    }
}
