/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.TextComponent;


import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;

public class KnappingButton extends Button
{
    public int id;
    private final ResourceLocation texture;
    private final SoundEvent sound;

    public KnappingButton(int id, int x, int y, int width, int height, ResourceLocation texture, SoundEvent sound)
    {
        super(x, y, width, height, TextComponent.EMPTY, button -> {});
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
    public void playDownSound(SoundManager handler)
    {
        handler.play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            RenderSystem.setShaderTexture(0, texture);
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
