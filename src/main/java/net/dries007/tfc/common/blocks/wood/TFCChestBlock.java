/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Optional;
import java.util.function.Supplier;

import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class TFCChestBlock extends ChestBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final String textureLocation;
    private final ExtendedProperties extendedProperties;

    // WHY MINECRAFT :(
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        public Optional<MenuProvider> acceptDouble(final ChestBlockEntity chest1, final ChestBlockEntity chest2)
        {
            final Container container = new CompoundContainer(chest1, chest2);
            return Optional.of(new MenuProvider()
            {
                @Nullable
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
                {
                    if (chest1.canOpen(player) && chest2.canOpen(player))
                    {
                        chest1.unpackLootTable(inventory.player);
                        chest2.unpackLootTable(inventory.player);
                        return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x4.get(), id, inventory, container, 4);
                    }
                    return null;
                }

                public Component getDisplayName()
                {
                    if (chest1.hasCustomName())
                    {
                        return chest1.getDisplayName();
                    }
                    return chest2.hasCustomName() ? chest2.getDisplayName() : Helpers.translatable("container.chestDouble");
                }
            });
        }

        @Override
        public Optional<MenuProvider> acceptSingle(ChestBlockEntity chest)
        {
            return Optional.of(chest);
        }

        @Override
        public Optional<MenuProvider> acceptNone()
        {
            return Optional.empty();
        }
    };

    public TFCChestBlock(ExtendedProperties properties, String textureLocation)
    {
        this(properties, textureLocation, TFCBlockEntities.CHEST::get); // () -> x.get() passes type check but casting (Sup<BEType<ChestBE>>) fails. Thanks compiler
    }

    public TFCChestBlock(ExtendedProperties properties, String textureLocation, Supplier<BlockEntityType<? extends ChestBlockEntity>> typeSupplier)
    {
        super(properties.properties(), typeSupplier);
        this.textureLocation = textureLocation;
        this.extendedProperties = properties;
    }

    public String getTextureLocation()
    {
        return textureLocation;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return extendedProperties;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return EntityBlockExtension.super.newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        return this.combine(state, level, pos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType)
    {
        return EntityBlockExtension.super.getTicker(level, state, givenType);
    }
}
