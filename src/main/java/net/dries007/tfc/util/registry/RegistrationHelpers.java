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

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ItemStackContainer;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.fluids.FlowingFluidRegistryObject;
import net.dries007.tfc.util.Metal;
import org.jetbrains.annotations.Nullable;

/**
 * Public APIs for registering things that are nontrivial.
 * All methods here take a {@link net.minecraftforge.registries.DeferredRegister} as they are not mod specific.
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
    public static <T extends Block> RegistryObject<T> registerBlock(DeferredRegister<Block> blocks, DeferredRegister<Item> items, String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        final String actualName = name.toLowerCase(Locale.ROOT);
        final RegistryObject<T> block = blocks.register(actualName, blockSupplier);
        if (blockItemFactory != null)
        {
            items.register(actualName, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }

    // Fluids

    /**
     * Registers a {@link FlowingFluid}, and returns the pair of both flowing and source fluids.
     */
    public static FlowingFluidRegistryObject<ForgeFlowingFluid> registerFluid(DeferredRegister<Fluid> fluids, String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes)
    {
        return registerFluid(fluids, sourceName, flowingName, builder, attributes, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new);
    }

    /**
     * Registers a {@link FlowingFluid}, and returns the pair of both flowing and source fluids.
     */
    public static <F extends FlowingFluid> FlowingFluidRegistryObject<F> registerFluid(DeferredRegister<Fluid> fluids, String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        // The properties need a reference to both source and flowing
        // In addition, the properties' builder cannot be invoked statically, as it has hard references to registry objects, which may not be populated based on class load order - it must be invoked at registration time.
        // So, first we prepare the source and flowing registry objects, referring to the properties box (which will be opened during registration, which is ok)
        // Then, we populate the properties box lazily, (since it's a mutable lazy), so the properties inside are only constructed when the box is opened (again, during registration)
        final Mutable<Lazy<ForgeFlowingFluid.Properties>> propertiesBox = new MutableObject<>();
        final RegistryObject<F> source = fluids.register(sourceName, () -> sourceFactory.apply(propertiesBox.getValue().get()));
        final RegistryObject<F> flowing = fluids.register(flowingName, () -> flowingFactory.apply(propertiesBox.getValue().get()));

        propertiesBox.setValue(Lazy.of(() -> {
            ForgeFlowingFluid.Properties lazyProperties = new ForgeFlowingFluid.Properties(source, flowing, attributes);
            builder.accept(lazyProperties);
            return lazyProperties;
        }));

        return new FlowingFluidRegistryObject<>(flowing, source);
    }

    // Block Entities

    /**
     * Registers a {@link BlockEntityType} for a given {@link BlockEntity}
     */
    @SuppressWarnings("ConstantConditions")
    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(DeferredRegister<BlockEntityType<?>> blockEntities, String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return blockEntities.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    /**
     * Registers a {@link BlockEntityType} for a given {@link BlockEntity}
     */
    @SuppressWarnings("ConstantConditions")
    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(DeferredRegister<BlockEntityType<?>> blockEntities, String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return blockEntities.register(name, () -> BlockEntityType.Builder.of(factory, blocks.map(Supplier::get).toArray(Block[]::new)).build(null));
    }

    // Containers

    /**
     * Registers a {@link BlockEntityContainer} for a {@link InventoryBlockEntity}
     */
    public static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> RegistryObject<MenuType<C>> registerBlockEntityContainer(DeferredRegister<MenuType<?>> containers, String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return registerContainer(containers, name, (windowId, playerInventory, buffer) -> {
            final Level world = playerInventory.player.level;
            final BlockPos pos = buffer.readBlockPos();
            final T entity = world.getBlockEntity(pos, type.get()).orElseThrow();

            return factory.create(entity, playerInventory, windowId);
        });
    }

    /**
     * Registers a {@link ItemStackContainer} for a {@link ItemStack}
     */
    public static <C extends ItemStackContainer> RegistryObject<MenuType<C>> registerItemStackContainer(DeferredRegister<MenuType<?>> containers, String name, ItemStackContainer.Factory<C> factory)
    {
        return registerContainer(containers, name, (windowId, playerInventory, buffer) -> {
            final InteractionHand hand = ItemStackContainerProvider.read(buffer);
            final ItemStack stack = playerInventory.player.getItemInHand(hand);

            return factory.create(stack, hand, playerInventory, windowId);
        });
    }

    /**
     * Registers an {@link AbstractContainerMenu}
     */
    public static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> registerContainer(DeferredRegister<MenuType<?>> containers, String name, IContainerFactory<C> factory)
    {
        return containers.register(name, () -> IForgeMenuType.create(factory));
    }
}
