/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.client.BarSystem;
import net.dries007.tfc.client.ClientEventHandler;
import net.dries007.tfc.client.ClientForgeEventHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCAttachments;
import net.dries007.tfc.common.TFCCreativeTabs;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.Faunas;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.PropickItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.ingredients.TFCIngredients;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.CauldronInteractions;
import net.dries007.tfc.util.DispenserBehaviors;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.SelfTests;
import net.dries007.tfc.util.TFCPaintings;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.CalendarEventHandler;
import net.dries007.tfc.util.calendar.ServerCalendar;
import net.dries007.tfc.util.climate.ClimateModels;
import net.dries007.tfc.util.data.DataManagers;
import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.world.TFCWorldGen;
import net.dries007.tfc.world.blockpredicate.TFCBlockPredicates;
import net.dries007.tfc.world.carver.TFCCarvers;
import net.dries007.tfc.world.density.TFCDensityFunctions;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.placement.TFCPlacements;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.stateprovider.TFCStateProviders;
import net.dries007.tfc.world.structure.TFCStructureHooks;

@Mod(TerraFirmaCraft.MOD_ID)
public final class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<WorldPreset> PRESET = ResourceKey.create(Registries.WORLD_PRESET, Helpers.identifier("overworld"));

    private @Nullable Throwable syncLoadError;

    public TerraFirmaCraft(
        ModContainer mod,
        IEventBus bus
    ) {
        LOGGER.info("Initializing TerraFirmaCraft");
        LOGGER.info("Options: Assertions Enabled = {}, Boostrap = {}, Test = {}, Debug Logging = {}", Helpers.ASSERTIONS_ENABLED, Helpers.BOOTSTRAP_ENVIRONMENT, Helpers.TEST_ENVIRONMENT, LOGGER.isDebugEnabled());

        SelfTests.runWorldVersionTest();

        mod.registerConfig(ModConfig.Type.CLIENT, TFCConfig.CLIENT.spec());
        mod.registerConfig(ModConfig.Type.SERVER, TFCConfig.SERVER.spec());
        mod.registerConfig(ModConfig.Type.COMMON, TFCConfig.COMMON.spec());

        bus.addListener(this::setup);
        bus.addListener(this::registerRegistries);
        bus.addListener(this::registerCapabilities);
        bus.addListener(this::loadComplete);
        bus.addListener(this::onInterModComms);
        bus.addListener(TFCEntities::onEntityAttributeCreation);
        bus.addListener(Faunas::registerSpawnPlacements);
        bus.addListener(TFCComponents::onModifyDefaultComponents);
        bus.addListener(PacketHandler::setup);

        // Core Registries (vanilla)
        TFCBlocks.BLOCKS.register(bus);
        TFCItems.ITEMS.register(bus);
        TFCContainerTypes.CONTAINERS.register(bus);
        TFCEntities.ENTITIES.register(bus);
        TFCFluids.FLUIDS.register(bus);
        TFCRecipeTypes.RECIPE_TYPES.register(bus);
        TFCRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        TFCSounds.SOUNDS.register(bus);
        TFCParticles.PARTICLE_TYPES.register(bus);
        TFCBlockEntities.BLOCK_ENTITIES.register(bus);
        TFCCreativeTabs.CREATIVE_TABS.register(bus);
        TFCLoot.CONDITIONS.register(bus);
        TFCLoot.NUMBER_PROVIDERS.register(bus);
        TFCLoot.LOOT_FUNCTIONS.register(bus);
        TFCEffects.EFFECTS.register(bus);
        TFCBrain.ACTIVITIES.register(bus);
        TFCBrain.MEMORY_TYPES.register(bus);
        TFCBrain.SCHEDULES.register(bus);
        TFCBrain.SENSOR_TYPES.register(bus);
        TFCBrain.POI_TYPES.register(bus);
        TFCPaintings.PAINTING_TYPES.register(bus);
        TFCAdvancements.TRIGGERS.register(bus);
        TFCComponents.COMPONENTS.register(bus);

        // World Generation (vanilla)
        TFCBlockPredicates.BLOCK_PREDICATES.register(bus);
        TFCPlacements.PLACEMENT_MODIFIERS.register(bus);
        TFCFeatures.FEATURES.register(bus);
        TFCCarvers.CARVERS.register(bus);
        TFCWorldGen.CHUNK_GENERATOR.register(bus);
        TFCWorldGen.BIOME_SOURCE.register(bus);
        TFCStateProviders.BLOCK_STATE_PROVIDERS.register(bus);
        TFCStructureHooks.STRUCTURE_PLACEMENTS.register(bus);
        TFCDensityFunctions.TYPES.register(bus);

        // Custom Registries (tfc)
        FoodTraits.TRAITS.register(bus);
        ItemStackModifiers.TYPES.register(bus);
        PotOutput.TYPES.register(bus);
        ClimateModels.TYPES.register(bus);
        DataManagers.MANAGERS.register(bus);
        BarSystem.BARS.register(bus);

        // Custom Registries (neoforge)
        TFCFluids.FLUID_TYPES.register(bus);
        TFCIngredients.TYPES.register(bus);
        TFCAttachments.TYPES.register(bus);

        CalendarEventHandler.init();
        ForgeEventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init(bus);
            ClientForgeEventHandler.init();
            // todo 1.21, compat with patchy
            //PatchouliClientEventHandler.init();
        }

        NeoForgeMod.enableMilkFluid();
    }

    public void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TFC Common Setup");

        PropickItem.registerDefaultRepresentativeBlocks();
        InteractionManager.registerDefaultInteractions();
        RockSettings.registerDefaultRocks();
        ServerCalendar.overrideDoDaylightCycleCallback();

        event.enqueueWork(() -> {
            DispenserBehaviors.registerDispenserBehaviors();
            IBellowsConsumer.registerDefaultOffsets();
            Wood.registerBlockSetTypes();
            TFCBrain.initializeScheduleContents();

            CauldronInteractions.registerCauldronInteractions();
            TFCBlocks.registerFlowerPotFlowers();
            TFCBlocks.editBlockRequiredTools();
        }).exceptionally(e -> {
            // MinecraftForge#8255 I swear to god. Nuke parallel mod loading from the face of the earth
            LOGGER.error("An unhandled exception was thrown during synchronous mod loading:", e);
            syncLoadError = e;
            return null;
        });

        // todo 1.21, compat with patchy and jade
        /*PatchouliIntegration.registerMultiBlocks();
        if (ModList.get().isLoaded("jade"))
        {
            JadeIntegration.registerToolHandlers();
        }*/
    }

    public void registerRegistries(NewRegistryEvent event)
    {
        event.register(FoodTraits.REGISTRY);
        event.register(ItemStackModifiers.REGISTRY);
        event.register(PotOutput.REGISTRY);
        event.register(ClimateModels.REGISTRY);
        event.register(DataManagers.REGISTRY);
        event.register(BarSystem.REGISTRY);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        // todo: most of these won't be capabilities anymore, probably none
        // event.register(IHeat.class);
        // event.register(IHeatBlock.class);
        // event.register(Forging.class);
        // event.register(ChunkData.class);
        // event.register(WorldTracker.class);
        // event.register(IFood.class);
        // event.register(PlayerData.class);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        FoodHandler.setNonDecaying(false);
        if (syncLoadError != null)
        {
            Helpers.throwAsUnchecked(syncLoadError);
        }
    }

    public void onInterModComms(InterModEnqueueEvent event)
    {
        if (ModList.get().isLoaded("theoneprobe"))
        {
            // todo: 1.21, compat with top
            //InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeIntegration::new);
        }
    }
}