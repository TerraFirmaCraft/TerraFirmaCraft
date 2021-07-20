/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.dries007.tfc.client.particle.BubbleParticle;
import net.dries007.tfc.client.particle.SteamParticle;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.client.render.*;
import net.dries007.tfc.client.screen.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.mixin.world.biome.BiomeColorsAccessor;

import static net.dries007.tfc.common.types.Wood.BlockType.*;

public final class ClientEventHandler
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::clientSetup);
        bus.addListener(ClientEventHandler::registerColorHandlerBlocks);
        bus.addListener(ClientEventHandler::registerColorHandlerItems);
        bus.addListener(ClientEventHandler::registerParticleFactoriesAndOtherStuff);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Screens
        ScreenManager.register(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
        ScreenManager.register(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);
        ScreenManager.register(TFCContainerTypes.CLIMATE.get(), ClimateScreen::new);
        ScreenManager.register(TFCContainerTypes.FIREPIT.get(), FirepitScreen::new);
        ScreenManager.register(TFCContainerTypes.GRILL.get(), GrillScreen::new);
        ScreenManager.register(TFCContainerTypes.POT.get(), PotScreen::new);
        ScreenManager.register(TFCContainerTypes.LOG_PILE.get(), LogPileScreen::new);
        ScreenManager.register(TFCContainerTypes.WORKBENCH.get(), CraftingScreen::new);
        ScreenManager.register(TFCContainerTypes.CLAY_KNAPPING.get(), KnappingScreen::new);
        ScreenManager.register(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), KnappingScreen::new);
        ScreenManager.register(TFCContainerTypes.LEATHER_KNAPPING.get(), KnappingScreen::new);
        ScreenManager.register(TFCContainerTypes.ROCK_KNAPPING.get(), KnappingScreen::new);

        // Keybindings
        ClientRegistry.registerKeyBinding(TFCKeyBindings.PLACE_BLOCK);

        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> RenderTypeLookup.setRenderLayer(reg.get(), cutout))));

        // Wood blocks
        TFCBlocks.WOODS.values().forEach(map -> {
            Stream.of(SAPLING, DOOR, TRAPDOOR, FENCE, FENCE_GATE, BUTTON, PRESSURE_PLATE, SLAB, STAIRS, TWIG).forEach(type -> RenderTypeLookup.setRenderLayer(map.get(type).get(), cutout));
            Stream.of(LEAVES, FALLEN_LEAVES).forEach(type -> RenderTypeLookup.setRenderLayer(map.get(type).get(), layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid));
        });

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
        TFCBlocks.SPREADING_BUSHES.values().forEach(bush -> RenderTypeLookup.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.SPREADING_CANES.values().forEach(bush -> RenderTypeLookup.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.STATIONARY_BUSHES.values().forEach(bush -> RenderTypeLookup.setRenderLayer(bush.get(), cutoutMipped));
        RenderTypeLookup.setRenderLayer(TFCBlocks.CRANBERRY_BUSH.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(TFCBlocks.DEAD_BERRY_BUSH.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.DEAD_CANE.get(), cutout);
        TFCBlocks.FRUIT_TREE_LEAVES.values().forEach(leaves -> RenderTypeLookup.setRenderLayer(leaves.get(), cutoutMipped));
        TFCBlocks.FRUIT_TREE_SAPLINGS.values().forEach(leaves -> RenderTypeLookup.setRenderLayer(leaves.get(), cutout));
        RenderTypeLookup.setRenderLayer(TFCBlocks.BANANA_PLANT.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.BANANA_SAPLING.get(), cutout);

        // Other
        RenderTypeLookup.setRenderLayer(TFCBlocks.FIREPIT.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.TORCH.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.WALL_TORCH.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.DEAD_TORCH.get(), cutout);
        RenderTypeLookup.setRenderLayer(TFCBlocks.DEAD_WALL_TORCH.get(), cutout);

        // Fluids
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getFlowing(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SALT_WATER.getSource(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getFlowing(), translucent);
        RenderTypeLookup.setRenderLayer(TFCFluids.SPRING_WATER.getSource(), translucent);

        // Entity Rendering
        RenderingRegistry.registerEntityRenderingHandler(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

        // TE Rendering

        ClientRegistry.bindTileEntityRenderer(TFCTileEntities.POT.get(), PotTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TFCTileEntities.GRILL.get(), GrillTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TFCTileEntities.PLACED_ITEM.get(), PlacedItemTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TFCTileEntities.PIT_KILN.get(), PitKilnTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TFCTileEntities.QUERN.get(), QuernTileEntityRenderer::new);

        // Misc
        BiomeColorsAccessor.accessor$setWaterColorResolver(TFCColors.FRESH_WATER);
    }

    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        final BlockColors registry = event.getBlockColors();
        final IBlockColor grassColor = (state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex);
        final IBlockColor foliageColor = (state, worldIn, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex);
        final IBlockColor seasonalFoliageColor = (state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex);

        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> registry.register(grassColor, reg.get()));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> registry.register(grassColor, reg.get()));
        registry.register(grassColor, TFCBlocks.PEAT_GRASS.get());

        TFCBlocks.PLANTS.forEach((plant, reg) -> registry.register(plant.isSeasonal() ? seasonalFoliageColor : grassColor, reg.get()));
        TFCBlocks.WOODS.forEach((wood, reg) -> registry.register(wood.isConifer() ? foliageColor : seasonalFoliageColor, reg.get(Wood.BlockType.LEAVES).get(), reg.get(Wood.BlockType.FALLEN_LEAVES).get()));

        registry.register((state, worldIn, pos, tintIndex) -> TFCColors.getWaterColor(pos), TFCBlocks.SALT_WATER.get(), TFCBlocks.SEA_ICE.get());
        registry.register((state, worldIn, pos, tintIndex) -> 0x5FB5B8, TFCBlocks.SPRING_WATER.get());
    }

    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        final ItemColors registry = event.getItemColors();
        final IItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);
        final IItemColor seasonalFoliageColor = (stack, tintIndex) -> TFCColors.getFoliageColor(null, tintIndex);

        TFCBlocks.PLANTS.forEach((plant, reg) -> registry.register(plant.isSeasonal() ? seasonalFoliageColor : grassColor));
        TFCBlocks.WOODS.forEach((key, value) -> registry.register(seasonalFoliageColor, value.get(Wood.BlockType.FALLEN_LEAVES).get().asItem()));
    }

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

        ParticleManager particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(TFCParticles.BUBBLE.get(), BubbleParticle.Factory::new);
        particleEngine.register(TFCParticles.STEAM.get(), SteamParticle.Factory::new);
    }
}