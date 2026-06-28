/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;
import net.dries007.tfc.world.volcano.VolcanoVariant;

public class StratovolcanoPlacement extends PlacementModifier
{
    public static MapCodec<StratovolcanoPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("center", false).forGetter(c -> c.center),
        Codec.BOOL.optionalFieldOf("use_offset_center", false).forGetter(c -> c.useOffsetCenter),
        Codec.STRING.optionalFieldOf("variant", "all").forGetter(c -> c.variant),
        Codecs.UNIT_FLOAT.optionalFieldOf("min_easing", 0f).forGetter(c -> c.minEasing),
        Codecs.UNIT_FLOAT.optionalFieldOf("max_easing", 1f).forGetter(c -> c.maxEasing),
        Codecs.UNIT_FLOAT.optionalFieldOf("hash_min", 0f).forGetter(c -> c.hashMin),
        Codecs.UNIT_FLOAT.optionalFieldOf("hash_max", 1f).forGetter(c -> c.hashMax)
    ).apply(instance, StratovolcanoPlacement::new));

    final boolean center, useOffsetCenter;
    final float minEasing, maxEasing, hashMin, hashMax;
    final String variant;

    private final ThreadLocal<LocalContext<CenteredFeatureNoiseSampler>> localContext;

    public StratovolcanoPlacement(boolean center, boolean useOffsetCenter, String variant, float minEasing, float maxEasing, float hashMin, float hashMax)
    {
        this.center = center;
        this.useOffsetCenter = useOffsetCenter;
        this.variant = variant;
        this.minEasing = minEasing;
        this.maxEasing = maxEasing;
        this.hashMin = hashMin;
        this.hashMax = hashMax;
        this.localContext = ThreadLocal.withInitial(() -> null);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        final WorldGenLevel level = context.getLevel();
        final long seed = level.getSeed();

        LocalContext<CenteredFeatureNoiseSampler> local = localContext.get();
        if (local == null || local.seed != seed)
        {
            local = new LocalContext<>(seed, createContext(Seed.unsafeOf(seed)));
            localContext.set(local);
        }

        final Biome biome = level.getBiome(pos).value();
        final BiomeExtension extension = TFCBiomes.getExtensionOrThrow(level, biome);
        if (local.context.isValidBiome(extension))
        {
            final Cellular2D.Cell cell = local.context.getCellularNoise().cell(pos.getX(), pos.getZ());
            final VolcanoVariant variantAt = local.context.getVolcanoVariant(cell);
            if (variantAt != null && (variant.equals("all") || Objects.equals(variantAt.name(), variant)))
            {
                // Special handling for volcanoes with crater centers offset from the cell center
                if (useOffsetCenter)
                {
                    if (Objects.equals(variantAt.name(), "crater_lake"))
                    {
                        return getCraterLakePositions(level, pos, cell);

                    }
                    else if (Objects.equals(variantAt.name(), "kelimutu"))
                    {
                        return getKelimutuPositions(level, pos, cell);
                    }
                }

                // We define hash here, as offset craters use their distinct hash values
                final double hash = Helpers.hashDouble(cell.noise(), 3199);
                if (hash >= hashMin && hash <= hashMax)
                {
                    if (center)
                    {
                        final BlockPos centerPos = local.context.calculateCenter(pos, extension);
                        if (centerPos != null &&
                            SectionPos.blockToSectionCoord(centerPos.getX()) == SectionPos.blockToSectionCoord(pos.getX()) &&
                            SectionPos.blockToSectionCoord(centerPos.getZ()) == SectionPos.blockToSectionCoord(pos.getZ()) &&
                            // We only check whether the center biome is correct for the center version of the feature, because this check
                            // only works when the center is in the chunk we are placing within
                            local.context.isValidBiome(TFCBiomes.getExtensionOrThrow(level, level.getBiome(centerPos).value())))
                        {
                            return Stream.of(centerPos);
                        }
                    }
                    else
                    {
                        final double easing = local.context.calculateEasing(pos, extension);
                        if (easing > this.minEasing && easing < this.maxEasing)
                        {
                            return Stream.of(pos);
                        }
                    }
                }
            }
        }
        return Stream.empty();
    }

    // Returns center of the island, or points within a distance (up to 30 blocks) of the island center
    public Stream<BlockPos> getCraterLakePositions(WorldGenLevel level, BlockPos pos, Cellular2D.Cell cell)
    {
        double xOffset = -45 + 90 * Helpers.hashDouble(cell.noise(), 6);
        double zOffset = -45 + 90 * Helpers.hashDouble(cell.noise(), 7);
        final double centerX = cell.x() + xOffset;
        final double centerZ = cell.y() + zOffset;
        final double hash = Helpers.hashDouble(cell.noise(), 3200);
        if (hash >= hashMin && hash <= hashMax)
        {
            if (center)
            {
                final BlockPos centerPos = new BlockPos((int) centerX, 0, (int) centerZ);
                if (SectionPos.blockToSectionCoord(centerPos.getX()) == SectionPos.blockToSectionCoord(pos.getX()) &&
                    SectionPos.blockToSectionCoord(centerPos.getZ()) == SectionPos.blockToSectionCoord(pos.getZ()) &&
                    // We only check whether the center biome is correct for the center version of the feature, because this check
                    // only works when the center is in the chunk we are placing within
                    localContext.get().context.isValidBiome(TFCBiomes.getExtensionOrThrow(level, level.getBiome(centerPos).value())))
                {
                    return Stream.of(centerPos);
                }
            }
            else
            {
                final double distance = (pos.getX() - centerX) * (pos.getX() - centerX) + (pos.getZ() - centerZ) * (pos.getZ() - centerZ);
                // Within 30 blocks of the center
                final double easing = Mth.clampedMap(distance, 0, 900, 1, 0);
                if (easing > this.minEasing)
                {
                    return Stream.of(pos);
                }
            }
        }
        return Stream.empty();
    }

    // Returns the nearest center, or points within a distance (up to 50 blocks) of the nearest center
    public Stream<BlockPos> getKelimutuPositions(WorldGenLevel level, BlockPos pos, Cellular2D.Cell cell)
    {
        final double noise = cell.noise();
        final double center0X = cell.x();
        final double center0Z = cell.y();
        final double distance0 = (pos.getX() - center0X) * (pos.getX() - center0X) + (pos.getZ() - center0Z) * (pos.getZ() - center0Z);

        final double randOffsetX1 = 2 * (0.5 - Helpers.hashDouble(noise, 68));
        final double randOffsetZ1 = 2 * (0.5 - Helpers.hashDouble(noise, 69));
        final double xOffset1 = randOffsetX1 > 0 ? 20 + randOffsetX1 * 70 : -20 + randOffsetX1 * 70;
        final double zOffset1 = randOffsetZ1 > 0 ? 20 + randOffsetZ1 * 70 : -20 + randOffsetZ1 * 70;
        double center1X = center0X + xOffset1;
        double center1Z = center0Z + zOffset1;
        final double distance1 = (pos.getX() - center1X) * (pos.getX() - center1X) + (pos.getZ() - center1Z) * (pos.getZ() - center1Z);

        double distance, centerX, centerZ, hash;
        if (distance0 < distance1)
        {
            distance = distance0;
            centerX = center0X;
            centerZ = center0Z;
            hash = Helpers.hashDouble(noise, 3199);
        }
        else
        {
            distance = distance1;
            centerX = center1X;
            centerZ = center1Z;
            hash = Helpers.hashDouble(noise, 3201);
        }

        if (Helpers.hashDouble(noise, 1066) > 0.3)
        {
            final double randOffsetX2 = 2 * (0.5 - Helpers.hashDouble(noise, 71));
            final double randOffsetZ2 = 2 * (0.5 - Helpers.hashDouble(noise, 72));
            final double xOffset2 = randOffsetX2 > 0 ? 20 + randOffsetX2 * 70 : -20 + randOffsetX2 * 73;
            final double zOffset2 = randOffsetZ2 > 0 ? 20 + randOffsetZ2 * 70 : -20 + randOffsetZ2 * 74;
            double center2X = center0X + xOffset2;
            double center2Z = center0Z + zOffset2;
            final double distance2 = (pos.getX() - center2X) * (pos.getX() - center2X) + (pos.getZ() - center2Z) * (pos.getZ() - center2Z);

            if (distance2 < distance)
            {
                distance = distance2;
                centerX = center2X;
                centerZ = center2Z;
                hash = Helpers.hashDouble(noise, 3202);
            }
        }

        if (hash >= hashMin && hash <= hashMax)
        {
            if (center)
            {
                final BlockPos centerPos = new BlockPos((int) centerX, 0, (int) centerZ);
                if (SectionPos.blockToSectionCoord(centerPos.getX()) == SectionPos.blockToSectionCoord(pos.getX()) &&
                    SectionPos.blockToSectionCoord(centerPos.getZ()) == SectionPos.blockToSectionCoord(pos.getZ()) &&
                    // We only check whether the center biome is correct for the center version of the feature, because this check
                    // only works when the center is in the chunk we are placing within
                    localContext.get().context.isValidBiome(TFCBiomes.getExtensionOrThrow(level, level.getBiome(centerPos).value())))
                {
                    return Stream.of(centerPos);
                }
            }
            else
            {
                // Within 50 blocks of the nearest center
                final double easing = Mth.clampedMap(distance, 0, 2500, 1, 0);
                if (easing > this.minEasing)
                {
                    return Stream.of(pos);
                }
            }
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.STRATOVOLCANO.get();
    }

    protected CenteredFeatureNoiseSampler createContext(Seed seed)
    {
        return CenteredFeatureNoise.stratovolcano(seed);
    }

    private record LocalContext<CenteredFeatureNoiseSampler>(long seed, CenteredFeatureNoiseSampler context) {}
}
