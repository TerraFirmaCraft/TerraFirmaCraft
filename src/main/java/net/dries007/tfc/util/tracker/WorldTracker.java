/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCAttachments;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.RainfallUpdatePacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.BiomeBasedClimateModel;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.collections.BufferedList;
import net.dries007.tfc.util.events.CollapseEvent;
import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.util.rotation.RotationNetworkManager;

public final class WorldTracker
{
    /**
     * Returns the world tracker for a given world. Note that we always expect <strong>every world</strong> to have a tracker attached, and thus this will throw
     * if the tracker does not exist. The world tracker exists on both client and server worlds, although it may be in various states of valid in client worlds.
     *
     * @param level The world to query.
     * @return The world tracker for this world.
     */
    public static WorldTracker get(Level level)
    {
        return level.getData(TFCAttachments.WORLD_TRACKER);
    }

    private final Level level;
    private final Random random;

    private final BufferedList<TickEntry> landslideTicks;
    private final BufferedList<BlockPos> isolatedPositions;
    private final List<Collapse> collapsesInProgress;

    private final ClimateModel defaultClimateModel = new BiomeBasedClimateModel();
    @Nullable private ClimateModel climateModel;

    private final RotationNetworkManager rotationManager;

    private long rainStartTick, rainEndTick;
    private float rainIntensity;

    public WorldTracker(Level level)
    {
        this.level = level;
        this.random = new Random();
        this.climateModel = null;
        this.landslideTicks = new BufferedList<>();
        this.isolatedPositions = new BufferedList<>();
        this.collapsesInProgress = new ArrayList<>();
        this.rotationManager = new RotationNetworkManager();
    }

    public void addLandslidePos(BlockPos pos)
    {
        landslideTicks.add(new TickEntry(pos, 2));
    }

    public void addIsolatedPos(BlockPos pos)
    {
        isolatedPositions.add(pos);
    }

    public void addCollapseData(Collapse collapse)
    {
        collapsesInProgress.add(collapse);
        NeoForge.EVENT_BUS.post(new CollapseEvent(level, collapse.centerPos, collapse.nextPositions, collapse.radiusSquared, false));
    }

    public void setClimateModel(ClimateModel climateModel)
    {
        this.climateModel = climateModel;
    }

    public ClimateModel getClimateModel()
    {
        return climateModel == null ? defaultClimateModel : climateModel;
    }

    public void addCollapsePositions(BlockPos centerPos, Collection<BlockPos> positions)
    {
        List<BlockPos> collapsePositions = new ArrayList<>();
        double maxRadiusSquared = 0;
        for (BlockPos pos : positions)
        {
            double distSquared = pos.distSqr(centerPos);
            if (distSquared > maxRadiusSquared)
            {
                maxRadiusSquared = distSquared;
            }
            if (random.nextFloat() < TFCConfig.SERVER.collapseExplosionPropagateChance.get())
            {
                collapsePositions.add(pos.above()); // Check the above position
            }
        }
        addCollapseData(new Collapse(centerPos, collapsePositions, maxRadiusSquared));
    }

    public void setWeatherData(long rainDuration, float rainIntensity)
    {
        final long tick = Calendars.get(level).getTicks();
        setWeatherData(tick, tick + rainDuration, rainIntensity);
    }

    public void setWeatherData(long rainStartTick, long rainEndTick, float rainIntensity)
    {
        this.rainStartTick = rainStartTick;
        this.rainEndTick = rainEndTick;
        this.rainIntensity = rainIntensity;
        sync();
    }

    public boolean isRaining(Level level, BlockPos pos)
    {
        return isRaining(Calendars.get(level).getTicks(), Climate.getRainfall(level, pos));
    }

    /**
     * @return If it should be raining at a given tick and rainfall value
     */
    public boolean isRaining(long tick, float rainfall)
    {
        return exactRainfallIntensity(tick) > Mth.clampedMap(rainfall, ClimateModel.MINIMUM_RAINFALL, ClimateModel.MAXIMUM_RAINFALL, 1, 0);
    }

    public RotationNetworkManager getRotationManager()
    {
        return rotationManager;
    }

