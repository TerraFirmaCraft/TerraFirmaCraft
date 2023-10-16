/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class TFCCeilingHangingSignBlock extends CeilingHangingSignBlock implements IForgeBlockExtension, EntityBlockExtension, ITFCHangingSignBlock
{
    private final ExtendedProperties properties;
    private final ResourceLocation metal;

    public TFCCeilingHangingSignBlock(ExtendedProperties properties, WoodType type, ResourceLocation metal)
    {
        super(properties.properties(), type);
        this.properties = properties;
        this.metal = metal;
    }

    public ResourceLocation metal()
    {
        return metal;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return EntityBlockExtension.super.getTicker(level, state, type);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return EntityBlockExtension.super.newBlockEntity(pos, state);
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
