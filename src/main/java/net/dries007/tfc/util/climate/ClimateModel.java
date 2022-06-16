/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Represents a model of the climate for a dimension.
 */
public interface ClimateModel
{
    float MINIMUM_RAINFALL = 0f;
    float MAXIMUM_RAINFALL = 500f;

    /**
     * The type of this climate model.
     * Must be registered through {@link Climate#register(ResourceLocation, Supplier)}
     */
    ClimateModelType type();

    /**
     * Get the temperature at a given position, and timestamp.
     *
     * @return A temperature, in degrees Celsius. Typically in the range [-40, 40]
     */
    float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth);

    /**
     * Get the average annual temperature for a given position.
     *
     * @return A temperature, in degrees Celsius. Typically in the range [-25, 25]
     */
    float getAverageTemperature(LevelReader level, BlockPos pos);

    /**
     * Get the average annual rainfall for a given position.
     * Should be <strong>time invariant</strong>.
     *
     * @return The average annual rainfall, roughly equivalent to mm/year. Should be in the range [0, 500]
     */
    float getRainfall(LevelReader level, BlockPos pos);

    /**
     * @return A value in the range [0, 1] scaling the sky fog as a % of the render distance
     */
    default float getFogginess(LevelReader level, BlockPos pos, long calendarTime)
    {
        return 0f;
    }

    /**
     * @return A value in the range [0, 1] scaling the water fog as a % of render distance
     */
    default float getWaterFogginess(LevelReader level, BlockPos pos, long calendarTime)
    {
        return 1f;
    }

    /**
     * Update a chunk on load with climate specific modifications, such as melting or freezing blocks.
     */
    default void onChunkLoad(WorldGenLevel level, ChunkAccess chunk, ChunkData chunkData) {}

    /**
     * Update a climate model when a world loads, just after the climate model is selected.
     */
    default void onWorldLoad(ServerLevel level) {}

    /**
     * Allows this climate model to write some data to be synced to client automatically
     */
    default void onSyncToClient(FriendlyByteBuf buffer) {}

    /**
     * @see #onSyncToClient(FriendlyByteBuf)
     */
    default void onReceiveOnClient(FriendlyByteBuf buffer) {}
}
