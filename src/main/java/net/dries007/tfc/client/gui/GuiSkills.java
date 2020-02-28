/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonPage;
import net.dries007.tfc.client.button.GuiButtonPlayerInventoryTab;
import net.dries007.tfc.network.PacketSwitchPlayerInventoryTab;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiSkills extends GuiContainerTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_skills.png");

    private final String[] skillTooltips;
    private final int[] skillBarWidths;
    private final int[] skillBarColors;

    private int currentPage;
    private int skillsToDisplay;

    private GuiButton buttonLeft, buttonRight;

    public GuiSkills(Container container, InventoryPlayer playerInv)
    {
        super(container, playerInv, BACKGROUND);

        this.skillTooltips = new String[4];
        this.skillBarWidths = new int[4];
        this.skillBarColors = new int[4];
        this.currentPage = 0;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonId = 0;
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.INVENTORY, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.SKILLS, guiLeft, guiTop, ++buttonId, false));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.CALENDAR, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.NUTRITION, guiLeft, guiTop, ++buttonId, true));

        buttonLeft = addButton(new GuiButtonPage(++buttonId, guiLeft + 7, guiTop + 68, GuiButtonPage.Type.LEFT, "tfc.tooltip.previous_page"));

        buttonRight = addButton(new GuiButtonPage(++buttonId, guiLeft + 154, guiTop + 68, GuiButtonPage.Type.RIGHT, "tfc.tooltip.next_page"));
        updateSkillValues();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        for (int i = 0; i < skillsToDisplay; i++)
        {
            // Tooltip
            fontRenderer.drawString(skillTooltips[i], 7, 5 + (16 * i), 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        for (int i = 0; i < skillsToDisplay; i++)
        {
            // Background
            drawTexturedModalRect(guiLeft + 7, guiTop + 14 + (16 * i), 0, 166, 162, 5);

            // Progress Bar
            drawTexturedModalRect(guiLeft + 8, guiTop + 15 + (16 * i), 0, 171 + skillBarColors[i] * 3, skillBarWidths[i], 3);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiButtonPlayerInventoryTab && ((GuiButtonPlayerInventoryTab) button).isActive())
        {
            GuiButtonPlayerInventoryTab tabButton = (GuiButtonPlayerInventoryTab) button;
            if (tabButton.isActive())
            {
                if (tabButton.getGuiType() == TFCGuiHandler.Type.INVENTORY)
                {
                    this.mc.displayGuiScreen(new GuiInventory(playerInv.player));
                }
                TerraFirmaCraft.getNetwork().sendToServer(new PacketSwitchPlayerInventoryTab(tabButton.getGuiType()));
            }
        }
        else if (button == buttonLeft)
        {
            currentPage--;
            updateSkillValues();
        }
        else if (button == buttonRight)
        {
            currentPage++;
            updateSkillValues();
        }
    }

    private void updateSkillValues()
    {
        skillsToDisplay = 0;
        buttonLeft.enabled = currentPage >= 1;
        buttonRight.enabled = false;

        IPlayerData skills = playerInv.player.getCapability(CapabilityPlayerData.CAPABILITY, null);
        if (skills != null)
        {
            List<SkillType<? extends Skill>> skillOrder = SkillType.getSkills();
            int totalSkills = skillOrder.size();
            int startSkill = currentPage * 4;
            if (startSkill >= totalSkills || startSkill < 0)
            {
                TerraFirmaCraft.getLog().warn("Invalid skill page! Page: {}, Start at: {}, Skill Order is: {}", currentPage, startSkill, skillOrder);
                return;
            }
            buttonRight.enabled = startSkill + 4 < totalSkills;

            // Hide both buttons if there's only 4 skills (default TFC)
            buttonLeft.visible = totalSkills > 4;
            buttonRight.visible = totalSkills > 4;

            // Loop through the next four or less skills
            skillsToDisplay = Math.min(4, totalSkills - startSkill);
            for (int i = 0; i < skillsToDisplay; i++)
            {
                // Load the skills as per the skill order
                SkillType<? extends Skill> skillType = skillOrder.get(startSkill + i);
                Skill skill = skills.getSkill(skillType);
                if (skill != null)
                {
                    skillTooltips[i] = I18n.format("tfc.skill." + skillType.getName(), I18n.format(Helpers.getEnumName(skill.getTier())));
                    skillBarWidths[i] = (int) (160 * skill.getLevel());
                    skillBarColors[i] = skill.getTier().ordinal();
                }
            }
        }

    }
}
