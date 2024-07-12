/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.common.recipes.SewingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.dries007.tfc.util.Helpers;

public class SewingTableScreen extends TFCContainerScreen<SewingTableContainer>
{
    public static void forEachStitch(TriFunction<Integer, Integer, Integer, Boolean> action)
    {
        int i = 0;
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                if (action.apply(x, y, i))
                    return;
                i++;
            }
        }
    }

    public static void forEachClothSquare(TriConsumer<Integer, Integer, Integer> action)
    {
        int i = 0;
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                action.accept(x, y, i);
                i++;
            }
        }
    }

    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/sewing.png");
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 16;
    private static final int RECIPES_PER_PAGE = 16;

    private final List<SewingRecipe> recipes = new ArrayList<>();
    private boolean showRecipes = false;
    @Nullable private SewingRecipe selectedRecipe = null;
    private int startIndex = 0;

    public SewingTableScreen(SewingTableContainer menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title, TEXTURE);
        imageHeight += 30;
        inventoryLabelY += 30;
        titleLabelY -= 1;
    }

    @Override
    protected void init()
    {
        super.init();
        createButton(leftPos + 125, topPos + 13, 20, 20, 236, 0, 20, SewingTableContainer.BURLAP_ID, "tfc.tooltip.sewing.dark_cloth");
        createButton(leftPos + 150, topPos + 13, 20, 20, 236, 40, 20, SewingTableContainer.WOOL_ID, "tfc.tooltip.sewing.light_cloth");
        createButton(leftPos + 125, topPos + 38, 20, 20, 236, 80, 20, SewingTableContainer.REMOVE_ID, "tfc.tooltip.sewing.remove_stitch");
        createButton(leftPos + 150, topPos + 38, 20, 20, 236, 120, 20, SewingTableContainer.NEEDLE_ID, "tfc.tooltip.sewing.stitch");
        createButton(leftPos + 135, topPos + 63, 20, 18, 192, 96, 18, SewingTableContainer.RECIPE_ID, "tfc.tooltip.sewing.select_recipe");

        forEachClothSquare((x, y, i) -> {
            final int id = i + SewingTableContainer.PLACED_SLOTS_OFFSET;
            createButton(getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 12, 12, 208, 32, 0, id, null);
        });

        recipes.addAll(ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(TFCRecipeTypes.SEWING.get()));
        recipes.addAll(ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(TFCRecipeTypes.SEWING.get()));
        recipes.addAll(ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(TFCRecipeTypes.SEWING.get()));
        recipes.addAll(ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(TFCRecipeTypes.SEWING.get()));
        recipes.addAll(ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(TFCRecipeTypes.SEWING.get()));
    }

    private void createButton(int x, int y, int sizeX, int sizeY, int u, int v, int yDiffTex, int packetButtonId, @Nullable String translationKey)
    {
        ImageButton button;
        if (translationKey != null)
        {
            button = new ImageButton(x, y, sizeX, sizeY, u, v, yDiffTex, TEXTURE, 256, 256, btn -> {
                if (packetButtonId == SewingTableContainer.RECIPE_ID)
                {
                    selectedRecipe = null;
                    showRecipes = !showRecipes;
                    return;
                }
                if (menu.getCarried().isEmpty() && !showRecipes)
                {
                    PacketDistributor.sendToServer(new ScreenButtonPacket(packetButtonId));
                }
            }, Component.translatable(translationKey));
            button.setTooltip(Tooltip.create(Component.translatable(translationKey)));
        }
        else
        {
            button = new SilentImageButton(x, y, sizeX, sizeY, u, v, yDiffTex, TEXTURE, 256, 256, btn -> {
                if (menu.getCarried().isEmpty() && !showRecipes)
                {
                    PacketDistributor.sendToServer(new ScreenButtonPacket(packetButtonId));
                }
            });
        }
        addRenderableWidget(button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        final int sewX = getSewingX(mouseX);
        final int sewY = getSewingY(mouseY);
        if (showRecipes && isSewing(mouseX, mouseY))
        {
            final int x = sewX / 16;
            final int y = sewY / 16;
            final int i = y * 6 + x;
            if (i == 16)
            {
                if (hasLeftPage())
                    startIndex = Math.max(0, startIndex - RECIPES_PER_PAGE);
                playSound(SoundEvents.BOOK_PAGE_TURN);
            }
            else if (i == 17)
            {
                if (hasRightPage())
                    startIndex += RECIPES_PER_PAGE;
                playSound(SoundEvents.BOOK_PAGE_TURN);
            }
            else if (i + startIndex < recipes.size())
            {
                selectedRecipe = recipes.get(i + startIndex);
                showRecipes = false;
                startIndex = 0;
                playSound(SoundEvents.UI_LOOM_SELECT_PATTERN);
                return true;
            }
        }
        final int mat = menu.getActiveMaterial();
        if (!showRecipes && isSewing(mouseX, mouseY) && (mat == SewingTableContainer.NEEDLE_ID || mat == SewingTableContainer.REMOVE_ID))
        {
            forEachStitch((x, y, i) -> {
                final int leftX = x * 12 + 6;
                final int topY = y * 12 + 6;
                // offset by 6 inwards to center the click on the corner
                if (RenderHelpers.isInside(sewX + 6, sewY + 6, leftX, topY, 12, 12) && (menu.getStitchAt(i) == 1 || mat == SewingTableContainer.NEEDLE_ID))
                {
                    final CompoundTag tag = new CompoundTag();
                    tag.putInt("id", i);
                    tag.putInt("stitchType", mat == SewingTableContainer.NEEDLE_ID ? 1 : 0);
                    PacketDistributor.sendToServer(new ScreenButtonPacket(SewingTableContainer.PLACE_STITCH_ID, tag));
                    return true;
                }
                return false;
            });
            if (mat == SewingTableContainer.NEEDLE_ID)
                return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type)
    {
        if (slotId == SewingTableContainer.SLOT_RESULT)
            selectedRecipe = null;
        super.slotClicked(slot, slotId, mouseButton, type);
    }

    @Override
    public boolean mouseDragged(double x, double y, int clickType, double dragX, double dragY)
    {
        if (clickType == 0 && menu.getActiveMaterial() != SewingTableContainer.NEEDLE_ID && isSewing(x, y))
        {
            mouseClicked(x, y, clickType);
        }
        return super.mouseDragged(x, y, clickType, dragX, dragY);
    }

    @Override
    public void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        forEachClothSquare((x, y, i) -> {
            final int mat = menu.getPlacedMaterial(i);
            if (mat != -1)
            {
                graphics.blit(TEXTURE, getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 208, mat == SewingTableContainer.BURLAP_ID ? 16 : 0, 12, 12);
            }
            else if (selectedRecipe != null)
            {
                final int recipeMat = selectedRecipe.getSquare(i);
                if (recipeMat != -1)
                {
                    graphics.blit(TEXTURE, getScreenX(x * 12 + 6), getScreenY(y * 12 + 6), 208, recipeMat == SewingTableContainer.BURLAP_ID ? 80 : 64, 12, 12);
                }
            }
        });

        forEachStitch((x, y, i) -> {
            final int stitch = menu.getStitchAt(i);
            if (stitch == 1)
            {
                graphics.blit(TEXTURE, getScreenX(x * 12 + 6) - 2, getScreenY(y * 12 + 6) - 2, 2, 192, 0, 5, 5, 256, 256);
            }
            else if (selectedRecipe != null && selectedRecipe.getStitch(i))
            {
                graphics.blit(TEXTURE, getScreenX(x * 12 + 6) - 2, getScreenY(y * 12 + 6) - 2, 2, 192, 64, 5, 5, 256, 256);
            }
            return false;
        });
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);

        if (!menu.canPickup(SewingTableContainer.SLOT_YARN))
            renderSlotHighlight(graphics, 8, 83, 1);
        if (!menu.canPickup(SewingTableContainer.SLOT_INPUT_1))
            renderSlotHighlight(graphics, 62, 83, 1);
        if (!menu.canPickup(SewingTableContainer.SLOT_INPUT_2))
            renderSlotHighlight(graphics, 80, 83, 1);
        if (!menu.canPickup(SewingTableContainer.SLOT_TOOL))
            renderSlotHighlight(graphics, 26, 83, 1);

        if (menu.getCarried().isEmpty() && RenderHelpers.isInside(mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight) && !showRecipes)
        {
            final int mat = menu.getActiveMaterial();
            if (mat == SewingTableContainer.BURLAP_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 16, 12, 12);
            }
            else if (mat == SewingTableContainer.WOOL_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 0, 12, 12);
            }
            else if (mat == SewingTableContainer.REMOVE_ID || mat == SewingTableContainer.NEEDLE_ID)
            {
                graphics.blit(TEXTURE, mouseX - leftPos, mouseY - topPos, 208, 48, 16, 16);
            }
        }

        final int burlapCount = menu.getBurlapCount();
        final int woolCount = menu.getWoolCount();
        final int yarnCount = menu.getYarnCount();

        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(burlapCount, 99)), 135, 25, burlapCount == 0 ? 0x404040 : 0xFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(woolCount, 99)), 160, 25, woolCount == 0 ? 0x404040 : 0xFFFFFF);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(Math.min(yarnCount, 99)), 160, 48, yarnCount == 0 ? 0x404040 : 0xFFFFFF);

        if (showRecipes)
        {
            renderRecipes(graphics, mouseX, mouseY);
        }
    }

    private void renderRecipes(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.fillGradient(8, 14, 119, 77, -1072689136, -804253680);
        final RegistryAccess access = ClientHelpers.getLevelOrThrow().registryAccess();
        final int max = startIndex + RECIPES_PER_PAGE + 2;
        for (int idx = startIndex; idx < max; idx++)
        {
            final int i = idx - startIndex;
            final int x = i % 6 * 16 + X_OFFSET;
            final int y = i / 6 * 16 + Y_OFFSET;
            if (i == 16)
            {
                if (hasLeftPage())
                    graphics.blit(TEXTURE, x, y, 192, 144, 16, 16);
            }
            else if (i == 17)
            {
                if (hasRightPage())
                    graphics.blit(TEXTURE, x, y, 208, 144, 16, 16);
            }
            else if (idx < recipes.size())
            {
                final SewingRecipe recipe = recipes.get(idx);
                final ItemStack item = recipe.getResultItem(access);
                graphics.renderItem(item, x, y, 1);
                if (RenderHelpers.isInside(mouseX - leftPos, mouseY - topPos, x, y, 16, 16))
                {
                    graphics.renderTooltip(Minecraft.getInstance().font, item, x + 8, y + 8);
                }
            }
        }
    }

    private boolean hasLeftPage()
    {
        return startIndex > 0;
    }

    private boolean hasRightPage()
    {
        return startIndex + RECIPES_PER_PAGE < recipes.size();
    }

    private boolean isSewing(double mouseX, double mouseY)
    {
        return RenderHelpers.isInside((int) mouseX, (int) mouseY, X_OFFSET + leftPos, Y_OFFSET + topPos, 117 - X_OFFSET, 75 - Y_OFFSET);
    }

    private int getSewingX(double mouseX)
    {
        return (int) (mouseX - X_OFFSET - leftPos);
    }

    private int getSewingY(double mouseY)
    {
        return (int) (mouseY - Y_OFFSET - topPos);
    }

    private int getScreenX(int posX)
    {
        return posX == -1 ? 0 : posX + X_OFFSET + leftPos;
    }

    private int getScreenY(int posY)
    {
        return posY == -1 ? 0 : posY + Y_OFFSET + topPos;
    }

    private void playSound(SoundEvent sound)
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static class SilentImageButton extends ImageButton
    {
        public SilentImageButton(int x, int y, int width, int height, int texStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress)
        {
            super(x, y, width, height, texStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress);
        }

        @Override
        public void playDownSound(SoundManager handler) {}
    }
}
