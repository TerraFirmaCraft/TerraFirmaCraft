/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.dries007.tfc.client.model.ContainedFluidModel;
import net.dries007.tfc.client.model.entity.AlpacaModel;
import net.dries007.tfc.client.model.entity.BearModel;
import net.dries007.tfc.client.model.entity.BluegillModel;
import net.dries007.tfc.client.model.entity.BoarModel;
import net.dries007.tfc.client.model.entity.CougarModel;
import net.dries007.tfc.client.model.entity.DeerModel;
import net.dries007.tfc.client.model.entity.DirewolfModel;
import net.dries007.tfc.client.model.entity.DogModel;
import net.dries007.tfc.client.model.entity.DuckModel;
import net.dries007.tfc.client.model.entity.GrouseModel;
import net.dries007.tfc.client.model.entity.HorseshoeCrabModel;
import net.dries007.tfc.client.model.entity.IsopodModel;
import net.dries007.tfc.client.model.entity.JavelinModel;
import net.dries007.tfc.client.model.entity.JellyfishModel;
import net.dries007.tfc.client.model.entity.LionModel;
import net.dries007.tfc.client.model.entity.LobsterModel;
import net.dries007.tfc.client.model.entity.ManateeModel;
import net.dries007.tfc.client.model.entity.MooseModel;
import net.dries007.tfc.client.model.entity.MuskOxModel;
import net.dries007.tfc.client.model.entity.OrcaModel;
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
import net.dries007.tfc.client.model.entity.TurkeyModel;
import net.dries007.tfc.client.model.entity.YakModel;
import net.dries007.tfc.client.particle.AnimatedParticle;
import net.dries007.tfc.client.particle.BubbleParticle;
import net.dries007.tfc.client.particle.FluidDripParticle;
import net.dries007.tfc.client.particle.GlintParticleProvider;
import net.dries007.tfc.client.particle.LeafParticle;
import net.dries007.tfc.client.particle.SleepParticle;
import net.dries007.tfc.client.particle.SparkParticle;
import net.dries007.tfc.client.particle.SteamParticle;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.client.particle.VariableHeightSmokeParticle;
import net.dries007.tfc.client.render.blockentity.AnvilBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BarrelBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.BellowsBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.CrucibleBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.GrillBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.IngotPileBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.LoomBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.NextBoxBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PitKilnBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.PotBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.QuernBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.ScrapingBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.SheetPileBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.SluiceBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCBellBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCChestBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TFCSignBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.ToolRackBlockEntityRenderer;
import net.dries007.tfc.client.render.entity.AnimalRenderer;
import net.dries007.tfc.client.render.entity.DogRenderer;
import net.dries007.tfc.client.render.entity.GlowArrowRenderer;
import net.dries007.tfc.client.render.entity.OctopoteuthisRenderer;
import net.dries007.tfc.client.render.entity.OviparousRenderer;
import net.dries007.tfc.client.render.entity.PenguinRenderer;
import net.dries007.tfc.client.render.entity.RatRenderer;
import net.dries007.tfc.client.render.entity.SimpleMobRenderer;
import net.dries007.tfc.client.render.entity.TFCBoatRenderer;
import net.dries007.tfc.client.render.entity.TFCCatRenderer;
import net.dries007.tfc.client.render.entity.TFCChestedHorseRenderer;
import net.dries007.tfc.client.render.entity.TFCFishingHookRenderer;
import net.dries007.tfc.client.render.entity.TFCHorseRenderer;
import net.dries007.tfc.client.render.entity.TFCSquidRenderer;
import net.dries007.tfc.client.render.entity.ThrownJavelinRenderer;
import net.dries007.tfc.client.screen.AnvilPlanScreen;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.client.screen.BarrelScreen;
import net.dries007.tfc.client.screen.BlastFurnaceScreen;
import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.CharcoalForgeScreen;
import net.dries007.tfc.client.screen.ClimateScreen;
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
import net.dries007.tfc.client.screen.SmallVesselInventoryScreen;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ItemPropertyProviderBlock;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.fluids.FluidType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.accessor.BiomeColorsAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.common.blocks.wood.Wood.BlockType.*;

