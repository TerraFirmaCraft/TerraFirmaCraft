/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.NutritionScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.Climate;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
        LOGGER.debug("Client Setup");

        // Screens

        ScreenManager.registerFactory(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
        ScreenManager.registerFactory(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);

        // Render Types

        // Rock blocks
        TFCBlocks.ROCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout()));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout())));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout()))));

        // Wood blocks
        Stream.of(Wood.BlockType.SAPLING, Wood.BlockType.DOOR, Wood.BlockType.TRAPDOOR, Wood.BlockType.FENCE, Wood.BlockType.FENCE_GATE, Wood.BlockType.BUTTON, Wood.BlockType.PRESSURE_PLATE, Wood.BlockType.SLAB, Wood.BlockType.STAIRS).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(type).get(), RenderType.getCutout())));
        TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(Wood.BlockType.LEAVES).get(), RenderType.getCutoutMipped()));

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutoutMipped()));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutoutMipped()));
        RenderTypeLookup.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), RenderType.getCutoutMipped());

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.getCutout())));

        // Entity Rendering

        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

        // Dynamic water color setup
        BiomeColors.WATER_COLOR = (biome, posX, posZ) -> {
            BlockPos pos = new BlockPos(posX, 96, posZ);
            float temperature = Climate.getTemperature(pos);
            float rainfall = Climate.getRainfall(pos);
            return WaterColors.getWaterColor(temperature, rainfall);
        };
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
                // Bias both temperature + rainfall towards the edges
                double temp = MathHelper.clamp((Climate.getTemperature(pos) + 30) / 60, 0, 1);
                double rain = MathHelper.clamp((Climate.getRainfall(pos) - 50) / 400, 0, 1);
                return GrassColors.get(temp, rain);
            }
            return -1;
        };

        IBlockColor foliageColor = (state, worldIn, pos, tintIndex) -> {
            if (pos != null && tintIndex == 0)
            {
                double temp = MathHelper.clamp((Climate.getTemperature(pos) + 30) / 60, 0, 1);
                double rain = MathHelper.clamp((Climate.getRainfall(pos) - 50) / 400, 0, 1);
                return FoliageColors.get(temp, rain);
            }
            return -1;
        };

        blockColors.register(grassColor, TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().stream().map(RegistryObject::get).toArray(Block[]::new));
        blockColors.register(grassColor, TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().stream().map(RegistryObject::get).toArray(Block[]::new));
        blockColors.register(grassColor, TFCBlocks.PEAT_GRASS.get());

        blockColors.register(foliageColor, TFCBlocks.WOODS.entrySet().stream().filter(entry -> !entry.getKey().isConifer()).map(entry -> entry.getValue().get(Wood.BlockType.LEAVES).get()).toArray(Block[]::new));
    }

    @SubscribeEvent
    public static void registerParticleFactoriesAndOtherStuff(ParticleFactoryRegisterEvent event)
    {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new WaterColorReloadListener());
    }
}
