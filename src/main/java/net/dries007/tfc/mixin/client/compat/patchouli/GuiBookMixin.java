/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.patchouli;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;

@Mixin(GuiBook.class)
public abstract class GuiBookMixin extends Screen
{
    @Shadow
    public int bookLeft, bookTop;

    @Shadow
    public final Book book;

    @Shadow
    public abstract Minecraft getMinecraft();

    protected GuiBookMixin(Component title, Book book)
    {
        super(title);
        this.book = book;
    }

    @Inject(method = "init", at = @At("TAIL"), cancellable = false)
    public void injectTabButtons(CallbackInfo ci)
    {
        if (book.id.equals(Helpers.resourceLocation("tfc", "field_guide")))
        {
            final Inventory playerInventory = Objects.requireNonNull(getMinecraft().player).getInventory();

            addRenderableWidget(new PlayerInventoryTabButton(bookLeft, bookTop, false, true, PlayerInventoryTabButton.Tab.INVENTORY, button -> {
                playerInventory.player.containerMenu = playerInventory.player.inventoryMenu;
                Minecraft mc = Minecraft.getInstance();
                if (mc.gameMode != null && mc.gameMode.isServerControlledInventory() && mc.player != null && TFCConfig.CLIENT.enableTabsInCreative.get()) {
                    mc.setScreen(new CreativeModeInventoryScreen((LocalPlayer) playerInventory.player, mc.player.connection.enabledFeatures(), mc.options.operatorItemsTab().get()));
                } else {
                    mc.setScreen(new InventoryScreen(playerInventory.player));
                }
                PacketDistributor.sendToServer(new SwitchInventoryTabPacket(PlayerInventoryTabButton.Tab.INVENTORY));
            }));
            addRenderableWidget(new PlayerInventoryTabButton(bookLeft, bookTop, false, true, PlayerInventoryTabButton.Tab.CALENDAR));
            addRenderableWidget(new PlayerInventoryTabButton(bookLeft, bookTop, false, true, PlayerInventoryTabButton.Tab.NUTRITION));
            addRenderableWidget(new PlayerInventoryTabButton(bookLeft, bookTop, false, true, PlayerInventoryTabButton.Tab.CLIMATE));
            addRenderableWidget(new PlayerInventoryTabButton(bookLeft, bookTop, true, true, PlayerInventoryTabButton.Tab.BOOK));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = false)
    public void injectRenderConsistentBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
    {
        if (book.id.equals(Helpers.resourceLocation("tfc", "field_guide")))
        {
            graphics.pose().pushPose();
            graphics.pose().scale(8, 8, 1);
            renderTransparentBackground(graphics);
            graphics.pose().popPose();
        }
    }
}
