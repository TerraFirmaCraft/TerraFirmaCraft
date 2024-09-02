/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCPoiTypes;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.IcicleBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
import net.dries007.tfc.mixin.accessor.PoiSectionAccessor;
import net.dries007.tfc.mixin.accessor.SectionStorageAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Handler for custom weather and weather effects.
 */
public final class WeatherHelpers
{
    private static final Holder<PoiType> CLIMATE = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(TFCPoiTypes.CLIMATE.unwrapKey().orElseThrow());

    // The number of ticks per a single snow accumulation/melt event in a single chunk. For reference, vanilla operates at
    // (48 / randomTickSpeed), or 16 ticks. We do melting much slower, since it's statistically much less likely to be raining
    private static final int TICKS_PER_SNOW_ACCUMULATION = 80;
    private static final int TICKS_PER_SNOW_MELT_PER_SNOW_ACCUMULATION = 3;
    private static final int TICKS_PER_SNOW_MELT = TICKS_PER_SNOW_ACCUMULATION * TICKS_PER_SNOW_MELT_PER_SNOW_ACCUMULATION;

    // For fast forwarding, the number of "fast-forward" ticks that should be simulated for a given hour of either estimated
    // melting, or estimated snow accumulation.
    private static final int UPDATES_PER_SNOW_MELT_HOUR = 1 + 1_000 / TICKS_PER_SNOW_MELT;
    private static final int UPDATES_PER_SNOW_ACCUMULATION_HOUR = 1 + 1_000 / TICKS_PER_SNOW_ACCUMULATION;

    // The maximum number of single tick updates that can be scheduled to happen
    private static final int MAX_UPDATES_PER_TICK = 48;

