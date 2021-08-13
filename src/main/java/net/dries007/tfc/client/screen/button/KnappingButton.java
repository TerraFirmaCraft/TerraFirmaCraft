/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;

public class KnappingButton extends Button
{
    public int id;
    private final ResourceLocation texture;
    private final SoundEvent sound;

    public KnappingButton(int id, int x, int y, int width, int height, ResourceLocation texture, SoundEvent sound)
    {
        super(x, y, width, height, StringTextComponent.EMPTY, button -> {});
        this.id = id;
        this.texture = texture;
        this.sound = sound;
    }

    @Override
    public void onPress()
    {
        if (active)
        {
            visible = false;
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(id, null));
            playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }

    @Override
    public void playDownSound(SoundHandler handler)
    {
        handler.play(SimpleSound.forUI(sound, 1.0F));
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            Minecraft.getInstance().getTextureManager().bind(texture);
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
