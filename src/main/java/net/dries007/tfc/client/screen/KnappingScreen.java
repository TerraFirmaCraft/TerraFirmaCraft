/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.screen.button.KnappingButton;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.KnappingPattern;

public class KnappingScreen extends TFCContainerScreen<KnappingContainer>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/knapping.png");

    public static final ResourceLocation CLAY = Helpers.identifier("textures/gui/knapping/clay_ball.png");
    public static final ResourceLocation CLAY_DISABLED = Helpers.identifier("textures/gui/knapping/clay_ball_disabled.png");

    public static final ResourceLocation FIRE_CLAY = Helpers.identifier("textures/gui/knapping/fire_clay.png");
    public static final ResourceLocation FIRE_CLAY_DISABLED = Helpers.identifier("textures/gui/knapping/fire_clay_disabled.png");

    public static final ResourceLocation LEATHER = Helpers.identifier("textures/gui/knapping/leather.png");

    private final ResourceLocation buttonLocation;
    @Nullable private final ResourceLocation buttonDisabledLocation;
    private final List<ScreenParticle> particles = new ArrayList<>();

    public static ResourceLocation getButtonLocation(Item item, boolean disabled)
    {
        ResourceLocation buttonAssetPath = item.getRegistryName();
        assert buttonAssetPath != null;
        return Helpers.identifier("textures/gui/knapping/" + buttonAssetPath.getPath() + (disabled ? "_disabled" : "") + ".png");
    }

    public KnappingScreen(KnappingContainer container, Inventory inv, Component name)
    {
        super(container, inv, name, BACKGROUND);
        imageHeight = 186;
        inventoryLabelY += 22;
        titleLabelY -= 2;
        buttonLocation = getButtonLocation(container.getOriginalStack().getItem(), false);
        buttonDisabledLocation = container.getKnappingType().usesDisabledTexture() ? getButtonLocation(container.getOriginalStack().getItem(), true) : null;
    }

    @Override
    protected void init()
    {
        super.init();
        for (int x = 0; x < KnappingPattern.MAX_WIDTH; x++)
        {
            for (int y = 0; y < KnappingPattern.MAX_HEIGHT; y++)
            {
                int bx = (width - getXSize()) / 2 + 12 + 16 * x;
                int by = (height - getYSize()) / 2 + 12 + 16 * y;
                addRenderableWidget(new KnappingButton(x + 5 * y, bx, by, 16, 16, buttonLocation, menu.getKnappingType().clickSound(), this::spawnParticles));
            }
        }
    }

    private void spawnParticles(Button button)
    {
        if (button instanceof KnappingButton knappingButton && menu.getKnappingType().spawnsParticles())
        {
            final Random random = Minecraft.getInstance().font.random;
            final int amount = Mth.nextInt(random, 0, 3);
            for (int i = 0; i < amount; i++)
            {
                final var particle = new ScreenParticle(knappingButton.getTexture(), button.x, button.y, Mth.nextFloat(random, -0.1f, 0.1f), Mth.nextFloat(random, 1.2f, 1.5f), 16, 16, random);
                particles.add(particle);
            }
        }
    }

    @Override
    protected void containerTick()
    {
        super.containerTick();
        for (ScreenParticle particle : particles)
        {
            particle.tick();
        }
        particles.removeIf(ScreenParticle::shouldBeRemoved);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        // Check if the container has been updated
        if (menu.requiresReset())
        {
            for (Widget widget : renderables)
            {
                if (widget instanceof KnappingButton button)
                {
                    button.visible = menu.getPattern().get(button.id);
                }
            }
            menu.setRequiresReset(false);
        }

        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        for (Widget widget : renderables)
        {
            if (widget instanceof KnappingButton button)
            {
                if (button.visible) // Active button
                {
                    RenderSystem.setShaderTexture(0, buttonLocation);
                    blit(poseStack, button.x, button.y, 0, 0, 16, 16, 16, 16);
                }
                else if (buttonDisabledLocation != null) // Disabled / background texture
                {
                    RenderSystem.setShaderTexture(0, buttonDisabledLocation);
                    blit(poseStack, button.x, button.y, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        for (ScreenParticle particle : particles)
        {
            particle.render(poseStack);
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int clickType, double dragX, double dragY)
    {
        if (clickType == 0)
        {
            mouseClicked(x, y, clickType);
        }
        return super.mouseDragged(x, y, clickType, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double x, double y, int clickType)
    {
        if (clickType == 0)
        {
            undoAccidentalButtonPress(x, y);
        }
        return super.mouseClicked(x, y, clickType);
    }

    private void undoAccidentalButtonPress(double x, double y)
    {
        for (Widget widget : renderables)
        {
            if (widget instanceof KnappingButton button && button.isMouseOver(x, y))
            {
                menu.getPattern().set(button.id, false);
            }
        }
    }
}
