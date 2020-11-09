/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.dries007.tfc.util.function.FromByteFunction;
import net.dries007.tfc.util.function.ToByteFunction;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class Helpers
{
    /**
     * Default {@link ResourceLocation}, except with a TFC namespace
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
            JsonObject objectsJson = JSONUtils.getAsJsonObject(obj, path);
            for (K expectedKey : keyValues)
            {
                String jsonKey = keyStringMapper.apply(expectedKey);
                ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(objectsJson, jsonKey));
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
                    ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(objectsJson, jsonKey));
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
     * Maps a {@link Supplier} to an {@link Optional} by swallowing any runtime exceptions.
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
     * Like {@link Optional#map(Function)} but for suppliers. Does not unbox the provided supplier
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
     * Normally, one would just call {@link IWorld#isClientSide()}
     * HOWEVER
     * There exists a BIG HUGE PROBLEM in very specific scenarios with this
     * Since World's isClientSide() actually returns the isClientSide boolean, which is set AT THE END of the World constructor, many things may happen before this is set correctly. Mostly involving world generation.
     * At this point, THE CLIENT WORLD WILL RETURN {@code false} to {@link IWorld#isClientSide()}
     *
     * So, this does a roundabout check "is this instanceof ClientWorld or not" without classloading shenanigans.
     */
    public static boolean isClientSide(IWorldReader world)
    {
        return world instanceof World ? !(world instanceof ServerWorld) : world.isClientSide();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTileEntity(IWorldReader world, BlockPos pos, Class<T> tileEntityClass)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTileEntityOrThrow(IWorldReader world, BlockPos pos, Class<T> tileEntityClass)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        throw new IllegalStateException("Expected a tile entity at " + pos + " of class " + tileEntityClass.getSimpleName());
    }

    /**
     * This returns the previous result of {@link ServerWorld#getBlockRandomPos(int, int, int, int)}.
     */
    public static BlockPos getPreviousRandomPos(int x, int y, int z, int yMask, int randValue)
    {
        int i = randValue >> 2;
        return new BlockPos(x + (i & 15), y + (i >> 16 & yMask), z + (i >> 8 & 15));
    }

    /**
     * You know this will work, and I know this will work, but this compiler looks pretty stupid.
     */
    public static <E> E resolveEither(Either<E, E> either)
    {
        return either.map(e -> e, e -> e);
    }

    public static <E, R> R resolveEither(Either<E, E> either, Function<E, R> map)
    {
        return either.map(map, map);
    }
}