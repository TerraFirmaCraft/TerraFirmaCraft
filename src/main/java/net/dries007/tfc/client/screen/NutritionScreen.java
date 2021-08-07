/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.NutritionStats;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class NutritionScreen extends TFCContainerScreen<SimpleContainer>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/player_nutrition.png");

    public NutritionScreen(SimpleContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, TEXTURE);
    }

    @Override
    public void init()
    {
        super.init();
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            inventory.player.containerMenu = inventory.player.inventoryMenu;
            Minecraft.getInstance().setScreen(new InventoryScreen(inventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 50, 20 + 3, 22, 128 + 20, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE));
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        final PlayerEntity player = ClientHelpers.getPlayer();
        if (player != null && player.getFoodData() instanceof TFCFoodStats)
        {
            final NutritionStats nutrition = ((TFCFoodStats) player.getFoodData()).getNutrition();
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final String text = I18n.get(Helpers.getEnumTranslationKey(nutrient));
                int width = (int) (nutrition.getNutrient(nutrient) * 50);

                font.draw(stack, text, 112 - font.width(text), 19 * 13 * nutrient.ordinal(), 0x404040);
                blit(stack, leftPos + 118, topPos + 21 + 13 * nutrient.ordinal(), 176, 0, width, 5);
            }
        }
    }
}