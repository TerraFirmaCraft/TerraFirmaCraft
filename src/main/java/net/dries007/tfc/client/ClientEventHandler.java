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
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.dries007.tfc.client.screen.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.mixin.world.biome.BiomeColorsAccessor;

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
        ScreenManager.register(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
        ScreenManager.register(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);
        ScreenManager.register(TFCContainerTypes.CLIMATE.get(), ClimateScreen::new);
        ScreenManager.register(TFCContainerTypes.FIREPIT.get(), FirepitScreen::new);
        ScreenManager.register(TFCContainerTypes.GRILL.get(), GrillScreen::new);
        ScreenManager.register(TFCContainerTypes.POT.get(), PotScreen::new);

        // Render Types

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout()));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout())));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout()))));

        // Wood blocks
        Stream.of(Wood.BlockType.SAPLING, Wood.BlockType.DOOR, Wood.BlockType.TRAPDOOR, Wood.BlockType.FENCE, Wood.BlockType.FENCE_GATE, Wood.BlockType.BUTTON, Wood.BlockType.PRESSURE_PLATE, Wood.BlockType.SLAB, Wood.BlockType.STAIRS).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(type).get(), RenderType.cutout())));
        Stream.of(Wood.BlockType.LEAVES, Wood.BlockType.FALLEN_LEAVES, Wood.BlockType.TWIG).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(type).get(), RenderType.cutoutMipped())));

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutoutMipped()));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutoutMipped()));
        RenderTypeLookup.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), RenderType.cutoutMipped());

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout())));

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout()));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout()));
        RenderTypeLookup.setRenderLayer(TFCBlocks.CALCITE.get(), RenderType.cutout());

        // Plants
        TFCBlocks.PLANTS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), RenderType.cutout()));

        // Other
        RenderTypeLookup.setRenderLayer(TFCBlocks.FIREPIT.get(), RenderType.cutout());

        // Fluids
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getFlowing(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getSource(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getFlowing(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getSource(), RenderType.translucent());

        // Entity Rendering
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

        // Redirect vanilla water color resolver
        BiomeColorsAccessor.accessor$setWaterColorResolver(TFCColors.FRESH_WATER);
    }

    @SubscribeEvent
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        LOGGER.debug("Registering Color Handler Blocks");
        final BlockColors registry = event.getBlockColors();
        final int nope = -1;

        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().stream().map(RegistryObject::get).toArray(Block[]::new));
        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().stream().map(RegistryObject::get).toArray(Block[]::new));
        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.PEAT_GRASS.get());
        // Plants
        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.PLANTS.values().stream().map(RegistryObject::get).toArray(Block[]::new));

        TFCBlocks.WOODS.forEach((key, value) -> {
            Block leaves = value.get(Wood.BlockType.LEAVES).get();
            Block fallenLeaves = value.get(Wood.BlockType.FALLEN_LEAVES).get();
            if (key.isConifer())
            {
                registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex), leaves, fallenLeaves);
            }
            else
            {
                registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(state, pos, tintIndex, key.getFallFoliageCoords()), leaves, fallenLeaves);
            }
        });

        registry.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getWaterColor(pos) : nope, TFCBlocks.SALT_WATER.get());
        registry.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getSpringWaterColor(pos) : nope, TFCBlocks.SPRING_WATER.get());
    }

    @SubscribeEvent
    public static void registerParticleFactoriesAndOtherStuff(ParticleFactoryRegisterEvent event)
    {
        // Add client reload listeners here, as it's closest to the location where they are added in vanilla
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();

        // Color maps
        // We maintain a series of color maps independent and beyond the vanilla color maps
        // Water and water fog color to replace hardcoded per-biome water colors
        // Grass and foliage (which we replace vanilla's anyway, but use our own for better indexing)
        // Foliage winter and fall (for deciduous trees which have leaves which change color during those seasons)

        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setSkyColors, TFCColors.SKY_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterColors, TFCColors.WATER_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterFogColors, TFCColors.WATER_FOG_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setGrassColors, TFCColors.GRASS_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageColors, TFCColors.FOLIAGE_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageFallColors, TFCColors.FOLIAGE_FALL_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageWinterColors, TFCColors.FOLIAGE_WINTER_COLORS_LOCATION));
    }
}