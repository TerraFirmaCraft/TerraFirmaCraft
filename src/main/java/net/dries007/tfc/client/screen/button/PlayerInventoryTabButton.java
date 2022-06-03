/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;

public class PlayerInventoryTabButton extends Button
{
    private final int textureU;
    private final int textureV;
    private final int iconU;
    private final int iconV;
    private int iconX;
    private int iconY;
    private int prevGuiLeft;
    private int prevGuiTop;
    private Runnable tickCallback;

    public PlayerInventoryTabButton(int guiLeft, int guiTop, int xIn, int yIn, int widthIn, int heightIn, int textureU, int textureV, int iconX, int iconY, int iconU, int iconV, SwitchInventoryTabPacket.Type type)
    {
        this(guiLeft, guiTop, xIn, yIn, widthIn, heightIn, textureU, textureV, iconX, iconY, iconU, iconV, button -> PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(type)));
    }

    public PlayerInventoryTabButton(int guiLeft, int guiTop, int xIn, int yIn, int widthIn, int heightIn, int textureU, int textureV, int iconX, int iconY, int iconU, int iconV, OnPress onPressIn)
    {
        super(guiLeft + xIn, guiTop + yIn, widthIn, heightIn, TextComponent.EMPTY, onPressIn);
        this.prevGuiLeft = guiLeft;
        this.prevGuiTop = guiTop;
        this.textureU = textureU;
        this.textureV = textureV;
        this.iconX = guiLeft + xIn + iconX;
        this.iconY = guiTop + yIn + iconY;
        this.iconU = iconU;
        this.iconV = iconV;
        this.tickCallback = () -> {};
    }

    public PlayerInventoryTabButton setRecipeBookCallback(InventoryScreen screen)
    {
        // Because forge is ass and removed the event for "button clicked", and I don't care to deal with the shit in MinecraftForge#5548, this will do for now
        this.tickCallback = new Runnable()
        {
            boolean recipeBookVisible = screen.getRecipeBookComponent().isVisible();

            @Override
            public void run()
            {
                boolean newRecipeBookVisible = screen.getRecipeBookComponent().isVisible();
                if (newRecipeBookVisible != recipeBookVisible)
                {
                    recipeBookVisible = newRecipeBookVisible;
                    PlayerInventoryTabButton.this.updateGuiSize(screen.getGuiLeft(), screen.getGuiTop());
                }
            }
        };
        return this;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShaderTexture(0, ClientHelpers.GUI_ICONS);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();

        tickCallback.run();

        blit(matrixStack, x, y, 0, (float) textureU, (float) textureV, width, height, 256, 256);
        blit(matrixStack, iconX, iconY, 16, 16, (float) iconU, (float) iconV, 32, 32, 256, 256);
    }

    public void updateGuiSize(int guiLeft, int guiTop)
    {
        this.x += guiLeft - prevGuiLeft;
        this.y += guiTop - prevGuiTop;

        this.iconX += guiLeft - prevGuiLeft;
        this.iconY += guiTop - prevGuiTop;

        prevGuiLeft = guiLeft;
        prevGuiTop = guiTop;
    }
}