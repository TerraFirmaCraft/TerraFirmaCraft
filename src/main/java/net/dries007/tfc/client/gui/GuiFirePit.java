/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;
import net.dries007.tfc.objects.te.TEFirePit;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiFirePit extends GuiContainerTE<TEFirePit>
{
    private static final ResourceLocation FIRE_PIT_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit.png");
    private static final ResourceLocation FIRE_PIT_COOKING_POT_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_cooking_pot.png");
    private static final ResourceLocation FIRE_PIT_GRILL_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_grill.png");

    private final BlockFirePit.FirePitAttachment attachment;

    public GuiFirePit(Container container, InventoryPlayer playerInv, TEFirePit tile)
    {
        super(container, playerInv, tile, FIRE_PIT_BACKGROUND);

        attachment = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockFirePit.ATTACHMENT);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawBackground();

        // Draw the fire / burn time indicator
        int temperature = (int) (51 * tile.getField(TEFirePit.FIELD_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temperature > 0)
        {
            if (temperature > 51)
            {
                temperature = 51;
            }
            drawTexturedModalRect(guiLeft + 30, guiTop + 66 - temperature, 176, 0, 15, 5);
        }

        if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
        {
            // Draw soup overlays + text
            TEFirePit.CookingPotStage stage = tile.getCookingPotStage();
            String caption;
            if (stage == TEFirePit.CookingPotStage.WAITING || stage == TEFirePit.CookingPotStage.BOILING)
            {
                drawTexturedModalRect(guiLeft + 58, guiTop + 52, 191, 0, 24, 4);
                if (stage == TEFirePit.CookingPotStage.WAITING)
                {
                    caption = I18n.format("tfc.tooltip.firepit_cooking_pot_waiting");
                }
                else // boiling
                {
                    caption = I18n.format("tfc.tooltip.firepit_cooking_pot_boiling");
                }
            }
            else if (stage == TEFirePit.CookingPotStage.FINISHED)
            {
                drawTexturedModalRect(guiLeft + 58, guiTop + 52, 191, 4, 24, 4);
                caption = I18n.format("tfc.tooltip.firepit_cooking_pot_servings", tile.getSoupServings());
            }
            else
            {
                caption = I18n.format("tfc.tooltip.firepit_cooking_pot_empty");
            }

            fontRenderer.drawString(caption, guiLeft + 130 - fontRenderer.getStringWidth(caption) / 2, guiTop + 52, 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
        {
            TEFirePit.CookingPotStage stage = tile.getCookingPotStage();
            if (stage == TEFirePit.CookingPotStage.BOILING || stage == TEFirePit.CookingPotStage.FINISHED)
            {
                // slots are disabled while boiling
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                for (int i = TEFirePit.SLOT_EXTRA_INPUT_START; i <= TEFirePit.SLOT_EXTRA_INPUT_END; i++)
                {
                    drawSlotOverlay(inventorySlots.getSlot(i - 3)); // index of extra inputs
                }
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    protected void drawBackground()
    {
        GlStateManager.color(1, 1, 1, 1);
        switch (attachment)
        {
            case NONE:
                mc.getTextureManager().bindTexture(FIRE_PIT_BACKGROUND);
                break;
            case COOKING_POT:
                mc.getTextureManager().bindTexture(FIRE_PIT_COOKING_POT_BACKGROUND);
                break;
            case GRILL:
                mc.getTextureManager().bindTexture(FIRE_PIT_GRILL_BACKGROUND);
                break;
        }
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
