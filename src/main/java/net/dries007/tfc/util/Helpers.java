/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.AbstractIterator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class Helpers
{
    public static final Direction[] DIRECTIONS = Direction.values();

    private static final Random RANDOM = new Random();
    private static final int PRIME_X = 501125321;
    private static final int PRIME_Y = 1136930381;

    /**
     * Default {@link ResourceLocation}, except with a TFC namespace
     */
    public static ResourceLocation identifier(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }

    /**
     * Filter method for TFC namespaced resources
     */
    public static <T extends IForgeRegistryEntry<T>> Stream<T> streamOurs(IForgeRegistry<T> registry)
    {
        return registry.getValues().stream()
            .filter(e -> {
                assert e.getRegistryName() != null;
                return e.getRegistryName().getNamespace().equals(MOD_ID);
            });
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
     * Flattens a homogeneous stream of {@code Collection<T>}, {@code Stream<T>} and {@code T}s together into a {@code Stream<T>}
     * Usage: {@code stream.flatMap(Helpers::flatten)}
     */
    @SuppressWarnings("unchecked")
    public static <R> Stream<? extends R> flatten(Object t)
    {
        return t instanceof Collection<?> c ? (Stream<? extends R>) c.stream() : (t instanceof Stream<?> s ? (Stream<? extends R>) s : Stream.of((R) t));
    }

    public static TranslatableComponent translateEnum(Enum<?> anEnum)
    {
        return new TranslatableComponent(getEnumTranslationKey(anEnum));
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
        return String.join(".", MOD_ID, "enum", enumName, anEnum.name()).toLowerCase(Locale.ROOT);
    }

    /**
     * Normally, one would just call {@link Level#isClientSide()}
     * HOWEVER
     * There exists a BIG HUGE PROBLEM in very specific scenarios with this
     * Since World's isClientSide() actually returns the isClientSide boolean, which is set AT THE END of the World constructor, many things may happen before this is set correctly. Mostly involving world generation.
     * At this point, THE CLIENT WORLD WILL RETURN {@code false} to {@link Level#isClientSide()}
     *
     * So, this does a roundabout check "is this instanceof ClientWorld or not" without classloading shenanigans.
     */
    public static boolean isClientSide(LevelReader world)
    {
        return world instanceof Level ? !(world instanceof ServerLevel) : world.isClientSide();
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Level getUnsafeLevel(Object maybeLevel)
    {
        if (maybeLevel instanceof Level level)
        {
            return level; // Most obvious case, if we can directly cast up to level.
        }
        if (maybeLevel instanceof WorldGenRegion)
        {
            return ((WorldGenRegion) maybeLevel).getLevel(); // Special case for world gen, when we can access the level unsafely
        }
        return null; // A modder has done a strange ass thing
    }

    public static BlockHitResult rayTracePlayer(Level level, Player player, ClipContext.Fluid mode)
    {
        return ItemProtectedAccessor.invokeGetPlayerPOVHitResult(level, player, mode);
    }

    /**
     * @deprecated Use {@link BlockGetter#getBlockEntity(BlockPos, BlockEntityType)} instead as it's safer
     */
    @Nullable
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T extends BlockEntity> T getBlockEntity(BlockGetter world, BlockPos pos, Class<T> tileEntityClass)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        return null;
    }

    public static <T> LazyOptional<T> getCapability(@Nullable ICapabilityProvider provider, Capability<T> capability)
    {
        return provider == null ? LazyOptional.empty() : provider.getCapability(capability);
    }

    public static void slowEntityInBlock(Entity entity, float factor, int fallDamageReduction)
    {
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.multiply(factor, motion.y < 0 ? factor : 1, factor));
        if (entity.fallDistance > fallDamageReduction)
        {
            entity.causeFallDamage(entity.fallDistance - fallDamageReduction, 1.0f, DamageSource.FALL);
        }
        entity.fallDistance = 0;
    }

    /**
     * Copy pasta from {@link net.minecraft.server.level.PlayerRespawnLogic} except one that doesn't require the spawn block be equal to the surface builder config top block
     */
    @Nullable
    public static BlockPos findValidSpawnLocation(ServerLevel world, ChunkPos chunkPos)
    {
        final LevelChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); ++x)
        {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); ++z)
            {
                mutablePos.set(x, 0, z);

                final Biome biome = world.getBiome(mutablePos);
                final int motionBlockingHeight = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
                final int worldSurfaceHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x & 15, z & 15);
                final int oceanFloorHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR, x & 15, z & 15);
                if (worldSurfaceHeight >= oceanFloorHeight && biome.getMobSettings().playerSpawnFriendly())
                {
                    for (int y = 1 + motionBlockingHeight; y >= oceanFloorHeight; y--)
                    {
                        mutablePos.set(x, y, z);

                        final BlockState state = world.getBlockState(mutablePos);
                        if (!state.getFluidState().isEmpty())
                        {
                            break;
                        }

                        if (BlockTags.VALID_SPAWN.contains(state.getBlock()))
                        {
                            return mutablePos.above().immutable();
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
            return copyTo.setValue(property, copyFrom.getValue(property));
        }
        return copyTo;
    }

    public static void damageCraftingItem(ItemStack stack, int amount)
    {
        Player player = ForgeHooks.getCraftingPlayer(); // Mods may not set this properly
        if (player != null)
        {
            stack.hurtAndBreak(amount, player, entity -> {});
        }
        else
        {
            damageItem(stack, amount);
        }
    }

    /**
     * A replacement for {@link ItemStack#hurtAndBreak(int, LivingEntity, Consumer)} when an entity is not present
     */
    public static void damageItem(ItemStack stack, int amount)
    {
        if (stack.isDamageableItem())
        {
            // There's no player here so we can't safely do anything.
            //amount = stack.getItem().damageItem(stack, amount, null, e -> {});
            if (stack.hurt(amount, RANDOM, null))
            {
                stack.shrink(1);
                stack.setDamageValue(0);
            }
        }
    }

    /**
     * Set a {@code stack}'s count to {@code count}, after respecting both the slot stack limit, and the stack's max stack size.
     * Returns the difference between the count that was attempted to set, and the actual count set.
     */
    public static int setCountSafely(ItemStack stack, int count, int slotStackLimit)
    {
        final int initialCount = count;
        if (count > slotStackLimit)
        {
            count = slotStackLimit;
        }
        if (count > stack.getMaxStackSize())
        {
            count = stack.getMaxStackSize();
        }
        stack.setCount(count);
        return initialCount - count;
    }

    /**
     * Iterate through all slots in an {@code inventory}.
     */
    public static Iterable<ItemStack> iterate(IItemHandler inventory)
    {
        return () -> new AbstractIterator<>()
        {
            private int slot = -1;

            @Override
            protected ItemStack computeNext()
            {
                slot++;
                if (slot < inventory.getSlots())
                {
                    return inventory.getStackInSlot(slot);
                }
                return endOfData();
            }
        };
    }

    /**
     * Attempts to insert a stack across all slots of an item handler
     *
     * @param stack The stack to be inserted
     * @return The remainder after the stack is inserted, if any
     */
    public static ItemStack insertAllSlots(IItemHandler inventory, ItemStack stack)
    {
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            stack = inventory.insertItem(slot, stack, false);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    /**
     * Extracts all items of an {@code inventory}, and copies them into a list, indexed with the slots.
     *
     * @see #insertAllItems(IItemHandlerModifiable, NonNullList)
     */
    public static NonNullList<ItemStack> extractAllItems(IItemHandlerModifiable inventory)
    {
        NonNullList<ItemStack> saved = NonNullList.withSize(inventory.getSlots(), ItemStack.EMPTY);
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            saved.set(slot, inventory.getStackInSlot(slot).copy());
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return saved;
    }

    /**
     * Given a saved copy of an inventory {@code from}, inserts each stack into the provided {@code inventory}, if possible.
     *
     * @see #extractAllItems(IItemHandlerModifiable)
     */
    public static void insertAllItems(IItemHandlerModifiable inventory, NonNullList<ItemStack> from)
    {
        // We allow the list to have a different size than the new inventory
        for (int slot = 0; slot < Math.min(inventory.getSlots(), from.size()); slot++)
        {
            inventory.setStackInSlot(slot, from.get(slot));
        }
    }

    /**
     * Adds a tooltip based on an inventory, listing out the items inside.
     * Modified from {@link net.minecraft.world.level.block.ShulkerBoxBlock#appendHoverText(ItemStack, BlockGetter, List, TooltipFlag)}
     */
    public static void addInventoryTooltipInfo(IItemHandler inventory, List<Component> tooltips)
    {
        int maximumItems = 0, totalItems = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                ++totalItems;
                if (maximumItems <= 4)
                {
                    ++maximumItems;
                    tooltips.add(stack.getHoverName().copy()
                        .append(" x")
                        .append(String.valueOf(stack.getCount())));
                }
            }
        }

        if (totalItems - maximumItems > 0)
        {
            tooltips.add(new TranslatableComponent("container.shulkerBox.more", totalItems - maximumItems).withStyle(ChatFormatting.ITALIC));
        }
    }

    /**
     * Adds a tooltip based on a single fluid stack
     */
    public static void addFluidStackTooltipInfo(FluidStack fluid, List<Component> tooltips)
    {
        tooltips.add(new TranslatableComponent("tfc.tooltip.fluid_units_of", fluid.getAmount())
            .append(fluid.getDisplayName()));
    }

    /**
     * @return {@code true} if every slot in the provided inventory is empty.
     */
    public static boolean isEmpty(IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Copied from {@link Level#destroyBlock(BlockPos, boolean, Entity, int)}
     * Allows the loot context to be modified
     */
    public static void destroyBlockAndDropBlocksManually(Level worldIn, BlockPos pos, Consumer<LootContext.Builder> builder)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.isAir())
        {
            FluidState fluidstate = worldIn.getFluidState(pos);
            if (!(state.getBlock() instanceof BaseFireBlock))
            {
                worldIn.levelEvent(2001, pos, Block.getId(state));
            }

            if (worldIn instanceof ServerLevel)
            {
                BlockEntity tileEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;

                // Copied from Block.getDrops()
                LootContext.Builder lootContext = new LootContext.Builder((ServerLevel) worldIn)
                    .withRandom(worldIn.random)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, null)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileEntity);
                builder.accept(lootContext);
                state.getDrops(lootContext).forEach(stackToSpawn -> Block.popResource(worldIn, pos, stackToSpawn));
                state.spawnAfterBreak((ServerLevel) worldIn, pos, ItemStack.EMPTY);
            }
            worldIn.setBlock(pos, fluidstate.createLegacyBlock(), 3, 512);
        }
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent sound)
    {
        Random rand = world.getRandom();
        world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() + 0.7F + 0.3F);
    }

    public static boolean spawnItem(Level world, BlockPos pos, ItemStack stack, double yOffset)
    {
        return world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D, stack));
    }

    public static boolean spawnItem(Level world, BlockPos pos, ItemStack stack)
    {
        return spawnItem(world, pos, stack, 0.5D);
    }

    public static FluidStack mergeOutputFluidIntoSlot(IItemHandlerModifiable inventory, FluidStack fluidStack, float temperature, int slot)
    {
        if (!fluidStack.isEmpty())
        {
            final ItemStack mergeStack = inventory.getStackInSlot(slot);
            return mergeStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(fluidCap -> {
                int filled = fluidCap.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0)
                {
                    mergeStack.getCapability(HeatCapability.CAPABILITY).ifPresent(heatCap -> heatCap.setTemperature(temperature));
                }
                FluidStack remainder = fluidStack.copy();
                remainder.shrink(filled);
                return remainder;
            }).orElse(FluidStack.EMPTY);
        }
        return FluidStack.EMPTY;
    }

    public static void addTillable(Block block, Predicate<UseOnContext> condition, Consumer<UseOnContext> action)
    {
        HoeItemProtectedAccessor.TILLABLES_VIEW.put(block, Pair.of(condition, action));
    }

    public static <T> Tag<T> decodeTag(FriendlyByteBuf buffer, TagCollection<T> tags)
    {
        final ResourceLocation id = buffer.readResourceLocation();
        return tags.getTagOrEmpty(id);
    }

    public static <T> void encodeTag(FriendlyByteBuf buffer, Tag<T> tag, TagCollection<T> tags)
    {
        buffer.writeResourceLocation(Objects.requireNonNull(tags.getId(tag), "Tried to write unknown tag to network"));
    }

    public static <E, C extends Collection<E>> C decodeAll(FriendlyByteBuf buffer, C collection, Function<FriendlyByteBuf, E> decoder)
    {
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            collection.add(decoder.apply(buffer));
        }
        return collection;
    }

    public static <E, C extends Collection<E>> void encodeAll(FriendlyByteBuf buffer, C collection, BiConsumer<FriendlyByteBuf, E> encoder)
    {
        buffer.writeVarInt(collection.size());
        for (E e : collection)
        {
            encoder.accept(buffer, e);
        }
    }

    public static <T> void encodeNullable(@Nullable T instance, FriendlyByteBuf buffer, BiConsumer<T, FriendlyByteBuf> encoder)
    {
        if (instance != null)
        {
            buffer.writeBoolean(true);
            encoder.accept(instance, buffer);
        }
        else
        {
            buffer.writeBoolean(false);
        }
    }

    @Nullable
    public static <T> T decodeNullable(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> decoder)
    {
        if (buffer.readBoolean())
        {
            return decoder.apply(buffer);
        }
        return null;
    }

    /**
     * This returns the previous result of {@link ServerLevel#getBlockRandomPos(int, int, int, int)}.
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

    /**
     * @see net.minecraft.core.QuartPos#toBlock(int)
     */
    public static BlockPos quartToBlock(int x, int y, int z)
    {
        return new BlockPos(x << 2, y << 2, z << 2);
    }

    /**
     * Select N unique elements from a list, without having to shuffle the whole list.
     * This involves moving the selected elements to the end of the list. Note: this method will mutate the passed in list!
     * From <a href="https://stackoverflow.com/questions/4702036/take-n-random-elements-from-a-liste">Stack Overflow</a>
     */
    public static <T> List<T> uniqueRandomSample(List<T> list, int n, Random r)
    {
        final int length = list.size();
        if (length < n)
        {
            throw new IllegalArgumentException("Cannot select n=" + n + " from a list of size = " + length);
        }
        for (int i = length - 1; i >= length - n; --i)
        {
            Collections.swap(list, i, r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    public static <T> void insertBefore(List<? super T> list, T element, T before)
    {
        final int index = list.indexOf(before);
        if (index == -1)
        {
            list.add(element); // Element not found, so just insert at the end
        }
        else
        {
            list.add(index, element); // Insert at the target location, shifts the target forwards
        }
    }

    public static Field findUnobfField(Class<?> clazz, String fieldName)
    {
        try
        {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException("Missing field: " + clazz.getSimpleName() + "." + fieldName, e);
        }
    }

    public static void uncheck(ThrowingRunnable action)
    {
        uncheck(() -> {
            action.run();
            return null;
        });
    }

    /**
     * For when you want to ignore every possible safety measure in front of you
     */
    @SuppressWarnings("unchecked")
    public static <T> T uncheck(Callable<?> action)
    {
        try
        {
            return (T) action.call();
        }
        catch (Exception e)
        {
            return throwAsUnchecked(e);
        }
    }

    @SuppressWarnings({"AssertWithSideEffects", "ConstantConditions"})
    public static boolean detectAssertionsEnabled()
    {
        boolean enabled = false;
        assert enabled = true;
        return enabled;
    }

    /**
     * Detect if test sources are present, either if we're running in a ./gradlew test, or from a game test launch.
     */
    public static boolean detectTestSourcesPresent()
    {
        try
        {
            Class.forName("net.dries007.tfc.TestMarker");
            return true;
        }
        catch (ClassNotFoundException e) { /* Guess not */ }
        return false;
    }

    // Math Functions
    // Some are duplicated from Mth, but kept here as they might have slightly different parameter order or names

    /**
     * Linearly interpolates between [min, max].
     */
    public static float lerp(float delta, float min, float max)
    {
        return min + (max - min) * delta;
    }

    /**
     * Linearly interpolates between four values on a unit square.
     */
    public static float lerp4(float value00, float value01, float value10, float value11, float delta0, float delta1)
    {
        final float value0 = lerp(delta1, value00, value01);
        final float value1 = lerp(delta1, value10, value11);
        return lerp(delta0, value0, value1);
    }

    /**
     * @return A t = inverseLerp(value, min, max) s.t. lerp(t, min, max) = value;
     */
    public static float inverseLerp(float value, float min, float max)
    {
        return (value - min) / (max - min);
    }

    public static int hash(long salt, BlockPos pos)
    {
        return hash(salt, pos.getX(), pos.getY(), pos.getZ());
    }

    public static int hash(long salt, int x, int y, int z)
    {
        long hash = salt ^ ((long) x * PRIME_X) ^ ((long) y * PRIME_Y) ^ z;
        hash *= 0x27d4eb2d;
        return (int) hash;
    }

    /**
     * A triangle function, with input {@code value} and parameters {@code amplitude, midpoint, frequency}.
     * A period T = 1 / frequency, with a sinusoidal shape. triangle(0) = midpoint, with triangle(+/-1 / (4 * frequency)) = the first peak.
     */
    public static float triangle(float amplitude, float midpoint, float frequency, float value)
    {
        return midpoint + amplitude * (Math.abs(4f * frequency * value + 1f - 4f * Mth.floor(frequency * value + 0.75f)) - 1f);
    }

    /**
     * @return A random integer, uniformly distributed in the range [min, max).
     */
    public static int uniform(Random random, int min, int max)
    {
        return min == max ? min : min + random.nextInt(max - min);
    }

    /**
     * @return A random float, uniformly distributed in the range [min, max).
     */
    public static float uniform(Random random, float min, float max)
    {
        return random.nextFloat() * (max - min) + min;
    }

    /**
     * @return A random float, distributed around [-1, 1] in a triangle distribution X ~ pdf(t) = 1 - |t|.
     */
    public static float triangle(Random random)
    {
        return random.nextFloat() - random.nextFloat() * 0.5f;
    }

    /**
     * @return A random integer, distributed around (-range, range) in a triangle distribution X ~ pmf(t) ~= (1 - |t|)
     */
    public static int triangle(Random random, int range)
    {
        return random.nextInt(range) - random.nextInt(range);
    }

    /**
     * @return A random float, distributed around [-delta, delta] in a triangle distribution X ~ pdf(t) ~= (1 - |t|)
     */
    public static float triangle(Random random, float delta)
    {
        return (random.nextFloat() - random.nextFloat()) * delta;
    }

    /**
     * Checks the existence of a <a href="https://en.wikipedia.org/wiki/Perfect_matching">perfect matching</a> of a <a href="https://en.wikipedia.org/wiki/Bipartite_graph">bipartite graph</a>.
     * The graph is interpreted as the matches between the set of inputs, and the set of tests.
     * This algorithm computes the <a href="https://en.wikipedia.org/wiki/Edmonds_matrix">Edmonds Matrix</a> of the graph, which has the property that the determinant is identically zero iff the graph does not admit a perfect matching.
     */
    public static <T> boolean perfectMatchExists(List<T> inputs, List<? extends Predicate<T>> tests)
    {
        if (inputs.size() != tests.size())
        {
            return false;
        }
        final int size = inputs.size();
        final boolean[][] matrices = new boolean[size][];
        for (int i = 0; i < size; i++)
        {
            matrices[i] = new boolean[(size + 1) * (size + 1)];
        }
        final boolean[] matrix = matrices[size - 1];
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                matrix[i + size * j] = tests.get(i).test(inputs.get(j));
            }
        }
        return perfectMatchDet(matrices, size);
    }

    /**
     * Used by {@link Helpers#perfectMatchExists(List, List)}
     * Computes a symbolic determinant
     */
    public static boolean perfectMatchDet(boolean[][] matrices, int size)
    {
        // matrix true = nonzero = matches
        final boolean[] matrix = matrices[size - 1];
        switch (size)
        {
            case 1:
                return matrix[0];
            case 2:
                return (matrix[0] && matrix[3]) || (matrix[1] && matrix[2]);
            default:
            {
                for (int c = 0; c < size; c++)
                {
                    if (matrix[c])
                    {
                        perfectMatchSub(matrices, size, c);
                        if (perfectMatchDet(matrices, size - 1))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }

    /**
     * Used by {@link Helpers#perfectMatchExists(List, List)}
     * Computes the symbolic minor of a matrix by removing an arbitrary column.
     */
    public static void perfectMatchSub(boolean[][] matrices, int size, int dc)
    {
        final int subSize = size - 1;
        final boolean[] matrix = matrices[subSize], sub = matrices[subSize - 1];
        for (int c = 0; c < subSize; c++)
        {
            final int c0 = c + (c >= dc ? 1 : 0);
            for (int r = 0; r < subSize; r++)
            {
                sub[c + subSize * r] = matrix[c0 + size * (r + 1)];
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, T> T throwAsUnchecked(Exception exception) throws E
    {
        throw (E) exception;
    }

    static abstract class ItemProtectedAccessor extends Item
    {
        static BlockHitResult invokeGetPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid mode)
        {
            return /* protected */ Item.getPlayerPOVHitResult(level, player, mode);
        }

        @SuppressWarnings("ConstantConditions")
        private ItemProtectedAccessor() { super(null); } // Never called
    }

    static abstract class HoeItemProtectedAccessor extends HoeItem
    {
        static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES_VIEW = TILLABLES;

        @SuppressWarnings("ConstantConditions")
        private HoeItemProtectedAccessor() { super(null, 0, 0, null); }  // Never called
    }

    interface ThrowingRunnable
    {
        void run() throws Exception;
    }
}