    /**
     * Replaces a call to {@link Biome#getPrecipitationAt(BlockPos)} with one that is aware of both the local climate,
     * and the local rainfall. Note that of all the biome climate based methods, this is the only one we need to
     * aggressively replace callers of. All others are either (1) invoked in world gen we do not use, (2) invoked by
     * {@link ServerLevel#tickPrecipitation} which we disable, or (3) delegate / query something else which we do handle.
     *
     * @param defaultValue The default value to return, if the climate model does not support rain simulation.
     * @return the current precipitation mode at the given position, as per the climate model.
     */
    public static Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos, Biome.Precipitation defaultValue)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        if (!model.supportsRain())
        {
            return defaultValue;
        }

        final long calendarTicks = Calendars.get(level).getCalendarTicks();
        final float rainIntensity = tracker.isWeatherEnabled() ? model.getRain(calendarTicks) : -1;
        final float rainValue = model.getRainfall(level, pos);

        return isPrecipitating(rainIntensity, rainValue)
            ? model.getTemperature(level, pos) > 0f
                ? Biome.Precipitation.RAIN
                : Biome.Precipitation.SNOW
            : Biome.Precipitation.NONE;
    }

    /**
     * @param rainIntensity The rainfall intensity, i.e. {@link ClimateModel#getRain}
     * @param rainfall The time-variant average rainfall, i.e. {@link ClimateModel#getRainfall}
     * @return {@code true} if it is precipitating (rain or snow) with the provided values.
     */
    public static boolean isPrecipitating(float rainIntensity, float rainfall)
    {
        return calculateRealRainIntensity(rainIntensity, rainfall) > 0;
    }

    public static float calculateRealRainIntensity(float rainIntensity, float rainfall)
    {
        return rainIntensity - Mth.clampedMap(rainfall, ClimateModel.MIN_RAINFALL, ClimateModel.MAX_RAINFALL, 1, 0);
    }

    /**
     * Called in replacement of {@link ServerLevel#advanceWeatherCycle()} for worlds that have a climate-based weather cycle
     * @return {@code true} if the weather cycle was handled for this dimension.
     */
    public static boolean advanceWeatherCycle(ServerLevel level)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        if (!model.supportsRain())
        {
            return false;
        }

        final long calendarTicks = Calendars.SERVER.getCalendarTicks();
        final float rain = tracker.isWeatherEnabled() ? model.getRain(calendarTicks) : -1;
        final boolean thunder = tracker.isWeatherEnabled() && model.getThunder(calendarTicks);
        final boolean wasRaining = level.isRaining();

        // Update vanilla's current and previous rain level, based on if it is currently raining. All clients will see this,
        // but clients will only visually see rain if it is within the rainfall range to occur. Clients also do their own
        // interpolation of this value
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Mth.clamp(level.rainLevel + (rain >= 0 ? 0.01f : -0.01f), 0, 1);

        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Mth.clamp(level.thunderLevel + (thunder ? 0.01f : -0.01f), 0, 1);

        // Now, if any updates were made, do syncing to all clients, via the vanilla packets.
        if (level.oRainLevel != level.rainLevel)
        {
            sendToAllInDimension(level, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel);
        }

        if (level.oThunderLevel != level.thunderLevel)
        {
            sendToAllInDimension(level, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel);
        }

        if (wasRaining != level.isRaining())
        {
            sendToAllInDimension(level, wasRaining
                ? ClientboundGameEventPacket.STOP_RAINING
                : ClientboundGameEventPacket.START_RAINING, 0);
            sendToAllInDimension(level, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel);
            sendToAllInDimension(level, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel);
        }

        return true;
    }

    private static void sendToAllInDimension(ServerLevel level, ClientboundGameEventPacket.Type event, float value)
    {
        level.getServer()
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(event, value), level.dimension());
    }

    /**
     * Handles chunk ticking. This occurs on chunks that are within a radius of the player (128 blocks), which is notably smaller
     * than chunks that are loaded. We need this to be *accurate*, and *fast*. We handle a number of possible mechanics in order
     * to try and keep the effects of weather (snow, ice, and icicles), up-to-date.
     *
     * <h3>Simulation</h3>
     * In order to properly handle simulation of chunks that have not been random ticked in a while, we support multiple methods of
     * "simulating" if snow should have accumulated, or melted. By default, vanilla will do snow proportional to {@code randomTickSpeed / 48}
     * blocks per tick, or once every 16 ticks, per chunk. We start with a baseline of half that speed (snow every 50 ticks).
     * <p>
     * Snow melting happens slower, since it's always happening, at a rate of roughly 1 per 200 ticks.
     * <ul>
     *     <li>Over short times, such as less than 1000 ticks, we do basic "catch-up". We calculate some number of additional chunk
     *     ticks to run, and execute them based on historical data for rainfall/temperature.</li>
     *     <li>Over longer times, we do a less accurate simulation of the weather, which tries to extrapolate how much the chunk should
     *     be melted, or covered in snow. This then does the same "catch-up", but with a specific end-goal in mind.</li>
     * </ul>
     *
     * <h3>Snow Accumulation and Melting</h3>
     * We use the POI system for snow, in order to have an accurate and fast count of the amount of snow (or ice or icicles) in a chunk, and
     * we do a very basic counting of previous ticks, how many times we should have been raining (accumulating snow), or positive temperature
     * (melting). Note that we do melting much slower than we do accumulation, which affects how we simulate.
     */
    public static void onTickChunk(ServerLevel level, ChunkAccess chunk)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        if (!tracker.isWeatherEnabled())
        {
            return; // If weather is disabled, we prevent snow accumulation and melting completely
        }

        final ClimateModel model = tracker.getClimateModel();
        if (!model.supportsRain())
        {
            return; // Don't handle with climate models that don't support simulation rain
        }

        final ChunkData data = ChunkData.get(chunk);
        final long currentTick = Calendars.SERVER.getTicks();
        final long currentCalendarTick = Calendars.SERVER.getCalendarTicks();
        final long timeSinceTick = currentTick - data.getLastRandomTick();

        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos surfacePos = getRandomSurfacePos(level, chunkPos);
        final float rainfall = model.getRainfall(level, surfacePos);

        if (timeSinceTick > 1_000)
        {
            // We have not ticked this chunk in a short while, so run catch-up ticks to see if we missed anything
            // First, we need to check for what we might've missed
            final int daysInMonth = Calendars.SERVER.getCalendarDaysInMonth();

            long calendarTick = currentCalendarTick - Math.min(48_000, timeSinceTick);
            int netChangeInSnow = 0; // >0 indicates melting, <0 indicates freezing

            while (calendarTick < currentCalendarTick)
            {
                calendarTick += 1_000;
                final float estimatedTemperature = model.getTemperature(level, surfacePos, calendarTick, daysInMonth);
                if (estimatedTemperature > 2f)
                {
                    netChangeInSnow = Math.max(netChangeInSnow - UPDATES_PER_SNOW_MELT_HOUR, -MAX_UPDATES_PER_TICK);
                }
                else if (estimatedTemperature < -2f && isPrecipitating(model.getRain(calendarTick), rainfall))
                {
                    netChangeInSnow = Math.min(netChangeInSnow + UPDATES_PER_SNOW_ACCUMULATION_HOUR, MAX_UPDATES_PER_TICK);
                }
            }

            if (netChangeInSnow > 0)
            {
                // First, if we're performing a large number of updates, we want to first count the amount of snow in the chunk,
                // and only do updates if it's between a threshold
                netChangeInSnow = Math.min(64 - countExistingSnowInChunk(level, chunkPos), netChangeInSnow);
                for (int i = 0; i < netChangeInSnow; i++)
                {
                    handleSnowAccumulation(level, getRandomSurfacePos(level, chunkPos));
                }
            }
            else if (netChangeInSnow < 0)
            {
                handleSnowMelting(level, chunkPos, -netChangeInSnow);
            }
        }
        else if (level.random.nextInt(TICKS_PER_SNOW_ACCUMULATION) == 0)
        {
            // Trigger either snow melting, or accumulation event
            final float realTemperature = model.getTemperature(level, surfacePos);
            if (realTemperature > 2f && level.random.nextInt(TICKS_PER_SNOW_MELT_PER_SNOW_ACCUMULATION) == 0)
            {
                // Trigger melting
                handleSnowMelting(level, chunkPos, 1);
            }
            else if (realTemperature < -2f && isPrecipitating(model.getRain(currentCalendarTick), rainfall))
            {
                // Trigger accumulation
                handleSnowAccumulation(level, surfacePos);
            }
        }

        data.setLastRandomTick(chunk, currentTick);
    }

    private static BlockPos getRandomSurfacePos(ServerLevel level, ChunkPos chunkPos)
    {
        final BlockPos randomPos = level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15);
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, randomPos);
    }

    private static int countExistingSnowInChunk(ServerLevel level, ChunkPos chunkPos)
    {
        int total = 0;

        final SectionStorageAccessor<PoiSection> poi = getPoiManager(level);
        for (int sectionY = level.getMaxSection() - 1; sectionY >= level.getMinSection(); sectionY--)
        {
            final Set<PoiRecord> objects = getPoiRecords(poi, chunkPos, sectionY);
            if (objects != null)
            {
                total += objects.size();
            }
        }
        return total;
    }

    /**
     * Snow melting, including ice and icicles, is done randomly per POI chunk section. It can do up to {@code amount} removals,
     * which simulates snow melting at a consistent rate (snow/tick), rather than random ticks which would be poportional to
     * the amount of snow in the chunk.
     */
    private static void handleSnowMelting(ServerLevel level, ChunkPos chunkPos, int amount)
    {
        // PoiManager doesn't have the methods we need, and they look pretty slow. We just need a randomly sampled poi from this chunk, and we
        // don't really care about section. So this is likely more efficient.
        final SectionStorageAccessor<PoiSection> poi = getPoiManager(level);
        for (int sectionY = level.getMinSection(); sectionY < level.getMaxSection(); sectionY++)
        {
            final Set<PoiRecord> entries = getPoiRecords(poi, chunkPos, sectionY);
            if (entries != null && !entries.isEmpty())
            {
                // Handle two cases:
                // - removing all (amount >= entries.size())
                // - removing some (amount < entries.size())
                final List<PoiRecord> copyOfEntries = new ArrayList<>(entries); // Must be a mutable view, since we swap to random sample later
                if (amount >= copyOfEntries.size())
                {
                    for (PoiRecord entry : copyOfEntries)
                    {
                        removeSnowAt(level, entry.getPos());
                    }
                    amount -= copyOfEntries.size();
                }
                else
                {
                    final List<PoiRecord> sampleOfEntries = Helpers.uniqueRandomSample(copyOfEntries, amount, level.random);
                    for (PoiRecord entry : sampleOfEntries)
                    {
                        removeSnowAt(level, entry.getPos());
                    }
                    amount -= sampleOfEntries.size();
                }

                if (amount <= 0)
                {
                    return;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static SectionStorageAccessor<PoiSection> getPoiManager(ServerLevel level)
    {
        return (SectionStorageAccessor<PoiSection>) level.getPoiManager();
    }

    @Nullable
    private static Set<PoiRecord> getPoiRecords(SectionStorageAccessor<PoiSection> poi, ChunkPos chunkPos, int sectionY)
    {
        final long sectionKey = SectionPos.asLong(chunkPos.x, sectionY, chunkPos.z);
        final Optional<PoiSection> section = poi.invoke$getOrLoad(sectionKey);
        return section.isPresent()
            ? ((PoiSectionAccessor) section.get()).accessor$byType().get(CLIMATE)
            : null;
    }

    private static void handleSnowAccumulation(ServerLevel level, BlockPos surfacePos)
    {
        // Handle smoother snow placement: if there's an adjacent position with less snow, switch to that position instead
        // Additionally, handle up to two block tall plants if they can be piled
        // This means we need to check three levels deep
        BlockPos groundPos, belowGroundPos;

        if (placeSnowOrSnowPile(level, surfacePos)) return;
        if (placeSnowOrSnowPile(level, groundPos = surfacePos.below())) return;
        if (placeSnowOrSnowPile(level, belowGroundPos = surfacePos.below(2))) return;

        // Otherwise, try placing an ice pile
        // First, since we want to handle water with a single block above, if we find no water, but we find one below, we choose that instead
        // However, we have to also exclude ice here, since we don't intend to freeze two layers down
        BlockState groundState = level.getBlockState(groundPos);
        if (isIce(groundState))
        {
            return;
        }
        if (groundState.getFluidState().getType() != Fluids.WATER)
        {
            groundPos = belowGroundPos;
            groundState = level.getBlockState(groundPos);
        }

        IcePileBlock.placeIcePileOrIce(level, groundPos, groundState, false);

        // Then place icicles at a lower rate, under overhangs. The lower rate is because the search for icicles is mildly expensive of a check
        if (level.random.nextInt(16) == 0)
        {
            // Place icicles under overhangs
            final BlockPos iciclePos = findIcicleLocation(level, surfacePos);
            if (iciclePos != null)
            {
                BlockPos posAbove = iciclePos.above();
                BlockState stateAbove = level.getBlockState(posAbove);
                if (Helpers.isBlock(stateAbove, BlockTags.ICE))
                {
                    return;
                }
                if (Helpers.isBlock(stateAbove, TFCBlocks.ICICLE.get()))
                {
                    level.setBlock(posAbove, stateAbove.setValue(ThinSpikeBlock.TIP, false), 3 | 16);
                }
                level.setBlock(iciclePos, TFCBlocks.ICICLE.get().defaultBlockState().setValue(ThinSpikeBlock.TIP, true), 3);
            }
        }
    }

    /**
     * @return {@code true} if a snow block or snow pile was placed.
     */
    private static boolean placeSnowOrSnowPile(ServerLevel level, BlockPos initialPos)
    {
        // First, try and find an optimal position, to smoothen out snow accumulation
        // This will only move to the side, if we're currently at a snow location
        final BlockPos pos = findOptimalSnowLocation(level, initialPos, level.getBlockState(initialPos));
        final BlockState state = level.getBlockState(pos);

        // If we didn't move to the side, then we still need to pass a can see sky check
        // If we did, we might've moved under an overhang from a previously valid snow location
        if (initialPos.equals(pos) && !level.canSeeSky(pos))
        {
            return false;
        }
        return placeSnowOrSnowPileAt(level, pos, state);
    }

    private static boolean placeSnowOrSnowPileAt(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Then, handle possibilities
        if (SnowPileBlock.canPlaceSnowPile(level, pos, state))
        {
            SnowPileBlock.placeSnowPile(level, pos, state, false);
            return true;
        }
        else if (state.getBlock() instanceof KrummholzBlock)
        {
            KrummholzBlock.updateFreezingInColumn(level, pos, true);
        }
        else if (state.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(level, pos))
        {
            // Vanilla snow placement (single layers)
            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
            return true;
        }
        else
        {
            // Fills cauldrons with snow
            state.getBlock().handlePrecipitation(state, level, pos, Biome.Precipitation.SNOW);
        }
        return false;
    }

    /**
     * Smoothens out snow creation, so it doesn't create as uneven piles, by moving snowfall to adjacent positions where possible.
     */
    private static BlockPos findOptimalSnowLocation(ServerLevel level, BlockPos pos, BlockState state)
    {
        BlockPos targetPos = null;
        int found = 0;
        if (isSnow(state))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                final BlockPos adjPos = pos.relative(direction);
                final BlockState adjState = level.getBlockState(adjPos);
                if ((adjState.isAir() || Helpers.isBlock(adjState.getBlock(), TFCTags.Blocks.CAN_BE_SNOW_PILED))
                    && Blocks.SNOW.defaultBlockState().canSurvive(level, adjPos))
                {
                    found++;
                    if (targetPos == null || level.random.nextInt(found) == 0)
                    {
                        targetPos = adjPos;
                    }
                }
            }
            if (targetPos != null)
            {
                return targetPos;
            }
        }
        return pos;
    }

    @Nullable
    private static BlockPos findIcicleLocation(ServerLevel level, BlockPos pos)
    {
        final Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(level.random);
        BlockPos adjacentPos = pos.relative(side);
        final int adjacentHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, adjacentPos.getX(), adjacentPos.getZ());
        BlockPos foundPos = null;

        int found = 0;
        for (int y = 0; y < adjacentHeight; y++)
        {
            final BlockState stateAt = level.getBlockState(adjacentPos);
            final BlockPos posAbove = adjacentPos.above();
            final BlockState stateAbove = level.getBlockState(posAbove);
            if (stateAt.isAir() && (stateAbove.getBlock() == TFCBlocks.ICICLE.get() || stateAbove.isFaceSturdy(level, posAbove, Direction.DOWN)))
            {
                found++;
                if (foundPos == null || level.random.nextInt(found) == 0)
                {
                    foundPos = adjacentPos;
                }
            }
            adjacentPos = posAbove;
        }

        if (foundPos == null)
        {
            return null;
        }

        // Ensure that icicles are always below a maximum length, which is determined by location (so that each not every location gets the same length).
        // This is technically a weird heuristic (icicle -> block -> icicle) might mess it up, but not in any meaningful way that is player visible
        final int maxLength = 1 + (Helpers.hash(7189237951231L, pos.getX(), 0, pos.getZ()) % 3);
        if (level.getBlockState(foundPos.above(maxLength)).getBlock() == TFCBlocks.ICICLE.get())
        {
            return null;
        }

        return foundPos;
    }

    /**
     * Removes snow, ice, and icicles. For icicles, we search downwards to find the lowest icicle to melt first.
     */
    private static void removeSnowAt(ServerLevel level, BlockPos pos)
    {
        // Snow melting - both snow and snow piles
        BlockState state = level.getBlockState(pos);
        if (isSnow(state))
        {
            // When melting snow, we melt layers at +2 from expected, while the temperature is still below zero
            // This slowly reduces massive excess amounts of snow, if they're present, but doesn't actually start melting snow a lot when we're still below freezing.
            SnowPileBlock.removePileOrSnow(level, pos, state);
        }
        else if (state.getBlock() instanceof KrummholzBlock)
        {
            KrummholzBlock.updateFreezingInColumn(level, pos, false);
        }
        else if (isIce(state))
        {
            IcePileBlock.removeIcePileOrIce(level, pos, state);
        }
        else if (state.getBlock() == TFCBlocks.ICICLE.get())
        {
            // Scan downwards to find the lowest icicle in the column to melt
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

            cursor.setWithOffset(pos, Direction.DOWN);
            BlockState belowState = level.getBlockState(cursor);
            while (belowState.getBlock() == TFCBlocks.ICICLE.get())
            {
                cursor.move(Direction.DOWN);
                belowState = level.getBlockState(cursor);
            }

            cursor.move(Direction.UP);
            level.removeBlock(cursor, false); // Remove the icicle
            cursor.move(Direction.UP);

            // Update the block above, if it is also an icicle
            final BlockState stateAbove = level.getBlockState(cursor);
            if (stateAbove.getBlock() == TFCBlocks.ICICLE.get())
            {
                level.setBlock(cursor, stateAbove.setValue(IcicleBlock.TIP, true), Block.UPDATE_ALL);
            }
        }
    }

    public static boolean isSnow(BlockState state)
    {
        return state.getBlock() == Blocks.SNOW || state.getBlock() == TFCBlocks.SNOW_PILE.get();
    }

    public static boolean isIce(BlockState state)
    {
        return state.getBlock() == Blocks.ICE || state.getBlock() == TFCBlocks.ICE_PILE.get() || state.getBlock() == TFCBlocks.SEA_ICE.get();
    }
}