    /**
     * Must only be called from logical server!
     */
    public void tick()
    {
        if (!collapsesInProgress.isEmpty() && random.nextInt(10) == 0)
        {
            for (Collapse collapse : collapsesInProgress)
            {
                Set<BlockPos> updatedPositions = new HashSet<>();
                for (BlockPos posAt : collapse.nextPositions)
                {
                    // Check the current position for collapsing
                    BlockState stateAt = level.getBlockState(posAt);
                    if (Helpers.isBlock(stateAt, TFCTags.Blocks.CAN_COLLAPSE) && TFCFallingBlockEntity.canFallInDirection(level, posAt, Direction.DOWN) && posAt.distSqr(collapse.centerPos) < collapse.radiusSquared && random.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                    {
                        if (CollapseRecipe.collapseBlock(level, posAt, stateAt))
                        {
                            // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                            updatedPositions.add(posAt.above());
                        }
                    }
                }
                collapse.nextPositions.clear();
                if (!updatedPositions.isEmpty())
                {
                    level.playSound(null, collapse.centerPos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
                    collapse.nextPositions.addAll(updatedPositions);
                    collapse.radiusSquared *= 0.8; // lower radius each successive time
                }
            }
            collapsesInProgress.removeIf(collapse -> collapse.nextPositions.isEmpty());
        }

        landslideTicks.flush();
        Iterator<TickEntry> tickIterator = landslideTicks.listIterator();
        while (tickIterator.hasNext())
        {
            TickEntry entry = tickIterator.next();
            if (entry.tick())
            {
                final BlockState currentState = level.getBlockState(entry.getPos());
                LandslideRecipe.tryLandslide(level, entry.getPos(), currentState);
                tickIterator.remove();
            }
        }

        isolatedPositions.flush();
        Iterator<BlockPos> isolatedIterator = isolatedPositions.listIterator();
        while (isolatedIterator.hasNext())
        {
            final BlockPos pos = isolatedIterator.next();
            final BlockState currentState = level.getBlockState(pos);
            if (Helpers.isBlock(currentState.getBlock(), TFCTags.Blocks.BREAKS_WHEN_ISOLATED) && isIsolated(level, pos))
            {
                Helpers.destroyBlockAndDropBlocksManually((ServerLevel) level, pos, ctx -> ctx.withParameter(TFCLoot.ISOLATED, true));
            }
            isolatedIterator.remove();
        }
    }

    public void addDebugTooltip(List<String> tooltips)
    {
        tooltips.add("R [%d, %d] I (%.2f) %.2f".formatted(rainStartTick, rainEndTick, rainIntensity, exactRainfallIntensity(Calendars.CLIENT.getTicks())));
    }

    public void sync()
    {
        if (level instanceof ServerLevel serverLevel)
        {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new RainfallUpdatePacket(rainStartTick, rainEndTick, rainIntensity));
        }
    }

    public void syncTo(ServerPlayer player)
    {
        PacketDistributor.sendToPlayer(player, new RainfallUpdatePacket(rainStartTick, rainEndTick, rainIntensity));
    }

    public CompoundTag serializeNBT()
    {
        landslideTicks.flush();
        isolatedPositions.flush();

        CompoundTag nbt = new CompoundTag();
        ListTag landslideNbt = new ListTag();
        for (TickEntry entry : landslideTicks)
        {
            landslideNbt.add(entry.serializeNBT());
        }
        nbt.put("landslideTicks", landslideNbt);

        LongArrayTag isolatedNbt = new LongArrayTag(isolatedPositions.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.put("isolatedPositions", isolatedNbt);

        ListTag collapseNbt = new ListTag();
        for (Collapse collapse : collapsesInProgress)
        {
            collapseNbt.add(collapse.serializeNBT());
        }
        nbt.put("collapsesInProgress", collapseNbt);

        nbt.putLong("rainStartTick", rainStartTick);
        nbt.putLong("rainEndTick", rainEndTick);
        nbt.putFloat("rainIntensity", rainIntensity);

        return nbt;
    }

    public void deserializeNBT(@Nullable CompoundTag nbt)
    {
        if (nbt != null)
        {
            landslideTicks.clear();
            collapsesInProgress.clear();
            isolatedPositions.clear();

            ListTag landslideNbt = nbt.getList("landslideTicks", Tag.TAG_COMPOUND);
            for (int i = 0; i < landslideNbt.size(); i++)
            {
                landslideTicks.add(new TickEntry(landslideNbt.getCompound(i)));
            }

            long[] isolatedNbt = nbt.getLongArray("isolatedPositions");
            Arrays.stream(isolatedNbt).mapToObj(BlockPos::of).forEach(isolatedPositions::add);

            ListTag collapseNbt = nbt.getList("collapsesInProgress", Tag.TAG_COMPOUND);
            for (int i = 0; i < collapseNbt.size(); i++)
            {
                collapsesInProgress.add(new Collapse(collapseNbt.getCompound(i)));
            }

            rainStartTick = nbt.getLong("rainStartTick");
            rainEndTick = nbt.getLong("rainEndTick");
            rainIntensity = nbt.getFloat("rainIntensity");
        }
    }

    private float exactRainfallIntensity(long tick)
    {
        final float progress = Mth.clamp(Helpers.inverseLerp(tick, rainStartTick, rainEndTick), 0, 1);
        final float progressFactor = progress > 0.5f ? 1 - progress : progress;
        return rainIntensity * 0.5f + progressFactor;
    }

    private boolean isIsolated(LevelAccessor level, BlockPos pos)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            BlockState state = level.getBlockState(pos.relative(direction));
            if (!state.getCollisionShape(level, pos).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}