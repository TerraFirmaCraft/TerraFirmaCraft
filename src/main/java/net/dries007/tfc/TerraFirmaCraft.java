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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.forge.IForging;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
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
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

@Mod(TerraFirmaCraft.MOD_ID)
public final class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";

    public static final Logger LOGGER = LogManager.getLogger();

    public TerraFirmaCraft()
    {
        LOGGER.info("Initializing TerraFirmaCraft");
        LOGGER.debug("Debug Logging Enabled");
        if (Helpers.detectAssertionsEnabled()) LOGGER.debug("Assertions Enabled");

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::registerCapabilities);
        bus.addListener(this::loadComplete);
        bus.addListener(TFCEntities::onEntityAttributeCreation);

        TFCBlocks.BLOCKS.register(bus);
        TFCItems.ITEMS.register(bus);
        TFCContainerTypes.CONTAINERS.register(bus);
        TFCEntities.ENTITIES.register(bus);
        TFCFluids.FLUIDS.register(bus);
        TFCRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        TFCSounds.SOUNDS.register(bus);
        TFCParticles.PARTICLE_TYPES.register(bus);
        TFCBlockEntities.TILE_ENTITIES.register(bus);

        TFCBiomes.BIOMES.register(bus);
        TFCFeatures.FEATURES.register(bus);
        TFCDecorators.DECORATORS.register(bus);
        TFCSurfaceBuilders.SURFACE_BUILDERS.register(bus);
        TFCCarvers.CARVERS.register(bus);
        TFCBlockPlacers.BLOCK_PLACERS.register(bus);
        TFCWorldType.WORLD_TYPES.register(bus);

        TFCConfig.init();
        PacketHandler.init();
        CalendarEventHandler.init();
        ForgeEventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init();
            ClientForgeEventHandler.init();
        }

        ForgeMod.enableMilkFluid();
    }

    public void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TFC Common Setup");

        TFCLoot.registerLootConditions();
        InteractionManager.registerDefaultInteractions();
        TFCRecipeTypes.registerPotRecipeOutputTypes();
        RockSettings.registerDefaultRocks();
        FarmlandBlock.registerTillables();
        TFCWorldType.overrideDefaultWorldType();
        ServerCalendar.overrideDoDaylightCycleCallback();

        // todo: remove once forge fixes it's damn fluid blocks
        TFCBlocks.fixForgeBrokenFluidBlocks();

        event.enqueueWork(() -> {
            ItemSizeManager.setupItemStackSizeOverrides();
            DispenserBehaviors.registerAll();

            Registry.register(Registry.CHUNK_GENERATOR, Helpers.identifier("overworld"), TFCChunkGenerator.CODEC);
            Registry.register(Registry.BIOME_SOURCE, Helpers.identifier("overworld"), TFCBiomeSource.CODEC);
        });
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IHeat.class);
        event.register(IHeatBlock.class);
        event.register(IForging.class);
        event.register(ChunkData.class);
        event.register(IWorldTracker.class);
        event.register(IFood.class);
        event.register(PlayerData.class);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        FoodHandler.setNonDecaying(false);
    }
}