/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.model.ContainedFluidModel;
import net.dries007.tfc.client.model.DoubleIngotPileBlockModel;
import net.dries007.tfc.client.model.IngotPileBlockModel;
import net.dries007.tfc.client.model.ScrapingBlockModel;
import net.dries007.tfc.client.model.SheetPileBlockModel;
import net.dries007.tfc.client.model.TrimmedItemModel;
import net.dries007.tfc.client.model.entity.AlpacaModel;
import net.dries007.tfc.client.model.entity.BearModel;
import net.dries007.tfc.client.model.entity.BluegillModel;
import net.dries007.tfc.client.model.entity.BoarModel;
import net.dries007.tfc.client.model.entity.BongoModel;
import net.dries007.tfc.client.model.entity.CaribouModel;
import net.dries007.tfc.client.model.entity.CougarModel;
import net.dries007.tfc.client.model.entity.CrocodileModel;
import net.dries007.tfc.client.model.entity.DeerModel;
import net.dries007.tfc.client.model.entity.DirewolfModel;
import net.dries007.tfc.client.model.entity.DogModel;
import net.dries007.tfc.client.model.entity.DuckModel;
import net.dries007.tfc.client.model.entity.GazelleModel;
import net.dries007.tfc.client.model.entity.GrouseModel;
import net.dries007.tfc.client.model.entity.HorseChestLayer;
import net.dries007.tfc.client.model.entity.HorseshoeCrabModel;
import net.dries007.tfc.client.model.entity.HyenaModel;
import net.dries007.tfc.client.model.entity.IsopodModel;
import net.dries007.tfc.client.model.entity.JavelinModel;
import net.dries007.tfc.client.model.entity.JellyfishModel;
import net.dries007.tfc.client.model.entity.LionModel;
import net.dries007.tfc.client.model.entity.LobsterModel;
import net.dries007.tfc.client.model.entity.ManateeModel;
import net.dries007.tfc.client.model.entity.MooseModel;
import net.dries007.tfc.client.model.entity.MuskOxModel;
import net.dries007.tfc.client.model.entity.OrcaModel;
import net.dries007.tfc.client.model.entity.PeafowlModel;
import net.dries007.tfc.client.model.entity.PenguinModel;
import net.dries007.tfc.client.model.entity.PheasantModel;
import net.dries007.tfc.client.model.entity.QuailModel;
import net.dries007.tfc.client.model.entity.RatModel;
import net.dries007.tfc.client.model.entity.SabertoothModel;
import net.dries007.tfc.client.model.entity.TFCChickenModel;
import net.dries007.tfc.client.model.entity.TFCCowModel;
import net.dries007.tfc.client.model.entity.TFCGoatModel;
import net.dries007.tfc.client.model.entity.TFCPigModel;
import net.dries007.tfc.client.model.entity.TFCSheepModel;
import net.dries007.tfc.client.model.entity.TFCTurtleModel;
import net.dries007.tfc.client.model.entity.TFCWolfModel;
import net.dries007.tfc.client.model.entity.TigerModel;
import net.dries007.tfc.client.model.entity.TurkeyModel;
import net.dries007.tfc.client.model.entity.WaterWheelModel;
import net.dries007.tfc.client.model.entity.WildebeestModel;
import net.dries007.tfc.client.model.entity.WindmillBladeModel;
import net.dries007.tfc.client.model.entity.YakModel;
import net.dries007.tfc.client.particle.AnimatedParticle;
import net.dries007.tfc.client.particle.BubbleParticle;
import net.dries007.tfc.client.particle.FallingLeafParticle;
import net.dries007.tfc.client.particle.FluidDripParticle;
import net.dries007.tfc.client.particle.GlintParticleProvider;
import net.dries007.tfc.client.particle.LeafParticle;
import net.dries007.tfc.client.particle.SleepParticle;
import net.dries007.tfc.client.particle.SparkParticle;
import net.dries007.tfc.client.particle.SteamParticle;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.client.particle.VariableHeightSmokeParticle;
import net.dries007.tfc.client.particle.WaterFlowParticle;
import net.dries007.tfc.client.particle.WindParticle;
import net.dries007.tfc.client.render.blockentity.AnvilBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.AxleBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BarrelBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BellowsBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BladedAxleBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BowlBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.CharcoalForgeBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.CrankshaftBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.CrucibleBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.FirepitBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.GlassBasinBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.GrillBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.HandWheelBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.HotPouredGlassBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.JarsBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.LoomBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.NestBoxBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PitKilnBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PotBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.QuernBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.SluiceBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCBellBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCChestBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCHangingSignBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCSignBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.ToolRackBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.WaterWheelBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.WindmillBlockEntityRenderer;
import net.dries007.tfc.client.render.entity.AnimalRenderer;
import net.dries007.tfc.client.render.entity.DogRenderer;
import net.dries007.tfc.client.render.entity.GlowArrowRenderer;
import net.dries007.tfc.client.render.entity.OctopoteuthisRenderer;
import net.dries007.tfc.client.render.entity.OviparousRenderer;
import net.dries007.tfc.client.render.entity.PenguinRenderer;
import net.dries007.tfc.client.render.entity.RatRenderer;
import net.dries007.tfc.client.render.entity.SalmonLikeRenderer;
import net.dries007.tfc.client.render.entity.SimpleMobRenderer;
import net.dries007.tfc.client.render.entity.TFCBoatRenderer;
import net.dries007.tfc.client.render.entity.TFCCatRenderer;
import net.dries007.tfc.client.render.entity.TFCChestBoatRenderer;
import net.dries007.tfc.client.render.entity.TFCChestedHorseRenderer;
import net.dries007.tfc.client.render.entity.TFCFishingHookRenderer;
import net.dries007.tfc.client.render.entity.TFCHorseRenderer;
import net.dries007.tfc.client.render.entity.TFCRabbitRenderer;
import net.dries007.tfc.client.render.entity.TFCSquidRenderer;
import net.dries007.tfc.client.render.entity.ThrownJavelinRenderer;
import net.dries007.tfc.client.screen.AnvilPlanScreen;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.client.screen.BarrelScreen;
import net.dries007.tfc.client.screen.BlastFurnaceScreen;
import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.CharcoalForgeScreen;
import net.dries007.tfc.client.screen.ClimateScreen;
import net.dries007.tfc.client.screen.CreateTFCWorldScreen;
import net.dries007.tfc.client.screen.CrucibleScreen;
import net.dries007.tfc.client.screen.FirepitScreen;
import net.dries007.tfc.client.screen.GrillScreen;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.client.screen.LargeVesselScreen;
import net.dries007.tfc.client.screen.LogPileScreen;
import net.dries007.tfc.client.screen.MoldLikeAlloyScreen;
import net.dries007.tfc.client.screen.NestBoxScreen;
import net.dries007.tfc.client.screen.NutritionScreen;
import net.dries007.tfc.client.screen.PotScreen;
import net.dries007.tfc.client.screen.PowderkegScreen;
import net.dries007.tfc.client.screen.SaladScreen;
import net.dries007.tfc.client.screen.ScribingTableScreen;
import net.dries007.tfc.client.screen.SewingTableScreen;
import net.dries007.tfc.client.screen.SmallVesselInventoryScreen;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.fluids.FluidId;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.accessor.BiomeColorsAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;
import net.dries007.tfc.util.data.Metal;

