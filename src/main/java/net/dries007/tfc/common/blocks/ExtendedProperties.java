/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.wood.TFCChestBlock;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * An extension of {@link BlockBehaviour.Properties} to allow setting properties in a constructor that normally require an override.
 *
 * BlockEntity related overrides occur only when the block implements {@link EntityBlockExtension}
 * This means that vanilla blocks often need explicit overrides to use the interface, due to already overriding the default methods.
 * For an example, see {@link TFCChestBlock#newBlockEntity(BlockPos, BlockState)}
 *
 * Some block related overrides are provided in {@link IForgeBlockExtension}
 */
public class ExtendedProperties
{
    public static ExtendedProperties of(BlockBehaviour.Properties properties)
    {
        return new ExtendedProperties(properties);
    }

    private final BlockBehaviour.Properties properties;

    // Handles block entity tickers without requiring overrides in every class
    @Nullable private BiFunction<BlockPos, BlockState, ? extends BlockEntity> blockEntityFactory;
    @Nullable private Supplier<? extends BlockEntityType<?>> blockEntityType;
    @Nullable private BlockEntityTicker<?> serverTicker;
    @Nullable private BlockEntityTicker<?> clientTicker;

    // Forge methods
    private int flammability;
    private int fireSpreadSpeed;
    @Nullable private BlockPathTypes pathType;

    private ExtendedProperties(BlockBehaviour.Properties properties)
    {
        this.properties = properties;

        blockEntityFactory = null;
        blockEntityType = null;
        serverTicker = null;
        clientTicker = null;

        flammability = 0;
        fireSpreadSpeed = 0;
        pathType = null;
    }

    public ExtendedProperties blockEntity(Supplier<? extends BlockEntityType<?>> blockEntityType)
    {
        this.blockEntityType = blockEntityType;
        this.blockEntityFactory = (pos, state) -> blockEntityType.get().create(pos, state);
        return this;
    }

    /**
     * In order to tick on both sides, you must use this method. Calling client and server ticks successively sets one to null!
     */
    public <T extends BlockEntity> ExtendedProperties ticks(BlockEntityTicker<T> ticker)
    {
        return ticks(ticker, ticker);
    }

    public <T extends BlockEntity> ExtendedProperties serverTicks(BlockEntityTicker<T> serverTicker)
    {
        return ticks(serverTicker, null);
    }

    public <T extends BlockEntity> ExtendedProperties clientTicks(BlockEntityTicker<T> clientTicker)
    {
        return ticks(null, clientTicker);
    }

    public <T extends BlockEntity> ExtendedProperties ticks(@Nullable BlockEntityTicker<T> serverTicker, @Nullable BlockEntityTicker<T> clientTicker)
    {
        assert this.blockEntityType != null : "Must call .blockEntity() before adding a ticker";
        this.serverTicker = serverTicker;
        this.clientTicker = clientTicker;
        return this;
    }

    /**
     * @param flammability A measure of how fast this block burns. Higher values = shorter lifetime.
     * @param fireSpreadSpeed A measure of how much fire tends to spread from this block. Higher values = more spreading.
     */
    public ExtendedProperties flammable(int flammability, int fireSpreadSpeed)
    {
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
        return this;
    }

    public ExtendedProperties pathType(BlockPathTypes type)
    {
        pathType = type;
        return this;
    }

    public BlockBehaviour.Properties properties()
    {
        return properties;
    }

    public boolean hasBlockEntity()
    {
        return blockEntityType != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> blockEntity()
    {
        assert blockEntityType != null;
        return (BlockEntityType<T>) blockEntityType.get();
    }

    // Internal methods

    @Nullable
    BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return blockEntityFactory == null ? null : blockEntityFactory.apply(pos, state);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType)
    {
        assert blockEntityType != null;
        if (givenType == blockEntityType.get())
        {
            return (BlockEntityTicker<T>) (level.isClientSide() ? clientTicker : serverTicker);
        }
        return null;
    }

    int getFlammability()
    {
        return flammability;
    }

    int getFireSpreadSpeed()
    {
        return fireSpreadSpeed;
    }

    @Nullable
    BlockPathTypes getPathType()
    {
        return pathType;
    }
}
