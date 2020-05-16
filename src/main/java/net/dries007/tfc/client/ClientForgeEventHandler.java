/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onInitGuiPre(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        LOGGER.debug("Init Gui Pre Event");
        if (event.getGui() instanceof CreateWorldScreen)
        {
            // Only change if default is selected, because coming back from customisation, this will be set already.
            CreateWorldScreen gui = ((CreateWorldScreen) event.getGui());
            Integer selectedIndex = ObfuscationReflectionHelper.getPrivateValue(CreateWorldScreen.class, gui, "field_146331_K");
            if (selectedIndex != null && selectedIndex == WorldType.DEFAULT.getId())
            {
                LOGGER.debug("Setting Selected World Type to TFC Default");
                ObfuscationReflectionHelper.setPrivateValue(CreateWorldScreen.class, gui, TFCWorldType.INSTANCE.getId(), "field_146331_K");
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.world != null && mc.gameSettings.showDebugInfo) // todo: config
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getRenderViewEntity().getPosX(), mc.getRenderViewEntity().getBoundingBox().minY, mc.getRenderViewEntity().getPosZ());
            if (mc.world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4))
            {
                IChunk chunk = mc.world.getChunk(pos);
                ChunkData.get(chunk).ifPresent(data -> {
                    list.add("");
                    list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                    if (data.isValid())
                    {
                        list.add(String.format("%sAvg. Temp: %s%.1f\u00b0C", GRAY, WHITE, data.getAverageTemp()));
                        list.add(String.format("%sRainfall: %s%.1f", GRAY, WHITE, data.getRainfall()));
                    }
                    else
                    {
                        list.add("Invalid Chunk Data");
                    }
                });
            }
        }
    }
}