public final class ClientEventHandler
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::clientSetup);
        bus.addListener(ClientEventHandler::onConfigReload);
        bus.addListener(ClientEventHandler::registerModelLoaders);
        bus.addListener(ClientEventHandler::registerColorHandlerBlocks);
        bus.addListener(ClientEventHandler::registerColorHandlerItems);
        bus.addListener(ClientEventHandler::registerParticleFactories);
        bus.addListener(ClientEventHandler::registerClientReloadListeners);
        bus.addListener(ClientEventHandler::registerEntityRenderers);
        bus.addListener(ClientEventHandler::registerLayerDefinitions);
        bus.addListener(ClientEventHandler::onTextureStitch);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        // Screens
        event.enqueueWork(() -> {

            // Not thread-safe
            MenuScreens.register(TFCContainerTypes.CALENDAR.get(), CalendarScreen::new);
            MenuScreens.register(TFCContainerTypes.NUTRITION.get(), NutritionScreen::new);
            MenuScreens.register(TFCContainerTypes.CLIMATE.get(), ClimateScreen::new);
            MenuScreens.register(TFCContainerTypes.SALAD.get(), SaladScreen::new);
            MenuScreens.register(TFCContainerTypes.WORKBENCH.get(), CraftingScreen::new);

            MenuScreens.register(TFCContainerTypes.FIREPIT.get(), FirepitScreen::new);
            MenuScreens.register(TFCContainerTypes.GRILL.get(), GrillScreen::new);
            MenuScreens.register(TFCContainerTypes.POT.get(), PotScreen::new);
            MenuScreens.register(TFCContainerTypes.POWDERKEG.get(), PowderkegScreen::new);
            MenuScreens.register(TFCContainerTypes.CHARCOAL_FORGE.get(), CharcoalForgeScreen::new);
            MenuScreens.register(TFCContainerTypes.LOG_PILE.get(), LogPileScreen::new);
            MenuScreens.register(TFCContainerTypes.NEST_BOX.get(), NestBoxScreen::new);
            MenuScreens.register(TFCContainerTypes.CRUCIBLE.get(), CrucibleScreen::new);
            MenuScreens.register(TFCContainerTypes.BARREL.get(), BarrelScreen::new);
            MenuScreens.register(TFCContainerTypes.ANVIL.get(), AnvilScreen::new);
            MenuScreens.register(TFCContainerTypes.ANVIL_PLAN.get(), AnvilPlanScreen::new);
            MenuScreens.register(TFCContainerTypes.BLAST_FURNACE.get(), BlastFurnaceScreen::new);
            MenuScreens.register(TFCContainerTypes.CHEST_9x2.get(), ContainerScreen::new);
            MenuScreens.register(TFCContainerTypes.CHEST_9x4.get(), ContainerScreen::new);

            MenuScreens.register(TFCContainerTypes.CLAY_KNAPPING.get(), KnappingScreen::new);
            MenuScreens.register(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), KnappingScreen::new);
            MenuScreens.register(TFCContainerTypes.LEATHER_KNAPPING.get(), KnappingScreen::new);
            MenuScreens.register(TFCContainerTypes.ROCK_KNAPPING.get(), KnappingScreen::new);
            MenuScreens.register(TFCContainerTypes.SMALL_VESSEL_INVENTORY.get(), SmallVesselInventoryScreen::new);
            MenuScreens.register(TFCContainerTypes.MOLD_LIKE_ALLOY.get(), MoldLikeAlloyScreen::new);
            MenuScreens.register(TFCContainerTypes.LARGE_VESSEL.get(), LargeVesselScreen::new);
            MenuScreens.register(TFCContainerTypes.SCRIBING_TABLE.get(), ScribingTableScreen::new);

            for (Metal.Default metal : Metal.Default.values())
            {
                if (metal.hasTools())
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
                    ItemProperties.register(shield, new ResourceLocation("blocking"), (stack, level, entity, unused) -> {
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

            ItemProperties.register(TFCItems.FILLED_PAN.get(), Helpers.identifier("stage"), (stack, level, entity, unused) -> {
                if (entity instanceof Player player && player.isUsingItem() && stack == player.getMainHandItem())
                {
                    return (float) player.getUseItemRemainingTicks() / PanItem.USE_TIME;
                }
                return 1F;
            });

            ItemProperties.register(TFCItems.FILLED_PAN.get(), OreDeposit.ROCK_PROPERTY.id(), (stack, level, entity, unused) -> {
                final BlockState state = PanItem.readState(stack);
                return state != null ? ItemPropertyProviderBlock.getValue(state.getBlock(), OreDeposit.ROCK_PROPERTY) : 0f;
            });

            ItemProperties.register(TFCItems.FILLED_PAN.get(), OreDeposit.ORE_PROPERTY.id(), (stack, level, entity, unused) -> {
                final BlockState state = PanItem.readState(stack);
                return state != null ? ItemPropertyProviderBlock.getValue(state.getBlock(), OreDeposit.ORE_PROPERTY) : 0F;
            });

            ItemProperties.register(TFCItems.HANDSTONE.get(), Helpers.identifier("damaged"), (stack, level, entity, unused) -> stack.getDamageValue() > stack.getMaxDamage() - 10 ? 1F : 0F);

            TFCBlocks.WOODS.values().forEach(map -> ItemProperties.register(map.get(BARREL).get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));

            ItemProperties.register(TFCBlocks.POWDERKEG.get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f);

            Stream.of(TFCBlocks.LARGE_VESSEL, TFCBlocks.GLAZED_LARGE_VESSELS.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten).forEach(vessel -> ItemProperties.register(vessel.get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));

            ItemProperties.register(TFCBlocks.LIGHT.get().asItem(), new ResourceLocation("level"), (stack, level, entity, unused) -> {
                CompoundTag stackTag = stack.getTag();
                if (stackTag != null && stackTag.contains("level", Tag.TAG_INT))
                {
                    return stackTag.getInt("level") / 16F;
                }
                return 1.0F;
            });

            TFCBlocks.WOODS.values().forEach(map -> ItemProperties.register(map.get(BARREL).get().asItem(), Helpers.identifier("sealed"), (stack, level, entity, unused) -> stack.hasTag() ? 1.0f : 0f));
        });

        // Keybindings
        ClientRegistry.registerKeyBinding(TFCKeyBindings.PLACE_BLOCK);
        ClientRegistry.registerKeyBinding(TFCKeyBindings.CYCLE_CHISEL_MODE);
        ClientRegistry.registerKeyBinding(TFCKeyBindings.STACK_FOOD);

        // Render Types
        final RenderType solid = RenderType.solid();
        final RenderType cutout = RenderType.cutout();
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType translucent = RenderType.translucent();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().forEach(map -> {
            ItemBlockRenderTypes.setRenderLayer(map.get(Rock.BlockType.SPIKE).get(), cutout);
            ItemBlockRenderTypes.setRenderLayer(map.get(Rock.BlockType.AQUEDUCT).get(), cutout);
        });
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout))));
        TFCBlocks.ORE_DEPOSITS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));

        // Wood blocks
        TFCBlocks.WOODS.values().forEach(map -> {
            Stream.of(SAPLING, DOOR, TRAPDOOR, FENCE, FENCE_GATE, BUTTON, PRESSURE_PLATE, SLAB, STAIRS, TWIG, BARREL, SCRIBING_TABLE, POTTED_SAPLING).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), cutout));
            Stream.of(LEAVES, FALLEN_LEAVES).forEach(type -> ItemBlockRenderTypes.setRenderLayer(map.get(type).get(), layer -> Minecraft.useFancyGraphics() ? layer == cutoutMipped : layer == solid));
        });

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutoutMipped));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.PEAT_GRASS.get(), cutoutMipped);

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout)));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.STEEL_BARS.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.RED_STEEL_BARS.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BLUE_STEEL_BARS.get(), cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.BLACK_STEEL_BARS.get(), cutoutMipped);

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.CALCITE.get(), cutout);

        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.ICICLE.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SEA_ICE.get(), cutout);

        // Plants
        TFCBlocks.CROPS.values().forEach(reg -> ItemBlockRenderTypes.setRenderLayer(reg.get(), cutout));
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
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.FIREPIT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WALL_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.DEAD_WALL_TORCH.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.WATTLE.get(), cutout);
        TFCBlocks.STAINED_WATTLE.values().forEach(wattle -> ItemBlockRenderTypes.setRenderLayer(wattle.get(), cutout));
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.UNSTAINED_WATTLE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.SHEET_PILE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TFCBlocks.INGOT_PILE.get(), cutout);

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

        IngameOverlays.reloadOverlays();
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        event.registerEntityRenderer(TFCEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(TFCEntities.FISHING_BOBBER.get(), TFCFishingHookRenderer::new);
        event.registerEntityRenderer(TFCEntities.THROWN_JAVELIN.get(), ThrownJavelinRenderer::new);
        event.registerEntityRenderer(TFCEntities.GLOW_ARROW.get(), GlowArrowRenderer::new);
        event.registerEntityRenderer(TFCEntities.SEAT.get(), NoopRenderer::new);
        event.registerEntityRenderer(TFCEntities.CHEST_MINECART.get(), ctx -> new MinecartRenderer<>(ctx, RenderHelpers.modelIdentifier("chest_minecart")));
        event.registerEntityRenderer(TFCEntities.HOLDING_MINECART.get(), ctx -> new MinecartRenderer<>(ctx, RenderHelpers.modelIdentifier("holding_minecart")));
        for (Wood wood : Wood.VALUES)
        {
            event.registerEntityRenderer(TFCEntities.BOATS.get(wood).get(), ctx -> new TFCBoatRenderer(ctx, wood.getSerializedName()));
        }
        event.registerEntityRenderer(TFCEntities.COD.get(), CodRenderer::new);
        event.registerEntityRenderer(TFCEntities.SALMON.get(), SalmonRenderer::new);
        event.registerEntityRenderer(TFCEntities.TROPICAL_FISH.get(), TropicalFishRenderer::new);
        event.registerEntityRenderer(TFCEntities.PUFFERFISH.get(), PufferfishRenderer::new);
        event.registerEntityRenderer(TFCEntities.BLUEGILL.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BluegillModel::new, "bluegill").flops().build());
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
        event.registerEntityRenderer(TFCEntities.POLAR_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "polar_bear").shadow(0.9f).scale(1.3f).build());
        event.registerEntityRenderer(TFCEntities.GRIZZLY_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "grizzly_bear").shadow(0.9f).scale(1.1f).build());
        event.registerEntityRenderer(TFCEntities.BLACK_BEAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BearModel::new, "black_bear").shadow(0.9f).scale(0.9f).build());
        event.registerEntityRenderer(TFCEntities.COUGAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CougarModel::new, "cougar").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.PANTHER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, CougarModel::new, "panther").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.LION.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, LionModel::new, "lion").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.SABERTOOTH.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, SabertoothModel::new, "sabertooth").shadow(0.8f).build());
        event.registerEntityRenderer(TFCEntities.WOLF.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TFCWolfModel::new, "wolf").shadow(0.5f).scale(1.1f).texture(p -> TFCWolfModel.WOLF_LOCATION).build());
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
        event.registerEntityRenderer(TFCEntities.RABBIT.get(), RabbitRenderer::new);
        event.registerEntityRenderer(TFCEntities.FOX.get(), FoxRenderer::new);
        event.registerEntityRenderer(TFCEntities.PANDA.get(), PandaRenderer::new);
        event.registerEntityRenderer(TFCEntities.OCELOT.get(), OcelotRenderer::new);
        event.registerEntityRenderer(TFCEntities.BOAR.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, BoarModel::new, "boar").build());
        event.registerEntityRenderer(TFCEntities.DEER.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, DeerModel::new, "deer").shadow(0.6f).hasBabyTexture().build());
        event.registerEntityRenderer(TFCEntities.MOOSE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, MooseModel::new, "moose").shadow(1.0f).scale(0.8f).build());
        event.registerEntityRenderer(TFCEntities.GROUSE.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, GrouseModel::new, "grouse").shadow(0.5f).texture(e -> Helpers.getGenderedTexture(e, "grouse")).build());
        event.registerEntityRenderer(TFCEntities.PHEASANT.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, PheasantModel::new, "pheasant").shadow(0.5f).texture(e -> Helpers.getGenderedTexture(e, "pheasant")).build());
        event.registerEntityRenderer(TFCEntities.TURKEY.get(), ctx -> new SimpleMobRenderer.Builder<>(ctx, TurkeyModel::new, "turkey").shadow(0.5f).texture(e -> Helpers.getGenderedTexture(e, "turkey")).build());
        event.registerEntityRenderer(TFCEntities.MULE.get(), ctx -> new TFCChestedHorseRenderer<>(ctx, 0.92F, ModelLayers.MULE, "mule"));
        event.registerEntityRenderer(TFCEntities.DONKEY.get(), ctx -> new TFCChestedHorseRenderer<>(ctx, 0.87F, ModelLayers.DONKEY, "donkey"));
        event.registerEntityRenderer(TFCEntities.HORSE.get(), TFCHorseRenderer::new);
        event.registerEntityRenderer(TFCEntities.RAT.get(), RatRenderer::new);
        event.registerEntityRenderer(TFCEntities.CAT.get(), TFCCatRenderer::new);
        event.registerEntityRenderer(TFCEntities.DOG.get(), DogRenderer::new);

        // BEs
        event.registerBlockEntityRenderer(TFCBlockEntities.POT.get(), ctx -> new PotBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.GRILL.get(), ctx -> new GrillBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.PLACED_ITEM.get(), ctx -> new PlacedItemBlockEntityRenderer<>());
        event.registerBlockEntityRenderer(TFCBlockEntities.PIT_KILN.get(), ctx -> new PitKilnBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.QUERN.get(), ctx -> new QuernBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SCRAPING.get(), ctx -> new ScrapingBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.CHEST.get(), TFCChestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.TRAPPED_CHEST.get(), TFCChestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.LOOM.get(), ctx -> new LoomBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SLUICE.get(), ctx -> new SluiceBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BELLOWS.get(), ctx -> new BellowsBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.TOOL_RACK.get(), ctx -> new ToolRackBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SIGN.get(), TFCSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.BARREL.get(), ctx -> new BarrelBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.CRUCIBLE.get(), ctx -> new CrucibleBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.LECTERN.get(), LecternRenderer::new);
        event.registerBlockEntityRenderer(TFCBlockEntities.ANVIL.get(), ctx -> new AnvilBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.SHEET_PILE.get(), ctx -> new SheetPileBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.INGOT_PILE.get(), ctx -> new IngotPileBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.NEST_BOX.get(), ctx -> new NextBoxBlockEntityRenderer());
        event.registerBlockEntityRenderer(TFCBlockEntities.BELL.get(), TFCBellBlockEntityRenderer::new);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        LayerDefinition boatLayer = BoatModel.createBodyModel();
        LayerDefinition signLayer = SignRenderer.createSignLayer();
        for (Wood wood : Wood.VALUES)
        {
            event.registerLayerDefinition(TFCBoatRenderer.boatName(wood.getSerializedName()), () -> boatLayer);
            event.registerLayerDefinition(RenderHelpers.modelIdentifier("sign/" + wood.name().toLowerCase(Locale.ROOT)), () -> signLayer);
        }
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("bluegill"), BluegillModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("jellyfish"), JellyfishModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("lobster"), LobsterModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("crayfish"), LobsterModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("horseshoe_crab"), HorseshoeCrabModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("isopod"), IsopodModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("orca"), OrcaModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("manatee"), ManateeModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("turtle"), TFCTurtleModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("penguin"), PenguinModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("polar_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("grizzly_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("black_bear"), BearModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("cougar"), CougarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("panther"), CougarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("lion"), LionModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("sabertooth"), SabertoothModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("wolf"), TFCWolfModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("direwolf"), DirewolfModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("squid"), SquidModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("glow_squid"), SquidModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("pig"), () -> TFCPigModel.createTFCBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("cow"), TFCCowModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("goat"), GoatModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("yak"), YakModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("alpaca"), AlpacaModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("sheep"), TFCSheepModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("musk_ox"), MuskOxModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("chicken"), TFCChickenModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("duck"), DuckModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("quail"), QuailModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("deer"), DeerModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("moose"), MooseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("grouse"), GrouseModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("pheasant"), PheasantModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("turkey"), TurkeyModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("rat"), RatModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("dog"), DogModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("cat"), () -> LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32));
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("cat_collar"), () -> LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01f)), 64, 32));
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("boar"), BoarModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("javelin"), JavelinModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("chest_minecart"), MinecartModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("holding_minecart"), MinecartModel::createBodyLayer);
        event.registerLayerDefinition(RenderHelpers.modelIdentifier("bell_body"), BellRenderer::createBodyLayer);
    }

    public static void onConfigReload(ModConfigEvent.Reloading event)
    {
        IngameOverlays.reloadOverlays();
    }

    public static void registerModelLoaders(ModelRegistryEvent event)
    {
        ModelLoaderRegistry.registerLoader(Helpers.identifier("contained_fluid"), new ContainedFluidModel.Loader());
    }

    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        final BlockColors registry = event.getBlockColors();
        final BlockColor grassColor = (state, level, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex);
        final BlockColor tallGrassColor = (state, level, pos, tintIndex) -> TFCColors.getTallGrassColor(pos, tintIndex);
        final BlockColor foliageColor = (state, level, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex);
        final BlockColor seasonalFoliageColor = (state, level, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(pos, tintIndex);

        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> registry.register(grassColor, reg.get()));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> registry.register(grassColor, reg.get()));
        registry.register(grassColor, TFCBlocks.PEAT_GRASS.get());

        TFCBlocks.PLANTS.forEach((plant, reg) -> registry.register(plant.isTallGrass() ? tallGrassColor : plant.isSeasonal() ? seasonalFoliageColor : plant.isFoliage() ? foliageColor : grassColor, reg.get()));
        TFCBlocks.POTTED_PLANTS.forEach((plant, reg) -> registry.register(grassColor, reg.get()));
        TFCBlocks.WOODS.forEach((wood, reg) -> registry.register(wood.isConifer() ? foliageColor : seasonalFoliageColor, reg.get(Wood.BlockType.LEAVES).get(), reg.get(Wood.BlockType.FALLEN_LEAVES).get()));
        TFCBlocks.WILD_CROPS.forEach((crop, reg) -> registry.register(grassColor, reg.get()));

        registry.register((state, level, pos, tintIndex) -> TFCColors.getWaterColor(pos), TFCBlocks.SALT_WATER.get(), TFCBlocks.SEA_ICE.get(), TFCBlocks.RIVER_WATER.get(), TFCBlocks.CAULDRONS.get(FluidType.SALT_WATER).get());
        registry.register(blockColor(0x5FB5B8), TFCBlocks.SPRING_WATER.get(), TFCBlocks.CAULDRONS.get(FluidType.SPRING_WATER).get());

        TFCBlocks.CAULDRONS.forEach((type, reg) -> type.color().ifPresent(color -> registry.register(blockColor(color), reg.get())));
    }

    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        final ItemColors registry = event.getItemColors();
        final ItemColor grassColor = (stack, tintIndex) -> TFCColors.getGrassColor(null, tintIndex);
        final ItemColor seasonalFoliageColor = (stack, tintIndex) -> TFCColors.getFoliageColor(null, tintIndex);

        TFCBlocks.PLANTS.forEach((plant, reg) -> {
            if (plant.isItemTinted())
                registry.register(plant.isSeasonal() ? seasonalFoliageColor : grassColor, reg.get());
        });
        TFCBlocks.WOODS.forEach((key, value) -> registry.register(seasonalFoliageColor, value.get(Wood.BlockType.FALLEN_LEAVES).get(), value.get(LEAVES).get()));
        TFCBlocks.WILD_CROPS.forEach((key, value) -> registry.register(grassColor, value.get().asItem()));
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

    public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(TFCParticles.BUBBLE.get(), BubbleParticle.Provider::new);
        particleEngine.register(TFCParticles.STEAM.get(), SteamParticle.Provider::new);
        particleEngine.register(TFCParticles.NITROGEN.get(), set -> new GlintParticleProvider(set, ChatFormatting.AQUA));
        particleEngine.register(TFCParticles.PHOSPHORUS.get(), set -> new GlintParticleProvider(set, ChatFormatting.GOLD));
        particleEngine.register(TFCParticles.POTASSIUM.get(), set -> new GlintParticleProvider(set, ChatFormatting.LIGHT_PURPLE));
        particleEngine.register(TFCParticles.COMPOST_READY.get(), set -> new GlintParticleProvider(set, ChatFormatting.GRAY));
        particleEngine.register(TFCParticles.COMPOST_ROTTEN.get(), set -> new GlintParticleProvider(set, ChatFormatting.DARK_RED));
        particleEngine.register(TFCParticles.SLEEP.get(), SleepParticle.Provider::new);
        particleEngine.register(TFCParticles.LEAF.get(), set -> new LeafParticle.Provider(set, true));
        particleEngine.register(TFCParticles.FEATHER.get(), set -> new LeafParticle.Provider(set, false));
        particleEngine.register(TFCParticles.SPARK.get(), SparkParticle.Provider::new);
        particleEngine.register(TFCParticles.BUTTERFLY.get(), AnimatedParticle.Provider::new);
        particleEngine.register(TFCParticles.FLUID_DRIP.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidHangParticle::new));
        particleEngine.register(TFCParticles.FLUID_FALL.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidFallAndLandParticle::new));
        particleEngine.register(TFCParticles.FLUID_LAND.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.FluidLandParticle::new));
        particleEngine.register(TFCParticles.BARREL_DRIP.get(), set -> FluidDripParticle.provider(set, FluidDripParticle.BarrelDripParticle::new));

        for (int i = 0; i < 5; i++)
        {
            final int lifetime = i * 80 + 10;
            particleEngine.register(TFCParticles.SMOKES.get(i).get(), set -> new VariableHeightSmokeParticle.Provider(set, lifetime));
        }
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        final ResourceLocation sheet = event.getAtlas().location();
        if (sheet.equals(RenderHelpers.BLOCKS_ATLAS))
        {
            event.addSprite(Helpers.identifier("block/burlap"));
            event.addSprite(Helpers.identifier("block/unrefined_paper"));
            event.addSprite(Helpers.identifier("block/paper"));
            event.addSprite(Helpers.identifier("block/devices/bellows/back"));
            event.addSprite(Helpers.identifier("block/devices/bellows/side"));
            event.addSprite(Helpers.identifier("entity/bell/bronze"));
            event.addSprite(Helpers.identifier("entity/bell/brass"));

            for (Metal.Default metal : Metal.Default.values())
            {
                event.addSprite(Helpers.identifier("block/metal/full/" + metal.getSerializedName()));
            }
            for (String texture : TFCConfig.CLIENT.additionalMetalSheetTextures.get())
            {
                event.addSprite(new ResourceLocation(texture));
            }
        }
        else if (sheet.equals(Sheets.CHEST_SHEET))
        {
            Arrays.stream(Wood.VALUES).map(Wood::getSerializedName).forEach(name -> {
                event.addSprite(Helpers.identifier("entity/chest/normal/" + name));
                event.addSprite(Helpers.identifier("entity/chest/normal_left/" + name));
                event.addSprite(Helpers.identifier("entity/chest/normal_right/" + name));
                event.addSprite(Helpers.identifier("entity/chest/trapped/" + name));
                event.addSprite(Helpers.identifier("entity/chest/trapped_left/" + name));
                event.addSprite(Helpers.identifier("entity/chest/trapped_right/" + name));
            });
        }
        else if (sheet.equals(Sheets.SIGN_SHEET))
        {
            Arrays.stream(Wood.VALUES).map(Wood::getSerializedName).forEach(name -> event.addSprite(Helpers.identifier("entity/signs/" + name)));
        }
    }

    private static BlockColor blockColor(int color)
    {
        return (state, level, pos, tintIndex) -> color;
    }
}