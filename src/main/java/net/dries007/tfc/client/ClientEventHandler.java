package net.dries007.tfc.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GrassColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.util.climate.ClimateTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
        LOGGER.debug("Client Setup");

        // Rock blocks
        TFCBlocks.ROCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout()));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout())));

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutoutMipped()));
    }

    @SubscribeEvent
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        LOGGER.debug("Registering Color Handler Blocks");
        BlockColors blockColors = event.getBlockColors();

        // Grass Colors
        IBlockColor grassColor = (state, worldIn, pos, tintIndex) -> {
            if (pos != null && tintIndex == 0)
            {
                // todo: change this to use monthly temp
                double temp = MathHelper.clamp((ClimateTFC.getAvgTemp(pos) + 30) / 60, 0, 1);
                double rain = MathHelper.clamp((ClimateTFC.getRainfall(pos) - 50) / 400, 0, 1);
                return GrassColors.get(temp, rain);
            }
            return -1;
        };

        blockColors.register(grassColor, TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().stream().map(RegistryObject::get).toArray(Block[]::new));
    }
}
