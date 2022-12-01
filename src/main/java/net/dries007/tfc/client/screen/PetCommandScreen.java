/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.vertex.PoseStack;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PetCommandPacket;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.dries007.tfc.util.Helpers;

public class PetCommandScreen extends Screen
{
    private final TamableMammal entity;

    public PetCommandScreen(TamableMammal entity)
    {
        super(Helpers.translatable("tfc.screen.pet_command"));
        this.entity = entity;
    }

    @Override
    protected void init()
    {
        super.init();
        int y = 72;
        for (TamableMammal.Command command : TamableMammal.Command.VALUES)
        {
            if (entity.willListenTo(command, true))
            {
                addRenderableWidget(new Button(width / 2 - 100, height / 4 + y, 200, 20, Helpers.translateEnum(command), b -> {
                    PacketHandler.send(PacketDistributor.SERVER.noArg(), new PetCommandPacket(entity, command));
                    Minecraft.getInstance().setScreen(null);

                    final Player player = ClientHelpers.getPlayer();
                    if (player != null)
                    {
                        player.containerMenu = player.inventoryMenu;
                    }
                }, RenderHelpers.makeButtonTooltip(this, Helpers.translatable(Helpers.getEnumTranslationKey(command) + ".tooltip"))));
                y += 24;
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        fillGradient(poseStack, 0, 0, width, height, -1072689136, -804253680);
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(poseStack, font, title, width / 2 / 2, 30, 16777215);
        poseStack.popPose();
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, ItemStack stack, int mouseX, int mouseY)
    {
        super.renderTooltip(poseStack, stack, mouseX, mouseY);
        for (Widget widget : renderables)
        {
            if (widget instanceof Button button && button.isHoveredOrFocused())
            {
                button.renderToolTip(poseStack, mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
