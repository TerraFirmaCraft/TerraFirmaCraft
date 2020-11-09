/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlayerInventoryTabButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/icons.png");

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

    public PlayerInventoryTabButton(int guiLeft, int guiTop, int xIn, int yIn, int widthIn, int heightIn, int textureU, int textureV, int iconX, int iconY, int iconU, int iconV, IPressable onPressIn)
    {
        super(guiLeft + xIn, guiTop + yIn, widthIn, heightIn, StringTextComponent.EMPTY, onPressIn);
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
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(TEXTURE);
        RenderSystem.disableDepthTest();

        tickCallback.run();

        blit(matrixStack, x, y, 0, (float) textureU, (float) textureV, width, height, 256, 256);
        blit(matrixStack, iconX, iconY, 16, 16, (float) iconU, (float) iconV, 32, 32, 256, 256);
        RenderSystem.enableDepthTest();
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