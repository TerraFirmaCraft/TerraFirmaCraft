/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.dries007.tfc.common.blocks.wood.TFCChestBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
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

    public static ExtendedProperties of(BlockBehaviour block) { return of(BlockBehaviour.Properties.copy(block)); }
    public static ExtendedProperties of(Material material) { return of(BlockBehaviour.Properties.of(material, material.getColor())); }
    public static ExtendedProperties of(Material material, DyeColor color) { return of(BlockBehaviour.Properties.of(material, color)); }
    public static ExtendedProperties of(Material material, MaterialColor materialColor) { return of(BlockBehaviour.Properties.of(material, materialColor)); }
    public static ExtendedProperties of(Material material, Function<BlockState, MaterialColor> materialColor) { return of(BlockBehaviour.Properties.of(material, materialColor)); }


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
    private ToFloatFunction<BlockState> enchantmentPower;

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
        enchantmentPower = s -> 0;
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
        assert this.serverTicker == null && this.clientTicker == null : "Calling ticks() twice, can only call one of ticks(), clientTicks(), or serverTicks()";
        this.serverTicker = serverTicker;
        this.clientTicker = clientTicker;
        return this;
    }

    /**
     * @param flammability A measure of how fast this block burns. Higher values = shorter lifetime.
     * @param fireSpreadSpeed A measure of how much fire tends to spread from this block. Higher values = more spreading.
     *
     * @see FireBlock#bootStrap()
     */
    public ExtendedProperties flammable(int flammability, int fireSpreadSpeed)
    {
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
        return this;
    }

    public ExtendedProperties flammableLikeLogs() { return flammable(5, 5); }
    public ExtendedProperties flammableLikePlanks() { return flammable(20, 5); }
    public ExtendedProperties flammableLikeLeaves() { return flammable(60, 30); }
    public ExtendedProperties flammableLikeWool() { return flammable(100, 60); }

    public ExtendedProperties pathType(BlockPathTypes type)
    {
        pathType = type;
        return this;
    }

    public ExtendedProperties enchantmentPower(int power)
    {
        return enchantmentPower(s -> power);
    }

    public ExtendedProperties enchantmentPower(ToFloatFunction<BlockState> powerGetter)
    {
        enchantmentPower = powerGetter;
        return this;
    }

    public BlockBehaviour.Properties properties()
    {
        return properties;
    }

    @ApiStatus.Internal
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

    // Bouncer methods for vanilla properties

    public ExtendedProperties noCollission() { properties.noCollission(); return this; }
    public ExtendedProperties noOcclusion() { properties.noOcclusion(); return this; }
    public ExtendedProperties friction(float friction) { properties.friction(friction); return this; }
    public ExtendedProperties speedFactor(float speedFactor) { properties.speedFactor(speedFactor); return this; }
    public ExtendedProperties jumpFactor(float jumpFactor) { properties.jumpFactor(jumpFactor); return this; }
    public ExtendedProperties sound(SoundType sound) { properties.sound(sound); return this; }
    public ExtendedProperties lightLevel(ToIntFunction<BlockState> lightLevel) { properties.lightLevel(lightLevel); return this; }
    public ExtendedProperties strength(float destroyTime, float explosionResistance) { properties.strength(destroyTime, explosionResistance); return this; }
    public ExtendedProperties instabreak() { properties.instabreak(); return this; }
    public ExtendedProperties strength(float strength) { properties.strength(strength); return this; }
    public ExtendedProperties randomTicks() { properties.randomTicks(); return this; }
    public ExtendedProperties dynamicShape() { properties.dynamicShape(); return this; }
    public ExtendedProperties noDrops() { properties.noDrops(); return this; }
    public ExtendedProperties dropsLike(Block block) { properties.lootFrom(() -> block); return this; }
    public ExtendedProperties dropsLike(Supplier<Block> block) { properties.lootFrom(block); return this; }
    public ExtendedProperties air() { properties.air(); return this; }
    public ExtendedProperties isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn) { properties.isValidSpawn(isValidSpawn); return this; }
    public ExtendedProperties isRedstoneConductor(BlockBehaviour.StatePredicate isRedstoneConductor) { properties.isRedstoneConductor(isRedstoneConductor); return this; }
    public ExtendedProperties isSuffocating(BlockBehaviour.StatePredicate isSuffocating) { properties.isSuffocating(isSuffocating); return this; }
    public ExtendedProperties isViewBlocking(BlockBehaviour.StatePredicate isViewBlocking) { properties.isViewBlocking(isViewBlocking); return this; }
    public ExtendedProperties hasPostProcess(BlockBehaviour.StatePredicate hasPostProcess) { properties.hasPostProcess(hasPostProcess); return this; }
    public ExtendedProperties emissiveRendering(BlockBehaviour.StatePredicate emissiveRendering) { properties.emissiveRendering(emissiveRendering); return this; }
    public ExtendedProperties requiresCorrectToolForDrops() { properties.requiresCorrectToolForDrops(); return this; }
    public ExtendedProperties color(MaterialColor color) { properties.color(color); return this; }
    public ExtendedProperties destroyTime(float destroyTime) { properties.destroyTime(destroyTime); return this; }
    public ExtendedProperties explosionResistance(float explosionResistance) { properties.explosionResistance(explosionResistance); return this; }

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

    float getEnchantmentPower(BlockState state)
    {
        return enchantmentPower.apply(state);
    }
}
