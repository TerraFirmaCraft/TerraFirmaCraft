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
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.surface.builder.*;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.world.biome.BiomeBuilder.builder;

public final class TFCBiomes
{
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);

    private static final Map<ResourceKey<Biome>, BiomeExtension> EXTENSIONS = new IdentityHashMap<>();

    // Aquatic biomes
    public static final BiomeExtension OCEAN = register("ocean", builder().heightmap(seed -> BiomeNoise.ocean(seed, -26, -12)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).salty().group(BiomeExtension.Group.OCEAN)); // Ocean biome found near continents.
    public static final BiomeExtension OCEAN_REEF = register("ocean_reef", builder().heightmap(seed -> BiomeNoise.ocean(seed, -16, -8)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).salty().group(BiomeExtension.Group.OCEAN)); // Ocean biome with reefs depending on climate. Could be interpreted as either barrier, fringe, or platform reefs.
    public static final BiomeExtension DEEP_OCEAN = register("deep_ocean", builder().heightmap(seed -> BiomeNoise.ocean(seed, -30, -16)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).group(BiomeExtension.Group.OCEAN).salty()); // Deep ocean biome covering most all oceans.
    public static final BiomeExtension DEEP_OCEAN_TRENCH = register("deep_ocean_trench", builder().heightmap(seed -> BiomeNoise.oceanRidge(seed, -30, -16)).surface(OceanSurfaceBuilder.INSTANCE).aquiferHeightOffset(-24).group(BiomeExtension.Group.OCEAN).salty()); // Deeper ocean with sharp relief carving to create very deep trenches

    // Low biomes
    public static final BiomeExtension PLAINS = register("plains", builder().heightmap(seed -> BiomeNoise.hills(seed, 4, 10)).surface(NormalSurfaceBuilder.INSTANCE).spawnable()); // Very flat, slightly above sea level.
    public static final BiomeExtension HILLS = register("hills", builder().heightmap(seed -> BiomeNoise.hills(seed, -5, 16)).surface(NormalSurfaceBuilder.INSTANCE).spawnable()); // Small hills, slightly above sea level.
    public static final BiomeExtension LOWLANDS = register("lowlands", builder().heightmap(BiomeNoise::lowlands).surface(LowlandsSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable()); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeExtension LOW_CANYONS = register("low_canyons", builder().heightmap(seed -> BiomeNoise.canyons(seed, -8, 21)).surface(NormalSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).spawnable()); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeExtension ROLLING_HILLS = register("rolling_hills", builder().heightmap(seed -> BiomeNoise.hills(seed, -5, 28)).surface(NormalSurfaceBuilder.INSTANCE).spawnable()); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeExtension BADLANDS = register("badlands", builder().heightmap(BiomeNoise::badlands).surface(BadlandsSurfaceBuilder.NORMAL).spawnable()); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeExtension INVERTED_BADLANDS = register("inverted_badlands", builder().heightmap(BiomeNoise::bryceCanyon).surface(BadlandsSurfaceBuilder.INVERTED).spawnable()); // Inverted badlands: hills with additive ridges, similar to vanilla bryce canyon mesas.
    public static final BiomeExtension PLATEAU = register("plateau", builder().heightmap(seed -> BiomeNoise.hills(seed, 20, 30)).surface(MountainSurfaceBuilder.INSTANCE).spawnable()); // Very high area, very flat top.
    public static final BiomeExtension CANYONS = register("canyons", builder().heightmap(seed -> BiomeNoise.canyons(seed, -2, 40)).surface(NormalSurfaceBuilder.INSTANCE).volcanoes(6, 14, 30, 28).spawnable()); // Medium height with snake like ridges, minor volcanic activity

    // High biomes
    public static final BiomeExtension MOUNTAINS = register("mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 70)).surface(MountainSurfaceBuilder.INSTANCE).spawnable()); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeExtension OLD_MOUNTAINS = register("old_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 16, 40)).surface(MountainSurfaceBuilder.INSTANCE).spawnable()); // Rounded top mountains, very large hills.
    public static final BiomeExtension OCEANIC_MOUNTAINS = register("oceanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).aquiferHeightOffset(-8).salty().spawnable()); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeExtension VOLCANIC_MOUNTAINS = register("volcanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 60)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(4, 25, 50, 40)); // Volcanic mountains - slightly smaller, but with plentiful tall volcanoes
    public static final BiomeExtension VOLCANIC_OCEANIC_MOUNTAINS = register("volcanic_oceanic_mountains", builder().heightmap(seed -> BiomeNoise.mountains(seed, -24, 50)).surface(MountainSurfaceBuilder.INSTANCE).aquiferHeightOffset(-8).salty().volcanoes(2, -12, 50, 20)); // Volcanic oceanic islands. Slightly smaller and lower but with very plentiful volcanoes

    // Shores
    public static final BiomeExtension SHORE = register("shore", builder().heightmap(BiomeNoise::shore).surface(ShoreSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).group(BiomeExtension.Group.OCEAN).salty()); // Standard shore / beach. Material will vary based on location

    // Water
    public static final BiomeExtension LAKE = register("lake", builder().heightmap(BiomeNoise::lake).surface(NormalSurfaceBuilder.INSTANCE).aquiferHeightOffset(-16).group(BiomeExtension.Group.LAKE));
    public static final BiomeExtension RIVER = register("river", builder().noise(BiomeNoise::riverSampler).surface(NormalSurfaceBuilder.INSTANCE).aquiferHeight(h -> TFCChunkGenerator.SEA_LEVEL_Y - 16).group(BiomeExtension.Group.RIVER));

    // Mountain Fresh water / carving biomes
    public static final BiomeExtension MOUNTAIN_RIVER = register("mountain_river", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 70)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundRivers).group(BiomeExtension.Group.RIVER));
    public static final BiomeExtension OLD_MOUNTAIN_RIVER = register("old_mountain_river", builder().heightmap(seed -> BiomeNoise.mountains(seed, 16, 40)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundRivers).group(BiomeExtension.Group.RIVER));
    public static final BiomeExtension OCEANIC_MOUNTAIN_RIVER = register("oceanic_mountain_river", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundRivers).salty().group(BiomeExtension.Group.RIVER));
    public static final BiomeExtension VOLCANIC_MOUNTAIN_RIVER = register("volcanic_mountain_river", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 60)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(4, 25, 50, 40).carving(BiomeNoise::undergroundRivers).group(BiomeExtension.Group.RIVER));
    public static final BiomeExtension VOLCANIC_OCEANIC_MOUNTAIN_RIVER = register("volcanic_oceanic_mountain_river", builder().heightmap(seed -> BiomeNoise.mountains(seed, -24, 50)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(2, -12, 50, 20).carving(BiomeNoise::undergroundRivers).salty().group(BiomeExtension.Group.RIVER));

    public static final BiomeExtension MOUNTAIN_LAKE = register("mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 70)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).group(BiomeExtension.Group.LAKE));
    public static final BiomeExtension OLD_MOUNTAIN_LAKE = register("old_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).group(BiomeExtension.Group.LAKE));
    public static final BiomeExtension OCEANIC_MOUNTAIN_LAKE = register("oceanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -16, 60)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).salty().group(BiomeExtension.Group.LAKE));
    public static final BiomeExtension VOLCANIC_MOUNTAIN_LAKE = register("volcanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, 10, 60)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(4, 25, 50, 40).carving(BiomeNoise::undergroundLakes).group(BiomeExtension.Group.LAKE));
    public static final BiomeExtension VOLCANIC_OCEANIC_MOUNTAIN_LAKE = register("volcanic_oceanic_mountain_lake", builder().heightmap(seed -> BiomeNoise.mountains(seed, -24, 50)).surface(MountainSurfaceBuilder.INSTANCE).volcanoes(2, -12, 50, 20).carving(BiomeNoise::undergroundLakes).salty().group(BiomeExtension.Group.LAKE));

    public static final BiomeExtension PLATEAU_LAKE = register("plateau_lake", builder().heightmap(seed -> BiomeNoise.hills(seed, 20, 30)).surface(MountainSurfaceBuilder.INSTANCE).carving(BiomeNoise::undergroundLakes).group(BiomeExtension.Group.LAKE));


    public static BiomeExtension getExtensionOrThrow(LevelAccessor level, Biome biome)
    {
        return Objects.requireNonNull(getExtension(level, biome), () -> "Biome: " + biome.getRegistryName());
    }

    public static boolean hasExtension(CommonLevelAccessor level, Biome biome)
    {
        return getExtension(level, biome) != null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static BiomeExtension getExtension(CommonLevelAccessor level, Biome biome)
    {
        return ((BiomeBridge) (Object) biome).tfc$getExtension(() -> findExtension(level, biome));
    }

    public static Collection<ResourceKey<Biome>> getAllKeys()
    {
        return EXTENSIONS.keySet();
    }

    public static Collection<BiomeExtension> getExtensions()
    {
        return EXTENSIONS.values();
    }

    public static Collection<ResourceLocation> getExtensionKeys()
    {
        return EXTENSIONS.keySet().stream().map(ResourceKey::location).toList();
    }

    @Nullable
    public static BiomeExtension getById(ResourceLocation id)
    {
        return EXTENSIONS.get(ResourceKey.create(Registry.BIOME_REGISTRY, id));
    }

    @Nullable
    private static BiomeExtension findExtension(CommonLevelAccessor level, Biome biome)
    {
        final RegistryAccess registryAccess = level.registryAccess();
        final Registry<Biome> registry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        return registry.getResourceKey(biome).map(EXTENSIONS::get).orElse(null);
    }

    private static BiomeExtension register(String name, BiomeBuilder builder)
    {
        final ResourceLocation id = Helpers.identifier(name);
        final ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, id);
        final BiomeExtension variants = builder.build(key);

        EXTENSIONS.put(key, variants);
        TFCBiomes.BIOMES.register(name, OverworldBiomes::theVoid);

        return variants;
    }
}