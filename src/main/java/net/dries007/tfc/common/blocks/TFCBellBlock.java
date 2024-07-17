/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class TFCBellBlock extends BellBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final ExtendedProperties properties;
    private final float pitch;
    private final ResourceLocation textureLocation;

    public TFCBellBlock(ExtendedProperties properties, float pitch, String textureLocation)
    {
        this(properties, pitch, Helpers.identifier("entity/bell/" + textureLocation));
    }

    public TFCBellBlock(ExtendedProperties properties, float pitch, ResourceLocation textureLocation)
    {
        super(properties.properties());
        this.properties = properties;
        this.pitch = pitch;
        this.textureLocation = textureLocation;
    }

    public ResourceLocation getTextureLocation()
    {
        return textureLocation;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    public boolean attemptToRing(@Nullable Entity entity, Level level, BlockPos pos, @Nullable Direction side)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide && blockEntity instanceof BellBlockEntity bell)
        {
            if (side == null)
            {
                side = level.getBlockState(pos).getValue(FACING);
            }
            bell.onHit(side);
            final boolean hard = entity instanceof Player player && Helpers.isItem(player.getMainHandItem(), TFCTags.Items.TOOLS_HAMMER);
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS,  2.0F, hard ? pitch - 0.1f : pitch);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return EntityBlockExtension.super.newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return EntityBlockExtension.super.getTicker(level, state, type);
    }
}
