/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ItemStackContainer;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.fluids.FluidHolder;
import net.dries007.tfc.util.Metal;

/**
 * Public APIs for registering things that are nontrivial.
 * All methods here take a {@link DeferredRegister} as they are not mod specific.
 * <br>
 * In most cases with blocks and items, TFC uses enums to register many variants of a specific block or item.
 * These enums <strong>are not</strong> intended for use by addons or to be extended.
 * However, if addons wish to use the API from, i.e. {@link Metal.ItemType} to register the same sorts of items as are present in TFC itself, they can use interfaces for these enums, i.e. {@link RegistryMetal}.
 *
 * @see RegistryMetal
 * @see RegistryRock
 * @see RegistryWood
 */
public final class RegistrationHelpers
{
    // Blocks / Items

    /**
     * Register a {@link Block}, and optionally a {@link BlockItem} based on said block.
     */
    public static <T extends Block> DeferredHolder<Block, T> registerBlock(DeferredRegister<Block> blocks, DeferredRegister<Item> items, String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        final String actualName = name.toLowerCase(Locale.ROOT);
        final DeferredHolder<Block, T> block = blocks.register(actualName, blockSupplier);
        if (blockItemFactory != null)
        {
            items.register(actualName, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }

    // Fluids

    /**
     * Registers a {@link FlowingFluid} and {@link FluidType}, and returns the pair of both flowing and source fluids.
     */
    public static <F extends FlowingFluid> FluidHolder<F> registerFluid(
        DeferredRegister<FluidType> fluidTypes,
        DeferredRegister<Fluid> fluids,
        String typeName,
        String sourceName,
        String flowingName,
        Consumer<BaseFlowingFluid.Properties> builder,
        Supplier<FluidType> typeFactory,
        Function<BaseFlowingFluid.Properties, F> sourceFactory,
        Function<BaseFlowingFluid.Properties, F> flowingFactory)
    {
        // The type need a reference to both source and flowing
        // In addition, the properties' builder cannot be invoked statically, as it has hard references to registry objects, which may not be populated based on class load order - it must be invoked at registration time.
        // So, first we prepare the source and flowing registry objects, referring to the properties box (which will be opened during registration, which is ok)
        // Then, we populate the properties box lazily, (since it's a mutable lazy), so the properties inside are only constructed when the box is opened (again, during registration)
        final Mutable<Supplier<BaseFlowingFluid.Properties>> typeBox = new MutableObject<>();
        final DeferredHolder<Fluid, F> source = fluids.register(sourceName, () -> sourceFactory.apply(typeBox.getValue().get()));
        final DeferredHolder<Fluid, F> flowing = fluids.register(flowingName, () -> flowingFactory.apply(typeBox.getValue().get()));

        final DeferredHolder<FluidType, FluidType> fluidType = fluidTypes.register(typeName, typeFactory);

        typeBox.setValue(Suppliers.memoize(() -> {
            final BaseFlowingFluid.Properties lazyProperties = new BaseFlowingFluid.Properties(fluidType, source, flowing);
            builder.accept(lazyProperties);
            return lazyProperties;
        }));

        return new FluidHolder<>(fluidType, flowing, source);
    }

    // Block Entities

    /**
     * Registers a {@link BlockEntityType} for a given {@link BlockEntity}
     */
    @SuppressWarnings("ConstantConditions")
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(DeferredRegister<BlockEntityType<?>> blockEntities, String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return blockEntities.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    /**
     * Registers a {@link BlockEntityType} for a given {@link BlockEntity}
     */
    @SuppressWarnings("ConstantConditions")
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(DeferredRegister<BlockEntityType<?>> blockEntities, String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return blockEntities.register(name, () -> BlockEntityType.Builder.of(factory, blocks.map(Supplier::get).toArray(Block[]::new)).build(null));
    }

    // Containers

    /**
     * Registers a {@link BlockEntityContainer} for a {@link InventoryBlockEntity}
     */
    public static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> DeferredHolder<MenuType<?>, MenuType<C>> registerBlockEntityContainer(DeferredRegister<MenuType<?>> containers, String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return registerContainer(containers, name, (windowId, playerInventory, buffer) -> {
            final Level level = playerInventory.player.level();
            final BlockPos pos = buffer.readBlockPos();
            final T entity = level.getBlockEntity(pos, type.get()).orElseThrow();

            return factory.create(entity, playerInventory, windowId);
        });
    }

    /**
     * Registers a {@link ItemStackContainer} for a {@link ItemStack}
     */
    public static <C extends ItemStackContainer> DeferredHolder<MenuType<?>, MenuType<C>> registerItemStackContainer(DeferredRegister<MenuType<?>> containers, String name, ItemStackContainer.Factory<C> factory)
    {
        return registerContainer(containers, name, (windowId, playerInventory, buffer) -> {
            final ItemStackContainerProvider.Info info = ItemStackContainerProvider.read(buffer, playerInventory);
            return factory.create(info.stack(), info.hand(), info.slot(), playerInventory, windowId);
        });
    }

    /**
     * Registers an {@link AbstractContainerMenu}
     */
    public static <C extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<C>> registerContainer(DeferredRegister<MenuType<?>> containers, String name, IContainerFactory<C> factory)
    {
        return containers.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }
}
