/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.surface.builder.BadlandsSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.DuneSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.GrassyDunesSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.LowlandsSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.MountainSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.MudFlatsSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.NormalSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.OceanSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.RiverSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SaltFlatsSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.ShoreSurfaceBuilder;

import static net.dries007.tfc.world.biome.BiomeBuilder.*;

public final class TFCBiomes
{
    public static final ResourceKey<Registry<BiomeExtension>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("biome_extension"));
    public static final Registry<BiomeExtension> REGISTRY = new RegistryBuilder<>(KEY).create();
    public static final DeferredRegister<BiomeExtension> EXTENSIONS = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    // Aquatic biomes
    public static final BiomeExtension OCEAN = register("ocean", builder().heightmap(seed -> BiomeNoise.ocean(seed, -26, -12)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).salty().type(BiomeBlendType.OCEAN).noRivers()); // Ocean biome found near continents.
    public static final BiomeExtension OCEAN_REEF = register("ocean_reef", builder().heightmap(seed -> BiomeNoise.ocean(seed, -16, -8)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).salty().type(BiomeBlendType.OCEAN).noRivers()); // Ocean biome with reefs depending on climate. Could be interpreted as either barrier, fringe, or platform reefs.
    public static final BiomeExtension DEEP_OCEAN = register("deep_ocean", builder().heightmap(seed -> BiomeNoise.ocean(seed, -30, -16)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).type(BiomeBlendType.OCEAN).salty().noRivers()); // Deep ocean biome covering most all oceans.
    public static final BiomeExtension DEEP_OCEAN_TRENCH = register("deep_ocean_trench", builder().heightmap(seed -> BiomeNoise.oceanRidge(seed, -30, -16)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).type(BiomeBlendType.OCEAN).salty().noRivers()); // Deeper ocean with sharp relief carving to create very deep trenches

    // Low biomes
    public static final BiomeExtension PLAINS = register("plains", builder().heightmap(seed -> BiomeNoise.hills(seed, 4, 10)).surface(NormalSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.WIDE)); // Very flat, slightly above sea level.
    public static final BiomeExtension HILLS = register("hills", builder().heightmap(seed -> BiomeNoise.hills(seed, -5, 16)).surface(NormalSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.WIDE)); // Small hills, slightly above sea level.
    public static final BiomeExtension LOWLANDS = register("lowlands", builder().heightmap(BiomeNoise::lowlands).surface(LowlandsSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().type(RiverBlendType.WIDE).noSandyRiverShores()); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeExtension SALT_MARSH = register("salt_marsh", builder().heightmap(BiomeNoise::lowlands).surface(LowlandsSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().salty().type(RiverBlendType.WIDE).noSandyRiverShores()); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeExtension LOW_CANYONS = register("low_canyons", builder().heightmap(seed -> BiomeNoise.canyons(seed, -8, 21)).surface(NormalSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().type(RiverBlendType.WIDE).noSandyRiverShores()); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeExtension ROLLING_HILLS = register("rolling_hills", builder().heightmap(seed -> BiomeNoise.hills(seed, -5, 28)).surface(NormalSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.CANYON)); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeExtension HIGHLANDS = register("highlands", builder().heightmap(BiomeNoise::sharpHills).surface(NormalSurfaceBuilder.ROCKY).spawnable().type(RiverBlendType.CANYON)); // Hills with sharp, exposed rocky areas.
    public static final BiomeExtension BADLANDS = register("badlands", builder().heightmap(BiomeNoise::badlands).surface(BadlandsSurfaceBuilder.NORMAL).spawnable().type(RiverBlendType.CANYON)); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeExtension INVERTED_BADLANDS = register("inverted_badlands", builder().heightmap(BiomeNoise::bryceCanyon).surface(BadlandsSurfaceBuilder.INVERTED).spawnable().type(RiverBlendType.CANYON)); // Inverted badlands: hills with additive ridges, similar to vanilla bryce canyon mesas.
    public static final BiomeExtension PLATEAU = register("plateau", builder().heightmap(seed -> BiomeNoise.hills(seed, 20, 30)).surface(MountainSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.TALL_CANYON).noSandyRiverShores()); // Very high area, very flat top.
    public static final BiomeExtension CANYONS = register("canyons", builder().heightmap(seed -> BiomeNoise.canyons(seed, -2, 40)).surface(NormalSurfaceBuilder.INSTANCE).volcanoes(6, 14, 30, 28).spawnable().type(RiverBlendType.CANYON).noSandyRiverShores()); // Medium height with snake like ridges, minor volcanic activity

    // High biomes
    public static final BiomeExtension MOUNTAINS = register("mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 70)).surface(MountainSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.CAVE)); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeExtension OLD_MOUNTAINS = register("old_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 16, 40)).surface(MountainSurfaceBuilder.INSTANCE).spawnable().type(RiverBlendType.CAVE)); // Rounded top mountains, very large hills.
    public static final BiomeExtension OCEANIC_MOUNTAINS = register("oceanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).aquiferHeightOffset(-8).salty().spawnable().type(RiverBlendType.CAVE)); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeExtension VOLCANIC_MOUNTAINS = register("volcanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 60)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(4, 25, 50, 40).type(RiverBlendType.CAVE)); // Volcanic mountains - slightly smaller, but with plentiful tall volcanoes
    public static final BiomeExtension VOLCANIC_OCEANIC_MOUNTAINS = register("volcanic_oceanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, -24, 50)).surface(MountainSurfaceBuilder.INSTANCE).aquiferHeightOffset(-8).salty().volcanoes(2, -12, 50, 20).type(RiverBlendType.CAVE)); // Volcanic oceanic islands. Slightly smaller and lower but with very plentiful volcanoes

    // Shores
    public static final BiomeExtension SHORE = register("shore", builder().heightmap(BiomeNoise::shore).surface(ShoreSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).type(BiomeBlendType.LAND).salty().shore().type(RiverBlendType.WIDE).noRivers().noSandyRiverShores()); // Standard shore / beach. Material will vary based on location
    public static final BiomeExtension TIDAL_FLATS = register("tidal_flats", builder().heightmap(BiomeNoise::tidalFlats).surface(ShoreSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).type(BiomeBlendType.OCEAN).salty().shore().type(RiverBlendType.WIDE).noRivers().noSandyRiverShores());

    // Water
    public static final BiomeExtension LAKE = register("lake", builder().heightmap(BiomeNoise::lake).surface(NormalSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).type(BiomeBlendType.LAKE).type(RiverBlendType.WIDE).noRivers());
    public static final BiomeExtension RIVER = register("river", builder().surface(RiverSurfaceBuilder.INSTANCE));

    // Lakes
    public static final BiomeExtension MOUNTAIN_LAKE = register("mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 70)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).type(BiomeBlendType.LAKE).noRivers());
    public static final BiomeExtension OLD_MOUNTAIN_LAKE = register("old_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).type(BiomeBlendType.LAKE).noRivers());
    public static final BiomeExtension OCEANIC_MOUNTAIN_LAKE = register("oceanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).salty().type(BiomeBlendType.LAKE).noRivers());
    public static final BiomeExtension VOLCANIC_MOUNTAIN_LAKE = register("volcanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 60)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(4, 25, 50, 40).carving(BiomeNoise::undergroundLakes).type(BiomeBlendType.LAKE).noRivers());
    public static final BiomeExtension VOLCANIC_OCEANIC_MOUNTAIN_LAKE = register("volcanic_oceanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -24, 50)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(2, -12, 50, 20).carving(BiomeNoise::undergroundLakes).salty().type(BiomeBlendType.LAKE).noRivers());

    public static final BiomeExtension PLATEAU_LAKE = register("plateau_lake", builder().heightmap(seed -> BiomeNoise.hills(seed, 20, 30)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).type(BiomeBlendType.LAKE).noRivers());

    // Dry Biomes
    public static final BiomeExtension MUD_FLATS = register("mud_flats", builder().heightmap(BiomeNoise::flats).surface(MudFlatsSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().type(RiverBlendType.WIDE).noSandyRiverShores());
    public static final BiomeExtension SALT_FLATS = register("salt_flats", builder().heightmap(BiomeNoise::saltFlats).surface(SaltFlatsSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).salty().spawnable().type(RiverBlendType.WIDE).noSandyRiverShores());
    public static final BiomeExtension DUNE_SEA = register("dune_sea", builder().heightmap(seed -> BiomeNoise.dunes(seed, 2, 16)).surface(DuneSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().type(RiverBlendType.WIDE));
    public static final BiomeExtension GRASSY_DUNES = register("grassy_dunes", builder().heightmap(seed -> BiomeNoise.dunes(seed, 2, 16)).surface(GrassyDunesSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable().type(RiverBlendType.WIDE));

    public static BiomeExtension getExtensionOrThrow(LevelAccessor level, Biome biome)
    {
        return Objects.requireNonNull(getExtension(level, biome), () -> "Biome: " + level.registryAccess().registryOrThrow(Registries.BIOME).getId(biome));
    }

    public static boolean hasExtension(CommonLevelAccessor level, Biome biome)
    {
        return getExtension(level, biome) != null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static BiomeExtension getExtension(CommonLevelAccessor level, Biome biome)
    {
        return ((BiomeBridge) (Object) biome).tfc$getExtension(level);
    }

    @Nullable
    @ApiStatus.Internal // Use `getExtension`, this is only to find load the cache
    public static BiomeExtension findExtension(CommonLevelAccessor level, Biome biome)
    {
        return level.registryAccess()
            .registryOrThrow(Registries.BIOME)
            .getResourceKey(biome)
            .map(key -> REGISTRY.get(ResourceKey.create(KEY, key.location())))
            .orElse(null);
    }

    private static BiomeExtension register(String name, BiomeBuilder builder)
    {
        final ResourceLocation id = Helpers.identifier(name);
        final ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, id);
        final BiomeExtension extension = builder.build(key);

        EXTENSIONS.register(name, () -> extension);

        return extension;
    }
}