import static net.dries007.tfc.common.blocks.wood.Wood.BlockType.*;

public final class ClientEventHandler
{
    public static void init(IEventBus bus)
    {
        bus.addListener(ClientEventHandler::clientSetup);
        bus.addListener(ClientEventHandler::registerModelLoaders);
        bus.addListener(ClientEventHandler::registerSpecialModels);
        bus.addListener(ClientEventHandler::registerColorHandlerBlocks);
        bus.addListener(ClientEventHandler::registerColorHandlerItems);
        bus.addListener(ClientEventHandler::registerColorResolvers);
        bus.addListener(ClientEventHandler::registerParticleFactories);
        bus.addListener(ClientEventHandler::registerClientReloadListeners);
        bus.addListener(ClientEventHandler::registerEntityRenderers);
        bus.addListener(ClientEventHandler::registerKeyBindings);
        bus.addListener(ClientEventHandler::onTooltipFactoryRegistry);
        bus.addListener(ClientEventHandler::registerLayerDefinitions);
        bus.addListener(ClientEventHandler::registerPresetEditors);
        //bus.addListener(IngameOverlays::registerOverlays); // todo: 1.21, overlays
    }

    @SuppressWarnings("deprecation")
    public static void clientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {

            for (Metal.Default metal : Metal.Default.values())
            {
                if (metal.allParts())
                {
                    Item rod = TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.FISHING_ROD).get();
                    ItemProperties.register(rod, Helpers.identifier("cast"), (stack, level, entity, unused) -> {
                        if (entity == null)
                        {
                            return 0.0F;
                        }
                        else
                        {
                            return entity instanceof Player player && TFCFishingRodItem.isThisTheHeldRod(player, stack) && player.fishing != null ? 1.0F : 0.0F;
                        }
                    });

                    Item shield = TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.SHIELD).get();
                    ItemProperties.register(shield, Helpers.identifierMC("blocking"), (stack, level, entity, unused) -> {
                        if (entity == null)
                        {
                            return 0.0F;
                        }
                        else
                        {
                            return entity instanceof Player && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f;
                        }
                    });

                    Item javelin = TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.JAVELIN).get();
                    ItemProperties.register(javelin, Helpers.identifier("throwing"), (stack, level, entity, unused) ->
                        entity != null && ((entity.isUsingItem() && entity.getUseItem() == stack) || (entity instanceof Monster monster && monster.isAggressive())) ? 1.0F : 0.0F
                    );
                }
            }

            TFCItems.ROCK_TOOLS.values().forEach(tool -> {
                Item javelin = tool.get(RockCategory.ItemType.JAVELIN).get();
                ItemProperties.register(javelin, Helpers.identifier("throwing"), (stack, level, entity, unused) ->
                    entity != null && ((entity.isUsingItem() && entity.getUseItem() == stack) || (entity instanceof Monster monster && monster.isAggressive())) ? 1.0F : 0.0F
                );
            });


            ItemProperties.register(TFCItems.HANDSTONE.get(), Helpers.identifier("damaged"), (stack, level, entity, unused) -> stack.getDamageValue() > stack.getMaxDamage() - 10 ? 1F : 0F);

            TFCBlocks.WOODS.values().forEach(map -> ItemProperties.register(map.get(BARREL).get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));

            ItemProperties.register(TFCBlocks.POWDERKEG.get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f);

            Stream.of(TFCBlocks.LARGE_VESSEL, TFCBlocks.GLAZED_LARGE_VESSELS.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten).forEach(vessel -> ItemProperties.register(vessel.get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));

            ItemProperties.register(TFCBlocks.LIGHT.get().asItem(), Helpers.identifierMC("level"), (stack, level, entity, unused) -> {
                CompoundTag stackTag = stack.getTag();
                if (stackTag != null && stackTag.contains("level", Tag.TAG_INT))
                {
                    return stackTag.getInt("level") / 16F;
                }
                return 1.0F;
            });

            TFCBlocks.WOODS.values().forEach(map -> ItemProperties.register(map.get(BARREL).get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));

            ItemProperties.register(TFCItems.BLOWPIPE_WITH_GLASS.get(), Helpers.identifier("heat"), (stack, level, entity, unused) -> Mth.clamp(HeatCapability.getTemperature(stack) / Heat.maxVisibleTemperature(), 0, 1));
            ItemProperties.register(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.get(), Helpers.identifier("heat"), (stack, level, entity, unused) -> Mth.clamp(HeatCapability.getTemperature(stack) / Heat.maxVisibleTemperature(), 0, 1));

            TFCBlocks.WOODS.forEach((wood, map) -> {
                HorseChestLayer.registerChest(map.get(CHEST).get().asItem(), Helpers.identifier("textures/entity/chest/horse/" + wood.getSerializedName() + ".png"));
                HorseChestLayer.registerChest(map.get(TRAPPED_CHEST).get().asItem(), Helpers.identifier("textures/entity/chest/horse/" + wood.getSerializedName() + ".png"));
                HorseChestLayer.registerChest(map.get(BARREL).get().asItem(), Helpers.identifier("textures/entity/chest/horse/" + wood.getSerializedName() + "_barrel.png"));
            });
        });

        BarSystem.registerDefaultBars();

        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();
        final Predicate<RenderType> ghostBlock = rt -> rt == cutoutMipped || rt == Sheets.translucentCullBlockSheet();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().forEach(map -> {
            ItemBlockRenderTypes.setRenderLayer(map.get(Rock.BlockType.SPIKE).get(), cutout);
            ItemBlockRenderTypes.setRenderLayer(map.get(Rock.BlockType.AQUEDUCT).get(), cutout);
        });
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout))));
        TFCBlocks.ORE_DEPOSITS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));

        // Wood blocks
        final Predicate<RenderType> leafPredicate = layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid;
        TFCBlocks.WOODS.values().forEach(map -> {
            Stream.of(SAPLING, DOOR, TRAPDOOR, FENCE, FENCE_GATE, BUTTON, PRESSURE_PLATE, SLAB, STAIRS, TWIG, BARREL, SCRIBING_TABLE, SEWING_TABLE, JAR_SHELF, POTTED_SAPLING, ENCASED_AXLE, CLUTCH, GEAR_BOX).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), cutout));
            Stream.of(LEAVES, FALLEN_LEAVES).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), leafPredicate));
        });

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.TREE_ROOTS.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.PINE_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SPRUCE_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WHITE_CEDAR_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.ASPEN_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POTTED_PINE_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POTTED_DOUGLAS_FIR_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POTTED_SPRUCE_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POTTED_WHITE_CEDAR_KRUMMHOLZ.get(), leafPredicate);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POTTED_ASPEN_KRUMMHOLZ.get(), leafPredicate);

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.KAOLIN_CLAY_GRASS.get(), cutoutMipped);

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CALCITE.get(), cutout);

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.ICICLE.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SEA_ICE.get(), cutout);
        TFCBlocks.COLORED_POURED_GLASS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), translucent));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POURED_GLASS.get(), translucent);

        // Plants
        TFCBlocks.CROPS.values().forEach(reg -> {
            if (reg.get() instanceof IGhostBlockHandler)
            {
                ItemBlockRenderTypes.setRenderLayer(reg.get(), ghostBlock);
            }
            else
            {
                ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout);
            }
        });
        TFCBlocks.DEAD_CROPS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.WILD_CROPS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));

        TFCBlocks.PLANTS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.POTTED_PLANTS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.SPREADING_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.SPREADING_CANES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        TFCBlocks.STATIONARY_BUSHES.values().forEach(bush -> ItemBlockRenderTypes.setRenderLayer(bush.get(), cutoutMipped));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CRANBERRY_BUSH.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_BERRY_BUSH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_CANE.get(), cutout);
        TFCBlocks.FRUIT_TREE_LEAVES.values().forEach(leaves -> ItemBlockRenderTypes.setRenderLayer(leaves.get(), cutoutMipped));
        TFCBlocks.FRUIT_TREE_SAPLINGS.values().forEach(leaves -> ItemBlockRenderTypes.setRenderLayer(leaves.get(), cutout));
        TFCBlocks.FRUIT_TREE_POTTED_SAPLINGS.values().forEach(leaves -> ItemBlockRenderTypes.setRenderLayer(leaves.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BANANA_PLANT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_BANANA_PLANT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BANANA_SAPLING.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BANANA_POTTED_SAPLING.get(), cutout);

        // Other
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.FIREPIT.get(), ghostBlock);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.GRILL.get(), ghostBlock);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.POT.get(), ghostBlock);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CERAMIC_BOWL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WALL_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_WALL_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WATTLE.get(), ghostBlock);
        TFCBlocks.STAINED_WATTLE.values().forEach(wattle -> ItemBlockRenderTypes.setRenderLayer(wattle.get(), ghostBlock));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.UNSTAINED_WATTLE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SHEET_PILE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.INGOT_PILE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DOUBLE_INGOT_PILE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SCRAPING.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.STEEL_PIPE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.STEEL_PUMP.get(), cutout);

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.COMPOSTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BLOOMERY.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.ICE_PILE.get(), translucent);

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.LARGE_VESSEL.get(), cutout);
        TFCBlocks.GLAZED_LARGE_VESSELS.values().forEach(vessel -> ItemBlockRenderTypes.setRenderLayer(vessel.get(), cutout));

        // Fluids
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SALT_WATER.getFlowing(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SALT_WATER.getSource(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SPRING_WATER.getFlowing(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.SPRING_WATER.getSource(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCFluids.RIVER_WATER.get(), translucent);

        // Misc
        BiomeColorsAccessor.accessor$setWaterColorsResolver(TFCColors.FRESH_WATER);

        for (Wood wood : Wood.VALUES)
        {
            Sheets.addWoodType(wood.getVanillaWoodType());
        }
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
        event.register(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);
        event.register(TFCContainerTypes.CLIMATE.get(), ClimateScreen::new);
        event.register(TFCContainerTypes.SALAD.get(), SaladScreen::new);
        event.register(TFCContainerTypes.WORKBENCH.get(), CraftingScreen::new);
        event.register(TFCContainerTypes.FIREPIT.get(), FirepitScreen::new);
        event.register(TFCContainerTypes.GRILL.get(), GrillScreen::new);
        event.register(TFCContainerTypes.POT.get(), PotScreen::new);
        event.register(TFCContainerTypes.POWDERKEG.get(), PowderkegScreen::new);
        event.register(TFCContainerTypes.CHARCOAL_FORGE.get(), CharcoalForgeScreen::new);
        event.register(TFCContainerTypes.LOG_PILE.get(), LogPileScreen::new);
        event.register(TFCContainerTypes.NEST_BOX.get(), NestBoxScreen::new);
        event.register(TFCContainerTypes.CRUCIBLE.get(), CrucibleScreen::new);
        event.register(TFCContainerTypes.BARREL.get(), BarrelScreen::new);
        event.register(TFCContainerTypes.ANVIL.get(), AnvilScreen::new);
        event.register(TFCContainerTypes.ANVIL_PLAN.get(), AnvilPlanScreen::new);
        event.register(TFCContainerTypes.BLAST_FURNACE.get(), BlastFurnaceScreen::new);
        event.register(TFCContainerTypes.CHEST_9x2.get(), ContainerScreen::new);
        event.register(TFCContainerTypes.CHEST_9x4.get(), ContainerScreen::new);
        event.register(TFCContainerTypes.KNAPPING.get(), KnappingScreen::new);
        event.register(TFCContainerTypes.SMALL_VESSEL_INVENTORY.get(), SmallVesselInventoryScreen::new);
        event.register(TFCContainerTypes.MOLD_LIKE_ALLOY.get(), MoldLikeAlloyScreen::new);
        event.register(TFCContainerTypes.LARGE_VESSEL.get(), LargeVesselScreen::new);
        event.register(TFCContainerTypes.SCRIBING_TABLE.get(), ScribingTableScreen::new);
        event.register(TFCContainerTypes.SEWING_TABLE.get(), SewingTableScreen::new);
    }

    public static void onTooltipFactoryRegistry(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(Tooltips.DeviceImageTooltip.class, ClientDeviceImageTooltip::new);
    }

    public static void registerKeyBindings(RegisterKeyMappingsEvent event)
    {
        event.register(TFCKeyBindings.PLACE_BLOCK);
        event.register(TFCKeyBindings.CYCLE_CHISEL_MODE);
        event.register(TFCKeyBindings.STACK_FOOD);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        event.registerEntityRenderer(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(TFCEntities.FISHING_BOBBER.get(), TFCFishingHookRenderer::new);
        event.registerEntityRenderer(TFCEntities.THROWN_JAVELIN.get(), ThrownJavelinRenderer::new);
        event.registerEntityRenderer(TFCEntities.GLOW_ARROW.get(), GlowArrowRenderer::new);
        event.registerEntityRenderer(TFCEntities.SEAT.get(), NoopRenderer::new);
        event.registerEntityRenderer(TFCEntities.CHEST_MINECART.get(), ctx -> new MinecartRenderer<>(ctx, RenderHelpers.layerId("chest_minecart")));
        event.registerEntityRenderer(TFCEntities.HOLDING_MINECART.get(), ctx -> new MinecartRenderer<>(ctx, RenderHelpers.layerId("holding_minecart")));
        for (Wood wood : Wood.VALUES)
        {
            event.registerEntityRenderer(TFCEntities.BOATS.get(wood).get(), ctx -> new TFCBoatRenderer(ctx, wood.getSerializedName()));
            event.registerEntityRenderer(TFCEntities.CHEST_BOATS.get(wood).get(), ctx -> new TFCChestBoatRenderer(ctx, wood.getSerializedName()));
        }
        event.registerEntityRenderer(TFCEntities.COD.get(), CodRenderer::new);
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.SALMON).get(), SalmonRenderer::new);
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.LARGEMOUTH_BASS).get(), ctx -> new SalmonLikeRenderer(ctx, "largemouth_bass"));
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.SMALLMOUTH_BASS).get(), ctx -> new SalmonLikeRenderer(ctx, "smallmouth_bass"));
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.LAKE_TROUT).get(), ctx -> new SalmonLikeRenderer(ctx, "lake_trout"));
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.RAINBOW_TROUT).get(), ctx -> new SalmonLikeRenderer(ctx, "rainbow_trout"));
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.CRAPPIE).get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CodModel::new, "crappie").flops().build());
        event.registerEntityRenderer(TFCEntities.FRESHWATER_FISH.get(Fish.BLUEGILL).get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BluegillModel::new, "bluegill").flops().build());
        event.registerEntityRenderer(TFCEntities.TROPICAL_FISH.get(), TropicalFishRenderer::new);
        event.registerEntityRenderer(TFCEntities.PUFFERFISH.get(), PufferfishRenderer::new);
        event.registerEntityRenderer(TFCEntities.JELLYFISH.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, JellyfishModel::new, "jellyfish").flops().texture(Jellyfish::getTextureLocation).build());
        event.registerEntityRenderer(TFCEntities.LOBSTER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, LobsterModel::new, "lobster").build());
        event.registerEntityRenderer(TFCEntities.CRAYFISH.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, LobsterModel::new, "crayfish").build());
        event.registerEntityRenderer(TFCEntities.ISOPOD.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, IsopodModel::new, "isopod").build());
        event.registerEntityRenderer(TFCEntities.HORSESHOE_CRAB.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, HorseshoeCrabModel::new, "horseshoe_crab").build());
        event.registerEntityRenderer(TFCEntities.DOLPHIN.get(), DolphinRenderer::new);
        event.registerEntityRenderer(TFCEntities.ORCA.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, OrcaModel::new, "orca").build());
        event.registerEntityRenderer(TFCEntities.MANATEE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, ManateeModel::new, "manatee").build());
        event.registerEntityRenderer(TFCEntities.TURTLE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TFCTurtleModel::new, "turtle").build());
        event.registerEntityRenderer(TFCEntities.PENGUIN.get(), PenguinRenderer::new);
        event.registerEntityRenderer(TFCEntities.FROG.get(), FrogRenderer::new);
        event.registerEntityRenderer(TFCEntities.POLAR_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "polar_bear").shadow(0.9f).scale(1.3f).build());
        event.registerEntityRenderer(TFCEntities.GRIZZLY_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "grizzly_bear").shadow(0.9f).scale(1.1f).build());
        event.registerEntityRenderer(TFCEntities.BLACK_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "black_bear").shadow(0.9f).scale(0.9f).build());
        event.registerEntityRenderer(TFCEntities.COUGAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CougarModel::new, "cougar").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.PANTHER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CougarModel::new, "panther").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.LION.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, LionModel::new, "lion").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.SABERTOOTH.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, SabertoothModel::new, "sabertooth").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.TIGER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TigerModel::new, "tiger").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.CROCODILE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CrocodileModel::new, "crocodile").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.WOLF.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TFCWolfModel::new, "wolf").shadow(0.5f).scale(1.1f).build());
        event.registerEntityRenderer(TFCEntities.HYENA.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, HyenaModel::new, "hyena").shadow(0.5f).scale(1.1f).build());
        event.registerEntityRenderer(TFCEntities.DIREWOLF.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, DirewolfModel::new, "direwolf").shadow(0.9f).build());
        event.registerEntityRenderer(TFCEntities.SQUID.get(), ctx -> new TFCSquidRenderer<>(ctx, new SquidModel<>(RenderHelpers.bakeSimple(ctx, "squid"))));
        event.registerEntityRenderer(TFCEntities.OCTOPOTEUTHIS.get(), ctx -> new OctopoteuthisRenderer(ctx, new SquidModel<>(RenderHelpers.bakeSimple(ctx, "glow_squid"))));
        event.registerEntityRenderer(TFCEntities.PIG.get(), ctx -> new AnimalRenderer<>(ctx, new TFCPigModel<>(RenderHelpers.bakeSimple(ctx, "pig")), "pig"));
        event.registerEntityRenderer(TFCEntities.COW.get(), ctx -> new AnimalRenderer<>(ctx, new TFCCowModel(RenderHelpers.bakeSimple(ctx, "cow")), "cow"));
        event.registerEntityRenderer(TFCEntities.GOAT.get(), ctx -> new AnimalRenderer<>(ctx, new TFCGoatModel(RenderHelpers.bakeSimple(ctx, "goat")), "goat"));
        event.registerEntityRenderer(TFCEntities.YAK.get(), ctx -> new AnimalRenderer<>(ctx, new YakModel(RenderHelpers.bakeSimple(ctx, "yak")), "yak"));
        event.registerEntityRenderer(TFCEntities.ALPACA.get(), ctx -> new AnimalRenderer<>(ctx, new AlpacaModel(RenderHelpers.bakeSimple(ctx, "alpaca")), "alpaca"));
        event.registerEntityRenderer(TFCEntities.SHEEP.get(), ctx -> new AnimalRenderer<>(ctx, new TFCSheepModel(RenderHelpers.bakeSimple(ctx, "sheep")), "sheep"));
        event.registerEntityRenderer(TFCEntities.MUSK_OX.get(), ctx -> new AnimalRenderer<>(ctx, new MuskOxModel(RenderHelpers.bakeSimple(ctx, "musk_ox")), "musk_ox"));
        event.registerEntityRenderer(TFCEntities.CHICKEN.get(), ctx -> new OviparousRenderer<>(ctx, new TFCChickenModel(RenderHelpers.bakeSimple(ctx, "chicken")), "chicken", "rooster", "chick"));
        event.registerEntityRenderer(TFCEntities.DUCK.get(), ctx -> new OviparousRenderer<>(ctx, new DuckModel(RenderHelpers.bakeSimple(ctx, "duck")), "duck", "drake", "duckling"));
        event.registerEntityRenderer(TFCEntities.QUAIL.get(), ctx -> new OviparousRenderer<>(ctx, new QuailModel(RenderHelpers.bakeSimple(ctx, "quail")), "quail", "quail_male", "quail_chick"));
        event.registerEntityRenderer(TFCEntities.RABBIT.get(), TFCRabbitRenderer::new);
        event.registerEntityRenderer(TFCEntities.FOX.get(), FoxRenderer::new);
        event.registerEntityRenderer(TFCEntities.PANDA.get(), PandaRenderer::new);
        event.registerEntityRenderer(TFCEntities.OCELOT.get(), OcelotRenderer::new);
        event.registerEntityRenderer(TFCEntities.BONGO.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BongoModel::new, "bongo").shadow(0.6f).build());
        event.registerEntityRenderer(TFCEntities.CARIBOU.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CaribouModel::new, "caribou").shadow(0.6f).build());
        event.registerEntityRenderer(TFCEntities.DEER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, DeerModel::new, "deer").shadow(0.6f).hasBabyTexture().build());
        event.registerEntityRenderer(TFCEntities.GAZELLE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, GazelleModel::new, "gazelle").shadow(0.6f).build());
        event.registerEntityRenderer(TFCEntities.GROUSE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, GrouseModel::new, "grouse").shadow(0.5f).texture(e -> RenderHelpers.getGenderedTexture(e, "grouse")).build());
        event.registerEntityRenderer(TFCEntities.PHEASANT.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, PheasantModel::new, "pheasant").shadow(0.5f).texture(e -> RenderHelpers.getGenderedTexture(e, "pheasant")).build());
        event.registerEntityRenderer(TFCEntities.TURKEY.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TurkeyModel::new, "turkey").shadow(0.5f).texture(e -> RenderHelpers.getGenderedTexture(e, "turkey")).build());
        event.registerEntityRenderer(TFCEntities.PEAFOWL.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, PeafowlModel::new, "peafowl").shadow(0.5f).texture(e -> RenderHelpers.getGenderedTexture(e, "peafowl")).build());
        event.registerEntityRenderer(TFCEntities.MULE.get(), ctx -> new TFCChestedHorseRenderer<>(ctx, 0.92F, RenderHelpers.layerId("mule"), "mule"));
        event.registerEntityRenderer(TFCEntities.DONKEY.get(), ctx -> new TFCChestedHorseRenderer<>(ctx, 0.87F, RenderHelpers.layerId("donkey"), "donkey"));
        event.registerEntityRenderer(TFCEntities.HORSE.get(), TFCHorseRenderer::new);
        event.registerEntityRenderer(TFCEntities.RAT.get(), RatRenderer::new);
        event.registerEntityRenderer(TFCEntities.CAT.get(), TFCCatRenderer::new);
        event.registerEntityRenderer(TFCEntities.DOG.get(), DogRenderer::new);
        event.registerEntityRenderer(TFCEntities.BOAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BoarModel::new, "boar").build());
        event.registerEntityRenderer(TFCEntities.WILDEBEEST.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, WildebeestModel::new, "wildebeest").build());
        event.registerEntityRenderer(TFCEntities.MOOSE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, MooseModel::new, "moose").shadow(1.0f).scale(0.8f).build());


        // BEs
        event.registerBlockEntityRenderer(TFCBlockEntities.FIREPIT.get(), ctx -> new FirepitBlockEntityRenderer<>());
        event.registerBlockEntityRenderer(TFCBlockEntities.POT.get(), ctx -> new PotBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.GRILL.get(), ctx -> new GrillBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.PLACED_ITEM.get(), ctx -> new PlacedItemBlockEntityRenderer<>());
        event.registerBlockEntityRenderer(TFCBlockEntities.PIT_KILN.get(), ctx -> new PitKilnBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.QUERN.get(), ctx -> new QuernBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.CHEST.get(), TFCChestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.TRAPPED_CHEST.get(), TFCChestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.LOOM.get(), ctx -> new LoomBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SLUICE.get(), ctx -> new SluiceBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BELLOWS.get(), ctx -> new BellowsBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.TOOL_RACK.get(), ctx -> new ToolRackBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SIGN.get(), TFCSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.HANGING_SIGN.get(), TFCHangingSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.BARREL.get(), ctx -> new BarrelBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.CRUCIBLE.get(), ctx -> new CrucibleBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.LECTERN.get(), LecternRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.ANVIL.get(), ctx -> new AnvilBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.CHARCOAL_FORGE.get(), ctx -> new CharcoalForgeBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.NEST_BOX.get(), ctx -> new NestBoxBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BELL.get(), TFCBellBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.BOWL.get(), ctx -> new BowlBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.HOT_POURED_GLASS.get(), ctx -> new HotPouredGlassBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.GLASS_BASIN.get(), ctx -> new GlassBasinBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.JARS.get(), ctx -> new JarsBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.AXLE.get(), ctx -> new AxleBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BLADED_AXLE.get(), ctx -> new BladedAxleBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.TRIP_HAMMER.get(), ctx -> new TripHammerBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.HAND_WHEEL.get(), ctx -> new HandWheelBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.WATER_WHEEL.get(), WaterWheelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.WINDMILL.get(), WindmillBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.CRANKSHAFT.get(), ctx -> new CrankshaftBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BELL.get(), TFCBellBlockEntityRenderer::new);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        LayerDefinition boatLayer = BoatModel.createBodyModel();
        LayerDefinition raftLayer = RaftModel.createBodyModel();
        LayerDefinition chestLayer = ChestBoatModel.createBodyModel();
        LayerDefinition chestRaftLayer = ChestRaftModel.createBodyModel();
        for (Wood wood : Wood.VALUES)
        {
            event.registerLayerDefinition(TFCBoatRenderer.boatName(wood.getSerializedName()), wood == Wood.PALM ? () -> raftLayer : () -> boatLayer);
            event.registerLayerDefinition(TFCChestBoatRenderer.chestBoatName(wood.getSerializedName()), wood == Wood.PALM ? () -> chestRaftLayer : () -> chestLayer);
        }
        event.registerLayerDefinition(RenderHelpers.layerId("bluegill"), BluegillModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("crappie"), CodModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("jellyfish"), JellyfishModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("lobster"), LobsterModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("crayfish"), LobsterModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("horseshoe_crab"), HorseshoeCrabModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("isopod"), IsopodModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("orca"), OrcaModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("manatee"), ManateeModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("turtle"), TFCTurtleModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("penguin"), PenguinModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("polar_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("grizzly_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("black_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("cougar"), CougarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("panther"), CougarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("lion"), LionModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("sabertooth"), SabertoothModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("tiger"), TigerModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("crocodile"), CrocodileModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("wolf"), TFCWolfModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("hyena"), HyenaModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("direwolf"), DirewolfModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("squid"), SquidModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("glow_squid"), SquidModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("pig"), () -> TFCPigModel.createTFCBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(RenderHelpers.layerId("cow"), TFCCowModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("goat"), GoatModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("yak"), YakModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("alpaca"), AlpacaModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("sheep"), TFCSheepModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("musk_ox"), MuskOxModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("chicken"), TFCChickenModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("duck"), DuckModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("quail"), QuailModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("bongo"), BongoModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("caribou"), CaribouModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("deer"), DeerModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("gazelle"), GazelleModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("moose"), MooseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("grouse"), GrouseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("pheasant"), PheasantModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("turkey"), TurkeyModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("peafowl"), PeafowlModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("rat"), RatModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("dog"), DogModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("cat"), () -> LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32));
        event.registerLayerDefinition(RenderHelpers.layerId("cat_collar"), () -> LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01f)), 64, 32));
        event.registerLayerDefinition(RenderHelpers.layerId("boar"), BoarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("wildebeest"), WildebeestModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("javelin"), JavelinModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("chest_minecart"), MinecartModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("holding_minecart"), MinecartModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("bell_body"), BellRenderer::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("water_wheel"), WaterWheelModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("windmill_blade"), WindmillBladeModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("horse_chest"), ChestedHorseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("mule"), ChestedHorseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("donkey"), ChestedHorseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.layerId("water_wheel"), WaterWheelModel::createBodyLayer);
    }


    public static void registerSpecialModels(ModelEvent.RegisterAdditional event)
    {
        for (String metal : new String[] {"native_copper", "native_silver", "native_gold", "cassiterite"})
        {
            register(event, Helpers.identifier("item/pan/" + metal + "/result"));

            for (Rock rock : Rock.values())
            {
                register(event, Helpers.identifier("item/pan/" + metal + "/" + rock.getSerializedName() + "_half"));
                register(event, Helpers.identifier("item/pan/" + metal + "/" + rock.getSerializedName() + "_full"));
            }
        }

        JarsBlockEntityRenderer.MODELS.forEach((item, model) -> event.register(model));

        for (AbstractFirepitBlockEntity.BurnStage stage : AbstractFirepitBlockEntity.BurnStage.values())
        {
            for (int i = AbstractFirepitBlockEntity.SLOT_FUEL_CONSUME; i <= AbstractFirepitBlockEntity.SLOT_FUEL_INPUT; i++)
            {
                register(event, stage.getModel(i));
            }
        }

        event.register(CrankshaftBlockEntityRenderer.WHEEL_MODEL);

        TFCConfig.CLIENT.additionalSpecialModels.get().forEach(s -> register(event, Helpers.resourceLocation(s)));
    }

    private static void register(ModelEvent.RegisterAdditional event, ResourceLocation id)
    {
        event.register(ModelResourceLocation.standalone(id)); // This event will assert this is the case, piss poor API design here
    }

    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(Helpers.identifier("contained_fluid"), new ContainedFluidModel.Loader());
        event.register(Helpers.identifier("trim"), new TrimmedItemModel.Loader());
        event.register(Helpers.identifier("ingot_pile"), IngotPileBlockModel.INSTANCE);
        event.register(Helpers.identifier("double_ingot_pile"), DoubleIngotPileBlockModel.INSTANCE);
        event.register(Helpers.identifier("sheet_pile"), SheetPileBlockModel.INSTANCE);
        event.register(Helpers.identifier("scraping"), ScrapingBlockModel.INSTANCE);
    }

    public static void registerColorHandlerBlocks(RegisterColorHandlersEvent.Block event)
    {
        final BlockColor grassColor = (state, level, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex);
        final BlockColor tallGrassColor = (state, level, pos, tintIndex) -> TFCColors.getTallGrassColor(pos, tintIndex);
        final BlockColor foliageColor = (state, level, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex);
        final BlockColor grassBlockColor = (state, level, pos, tintIndex) -> state.getValue(ConnectedGrassBlock.SNOWY) || tintIndex != 1 ? -1 : grassColor.getColor(state, level, pos, tintIndex);

        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> event.register(grassBlockColor, reg.get()));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> event.register(grassBlockColor, reg.get()));
        event.register(grassBlockColor, TFCBlocks.PEAT_GRASS.get());
        event.register(grassBlockColor, TFCBlocks.KAOLIN_CLAY_GRASS.get());

        TFCBlocks.PLANTS.forEach((plant, reg) -> {
            if (plant.isBlockTinted())
                event.register(
                    plant.isTallGrass() ?
                        tallGrassColor :
                        plant.isSeasonal() ?
                            (state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, 145) :
                            plant.isFoliage() ?
                                foliageColor :
                                grassColor, reg.get());
        });
        TFCBlocks.POTTED_PLANTS.forEach((plant, reg) -> {
            if (plant.isFlowerpotTinted())
                event.register(grassColor, reg.get());
        });
        TFCBlocks.WOODS.forEach((wood, reg) -> event.register(
            wood.isConifer() ?
                foliageColor :
                (state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, wood.autumnIndex()),
            reg.get(Wood.BlockType.LEAVES).get()));

        TFCBlocks.WOODS.forEach((wood, reg) -> event.register(
            wood.isConifer() ?
                blockColor(0xcf7d13) :
                (state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, wood.autumnIndex()),
            reg.get(Wood.BlockType.FALLEN_LEAVES).get()));

        event.register((state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.PINE.autumnIndex()), TFCBlocks.POTTED_PINE_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.DOUGLAS_FIR.autumnIndex()), TFCBlocks.POTTED_DOUGLAS_FIR_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.SPRUCE.autumnIndex()), TFCBlocks.POTTED_SPRUCE_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.WHITE_CEDAR.autumnIndex()), TFCBlocks.POTTED_WHITE_CEDAR_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.ASPEN.autumnIndex()), TFCBlocks.POTTED_ASPEN_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> state.getValue(KrummholzBlock.SNOWY) ? -1 : TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.PINE.autumnIndex()), TFCBlocks.PINE_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> state.getValue(KrummholzBlock.SNOWY) ? -1 : TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.DOUGLAS_FIR.autumnIndex()), TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> state.getValue(KrummholzBlock.SNOWY) ? -1 : TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.SPRUCE.autumnIndex()), TFCBlocks.SPRUCE_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> state.getValue(KrummholzBlock.SNOWY) ? -1 : TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.WHITE_CEDAR.autumnIndex()), TFCBlocks.WHITE_CEDAR_KRUMMHOLZ.get());
        event.register((state, level, pos, tintIndex) -> state.getValue(KrummholzBlock.SNOWY) ? -1 : TFCColors.getSeasonalFoliageColor(pos, tintIndex, Wood.ASPEN.autumnIndex()), TFCBlocks.ASPEN_KRUMMHOLZ.get());

        TFCBlocks.WILD_CROPS.forEach((crop, reg) -> event.register(grassColor, reg.get()));

        event.register((state, level, pos, tintIndex) -> TFCColors.getWaterColor(pos), TFCBlocks.SALT_WATER.get(), TFCBlocks.SEA_ICE.get(), TFCBlocks.RIVER_WATER.get(), TFCBlocks.CAULDRONS.get(FluidId.SALT_WATER).get());
        event.register(blockColor(0x5FB5B8), TFCBlocks.SPRING_WATER.get(), TFCBlocks.CAULDRONS.get(FluidId.SPRING_WATER).get());

        TFCBlocks.CAULDRONS.forEach((type, reg) -> type.color().ifPresent(color -> event.register(blockColor(color), reg.get())));
    }

    private static BlockColor blockColor(int color)
    {
        return (state, level, pos, tintIndex) -> color;
    }

    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event)
    {
        event.register(TFCColors.SALT_WATER);
        event.register(TFCColors.FRESH_WATER);
    }

    public static void registerColorHandlerItems(RegisterColorHandlersEvent.Item event)
    {
        final ItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);
        final ItemColor seasonalFoliageColor = (stack, tintIndex) -> TFCColors.getFoliageColor(null, tintIndex);

        TFCBlocks.PLANTS.forEach((plant, reg) -> {
            if (plant.isItemTinted())
                event.register(plant.isSeasonal() ? seasonalFoliageColor : grassColor, reg.get());
        });
        TFCBlocks.WOODS.forEach((key, value) -> event.register(seasonalFoliageColor, value.get(Wood.BlockType.FALLEN_LEAVES).get(), value.get(LEAVES).get()));
        TFCBlocks.WILD_CROPS.forEach((key, value) -> event.register(grassColor, value.get().asItem()));
        event.register(seasonalFoliageColor, TFCBlocks.PINE_KRUMMHOLZ.get().asItem());
        event.register(seasonalFoliageColor, TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ.get().asItem());
        event.register(seasonalFoliageColor, TFCBlocks.WHITE_CEDAR_KRUMMHOLZ.get().asItem());
        event.register(seasonalFoliageColor, TFCBlocks.SPRUCE_KRUMMHOLZ.get().asItem());
        event.register(seasonalFoliageColor, TFCBlocks.ASPEN_KRUMMHOLZ.get().asItem());

        for (Fluid fluid : BuiltInRegistries.FLUID)
        {
            if (Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(fluid)).getNamespace().equals(TerraFirmaCraft.MOD_ID))
            {
                event.register(new DynamicFluidContainerModel.Colors(), fluid.getBucket());
            }
        }

        TFCItems.MOLDS.values().forEach(reg -> event.register(new ContainedFluidModel.Colors(), reg.get()));
        event.register(new ContainedFluidModel.Colors(), TFCItems.WOODEN_BUCKET.get(), TFCItems.BELL_MOLD.get(), TFCItems.FIRE_INGOT_MOLD.get(), TFCItems.JUG.get(), TFCItems.SILICA_GLASS_BOTTLE.get(), TFCItems.HEMATITIC_GLASS_BOTTLE.get(), TFCItems.VOLCANIC_GLASS_BOTTLE.get(), TFCItems.OLIVINE_GLASS_BOTTLE.get());
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
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setTallGrassColors, TFCColors.TALL_GRASS_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageColors, TFCColors.FOLIAGE_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageFallColors, TFCColors.FOLIAGE_FALL_COLORS_LOCATION));
        event.registerReloadListener(new ColorMapReloadListener(TFCColors::setFoliageWinterColors, TFCColors.FOLIAGE_WINTER_COLORS_LOCATION));
    }

    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(TFCParticles.BUBBLE.get(), BubbleParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.WATER_FLOW.get(), WaterFlowParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.STEAM.get(), SteamParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.NITROGEN.get(), set -> new GlintParticleProvider(set, ChatFormatting.AQUA));
        event.registerSpriteSet(TFCParticles.PHOSPHORUS.get(), set -> new GlintParticleProvider(set, ChatFormatting.GOLD));
        event.registerSpriteSet(TFCParticles.POTASSIUM.get(), set -> new GlintParticleProvider(set, ChatFormatting.LIGHT_PURPLE));
        event.registerSpriteSet(TFCParticles.COMPOST_READY.get(), set -> new GlintParticleProvider(set, ChatFormatting.GRAY));
        event.registerSpriteSet(TFCParticles.COMPOST_ROTTEN.get(), set -> new GlintParticleProvider(set, ChatFormatting.DARK_RED));
        event.registerSpriteSet(TFCParticles.SLEEP.get(), SleepParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.LEAF.get(), set -> new LeafParticle.Provider(set, true));
        event.registerSpriteSet(TFCParticles.SNOWFLAKE.get(), FallingLeafParticle.SimpleProvider::new);
        event.registerSpriteSet(TFCParticles.FLYING_SNOWFLAKE.get(), WindParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.WIND.get(), WindParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.FALLING_LEAF.get(), set -> new FallingLeafParticle.Provider(set, true));
        event.registerSpriteSet(TFCParticles.FEATHER.get(), set -> new LeafParticle.Provider(set, false));
        event.registerSpriteSet(TFCParticles.SPARK.get(), SparkParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.BUTTERFLY.get(), AnimatedParticle.Provider::new);
        event.registerSpriteSet(TFCParticles.FLUID_DRIP.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidHangParticle::new));
        event.registerSpriteSet(TFCParticles.FLUID_FALL.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidFallAndLandParticle::new));
        event.registerSpriteSet(TFCParticles.FLUID_LAND.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidLandParticle::new));
        event.registerSpriteSet(TFCParticles.BARREL_DRIP.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.BarrelDripParticle::new));

        for (int i = 0; i < 5; i++)
        {
            final int lifetime = i * 80 + 10;
            event.registerSpriteSet(TFCParticles.SMOKES.get(i).get(), set -> new VariableHeightSmokeParticle.Provider(set, lifetime));
        }
    }

    public static void registerPresetEditors(RegisterPresetEditorsEvent event)
    {
        event.register(TerraFirmaCraft.PRESET, CreateTFCWorldScreen::new);
    }
}