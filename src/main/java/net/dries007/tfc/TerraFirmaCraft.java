/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import net.dries007.tfc.client.ClientEventHandler;
import net.dries007.tfc.client.ClientForgeEventHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.forge.IForging;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.DispenserBehaviors;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.calendar.CalendarEventHandler;
import net.dries007.tfc.util.calendar.ServerCalendar;
import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.util.tracker.IWorldTracker;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.biome.TFCBiomeSource;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.TFCCarvers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.decorator.TFCDecorators;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.placer.TFCBlockPlacers;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

@Mod(TerraFirmaCraft.MOD_ID)
public final class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";

    public static final Logger LOGGER = LogManager.getLogger();

    public TerraFirmaCraft()
    {
        LOGGER.info("TFC Constructor");
        LOGGER.debug("Debug Logging Enabled");

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::loadComplete);

        TFCBlocks.BLOCKS.register(modEventBus);
        TFCItems.ITEMS.register(modEventBus);
        TFCContainerTypes.CONTAINERS.register(modEventBus);
        TFCEntities.ENTITIES.register(modEventBus);
        TFCFluids.FLUIDS.register(modEventBus);
        TFCRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        TFCSounds.SOUNDS.register(modEventBus);
        TFCParticles.PARTICLE_TYPES.register(modEventBus);
        TFCTileEntities.TILE_ENTITIES.register(modEventBus);

        TFCBiomes.BIOMES.register(modEventBus);
        TFCFeatures.FEATURES.register(modEventBus);
        TFCDecorators.DECORATORS.register(modEventBus);
        TFCSurfaceBuilders.SURFACE_BUILDERS.register(modEventBus);
        TFCCarvers.CARVERS.register(modEventBus);
        TFCBlockPlacers.BLOCK_PLACERS.register(modEventBus);
        TFCWorldType.WORLD_TYPES.register(modEventBus);

        TFCConfig.init();
        PacketHandler.init();
        CalendarEventHandler.init();
        ForgeEventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init();
            ClientForgeEventHandler.init();
        }
    }

    public void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TFC Common Setup");

        // Setup methods
        HeatCapability.setup();
        ForgingCapability.setup();
        CapabilityManager.INSTANCE.register(ChunkData.class);
        WorldTrackerCapability.setup();
        ServerCalendar.setup();
        Helpers.registerSimpleCapability(IHeat.class);
        Helpers.registerSimpleCapability(IForging.class);
        Helpers.registerSimpleCapability(ChunkData.class);
        Helpers.registerSimpleCapability(IWorldTracker.class);
        Helpers.registerSimpleCapability(IFood.class);
        Helpers.registerSimpleCapability(PlayerData.class);
        TFCLoot.LOOT_CONDITIONS.registerAll();
        InteractionManager.setup();
        TFCWorldType.setup();

        ItemSizeManager.setup();
        ServerCalendar.setup();

        event.enqueueWork(DispenserBehaviors::syncSetup);

        // World gen registry objects
        Registry.register(Registry.CHUNK_GENERATOR, Helpers.identifier("overworld"), TFCChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, Helpers.identifier("overworld"), TFCBiomeSource.CODEC);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        FoodHandler.setNonDecaying(false);
    }
}