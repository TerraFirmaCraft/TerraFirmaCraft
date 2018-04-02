package net.dries007.tfc.client;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public class ClientEvents
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void openGUI(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        if (ConfigTFC.CLIENT.makeWorldTypeClassicDefault && event.getGui() instanceof GuiCreateWorld)
        {
            GuiCreateWorld gui = ((GuiCreateWorld) event.getGui());
            // Only change if default is selected, because coming back from customisation, this will be set already.
            if (gui.selectedIndex == WorldType.DEFAULT.getId()) gui.selectedIndex = TerraFirmaCraft.getWorldTypeTFC().getId();
        }
    }
}
