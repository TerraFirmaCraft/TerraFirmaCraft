/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.dries007.tfc.client.render.*;
import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.ClimateScreen;
import net.dries007.tfc.client.screen.NutritionScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
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
        ScreenManager.register(TFCContainerTypes.WORKBENCH.get(), CraftingScreen::new);

        // Render Types
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout))));

        // Wood blocks
        Stream.of(Wood.BlockType.SAPLING, Wood.BlockType.DOOR, Wood.BlockType.TRAPDOOR, Wood.BlockType.FENCE, Wood.BlockType.FENCE_GATE, Wood.BlockType.BUTTON, Wood.BlockType.PRESSURE_PLATE, Wood.BlockType.SLAB, Wood.BlockType.STAIRS).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(type).get(), cutout)));
        Stream.of(Wood.BlockType.LEAVES, Wood.BlockType.FALLEN_LEAVES, Wood.BlockType.TWIG).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(type).get(), cutoutMipped)));

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutoutMipped));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutoutMipped));
        RenderTypeLookup.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), cutoutMipped);

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout)));

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout));
        RenderTypeLookup.setRenderLayer(TFCBlocks.CALCITE.get(), cutout);

        RenderTypeLookup.setRenderLayer(TFCBlocks.ICICLE.get(), translucent);
        RenderTypeLookup.setRenderLayer(TFCBlocks.SEA_ICE.get(), cutout);

        // Plants
        TFCBlocks.PLANTS.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout));
        TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout)));

        // Fluids
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getFlowing(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getSource(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getFlowing(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getSource(), translucent);

        // Entity Rendering
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.COD.get(), TFCCodRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.PUFFERFISH.get(), PufferfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.SALMON.get(), TFCSalmonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.TROPICAL_FISH.get(), TropicalFishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.MANATEE.get(), ManateeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.JELLYFISH.get(), JellyfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.ORCA.get(), OrcaRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.ISOPOD.get(), IsopodRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.LOBSTER.get(), LobsterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.VULTURE.get(), VultureRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.DOLPHIN.get(), DolphinRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.BLUEGILL.get(), BluegillRenderer::new);

        // Misc
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
        Block[] leafyPlants = Stream.of(Plant.values()).filter(Plant::isLeafColored).map(p -> TFCBlocks.PLANTS.get(p).get()).toArray(Block[]::new);
        Block[] grassyPlants = Stream.of(Plant.values()).filter(p -> !p.isLeafColored()).map(p -> TFCBlocks.PLANTS.get(p).get()).toArray(Block[]::new);
        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(state, pos, tintIndex, Plant.BlockType.VINE.getFallFoliageCoords()), leafyPlants);
        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), grassyPlants);

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

        registry.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getWaterColor(pos) : nope, TFCBlocks.SALT_WATER.get(), TFCBlocks.SEA_ICE.get());
        registry.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getSpringWaterColor(pos) : nope, TFCBlocks.SPRING_WATER.get());
    }

    @SubscribeEvent
    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        LOGGER.debug("Registering Color Handler Blocks");
        final ItemColors registry = event.getItemColors();

        Item[] leafyPlants = Stream.of(Plant.values()).filter(Plant::isLeafColored).map(p -> TFCBlocks.PLANTS.get(p).get().asItem()).toArray(Item[]::new);
        Item[] grassyPlants = Stream.of(Plant.values()).filter(Plant::needsItemColor).map(p -> TFCBlocks.PLANTS.get(p).get().asItem()).toArray(Item[]::new);
        registry.register((itemStack, tintIndex) -> TFCColors.getGrassColor(new BlockPos(0, 96, 0), tintIndex), grassyPlants);
        registry.register((itemStack, tintIndex) -> TFCColors.getFoliageColor(new BlockPos(0, 96, 0), tintIndex), leafyPlants);
    }

    @SubscribeEvent
    public static void registerParticleFactoriesAndOtherStuff(ParticleFactoryRegisterEvent event)
    {
        // Add client reload listeners here, as it's closest to the location where they are added in vanilla
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();

        // Color maps
        // We maintain a series of color maps independent and beyond the vanilla color maps
        // Sky, Fog, Water and Water Fog color to replace hardcoded per-biome water colors
        // Grass and foliage (which we replace vanilla's anyway, but use our own for better indexing)
        // Foliage winter and fall (for deciduous trees which have leaves which change color during those seasons)

        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setSkyColors, TFCColors.SKY_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFogColors, TFCColors.FOG_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterColors, TFCColors.WATER_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterFogColors, TFCColors.WATER_FOG_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setGrassColors, TFCColors.GRASS_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageColors, TFCColors.FOLIAGE_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageFallColors, TFCColors.FOLIAGE_FALL_COLORS_LOCATION));
        resourceManager.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageWinterColors, TFCColors.FOLIAGE_WINTER_COLORS_LOCATION));
    }
}