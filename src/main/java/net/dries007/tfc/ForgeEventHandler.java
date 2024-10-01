/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import com.mojang.logging.LogUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.block.CreateFluidSourceEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blockentities.BowlBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.LampBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCCandleBlock;
import net.dries007.tfc.common.blocks.TFCCandleCakeBlock;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.blocks.rock.AqueductBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.blocks.wood.TFCLecternBlock;
import net.dries007.tfc.common.commands.TFCCommands;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.glass.GlassWorking;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.common.container.PestContainer;
import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.misc.HoldingMinecart;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.BlowpipeItem;
import net.dries007.tfc.common.items.TFCShieldItem;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.RecipeManagerAccessor;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.network.EffectExpirePacket;
import net.dries007.tfc.network.PlayerDrinkPacket;
import net.dries007.tfc.network.UpdateClimateModelPacket;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.PhysicalDamageType;
import net.dries007.tfc.util.SelfTests;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManagers;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.util.data.Fertilizer;
import net.dries007.tfc.util.data.FluidHeat;
import net.dries007.tfc.util.data.Support;
import net.dries007.tfc.util.events.DouseFireEvent;
import net.dries007.tfc.util.events.LoggingEvent;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.network.RotationNetworkManager;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;


public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final BlockHitResult FAKE_MISS = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);

    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(ForgeEventHandler::onCreateWorldSpawn);
        bus.addListener(ForgeEventHandler::onChunkWatch);
        bus.addListener(EventPriority.LOW, ForgeEventHandler::registerCommands); // Must come after neo, since we take over `/neoforge day`
        bus.addListener(ForgeEventHandler::onBlockBroken);
        bus.addListener(ForgeEventHandler::onBlockPlace);
        bus.addListener(ForgeEventHandler::onBreakSpeed);
        bus.addListener(ForgeEventHandler::onNeighborUpdate);
        bus.addListener(ForgeEventHandler::onExplosionDetonate);
        bus.addListener(ForgeEventHandler::onWorldTick);
        bus.addListener(ForgeEventHandler::onWorldLoad);
        bus.addListener(ForgeEventHandler::onCreateNetherPortal);
        bus.addListener(ForgeEventHandler::onFluidPlaceBlock);
        bus.addListener(ForgeEventHandler::onFluidCreateSource);
        bus.addListener(ForgeEventHandler::onFireStart);
        bus.addListener(ForgeEventHandler::onFireStop);
        bus.addListener(ForgeEventHandler::onProjectileImpact);
        bus.addListener(ForgeEventHandler::onPlayerTick);
        bus.addListener(ForgeEventHandler::onEffectRemove);
        bus.addListener(ForgeEventHandler::onEffectExpire);
        bus.addListener(ForgeEventHandler::onLivingJump);
        bus.addListener(ForgeEventHandler::onLivingHurt);
        bus.addListener(ForgeEventHandler::onShieldBlock);
        bus.addListener(ForgeEventHandler::onLivingSpawnCheck);
        bus.addListener(ForgeEventHandler::onItemStacked);
        bus.addListener(ForgeEventHandler::onEntityJoinLevel);
        bus.addListener(ForgeEventHandler::onItemExpire);
        bus.addListener(ForgeEventHandler::onPlayerLoggedIn);
        bus.addListener(ForgeEventHandler::onPlayerRespawn);
        bus.addListener(ForgeEventHandler::onPlayerDeath);
        bus.addListener(ForgeEventHandler::onPlayerChangeDimension);
        bus.addListener(ForgeEventHandler::onServerChat);
        bus.addListener(ForgeEventHandler::onPlayerRightClickBlock);
        bus.addListener(EventPriority.LOWEST, true, ForgeEventHandler::onPlayerRightClickBlockLowestPriority);
        bus.addListener(ForgeEventHandler::onPlayerRightClickItem);
        bus.addListener(ForgeEventHandler::onPlayerRightClickEmpty);
        bus.addListener(ForgeEventHandler::onUseItemOnBlock);
        bus.addListener(ForgeEventHandler::onItemUseFinish);
        bus.addListener(ForgeEventHandler::addReloadListeners);
        bus.addListener(ForgeEventHandler::onDataPackSync);
        bus.addListener(ForgeEventHandler::onTagsUpdated);
        bus.addListener(ForgeEventHandler::onBoneMeal);
        bus.addListener(EventPriority.HIGHEST, ForgeEventHandler::onSelectClimateModel);
        bus.addListener(ForgeEventHandler::onAnimalTame);
        bus.addListener(ForgeEventHandler::onContainerOpen);
        bus.addListener(ForgeEventHandler::onCropsGrow);
        bus.addListener(ForgeEventHandler::onMount);
        bus.addListener(ForgeEventHandler::onEntityInteract);
    }

    /**
     * Duplicates logic from {@link MinecraftServer#
     * setInitialSpawn(ServerLevel, ServerLevelData, boolean, boolean)} as that version only asks the dimension for the sea level...
     */
    public static void onCreateWorldSpawn(LevelEvent.CreateSpawnPosition event)
    {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension extension)
        {
            final ChunkGenerator generator = extension.self();
            final ServerLevelData levelData = event.getSettings();
            final RandomSource random = new XoroshiroRandomSource(level.getSeed());
            final ChunkPos chunkPos = new ChunkPos(extension.findSpawnBiome(random));

            levelData.setSpawn(chunkPos.getWorldPosition().offset(8, generator.getSpawnHeight(level), 8), 0.0F);
            boolean foundExactSpawn = false;
            int x = 0, z = 0;
            int xStep = 0;
            int zStep = -1;

            for (int tries = 0; tries < 1024; ++tries)
            {
                if (x > -16 && x <= 16 && z > -16 && z <= 16)
                {
                    final BlockPos spawnPos = PlayerRespawnLogic.getSpawnPosInChunk(level, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                    if (spawnPos != null)
                    {
                        levelData.setSpawn(spawnPos, 0);
                        foundExactSpawn = true;
                        break;
                    }
                }

                if ((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z))
                {
                    final int swap = xStep;
                    xStep = -zStep;
                    zStep = swap;
                }

                x += xStep;
                z += zStep;
            }

            if (!foundExactSpawn)
            {
                LOGGER.warn("Unable to find a suitable spawn location!");
            }

            if (level.getServer().getWorldData().worldGenOptions().generateBonusChest())
            {
                LOGGER.warn("No bonus chest for you, you cheaty cheater!");
            }

            event.setCanceled(true);
        }
    }

    public static void onChunkWatch(ChunkWatchEvent.Sent event)
    {
        // When we watch a chunk, the chunk data should already be generated on server, and have FULL status, (with a TFC chunk generator)
        // We then sync the data on these chunks to client directly
        final ChunkData chunkData = ChunkData.get(event.getChunk());
        if (chunkData.status() == ChunkData.Status.FULL)
        {
            PacketDistributor.sendToPlayer(event.getPlayer(), chunkData.getUpdatePacket());
        }
    }

    public static void registerCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.registerCommands(event.getDispatcher(), event.getBuildContext());
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Trigger a collapse
        final LevelAccessor levelAccess = event.getLevel();
        final BlockPos pos = event.getPos();
        final BlockState state = levelAccess.getBlockState(pos);

        if (Helpers.isBlock(state, TFCTags.Blocks.CAN_TRIGGER_COLLAPSE) && levelAccess instanceof Level level)
        {
            CollapseRecipe.tryTriggerCollapse(level, pos);
            return;
        }

        // Chop down a tree
        final ItemStack stack = event.getPlayer().getMainHandItem();
        if (AxeLoggingHelper.shouldLog(levelAccess, pos, state, stack) &&
            !NeoForge.EVENT_BUS.post(new LoggingEvent(levelAccess, pos, state, stack)).isCanceled())
        {
            event.setCanceled(true); // Cancel regardless of outcome of logging
            AxeLoggingHelper.doLogging(levelAccess, pos, event.getPlayer(), stack);
        }
    }

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if (event.getLevel() instanceof final ServerLevel world)
        {
            final BlockPos pos = event.getPos();
            final BlockState state = event.getState();

            if (LandslideRecipe.canLandslide(state))
            {
                WorldTracker.get(world).addLandslidePos(pos);
            }

            if (Helpers.isBlock(state, TFCTags.Blocks.BREAKS_WHEN_ISOLATED))
            {
                WorldTracker.get(world).addIsolatedPos(pos);
            }
        }
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        // Apply mining speed modifiers from forging bonuses
        final ForgingBonus bonus = ForgingBonus.get(event.getEntity().getMainHandItem());
        if (bonus != ForgingBonus.NONE)
        {
            event.setNewSpeed(event.getNewSpeed() * bonus.efficiency());
        }
    }

    public static void onNeighborUpdate(BlockEvent.NeighborNotifyEvent event)
    {
        if (event.getLevel() instanceof final ServerLevel level)
        {
            for (Direction direction : event.getNotifiedSides())
            {
                // Check each notified block for a potential gravity block
                final BlockPos pos = event.getPos().relative(direction);
                final BlockState state = level.getBlockState(pos);

                if (LandslideRecipe.canLandslide(state))
                {
                    WorldTracker.get(level).addLandslidePos(pos);
                }

                if (Helpers.isBlock(state.getBlock(), TFCTags.Blocks.BREAKS_WHEN_ISOLATED))
                {
                    WorldTracker.get(level).addIsolatedPos(pos);
                }
            }
        }
    }

    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        final Level level = event.getLevel();
        if (!level.isClientSide)
        {
            WorldTracker.get(level).addCollapsePositions(BlockPos.containing(event.getExplosion().center()), event.getExplosion().getToBlow());
        }
    }

    public static void onWorldTick(LevelTickEvent.Pre event)
    {
        if (event.getLevel() instanceof ServerLevel level)
        {
            WorldTracker.get(level).tick();
        }
    }

    public static void onWorldLoad(LevelEvent.Load event)
    {
        if (event.getLevel() instanceof final ServerLevel level)
        {
            final MinecraftServer server = level.getServer();
            final GameRules rules = level.getGameRules();

            if (TFCConfig.SERVER.enableForcedTFCGameRules.get())
            {
                rules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, server);
                rules.getRule(GameRules.RULE_DOINSOMNIA).set(false, server);
                rules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
                rules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);

                LOGGER.info("Updating TFC Relevant Game Rules for level {}.", level.dimension().location());
            }

            // This one is non-negotiable, it's required in order for the calendar to function
            rules.getRule(GameRules.RULE_DAYLIGHT).set(false, server);

            Climate.chooseModelForWorld(level);

            if (level.dimension() == Level.OVERWORLD)
            {
                SelfTests.runServerSelfTests(level.getServer());
            }
        }
    }

    public static void onCreateNetherPortal(BlockEvent.PortalSpawnEvent event)
    {
        if (!TFCConfig.SERVER.enableNetherPortals.get())
        {
            event.setCanceled(true);
        }
    }

    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        // Currently, getOriginalState gets the fluid block that's placing the block, not the block getting placed
        BlockState state = event.getNewState();
        if (Helpers.isBlock(state, Blocks.STONE))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.GABBRO).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (Helpers.isBlock(state, Blocks.COBBLESTONE))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.RHYOLITE).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (Helpers.isBlock(state, Blocks.BASALT))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
    }

    public static void onFluidCreateSource(CreateFluidSourceEvent event)
    {
        final LevelReader level = event.getLevel();
        final BlockPos pos = event.getPos();
        final BlockState state = event.getState();

        if (state.getBlock() instanceof AqueductBlock)
        {
            event.setCanConvert(false); // Waterlogged aqueducts do not count as the source when creating source blocks
        }

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            final BlockPos relPos = pos.relative(direction).above();
            final BlockState relState = level.getBlockState(relPos);
            if (relState.getBlock() instanceof SluiceBlock && !relState.getValue(SluiceBlock.UPPER) && relState.getValue(SluiceBlock.FACING) == direction.getOpposite())
            {
                event.setCanConvert(false); // This block might be being fed by a sluice - so don't allow it to create more source blocks.
            }
        }
    }

    public static void onFireStart(StartFireEvent event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if ((block == TFCBlocks.FIREPIT.get() || block == TFCBlocks.POT.get() || block == TFCBlocks.GRILL.get()) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof AbstractFirepitBlockEntity<?> firepit && firepit.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.TORCH.get() || block == TFCBlocks.WALL_TORCH.get())
        {
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().defaultBlockState());
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_WALL_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().withPropertiesOf(state));
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.LOG_PILE.get() && event.isStrong())
        {
            BurningLogPileBlock.lightLogPile(level, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.PIT_KILN.get() && state.getValue(PitKilnBlock.STAGE) == 15 && event.isStrong())
        {
            if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln && kiln.tryLight())
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.CHARCOAL_PILE.get() && state.getValue(CharcoalPileBlock.LAYERS) >= 7 && CharcoalForgeBlock.isValid(level, pos) && event.isStrong())
        {
            CharcoalForgeBlockEntity.createFromCharcoalPile(level, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.CHARCOAL_FORGE.get() && CharcoalForgeBlock.isValid(level, pos) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CharcoalForgeBlockEntity forge && forge.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.CRUCIBLE.get() && CharcoalForgeBlock.isValid(level, pos.below()) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos.below());
            if (entity instanceof CharcoalForgeBlockEntity forge && forge.light(level.getBlockState(pos.below())))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.BLOOMERY.get() && !state.getValue(BloomeryBlock.LIT) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BloomeryBlockEntity bloomery && bloomery.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.POWDERKEG.get() && state.getValue(PowderkegBlock.SEALED) && event.isStrong())
        {
            level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).ifPresent(entity -> {
                entity.setLit(true, event.getPlayer());
                event.setCanceled(true);
            });
        }
        else if (block == TFCBlocks.BLAST_FURNACE.get() && !state.getValue(BlastFurnaceBlock.LIT) && event.isStrong())
        {
            level.getBlockEntity(pos, TFCBlockEntities.BLAST_FURNACE.get()).ifPresent(blastFurnace -> {
                if (blastFurnace.light(level, pos, state))
                {
                    event.setCanceled(true);
                }
            });
        }
        else if (block instanceof LampBlock)
        {
            if (!state.getValue(LampBlock.LIT))
            {
                level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).ifPresent(lamp -> {
                    if (lamp.getFuel() != null && !state.getValue(LampBlock.LIT))
                    {
                        level.setBlock(pos, state.setValue(LampBlock.LIT, true), 3);
                        lamp.resetCounter();
                    }
                });
                event.setCanceled(true);
            }
        }
        else if (block instanceof TFCCandleBlock || block instanceof TFCCandleCakeBlock)
        {
            level.setBlock(pos, state.setValue(TFCCandleBlock.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
            TickCounterBlockEntity.reset(level, pos);
            event.setCanceled(true);
        }
        else if (block == Blocks.CARVED_PUMPKIN || block == TFCBlocks.JACK_O_LANTERN.get())
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperty(TFCBlocks.JACK_O_LANTERN.get().defaultBlockState(), state, HorizontalDirectionalBlock.FACING));
            TickCounterBlockEntity.reset(level, pos);
            event.setCanceled(true);
        }
        else if (block instanceof TntBlock tnt)
        {
            tnt.onCaughtFire(state, level, pos, event.getTargetedFace(), event.getPlayer());
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.CERAMIC_BOWL.get())
        {
            if (level.getBlockEntity(pos) instanceof BowlBlockEntity bowl)
            {
                final ItemStack stack = bowl.getInventory().getStackInSlot(0);
                if (stack.getItem() == Items.GUNPOWDER)
                {
                    level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.getCount() / 6f + 2f, Level.ExplosionInteraction.BLOCK);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onFireStop(DouseFireEvent event)
    {
        final Level level = event.getLevel();
        final BlockPos pos = event.getPos();
        final BlockState state = event.getState();
        final Block block = state.getBlock();
        final Player player = event.getPlayer();

        if (state.isAir())
            return;
        if (state.is(BlockTags.FIRE))
        {
            level.removeBlock(pos, false);
            Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
            event.setCanceled(true);
        }
        else if (AbstractCandleBlock.isLit(state))
        {
            AbstractCandleBlock.extinguish(null, state, level, pos);
            event.setCanceled(true);
        }
        else if (CampfireBlock.isLitCampfire(state))
        {
            level.levelEvent(player, 1009, pos, 0);
            CampfireBlock.dowse(player, level, pos, state);
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.WALL_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.DEAD_WALL_TORCH.get().withPropertiesOf(state));
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.DEAD_TORCH.get().withPropertiesOf(state));
            event.setCanceled(true);
        }
        if (block == TFCBlocks.WALL_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.DEAD_WALL_TORCH.get().withPropertiesOf(state));
            event.setCanceled(true);
        }
        if (block instanceof JackOLanternBlock lantern)
        {
            lantern.extinguish(level, pos, state);
            event.setCanceled(true);
        }

        if (event.isCanceled())
            return;
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LampBlockEntity lamp && state.getValue(LampBlock.LIT))
        {
            Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
            level.setBlockAndUpdate(pos, state.setValue(LampBlock.LIT, false));
            lamp.resetCounter();
            event.setCanceled(true);
        }
        else if (blockEntity instanceof AbstractFirepitBlockEntity<?> firepit && firepit.getTemperature() > 0f)
        {
            firepit.extinguish(state);
            event.setCanceled(true);
        }
        else if (blockEntity instanceof CharcoalForgeBlockEntity forge && forge.getTemperature() > 0f)
        {
            forge.extinguish(state);
            event.setCanceled(true);
        }
        else if (blockEntity instanceof PowderkegBlockEntity keg && keg.isLit())
        {
            keg.setLit(false, player);
            event.setCanceled(true);
        }
        else if (blockEntity instanceof BlastFurnaceBlockEntity furnace && furnace.getTemperature() > 0f)
        {
            furnace.extinguish(state);
            event.setCanceled(true);
        }
        else if (blockEntity instanceof CrucibleBlockEntity crucible)
        {
            crucible.getInventory().setTemperature(0f);
        }
    }


    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        final Projectile projectile = event.getProjectile();
        final HitResult result = event.getRayTraceResult();
        final Level level = projectile.level();
        if (projectile instanceof ThrownPotion potion)
        {
            final PotionContents contents = potion.getItem().get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER) && !contents.hasEffects())
            {
                final boolean lingering = potion.getItem().getItem() instanceof LingeringPotionItem;
                DouseFireEvent.douse(level, potion.getBoundingBox().inflate(lingering ? 4 : 2, 2, lingering ? 4 : 2), projectile.getOwner() instanceof Player player ? player : null);
            }
        }
        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        if (result.getType() == HitResult.Type.BLOCK && projectile.isOnFire())
        {
            BlockHitResult blockResult = (BlockHitResult) result;
            BlockPos pos = blockResult.getBlockPos();
            StartFireEvent.startFire(projectile.level(), pos, projectile.level().getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Pre event)
    {
        // When facing up in the rain, player slowly recovers thirst.
        final Player player = event.getEntity();
        final Level level = player.level();
        final float angle = Mth.wrapDegrees(player.getXRot()); // Copied from DebugScreenOverlay, which is the value in F3
        if (angle <= -80 && !level.isClientSide() && level.isRainingAt(player.blockPosition().above()))
        {
            IPlayerInfo.get(player).addThirst(TFCConfig.SERVER.thirstGainedFromDrinkingInTheRain.get().floatValue());
        }
        if (!level.isClientSide() && !player.getAbilities().invulnerable && TFCConfig.SERVER.enableOverburdening.get() && level.getGameTime() % 20 == 0)
        {
            final int hugeHeavyCount = Helpers.countOverburdened(player.getInventory());
            if (hugeHeavyCount >= 1)
            {
                player.addEffect(Helpers.getExhausted(false));
            }
            if (hugeHeavyCount == 2)
            {
                player.addEffect(Helpers.getOverburdened(false));
            }
        }
    }

    public static void onEffectRemove(MobEffectEvent.Remove event)
    {
        final MobEffectInstance inst = event.getEffectInstance();
        if (event.getEntity() instanceof ServerPlayer player && inst != null)
        {
            PacketDistributor.sendToPlayer(player, new EffectExpirePacket(inst.getEffect()));
            if (inst.getEffect() == TFCEffects.PINNED.get())
            {
                player.setForcedPose(null);
            }
        }
    }

    public static void onEffectExpire(MobEffectEvent.Expired event)
    {
        final MobEffectInstance instance = event.getEffectInstance();
        if (instance != null && event.getEntity() instanceof ServerPlayer player)
        {
            PacketDistributor.sendToPlayer(player, new EffectExpirePacket(instance.getEffect()));
            if (instance.getEffect() == TFCEffects.PINNED.get())
            {
                player.setForcedPose(null);
            }
        }
    }

    public static void onLivingJump(LivingEvent.LivingJumpEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(TFCEffects.PINNED.holder()) || entity.hasEffect(TFCEffects.OVERBURDENED.holder()))
        {
            entity.setDeltaMovement(0, 0, 0);
            entity.hasImpulse = false;
        }
    }

    /**
     * Apply modifications from damage types, player health, and forging bonus, before armor and absorption and other resources are consumed.
     */
    public static void onLivingHurt(LivingIncomingDamageEvent event)
    {
        float amount = event.getAmount();

        // Forging Bonus
        final Entity attackerEntity = event.getSource().getEntity();
        if (attackerEntity instanceof LivingEntity livingEntity)
        {
            amount *= ForgingBonus.get(livingEntity.getMainHandItem()).damage();
        }

        // Physical Damage Type Modifiers
        amount *= PhysicalDamageType.calculateMultiplier(event.getSource(), event.getEntity());

        // Player health modifier
        if (event.getEntity() instanceof Player player)
        {
            amount /= IPlayerInfo.get(player).getHealthModifier();
        }

        event.setAmount(amount);
    }

    public static void onShieldBlock(LivingShieldBlockEvent event)
    {
        float damageModifier = 1f;
        final Item useItem = event.getEntity().getUseItem().getItem();

        // todo: the original code here was broken during porting, what do we even want to do here?
        if (useItem == Items.SHIELD)
        {
            damageModifier = 0.25f;
        }
        if (useItem instanceof TFCShieldItem shield)
        {
            damageModifier = shield.getDamageBlocked();
        }

        event.setBlockedDamage(event.getOriginalBlockedDamage() * damageModifier);
    }

    public static void onItemStacked(ItemStackedOnOtherEvent event)
    {
        final ItemStack batch = event.getCarriedItem();
        final ItemStack pipe = event.getStackedOnItem();
        if (event.getClickAction() == ClickAction.SECONDARY && pipe.getCount() == 1 && Helpers.isItem(pipe, TFCTags.Items.TOOLS_BLOWPIPE) && Helpers.isItem(batch.getItem(), TFCTags.Items.GLASS_BATCHES))
        {
            final ItemStack newItem = new ItemStack(BlowpipeItem.transform(pipe.getItem()));
            GlassWorking.createNewBatch(newItem, batch);
            event.getCarriedSlotAccess().set(newItem);
            event.getSlot().getItem().shrink(1);
            event.setCanceled(true);
        }
    }

    /**
     * This prevents vanilla mobs from spawning either at all or on the surface.
     */
    public static void onLivingSpawnCheck(FinalizeSpawnEvent event)
    {
        final LivingEntity entity = event.getEntity();
        final LevelAccessor level = event.getLevel();
        final MobSpawnType spawn = event.getSpawnType();

        // we only care about "natural" spawns
        if (spawn == MobSpawnType.NATURAL || spawn == MobSpawnType.CHUNK_GENERATION || spawn == MobSpawnType.REINFORCEMENT)
        {
            if (Helpers.isEntity(entity, TFCTags.Entities.MONSTERS))
            {
                if (TFCConfig.SERVER.enableVanillaMonsters.get())
                {
                    if (!TFCConfig.SERVER.enableVanillaMonstersOnSurface.get())
                    {
                        final BlockPos pos = entity.blockPosition();
                        if (entity.getType() != EntityType.SLIME && level.getRawBrightness(pos, 0) != 0)
                        {
                            event.setSpawnCancelled(true);
                            event.setCanceled(true);
                        }
                        else if (level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) <= pos.getY())
                        {
                            event.setSpawnCancelled(true);
                            event.setCanceled(true);
                        }
                        else if (!Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.MONSTER_SPAWNS_ON))
                        {
                            event.setSpawnCancelled(true);
                            event.setCanceled(true);
                        }
                    }
                }
                else
                {
                    event.setSpawnCancelled(true);
                    event.setCanceled(true);
                }
            }
        }
    }

    /**
     * Applies multiple effect for entities joining the world:
     * <p>
     * - Set a very short lifespan to item entities that are cool-able. This causes ItemExpireEvent to fire at regular intervals
     * - Causes lightning bolts to strip nearby logs
     * - Prevents skeleton trap horses from spawning (see {@link ServerLevel#tickChunk(LevelChunk, int)}
     * - Prevents some categories of mobs from spawning. Some can't be done in {@link FinalizeSpawnEvent} because Forge does not always fire it.
     */
    public static void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if (event.loadedFromDisk())
        {
            // This event is used for modifications to entity spawning, so we shouldn't apply any effects for entities that already exist in the world.
            return;
        }

        final Level level = event.getLevel();

        Entity entity = event.getEntity();
        if (entity instanceof ItemEntity itemEntity && !level.isClientSide && TFCConfig.SERVER.coolHotItemEntities.get())
        {
            final ItemStack item = itemEntity.getItem();
            if (HeatCapability.isHot(item))
            {
                itemEntity.lifespan = TFCConfig.SERVER.ticksBeforeItemCool.get();
            }
        }
        else if (entity instanceof LightningBolt lightning && !level.isClientSide && !event.isCanceled())
        {
            if (!TFCConfig.SERVER.enableLightning.get())
            {
                event.setCanceled(true);
                return;
            }
            if (TFCConfig.SERVER.enableLightningStrippingLogs.get() && level.random.nextFloat() < 0.2f)
            {
                final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                BlockPos pos = lightning.blockPosition();
                for (int x = -5; x <= 5; x++)
                {
                    for (int y = -5; y <= 5; y++)
                    {
                        for (int z = -5; z <= 5; z++)
                        {
                            if (level.random.nextInt(3) == 0 && x * x + y * y + z * z <= 25)
                            {
                                mutable.setWithOffset(pos, x, y, z);
                                BlockState state = level.getBlockState(mutable);
                                BlockState modified = state.getToolModifiedState(new UseOnContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE), new BlockHitResult(Vec3.atBottomCenterOf(mutable), Direction.DOWN, mutable, false)), ItemAbilities.AXE_STRIP, true);
                                if (modified != null)
                                {
                                    level.setBlockAndUpdate(mutable, modified);
                                }
                            }
                        }
                    }
                }
            }

        }
        if (entity instanceof Monster monster && !TFCConfig.SERVER.enableVanillaMobsSpawningWithVanillaEquipment.get())
        {
            if (Helpers.isItem(monster.getItemInHand(InteractionHand.MAIN_HAND), TFCTags.Items.DISABLED_MONSTER_HELD_ITEMS))
            {
                monster.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            if (Helpers.isItem(monster.getItemInHand(InteractionHand.OFF_HAND), TFCTags.Items.DISABLED_MONSTER_HELD_ITEMS))
            {
                monster.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        if (!TFCConfig.SERVER.enableChickenJockies.get())
        {
            // Need to prevent both the chicken and the jockey from spawning
            if ((entity instanceof Chicken chicken && chicken.isChickenJockey)
                || (entity.getVehicle() != null && entity.getVehicle() instanceof Chicken vehicleChicken && vehicleChicken.isChickenJockey))
            {
                event.setCanceled(true);
            }
        }

        if (entity.getType() == EntityType.SKELETON && entity instanceof Skeleton skeleton)
        {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, Helpers.randomItem(TFCTags.Items.SKELETON_WEAPONS, entity.level().getRandom()).orElse(Items.BOW).getDefaultInstance());
        }
        else if (entity.getType() == EntityType.SKELETON_HORSE && !TFCConfig.SERVER.enableVanillaSkeletonHorseSpawning.get())
        {
            event.setCanceled(true);
        }
        else if ((entity instanceof IronGolem || entity instanceof SnowGolem) && !TFCConfig.SERVER.enableVanillaGolems.get())
        {
            event.setCanceled(true);
        }
    }

    /**
     * If the item is heated, we check for blocks below and within that would cause it to cool.
     * Since we don't want the item to actually expire, we set the expiry time to a small number that allows us to revisit the same code soon.
     * <p>
     * By cancelling the event, we guarantee that the item will not actually expire.
     */
    public static void onItemExpire(ItemExpireEvent event)
    {
        if (!TFCConfig.SERVER.coolHotItemEntities.get()) return;
        final ItemEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        final ServerLevel level = (ServerLevel) entity.level();
        final ItemStack stack = entity.getItem();
        final BlockPos pos = entity.blockPosition();
        final @Nullable IHeat heat = HeatCapability.get(stack);

        if (heat != null)
        {
            final int lifespan = stack.getItem().getEntityLifespan(stack, level);
            if (entity.lifespan >= lifespan)
                return; // the case where the item has been sitting out for longer than the lifespan. So it should be removed by the game.

            final float itemTemp = heat.getTemperature();
            if (itemTemp > 0f)
            {
                float coolAmount = 0;
                final BlockState state = level.getBlockState(pos);
                if (FluidHelpers.canFluidExtinguishFire(state.getFluidState().getType()))
                {
                    coolAmount = 50f;
                    if (level.random.nextFloat() < 0.001F && FluidHelpers.isAirOrEmptyFluid(state))
                    {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
                else if (Helpers.isBlock(state, Blocks.SNOW))
                {
                    coolAmount = 70f;
                    if (level.random.nextFloat() < 0.1F)
                    {
                        final int layers = state.getValue(SnowLayerBlock.LAYERS);
                        if (layers > 1)
                        {
                            level.destroyBlock(pos, false);
                            level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1));
                        }
                        else
                        {
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
                else
                {
                    final BlockPos belowPos = pos.below();
                    final BlockState belowState = level.getBlockState(belowPos);
                    if (Helpers.isBlock(belowState, Blocks.SNOW_BLOCK))
                    {
                        coolAmount = 75f;
                        if (level.random.nextFloat() < 0.1F)
                        {
                            level.destroyBlock(belowPos, false);
                        }
                    }
                    else if (belowState.getBlock() == Blocks.ICE || belowState.getBlock() == Blocks.FROSTED_ICE)
                    {
                        coolAmount = 100f;
                        if (level.random.nextFloat() < 0.01F)
                        {
                            level.setBlockAndUpdate(belowPos, Helpers.isBlock(belowState, TFCBlocks.SEA_ICE.get()) ? TFCBlocks.SALT_WATER.get().defaultBlockState() : Blocks.WATER.defaultBlockState());
                        }
                    }
                    else if (belowState.getBlock() == Blocks.PACKED_ICE || belowState.getBlock() == Blocks.BLUE_ICE)
                    {
                        coolAmount = 125f;
                        if (level.random.nextFloat() < 0.005F)
                        {
                            level.setBlockAndUpdate(belowPos, Blocks.WATER.defaultBlockState());
                        }
                    }
                }

                if (coolAmount > 0f)
                {
                    heat.setTemperature(Math.max(0f, heat.getTemperature() - coolAmount));
                    Helpers.playSound(level, pos, TFCSounds.ITEM_COOL.get());
                    level.sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY(), entity.getZ(), 1, 0D, 0D, 0D, 1f);
                }
                event.setExtraLife(heat.getTemperature() == 0f ? lifespan : TFCConfig.SERVER.ticksBeforeItemCool.get());
                //entity.setNoPickUpDelay();
            }
            else
            {
                event.setExtraLife(lifespan);
            }
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        onNewPlayerInWorld(event.getEntity());
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        onNewPlayerInWorld(event.getEntity());
    }

    public static void onPlayerDeath(PlayerEvent.Clone event)
    {
        // This event fires before respawn event, and allows us to copy nutrition to the new player.
        // Respawn event will handle syncing to client, as the network connection is setup by then.
        if (TFCConfig.SERVER.keepNutritionAfterDeath.get() && event.isWasDeath())
        {
            IPlayerInfo.copyOnDeath(event.getOriginal(), event.getEntity());
        }
    }

    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        onNewPlayerInWorld(event.getEntity());
    }

    /**
     * Common handling for creating new player entities. Called through logging in, changing dimension, and respawning.
     */
    private static void onNewPlayerInWorld(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            final ClimateModel model = Climate.get(serverPlayer.level());
            PacketDistributor.sendToPlayer(serverPlayer, new UpdateClimateModelPacket(model));
            RotationNetworkManager.get(serverPlayer.serverLevel()).syncTo(serverPlayer);
        }
    }

    public static void onServerChat(ServerChatEvent event)
    {
        // Apply intoxication after six hours
        final long intoxicatedTicks = IPlayerInfo.get(event.getPlayer()).getIntoxication() - 6 * ICalendar.TICKS_IN_HOUR;
        if (intoxicatedTicks > 0)
        {
            final float intoxicationChance = Mth.clamp((float) (intoxicatedTicks - 6 * ICalendar.TICKS_IN_HOUR) / PlayerInfo.MAX_INTOXICATED_TICKS, 0, 0.7f);
            final RandomSource random = event.getPlayer().getRandom();
            final String originalMessage = event.getMessage().getString();
            final String[] words = originalMessage.split(" ");
            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];
                if (word.isEmpty())
                {
                    continue;
                }

                // Swap two letters
                if (random.nextFloat() < intoxicationChance && word.length() >= 2)
                {
                    int pos = random.nextInt(word.length() - 1);
                    word = word.substring(0, pos) + word.charAt(pos + 1) + word.charAt(pos) + word.substring(pos + 2);
                }

                // Repeat / slur letters
                if (random.nextFloat() < intoxicationChance)
                {
                    int pos = random.nextInt(word.length());
                    char repeat = word.charAt(pos);
                    int amount = 1 + random.nextInt(3);
                    word = word.substring(0, pos) + new String(new char[amount]).replace('\0', repeat) + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                }

                // Add additional letters
                if (random.nextFloat() < intoxicationChance)
                {
                    int pos = random.nextInt(word.length());
                    char replacement = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
                    if (Character.isUpperCase(word.charAt(random.nextInt(word.length()))))
                    {
                        replacement = Character.toUpperCase(replacement);
                    }
                    word = word.substring(0, pos) + replacement + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                }

                words[i] = word;
            }
            event.setMessage(Component.literal(String.join(" ", words)));
        }
    }

    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final Level level = event.getLevel();
        final BlockState state = level.getBlockState(event.getPos());
        final ItemStack stack = event.getItemStack();

        if (Helpers.isItem(stack, Items.WRITABLE_BOOK) || Helpers.isItem(stack, Items.WRITTEN_BOOK))
        {
            // Lecterns, we only do a modification for known items *and* known blocks, so there's no need to simulate any other interaction
            if (state.getBlock() instanceof TFCLecternBlock && LecternBlock.tryPlaceBook(event.getEntity(), level, event.getPos(), state, stack))
            {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }

        // need position access to set smelled pos properly, so we cannot use container menus here.
        if (level.getBlockEntity(event.getPos()) instanceof BaseContainerBlockEntity container && container.canOpen(event.getEntity()) && container instanceof PestContainer test && test.canBeInfested())
        {
            int infestation = 0;
            for (int i = 0; i < container.getContainerSize(); i++)
            {
                if (Helpers.isItem(container.getItem(i), TFCTags.Items.FOODS))
                {
                    infestation++;
                    if (infestation == 5)
                    {
                        break;
                    }
                }
            }
            Helpers.tickInfestation(level, container.getBlockPos(), infestation, event.getEntity());
        }
    }

    public static void onPlayerRightClickBlockLowestPriority(PlayerInteractEvent.RightClickBlock event)
    {
        final Level level = event.getLevel();
        final BlockState state = level.getBlockState(event.getPos());
        final ItemStack stack = event.getItemStack();

        if (!event.isCanceled() && event.getHand() == InteractionHand.MAIN_HAND && stack.isEmpty())
        {
            // For drinking, when we have an empty hand, we want to first try and interact with a block.
            // We can't use interaction manager, as vanilla won't try and call onItemUse for empty stacks.
            // We do this on lowest priority, because we want other modifications to fire *first* - for instance, if a mod does a block interaction on this event, at normal priority
            // Thus if we get here, we're fairly certain another mod doesn't need to use this, so we can check the block `use()` method, and then if no, we can attempt drinking.
            // Possible issues:
            // - Right-click a chest underwater -> it should open the chest, not drink
            // - Try and remove the filter from a Create 'Basin', by right-clicking with an empty hand (create cancels this event)
            final ItemInteractionResult useBlockResult = state.useItemOn(stack, level, event.getEntity(), event.getHand(), event.getHitVec());
            if (useBlockResult.consumesAction())
            {
                if (event.getEntity() instanceof ServerPlayer serverPlayer)
                {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, event.getPos(), stack);
                }
                event.setCanceled(true);
                event.setCancellationResult(useBlockResult.result());
            }
            else
            {
                // If we haven't already interacted with a block, then we can attempt drinking.
                final InteractionResult result = Drinkable.attemptDrink(level, event.getEntity(), true);
                if (result != InteractionResult.PASS)
                {
                    event.setCanceled(true);
                    event.setCancellationResult(result);
                }
            }
        }

        // Some blocks have interactions that respect sneaking, both with items in hand and not
        // These need to be able to interact, regardless of if an item has sneakBypassesUse set
        // So, we have to explicitly allow the Block.use() interaction for these blocks.
        //
        // This happens at lowest priority, regardless if the event was cancelled, as we don't want this `ALLOW` to be overwritten.
        // Otherwise it breaks anvil shift interactions, see: TerraFirmaCraft#2254
        if (state.getBlock() instanceof AnvilBlock || state.getBlock() instanceof RockAnvilBlock || Fertilizer.get(stack) != null || (state.getBlock() instanceof BarrelBlock && !state.getValue(BarrelBlock.RACK) && state.getValue(BarrelBlock.FACING).getAxis().isHorizontal() && stack.getItem() == TFCBlocks.BARREL_RACK.get().asItem()))
        {
            event.setUseBlock(TriState.TRUE);
        }
    }

    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final UseOnContext context = new UseOnContext(event.getEntity(), event.getHand(), FAKE_MISS);
        InteractionManager.onItemUse(event.getItemStack(), context, true).ifPresent(result -> {
            event.setCanceled(true);
            event.setCancellationResult(result);
        });
    }

    public static void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty())
        {
            // Cannot be cancelled, only fired on client.
            InteractionResult result = Drinkable.attemptDrink(event.getLevel(), event.getEntity(), false);
            if (result == InteractionResult.SUCCESS)
            {
                PacketDistributor.sendToServer(PlayerDrinkPacket.PACKET);
            }
        }
    }

    public static void onUseItemOnBlock(UseItemOnBlockEvent event)
    {
        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK)
        {
            InteractionManager.onItemUse(event.getItemStack(), event.getUseOnContext(), false).ifPresent(result -> {
                event.cancelWithResult(switch (result)
                {
                    // This is the inverse of ItemInteractionResult.result()
                    case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
                    case CONSUME, CONSUME_PARTIAL -> ItemInteractionResult.CONSUME;
                    case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    case FAIL -> ItemInteractionResult.FAIL;
                });
            });
        }
    }

    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        final ItemStack stack = event.getItem();
        final @Nullable ItemComponent bowl = stack.get(TFCComponents.BOWL);
        if (bowl != null)
        {
            event.setResultStack(Bowl.onItemUse(bowl, stack, event.getResultStack(), event.getEntity()));
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        DataManagers.REGISTRY.forEach(event::addListener);
        Helpers.setCachedRecipeManager(event.getServerResources().getRecipeManager());
    }

    public static void onDataPackSync(OnDatapackSyncEvent event)
    {
        if (event.getPlayer() == null)
        {
            PacketDistributor.sendToAllPlayers(new DataManagerSyncPacket());
        }
        else
        {
            PacketDistributor.sendToPlayer(event.getPlayer(), new DataManagerSyncPacket());
        }
    }

    /**
     * This is when tags are safe to be loaded, so we can do post reload actions that involve querying ingredients.
     * It is fired on both logical server and client after resources are reloaded (or, sent from server).
     * In addition, during the first load on a server in {@link Main}, the server won't exist yet at all.
     * In that case, we need to rely on the fact that {@link AddReloadListenerEvent} will be fired before that point, and we can capture the server's recipe manager there.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onTagsUpdated(TagsUpdatedEvent event)
    {
        if (event.shouldUpdateStaticData())
        {
            // First, reload all caches
            final RecipeManager manager = Helpers.getUnsafeRecipeManager();
            IndirectHashCollection.reloadAllCaches(manager);

            // Then apply post reload actions which may query the cache
            Support.updateMaximumSupportRange();
            FluidHeat.updateCache();

            TFCComponents.onModifyDefaultComponentsAfterResourceReload();
            FoodCapability.markRecipeOutputsAsNonDecaying(event.getRegistryAccess(), manager);

            SelfTests.runDataPackTests(manager);

            final RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;
            for (RecipeType<?> type : BuiltInRegistries.RECIPE_TYPE)
            {
                LOGGER.debug("Loaded {} recipes of type {}", accessor.invoke$byType((RecipeType) type).size(), BuiltInRegistries.RECIPE_TYPE.getKey(type));
            }
        }
    }

    /**
     * Deny all traditional uses of bone meal directly to grow crops.
     * Fertilizer is used as a replacement.
     */
    public static void onBoneMeal(BonemealEvent event)
    {
        if (!TFCConfig.SERVER.enableVanillaBonemeal.get())
        {
            event.setCanceled(true);
        }
    }

    public static void onSelectClimateModel(SelectClimateModelEvent event)
    {
        final ServerLevel level = event.level();
        if (event.level().dimension() == Level.OVERWORLD && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
        {
            // TFC decides to select the climate model for the overworld, if we're using a TFC enabled chunk generator
            event.setModel(new OverworldClimateModel(level, ex));
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        final Player player = event.getEntity();
        if (event.getTarget().getType() == EntityType.MINECART && event.getTarget() instanceof Minecart oldCart && player.isShiftKeyDown() && player.isSecondaryUseActive())
        {
            ItemStack held = player.getItemInHand(event.getHand());
            if (Helpers.isItem(held, TFCTags.Items.MINECART_HOLDABLE))
            {
                final ItemStack holdingItem = held.split(1);
                if (!player.level().isClientSide)
                {
                    final HoldingMinecart minecart = new HoldingMinecart(player.level(), oldCart.getX(), oldCart.getY(), oldCart.getZ());
                    HoldingMinecart.copyMinecart(oldCart, minecart);
                    minecart.setHoldItem(holdingItem);
                    oldCart.discard();
                    player.level().addFreshEntity(minecart);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    public static void onMount(EntityMountEvent event)
    {
        if (event.getEntityBeingMounted() instanceof Boat && event.getEntityMounting() instanceof Predator)
        {
            event.setCanceled(true);
        }
    }

    public static void onAnimalTame(AnimalTameEvent event)
    {
        if (Helpers.isEntity(event.getEntity(), TFCTags.Entities.HORSES))
        {
            event.setCanceled(true); // cancel vanilla taming methods
        }
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (event.getContainer() instanceof BlockEntityContainer<?> container && event.getContainer() instanceof PestContainer test && test.canBeInfested())
        {
            final Player player = event.getEntity();
            final Level level = player.level();
            if (level.isClientSide) return;
            int amount = 0;
            if (TFCConfig.SERVER.enableInfestations.get())
            {
                for (Slot slot : container.slots)
                {
                    if (container.typeOf(slot.index) == Container.IndexType.CONTAINER && Helpers.isItem(slot.getItem(), TFCTags.Items.FOODS))
                    {
                        amount++;
                        if (amount == 5)
                        {
                            break;
                        }
                    }
                }
            }
            Helpers.tickInfestation(level, container.getBlockEntity().getBlockPos(), amount, player);
        }
    }

    public static void onCropsGrow(CropGrowEvent.Pre event)
    {
        final BlockState state = event.getState();
        final LevelAccessor level = event.getLevel();
        if (state.getBlock() instanceof BambooStalkBlock)
        {
            if (level instanceof ServerLevel server && server.random.nextFloat() > TFCConfig.SERVER.plantLongGrowthChance.get())
            {
                event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            }
        }
    }
}