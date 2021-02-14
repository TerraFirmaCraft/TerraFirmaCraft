/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.function.FromByteFunction;
import net.dries007.tfc.util.function.ToByteFunction;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class Helpers
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Default {  ResourceLocation}, except with a TFC namespace
     */
    public static ResourceLocation identifier(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }

    /**
     * Avoids IDE warnings by returning null for fields that are injected in by forge.
     *
     * @return Not null!
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T notNull()
    {
        return null;
    }

    public static <T> byte[] createByteArray(T[] array, ToByteFunction<T> byteConverter)
    {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            bytes[i] = byteConverter.get(array[i]);
        }
        return bytes;
    }

    public static <T> void createArrayFromBytes(byte[] byteArray, T[] array, FromByteFunction<T> byteConverter)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            array[i] = byteConverter.get(byteArray[i]);
        }
    }

    public static <K, V extends IForgeRegistryEntry<V>> Map<K, V> findRegistryObjects(JsonObject obj, String path, IForgeRegistry<V> registry, Collection<K> keyValues, NonNullFunction<K, String> keyStringMapper)
    {
        return findRegistryObjects(obj, path, registry, keyValues, Collections.emptyList(), keyStringMapper);
    }

    public static <K, V extends IForgeRegistryEntry<V>> Map<K, V> findRegistryObjects(JsonObject obj, String path, IForgeRegistry<V> registry, Collection<K> keyValues, Collection<K> optionalKeyValues, NonNullFunction<K, String> keyStringMapper)
    {
        if (obj.has(path))
        {
            Map<K, V> objects = new HashMap<>();
            JsonObject objectsJson = JSONUtils.getJsonObject(obj, path);
            for (K expectedKey : keyValues)
            {
                String jsonKey = keyStringMapper.apply(expectedKey);
                ResourceLocation id = new ResourceLocation(JSONUtils.getString(objectsJson, jsonKey));
                V registryObject = registry.getValue(id);
                if (registryObject == null)
                {
                    throw new JsonParseException("Unknown registry object: " + id);
                }
                objects.put(expectedKey, registryObject);
            }
            for (K optionalKey : optionalKeyValues)
            {
                String jsonKey = keyStringMapper.apply(optionalKey);
                if (objectsJson.has(jsonKey))
                {
                    ResourceLocation id = new ResourceLocation(JSONUtils.getString(objectsJson, jsonKey));
                    V registryObject = registry.getValue(id);
                    if (registryObject == null)
                    {
                        throw new JsonParseException("Unknown registry object: " + id);
                    }
                    objects.put(optionalKey, registryObject);
                }
            }
            return objects;
        }
        return Collections.emptyMap();
    }

    public static BlockState readBlockState(String block) throws JsonParseException
    {
        BlockStateParser parser = parseBlockState(block, false);
        if (parser.getState() != null)
        {
            return parser.getState();
        }
        throw new JsonParseException("Weird result, valid parse but not a block state: " + block);
    }

    public static BlockStateParser parseBlockState(String block, boolean allowTags) throws JsonParseException
    {
        StringReader reader = new StringReader(block);
        try
        {
            return new BlockStateParser(reader, allowTags).parse(false);
        }
        catch (CommandSyntaxException e)
        {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * Maps a {  Supplier} to an {  Optional} by swallowing any runtime exceptions.
     */
    public static <T> Optional<T> mapSafeOptional(Supplier<T> unsafeSupplier)
    {
        try
        {
            return Optional.of(unsafeSupplier.get());
        }
        catch (RuntimeException e)
        {
            return Optional.empty();
        }
    }

    /**
     * Like {  Optional#map(Function)} but for suppliers. Does not unbox the provided supplier
     */
    public static <T, R> Supplier<R> mapSupplier(Supplier<T> supplier, Function<T, R> mapper)
    {
        return () -> mapper.apply(supplier.get());
    }

    /**
     * Applies two possible consumers of a given lazy optional
     */
    public static <T> void ifPresentOrElse(LazyOptional<T> lazyOptional, Consumer<T> ifPresent, Runnable orElse)
    {
        lazyOptional.map(t -> {
            ifPresent.accept(t);
            return Unit.INSTANCE;
        }).orElseGet(() -> {
            orElse.run();
            return Unit.INSTANCE;
        });
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(Class<E> enumClass, Function<E, V> valueMapper)
    {
        return mapOfKeys(enumClass, key -> true, valueMapper);
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper, only using enum constants that match the provided predicate.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(Class<E> enumClass, Predicate<E> keyPredicate, Function<E, V> valueMapper)
    {
        return Arrays.stream(enumClass.getEnumConstants()).filter(keyPredicate).collect(Collectors.toMap(Function.identity(), valueMapper, (v, v2) -> v, () -> new EnumMap<>(enumClass)));
    }

    /**
     * Gets the translation key name for an enum. For instance, Metal.UNKNOWN would map to "tfc.enum.metal.unknown"
     */
    public static String getEnumTranslationKey(Enum<?> anEnum)
    {
        return getEnumTranslationKey(anEnum, anEnum.getDeclaringClass().getSimpleName());
    }

    /**
     * Gets the translation key name for an enum, using a custom name instead of the enum class name
     */
    public static String getEnumTranslationKey(Enum<?> anEnum, String enumName)
    {
        return String.join(".", MOD_ID, "enum", enumName, anEnum.name()).toLowerCase();
    }

    /**
     * Names a simple container provider.
     *
     * @return a singleton container provider
     */
    public static INamedContainerProvider createNamedContainerProvider(ITextComponent name, IContainerProvider provider)
    {
        return new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName()
            {
                return name;
            }

            @Nullable
            @Override
            public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
            {
                return provider.createMenu(windowId, inv, player);
            }
        };
    }

    /**
     * Normally, one would just call {  IWorld#isClientSide()}
     * HOWEVER
     * There exists a BIG HUGE PROBLEM in very specific scenarios with this
     * Since World's isClientSide() actually returns the isClientSide boolean, which is set AT THE END of the World constructor, many things may happen before this is set correctly. Mostly involving world generation.
     * At this point, THE CLIENT WORLD WILL RETURN {@code false} to {  IWorld#isClientSide()}
     *
     * So, this does a roundabout check "is this instanceof ClientWorld or not" without classloading shenanigans.
     */
    public static boolean isClientSide(IWorldReader world)
    {
        return world instanceof World ? !(world instanceof ServerWorld) : !world.isRemote();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTileEntity(IWorldReader world, BlockPos pos, Class<T> tileEntityClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTileEntityOrThrow(IWorldReader world, BlockPos pos, Class<T> tileEntityClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        throw new IllegalStateException("Expected a tile entity at " + pos + " of class " + tileEntityClass.getSimpleName());
    }

    /**
     * This returns the previous result of {  ServerWorld#getBlockRandomPos(int, int, int, int)}.
     */
    public static BlockPos getPreviousRandomPos(int x, int y, int z, int yMask, int randValue)
    {
        int i = randValue >> 2;
        return new BlockPos(x + (i & 15), y + (i >> 16 & yMask), z + (i >> 8 & 15));
    }

    public static BlockState getStateForPlacementWithFluid(IWorldReader world, BlockPos pos, BlockState state)
    {
        FluidState fluid = world.getFluidState(pos);
        if (state.getBlock() instanceof IFluidLoggable)
        {
            return ((IFluidLoggable) state.getBlock()).getStateWithFluid(state, fluid.getFluid());
        }
        return state;
    }

    /**
     * You know this will work, and I know this will work, but this compiler looks pretty stupid.
     */
    public static <E> E resolveEither(Either<E, E> either)
    {
        return either.map(e -> e, e -> e);
    }

    public static void slowEntityInBlock(Entity entity, float factor, int fallDamageReduction)
    {
        Vector3d motion = entity.getMotion();
        entity.setMotion(motion.mul(factor, motion.y < 0 ? factor : 1, factor));
        if (entity.fallDistance > fallDamageReduction)
        {
            entity.onLivingFall(entity.fallDistance - fallDamageReduction, 1.0f);
        }
        entity.fallDistance = 0;
    }

    public static void registerSimpleCapability(Class<?> clazz)
    {
        CapabilityManager.INSTANCE.register(clazz, new NoopStorage<>(), () -> {
            throw new UnsupportedOperationException("Creating default instances is not supported. Why would you ever do this");
        });
    }

    /**
     * Copy pasta from {  net.minecraft.entity.player.SpawnLocationHelper} except one that doesn't require the spawn block be equal to the surface builder config top block
     */
    @Nullable
    public static BlockPos findValidSpawnLocation(ServerWorld world, ChunkPos chunkPos)
    {
        final Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = chunkPos.getXStart(); x <= chunkPos.getXEnd(); ++x)
        {
            for (int z = chunkPos.getZStart(); z <= chunkPos.getZEnd(); ++z)
            {
                mutablePos.setPos(x, 0, z);

                final Biome biome = world.getBiome(mutablePos);
                final Heightmap motionBlockingHeight = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);//, x & 15, z & 15);
                final Heightmap worldSurfaceHeight = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE);// x & 15, z & 15);
                final Heightmap oceanFloorHeight = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR);// x & 15, z & 15);
                if (worldSurfaceHeight.getHeight(x&15,z&15) >= oceanFloorHeight.getHeight(x&15,z&15)
                    && biome.getMobSpawnInfo().isValidSpawnBiomeForPlayer())
                {
                    for (int y = 1 + motionBlockingHeight.getHeight(x&15,z&15); y >= oceanFloorHeight.getHeight(x&15,z&15); y--)
                    {
                        mutablePos.setPos(x, y, z);

                        final BlockState state = world.getBlockState(mutablePos);
                        if (!state.getFluidState().isEmpty())
                        {
                            break;
                        }

                        if (BlockTags.VALID_SPAWN.contains(state.getBlock()))
                        {
                            return mutablePos.up().toImmutable();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static BlockState copyProperties(BlockState copyTo, BlockState copyFrom)
    {
        for (Property<?> property : copyFrom.getProperties())
        {
            copyTo = copyProperty(copyTo, copyFrom, property);
        }
        return copyTo;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState copyTo, BlockState copyFrom, Property<T> property)
    {
        if (copyTo.hasProperty(property))
        {
            return copyTo.with(property, copyFrom.get(property));
        }
        return copyTo;
    }

    public static void damageCraftingItem(ItemStack stack, int amount)
    {
        PlayerEntity player = ForgeHooks.getCraftingPlayer(); // Mods may not set this properly
        if (player != null)
        {
            stack.damageItem(amount, player, entity -> {});
        }
        else
        {
            damageItem(stack, amount);
        }
    }

    /**
     * A replacement for {  ItemStack#hurtAndBreak(int, LivingEntity, Consumer)} when an entity is not present
     */
    public static void damageItem(ItemStack stack, int amount)
    {
        if (stack.isDamageable())
        {
            // There's no player here so we can't safely do anything.
            //amount = stack.getItem().damageItem(stack, amount, null, e -> {});
            if (stack.attemptDamageItem(amount, RANDOM, null))
            {
                stack.shrink(1);
                stack.setDamage(0);
            }
        }
    }

    /**
     * Copied from {  World#destroyBlock(BlockPos, boolean, Entity, int)}
     * Allows the loot context to be modified
     */
    @SuppressWarnings("deprecation")
    public static void destroyBlockAndDropBlocksManually(World worldIn, BlockPos pos, Consumer<LootContext.Builder> builder)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.isAir())
        {
            FluidState fluidstate = worldIn.getFluidState(pos);
            if (!(state.getBlock() instanceof AbstractFireBlock))
            {
                worldIn.playEvent(2001, pos, Block.getStateId(state));
            }

            if (worldIn instanceof ServerWorld)
            {
                TileEntity tileEntity = state.hasTileEntity() ? worldIn.getTileEntity(pos) : null;

                // Copied from Block.getDrops()
                LootContext.Builder lootContext = new LootContext.Builder((ServerWorld) worldIn)
                    .withRandom(worldIn.rand)
                    .withParameter(LootParameters..ORIGIN, Vector3d.atCenterOf(pos))
                    .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootParameters.THIS_ENTITY, null)
                    .withOptionalParameter(LootParameters.BLOCK_ENTITY, tileEntity);
                builder.accept(lootContext);
                state.getDrops(lootContext).forEach(stackToSpawn -> Block.popResource(worldIn, pos, stackToSpawn));
                state.spawnAfterBreak((ServerWorld) worldIn, pos, ItemStack.EMPTY);
            }
            worldIn.setBlockState(pos, fluidstate.createLegacyBlock(), 3, 512);
        }
    }
}