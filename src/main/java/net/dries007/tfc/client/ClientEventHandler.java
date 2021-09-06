/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.particle.BubbleParticle;
import net.dries007.tfc.client.particle.SteamParticle;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.client.render.*;
import net.dries007.tfc.client.screen.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.mixin.client.accessor.BiomeColorsAccessor;

import static net.dries007.tfc.common.blocks.wood.Wood.BlockType.*;

public final class ClientEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::clientSetup);
        bus.addListener(ClientEventHandler::onConfigReload);
        bus.addListener(ClientEventHandler::registerColorHandlerBlocks);
        bus.addListener(ClientEventHandler::registerColorHandlerItems);
        bus.addListener(ClientEventHandler::registerParticleFactories);
        bus.addListener(ClientEventHandler::registerClientReloadListeners);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Screens
        MenuScreens.register(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
        MenuScreens.register(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);
        MenuScreens.register(TFCContainerTypes.CLIMATE.get(), ClimateScreen::new);
        MenuScreens.register(TFCContainerTypes.FIREPIT.get(), FirepitScreen::new);
        MenuScreens.register(TFCContainerTypes.GRILL.get(), GrillScreen::new);
        MenuScreens.register(TFCContainerTypes.POT.get(), PotScreen::new);
        MenuScreens.register(TFCContainerTypes.LOG_PILE.get(), LogPileScreen::new);
        MenuScreens.register(TFCContainerTypes.WORKBENCH.get(), CraftingScreen::new);
        MenuScreens.register(TFCContainerTypes.CHARCOAL_FORGE.get(), CharcoalForgeScreen::new);
        MenuScreens.register(TFCContainerTypes.CLAY_KNAPPING.get(), KnappingScreen::new);
        MenuScreens.register(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), KnappingScreen::new);
        MenuScreens.register(TFCContainerTypes.LEATHER_KNAPPING.get(), KnappingScreen::new);
        MenuScreens.register(TFCContainerTypes.ROCK_KNAPPING.get(), KnappingScreen::new);

        // Keybindings
        ClientRegistry.registerKeyBinding(TFCKeyBindings.PLACE_BLOCK);

        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout))));

        // Wood blocks
        TFCBlocks.WOODS.values().forEach(map -> {
            Stream.of(SAPLING, DOOR, TRAPDOOR, FENCE, FENCE_GATE, BUTTON, PRESSURE_PLATE, SLAB, STAIRS, TWIG).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), cutout));
            Stream.of(LEAVES, FALLEN_LEAVES).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid));
        });

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), cutoutMipped);

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CALCITE.get(), cutout);

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.ICICLE.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SEA_ICE.get(), cutout);

        // Plants
        TFCBlocks.PLANTS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.SPREADING_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.SPREADING_CANES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.STATIONARY_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CRANBERRY_BUSH.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_BERRY_BUSH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_CANE.get(), cutout);
        TFCBlocks.FRUIT_TREE_LEAVES.values().forEach(leaves -> ItemBlockRenderTypes.setRenderLayer(leaves.get(), cutoutMipped));
        TFCBlocks.FRUIT_TREE_SAPLINGS.values().forEach(leaves -> ItemBlockRenderTypes.setRenderLayer(leaves.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BANANA_PLANT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BANANA_SAPLING.get(), cutout);

        // Other
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.FIREPIT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WALL_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_WALL_TORCH.get(), cutout);

        // Fluids
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SALT_WATER.getFlowing(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SALT_WATER.getSource(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SPRING_WATER.getFlowing(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SPRING_WATER.getSource(), translucent);

        // Entity Rendering
        EntityRenderers.register(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

        // TE Rendering

        BlockEntityRenderers.register(TFCTileEntities.POT.get(), context -> new PotTileEntityRenderer());
        BlockEntityRenderers.register(TFCTileEntities.GRILL.get(), context -> new GrillTileEntityRenderer());
        BlockEntityRenderers.register(TFCTileEntities.PLACED_ITEM.get(), context -> new PlacedItemTileEntityRenderer());
        BlockEntityRenderers.register(TFCTileEntities.PIT_KILN.get(), context -> new PitKilnTileEntityRenderer());
        BlockEntityRenderers.register(TFCTileEntities.QUERN.get(), context -> new QuernTileEntityRenderer());
        BlockEntityRenderers.register(TFCTileEntities.SCRAPING.get(), context -> new ScrapingTileEntityRenderer());

        // Misc
        BiomeColorsAccessor.accessor$setWaterColorsResolver(TFCColors.FRESH_WATER);

        IngameOverlays.reloadOverlays();
    }

    public static void onConfigReload(ModConfigEvent.Reloading event)
    {
        IngameOverlays.reloadOverlays();
    }

    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        final BlockColors registry = event.getBlockColors();
        final BlockColor grassColor = (state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex);
        final BlockColor foliageColor = (state, worldIn, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex);
        final BlockColor seasonalFoliageColor = (state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex);

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
        final ItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);
        final ItemColor seasonalFoliageColor = (stack, tintIndex) -> TFCColors.getFoliageColor(null, tintIndex);

        TFCBlocks.PLANTS.forEach((plant, reg) -> registry.register(plant.isSeasonal() ? seasonalFoliageColor : grassColor));
        TFCBlocks.WOODS.forEach((key, value) -> registry.register(seasonalFoliageColor, value.get(Wood.BlockType.FALLEN_LEAVES).get().asItem()));
    }

    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event)
    {
        // Color maps
        // We maintain a series of color maps independent and beyond the vanilla color maps
        // Sky, Fog, Water and Water Fog color to replace hardcoded per-biome water colors
        // Grass and foliage (which we replace vanilla's anyway, but use our own for better indexing)
        // Foliage winter and fall (for deciduous trees which have leaves which change color during those seasons)
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setSkyColors, TFCColors.SKY_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFogColors, TFCColors.FOG_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterColors, TFCColors.WATER_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setWaterFogColors, TFCColors.WATER_FOG_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setGrassColors, TFCColors.GRASS_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageColors, TFCColors.FOLIAGE_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageFallColors, TFCColors.FOLIAGE_FALL_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageWinterColors, TFCColors.FOLIAGE_WINTER_COLORS_LOCATION));
    }

    public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(TFCParticles.BUBBLE.get(), BubbleParticle.Factory::new);
        particleEngine.register(TFCParticles.STEAM.get(), SteamParticle.Factory::new);
    }

    /**
     * Invoked reflectively via Cyanide's self test injection mechanism
     */
    @SuppressWarnings({"unused", "deprecation"})
    public static void selfTest()
    {
        final BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        final BakedModel missingModel = shaper.getModelManager().getMissingModel();
        final TextureAtlasSprite missingParticle = missingModel.getParticleIcon();

        final List<Block> missingModelErrors = blocksWithStateMatching(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel);
        final List<Block> missingParticleErrors = blocksWithStateMatching(s -> !s.isAir() && shaper.getParticleIcon(s) == missingParticle);
        final List<Item> missingTranslationErrors = itemsWithStackMatching(s -> new TranslatableComponent(s.getDescriptionId()).getString().equals(s.getDescriptionId()));

        if (logValidationErrors("Blocks with missing models:", missingModelErrors, e -> LOGGER.error("  {}", e))
            | logValidationErrors("Blocks with missing particles:", missingParticleErrors, e -> LOGGER.error("  {}", e))
            | logValidationErrors("Items with missing translations:", missingTranslationErrors, e -> LOGGER.error("  {} ({})", e, e.getDescriptionId())))
        {
            throw new AssertionError("Fix the above errors!");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static List<Block> blocksWithStateMatching(Predicate<BlockState> condition)
    {
        return ForgeRegistries.BLOCKS.getValues()
            .stream()
            .filter(b -> b.getRegistryName().getNamespace().equals("tfc"))
            .filter(b -> b.getStateDefinition().getPossibleStates().stream().anyMatch(condition))
            .collect(Collectors.toList());
    }

    @SuppressWarnings("ConstantConditions")
    private static List<Item> itemsWithStackMatching(Predicate<ItemStack> condition)
    {
        final NonNullList<ItemStack> stacks = NonNullList.create();
        return ForgeRegistries.ITEMS.getValues()
            .stream()
            .filter(i -> i.getRegistryName().getNamespace().equals("tfc"))
            .filter(i -> {
                stacks.clear();
                i.fillItemCategory(CreativeModeTab.TAB_SEARCH, stacks);
                return stacks.stream().anyMatch(condition);
            })
            .collect(Collectors.toList());
    }

    private static <T> boolean logValidationErrors(String error, List<T> errors, Consumer<T> logger)
    {
        if (!errors.isEmpty())
        {
            LOGGER.error(error);
            errors.forEach(logger);
            return true;
        }
        return false;
    }
}