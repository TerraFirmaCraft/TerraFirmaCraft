/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;

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
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 4, 20, 22, 96, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.openContainer = playerInventory.player.container;
            Minecraft.getInstance().displayGuiScreen(new InventoryScreen(playerInventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 96, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 50, 20 + 3, 22, 96 + 20, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 96, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE));
    }
}
