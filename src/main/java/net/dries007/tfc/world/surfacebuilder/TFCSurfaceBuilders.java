/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCSurfaceBuilders
{
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, MOD_ID);

    public static final RegistryObject<NormalSurfaceBuilder> NORMAL = register("normal", NormalSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<ThinSurfaceBuilder> THIN = register("thin", ThinSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<BadlandsSurfaceBuilder> BADLANDS = register("badlands", BadlandsSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<MountainSurfaceBuilder> MOUNTAINS = register("mountains", MountainSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<ShoreSurfaceBuilder> SHORE = register("shore", ShoreSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<UnderwaterSurfaceBuilder> UNDERWATER = register("underwater", UnderwaterSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final RegistryObject<FrozenUnderwaterSurfaceBuilder> FROZEN_UNDERWATER = register("frozen_underwater", FrozenUnderwaterSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);

    public static final RegistryObject<VolcanoesSurfaceBuilder> WITH_VOLCANOES = register("with_volcanoes", VolcanoesSurfaceBuilder::new, ParentedSurfaceBuilderConfig.CODEC);

    // Used for shores - red sand = normal beach sand, sandstone = variant beach sand (pink / black)
    public static final Lazy<SurfaceBuilderConfig> RED_SAND_CONFIG = config(() -> Blocks.RED_SAND);
    public static final Lazy<SurfaceBuilderConfig> RED_SANDSTONE_CONFIG = config(() -> Blocks.RED_SANDSTONE);
    public static final Lazy<SurfaceBuilderConfig> COBBLE_COBBLE_RED_SAND_CONFIG = config(() -> Blocks.COBBLESTONE, () -> Blocks.COBBLESTONE, () -> Blocks.RED_SAND);

    public static final Lazy<SurfaceBuilderConfig> BASALT_CONFIG = config(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.RAW));

    /**
     * Tries to apply a {  IContextSurfaceBuilder} if it exists, otherwise delegates to the standard method.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <C extends ISurfaceBuilderConfig> void applySurfaceBuilderWithContext(ConfiguredSurfaceBuilder<C> configuredSurfaceBuilder, IWorld worldIn, Random random, ChunkData chunkData, IChunk chunk, Biome biome, int posX, int posZ, int posY, double noise, long seed, BlockState defaultBlock, BlockState defaultFluid, int seaLevel)
    {
        configuredSurfaceBuilder.builder.setSeed(seed);
        if (configuredSurfaceBuilder.builder instanceof IContextSurfaceBuilder)
        {
            // Need an ugly cast here to verify the config type
            ((IContextSurfaceBuilder) configuredSurfaceBuilder.builder).applyWithContext(worldIn, chunkData, random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
        else
        {
            configuredSurfaceBuilder.builder.buildSurface(random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
    }

    public static <C extends ISurfaceBuilderConfig> void applySurfaceBuilder(ConfiguredSurfaceBuilder<C> surfaceBuilder, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        applySurfaceBuilder(surfaceBuilder.builder, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, surfaceBuilder.config);
    }

    /**
     * Runs a surface builder directly from a provided builder and config, and ensures noise was initialized beforehand.
     */
    public static <C extends ISurfaceBuilderConfig> void applySurfaceBuilder(SurfaceBuilder<C> surfaceBuilder, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config)
    {
        surfaceBuilder.setSeed(seed);
        surfaceBuilder.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
    }

    private static <C extends ISurfaceBuilderConfig, S extends SurfaceBuilder<C>> RegistryObject<S> register(String name, Function<Codec<C>, S> factory, Codec<C> codec)
    {
        return SURFACE_BUILDERS.register(name, () -> factory.apply(codec));
    }

    private static Lazy<SurfaceBuilderConfig> config(Supplier<? extends Block> all)
    {
        return config(all, all, all);
    }

    private static Lazy<SurfaceBuilderConfig> config(Supplier<? extends Block> top, Supplier<? extends Block> under, Supplier<? extends Block> underwater)
    {
        return Lazy.of(() -> new SurfaceBuilderConfig(top.get().getDefaultState(), under.get().getDefaultState(), underwater.get().getDefaultState()));
    }
}