/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;

import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.WorldGenSettings;
import net.dries007.tfc.world.classic.WorldGenSettings.WorldGenSettingsBuilder;

/**
 * todo: remove, won't be supported in 1.13 by vanilla anyway, there will be something else in 1.14.
 */
@SideOnly(Side.CLIENT)
public class GuiCustomizeWorld extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder
{
    private static final int ID_DONE = 0;
    private static final int ID_CANCEL = 1;
    private static final int ID_DEFAULTS = 2;

    private static final int ID_SPAWN_FUZZ = 100;
    private static final int ID_FLAT_BEDROCK = 101;

    private static final int ID_RAVINE_RARITY = 102;
    private static final int ID_RAVINE_HEIGHT = 103;
    private static final int ID_RAVINE_VARIABILITY = 104;

    private static final int ID_SURFACE_RAVINE_RARITY = 105;
    private static final int ID_SURFACE_RAVINE_HEIGHT = 106;
    private static final int ID_SURFACE_RAVINE_VARIABILITY = 107;

    private static final int ID_RIVER_RAVINE_RARITY = 108;

    private final GuiCreateWorld parent;

    // Final by all but modifier
    private String title;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton defaultsBtn;

    private GuiPageButtonList list;

    private WorldGenSettingsBuilder settings;

    public GuiCustomizeWorld(GuiCreateWorld guiCreateWorld, String prevSettings)
    {
        parent = guiCreateWorld;
        settings = loadSettings(prevSettings);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, title, width / 2, 8, 16777215);
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        list.onKeyPressed(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        list.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        list.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!button.enabled) return;

        switch (button.id)
        {
            case ID_DONE:
                parent.chunkProviderSettingsJson = settings.build().toString();
                mc.displayGuiScreen(parent);
                break;

            case ID_CANCEL:
                mc.displayGuiScreen(parent);
                break;

            case ID_DEFAULTS:
                settings = loadSettings("");
                break;
        }
        super.actionPerformed(button);
        update();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        title = I18n.format("options.customizeTitle");

        doneBtn = addButton(new GuiButton(ID_DONE, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done")));
        cancelBtn = addButton(new GuiButton(ID_CANCEL, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel")));
        defaultsBtn = addButton(new GuiButton(ID_DEFAULTS, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.defaults")));

        GuiPageButtonList.GuiListEntry[] page1 = new GuiPageButtonList.GuiListEntry[] {
            new GuiPageButtonList.GuiSlideEntry(ID_SPAWN_FUZZ, I18n.format("createWorld.customize.custom.spawnfuzz"), true, this, 0, 2500, settings.spawnFuzz),
            new GuiPageButtonList.GuiButtonEntry(ID_FLAT_BEDROCK, I18n.format("createWorld.customize.custom.flatbedrock"), true, settings.flatBedrock),

            new GuiPageButtonList.GuiSlideEntry(ID_RAVINE_RARITY, I18n.format("createWorld.customize.custom.ravineRarity"), true, this, 0, 250, settings.ravineRarity),
            new GuiPageButtonList.GuiSlideEntry(ID_RAVINE_HEIGHT, I18n.format("createWorld.customize.custom.ravineHeight"), true, this, 0, 50, settings.ravineHeight),
            new GuiPageButtonList.GuiSlideEntry(ID_RAVINE_VARIABILITY, I18n.format("createWorld.customize.custom.ravineVariability"), true, this, 0, 100, settings.ravineVariability),

            new GuiPageButtonList.GuiSlideEntry(ID_SURFACE_RAVINE_RARITY, I18n.format("createWorld.customize.custom.surfaceRavineRarity"), true, this, 0, 250, settings.surfaceRavineRarity),
            new GuiPageButtonList.GuiSlideEntry(ID_SURFACE_RAVINE_HEIGHT, I18n.format("createWorld.customize.custom.surfaceRavineHeight"), true, this, 0, 250, settings.surfaceRavineHeight),
            new GuiPageButtonList.GuiSlideEntry(ID_SURFACE_RAVINE_VARIABILITY, I18n.format("createWorld.customize.custom.surfaceRavineVariability"), true, this, 0, 100, settings.surfaceRavineVariability),

            new GuiPageButtonList.GuiSlideEntry(ID_RIVER_RAVINE_RARITY, I18n.format("createWorld.customize.custom.riverRavineRarity"), true, this, 0, 500, settings.riverRavineRarity),
        };
        list = new GuiPageButtonList(mc, width, height, 32, height - 32, 25, this, new GuiPageButtonList.GuiListEntry[][] {
            page1
        });

        update();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    public void setEntryValue(int id, boolean value)
    {
        switch (id)
        {
            case ID_FLAT_BEDROCK:
                settings.flatBedrock = value;
                break;
        }
        update();
    }

    @Override
    public void setEntryValue(int id, float value)
    {
        switch (id)
        {
            case ID_SPAWN_FUZZ:
                settings.spawnFuzz = Math.round(value);
                break;

            case ID_RAVINE_RARITY:
                settings.ravineRarity = Math.round(value);
                break;
            case ID_RAVINE_HEIGHT:
                settings.ravineHeight = Math.round(value);
                break;
            case ID_RAVINE_VARIABILITY:
                settings.ravineVariability = Math.round(value);
                break;

            case ID_SURFACE_RAVINE_RARITY:
                settings.surfaceRavineRarity = Math.round(value);
                break;
            case ID_SURFACE_RAVINE_HEIGHT:
                settings.surfaceRavineHeight = Math.round(value);
                break;
            case ID_SURFACE_RAVINE_VARIABILITY:
                settings.surfaceRavineVariability = Math.round(value);
                break;

            case ID_RIVER_RAVINE_RARITY:
                settings.riverRavineRarity = Math.round(value);
                break;
        }
        update();
    }

    @Override
    public void setEntryValue(int id, String value)
    {
        update();
    }

    @Override
    public String getText(int id, String name, float value)
    {
        return name + ": " + this.getFormattedValue(id, value);
    }

    private void update()
    {
        defaultsBtn.enabled = !settings.isDefault();
    }

    private WorldGenSettingsBuilder loadSettings(String str)
    {
        if (!Strings.isNullOrEmpty(str))
        {
            try
            {
                return WorldGenSettings.fromString(str);
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().error("Error parsing s: {}", str);
                TerraFirmaCraft.getLog().catching(e);
            }
        }
        return new WorldGenSettingsBuilder();
    }

    private String getFormattedValue(int id, float value)
    {
        switch (id)
        {
            case ID_SPAWN_FUZZ:
            case ID_RAVINE_RARITY:
            case ID_RAVINE_HEIGHT:
            case ID_RAVINE_VARIABILITY:
            case ID_SURFACE_RAVINE_RARITY:
            case ID_SURFACE_RAVINE_HEIGHT:
            case ID_SURFACE_RAVINE_VARIABILITY:
            case ID_RIVER_RAVINE_RARITY:
                return String.valueOf(Math.round(value));
        }
        return String.valueOf(value);
    }
}
