package net.dries007.tfc.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.WorldGenSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;


@SideOnly(Side.CLIENT)
public class GuiCustomizeWorld extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder
{
    private static final int ID_DONE = 0;
    private static final int ID_CANCEL = 1;
    private static final int ID_DEFAULTS = 2;

    private static final int ID_SPAWN_FUZZ = 100;
    private static final int ID_FLAT_BEDROCK = 101;

    private final WorldGenSettings defaults = new WorldGenSettings();
    private final GuiCreateWorld parent;

    // Final by all but modifier
    private String title;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton defaultsBtn;

    private GuiPageButtonList list;

    private WorldGenSettings settings;

    public GuiCustomizeWorld(GuiCreateWorld guiCreateWorld, String prevSettings)
    {
        parent = guiCreateWorld;
        settings = loadSettings(prevSettings);
    }

    private void update()
    {
        defaultsBtn.enabled = !settings.equals(defaults);
    }

    private WorldGenSettings loadSettings(String str)
    {
        if (!Strings.isNullOrEmpty(str))
        {
            try
            {
                return WorldGenSettings.fromString(str);
            }
            catch (JsonParseException e)
            {
                TerraFirmaCraft.getLog().error("Error parsing settings: {}", str);
                TerraFirmaCraft.getLog().catching(e);
            }
        }
        return new WorldGenSettings();
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
        };
        list = new GuiPageButtonList(mc, width, height, 32, height - 32, 25, this, new GuiPageButtonList.GuiListEntry[][] {
                page1
        });

        update();
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
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!button.enabled) return;

        switch (button.id)
        {
            case ID_DONE:
                parent.chunkProviderSettingsJson = settings.toString();
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
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.list.handleMouseInput();
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

    private String getFormattedValue(int id, float value)
    {
        switch (id)
        {
            case ID_SPAWN_FUZZ: return String.valueOf(Math.round(value));
        }
        return String.valueOf(value);
    }
}
