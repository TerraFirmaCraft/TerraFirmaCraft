/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
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
import net.dries007.tfc.util.calendar.ServerCalendar;
import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.placer.TFCBlockPlacers;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.TFCCarvers;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.decorator.TFCDecorators;
import net.dries007.tfc.world.feature.TFCFeatures;
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

        // Event bus subscribers
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

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

        // Init methods
        TFCConfig.init();
        PacketHandler.init();
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TFC Common Setup");

        // Setup methods
        HeatCapability.setup();
        ForgingCapability.setup();
        ChunkDataCapability.setup();
        WorldTrackerCapability.setup();
        ServerCalendar.setup();
        InteractionManager.setup();
        TFCWorldType.setup();
        TFCLoot.setup();

        event.enqueueWork(DispenserBehaviors::syncSetup);

        // World gen registry objects
        Registry.register(Registry.CHUNK_GENERATOR, Helpers.identifier("overworld"), TFCChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, Helpers.identifier("overworld"), TFCBiomeProvider.CODEC);
    }

    @SubscribeEvent
    public void onConfigReloading(ModConfig.Reloading event)
    {
        TFCConfig.reload();
    }

    @SubscribeEvent
    public void onConfigLoading(ModConfig.Loading event)
    {
        TFCConfig.reload();
    }
}