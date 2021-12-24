/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

/**
 * Settings for noise sampling, specific to a given chunk generator, level, and individual chunk.
 * Immutable and therefore thread safe, although in practice only used for a single chunk.
 */
public record ChunkNoiseSamplingSettings(int minY, int cellCountXZ, int cellCountY, int cellWidth, int cellHeight, int firstCellX, int firstCellY, int firstCellZ) {}
