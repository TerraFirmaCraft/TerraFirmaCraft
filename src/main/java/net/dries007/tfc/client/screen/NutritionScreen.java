/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.component.food.INutritionData;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;

public class NutritionScreen extends TFCContainerScreen<Container>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/player_nutrition.png");

    public NutritionScreen(Container container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, TEXTURE);
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.INVENTORY, button -> {
            playerInventory.player.containerMenu = playerInventory.player.inventoryMenu;
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null && mc.gameMode.isServerControlledInventory() && mc.player != null && TFCConfig.CLIENT.enableTabsInCreative.get())
            {
                mc.setScreen(new CreativeModeInventoryScreen((LocalPlayer) playerInventory.player, mc.player.connection.enabledFeatures(), mc.options.operatorItemsTab().get()));
            }
            else
            {
                mc.setScreen(new InventoryScreen(playerInventory.player));
            }
            PacketDistributor.sendToServer(new SwitchInventoryTabPacket(PlayerInventoryTabButton.Tab.INVENTORY));
        }));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.CALENDAR));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, true, false, PlayerInventoryTabButton.Tab.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.CLIMATE));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.BOOK)));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            final INutritionData nutrition = IPlayerInfo.get(player).nutrition();
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int width = (int) (nutrition.getNutrient(nutrient) * 93);
                graphics.blit(texture, leftPos + 76, topPos + 18 + 11 * nutrient.ordinal(), 0, 166, width, 5);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            final Component text = Helpers.translateEnum(nutrient);
            drawLine(graphics, text, TextAlignment.RIGHT, -1, 105, 17 + 11 * nutrient.ordinal());
        }
    }
}