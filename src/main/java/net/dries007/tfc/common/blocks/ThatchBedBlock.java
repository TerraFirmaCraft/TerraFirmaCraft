/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.ThatchBedBlockEntity;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class ThatchBedBlock extends BedBlock implements EntityBlockExtension, IForgeBlockExtension
{
    private static final VoxelShape BED_SHAPE = box(0, 0, 0, 16, 9, 16);

    private final ExtendedProperties properties;

    public ThatchBedBlock(ExtendedProperties properties)
    {
        super(DyeColor.YELLOW, properties.properties()); // dye property unused, it's just in the Bed BEWLR which we don't need
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return state.getValue(PART) == BedPart.HEAD ? getExtendedProperties().newBlockEntity(pos, state) : null;
    }

    /**
     * This is based very closely on {@link BedBlock#useItemOn} to avoid bugs. Even if it's not the best practices.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.isClientSide)
        {
            return ItemInteractionResult.CONSUME;
        }
        else
        {
            if (state.getValue(PART) != BedPart.HEAD)
            {
                pos = pos.relative(state.getValue(FACING));
                state = level.getBlockState(pos);
                if (!Helpers.isBlock(state, this))
                {
                    return ItemInteractionResult.CONSUME;
                }
            }
        }
        if (!canSetSpawn(level))
        {
            level.removeBlock(pos, false);
            BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
            if (Helpers.isBlock(level.getBlockState(blockpos), this))
            {
                level.removeBlock(blockpos, false);
            }
            level.explode(null, level.damageSources().badRespawnPointExplosion(pos.getCenter()), null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Level.ExplosionInteraction.BLOCK);
            return ItemInteractionResult.SUCCESS;
        }
        else if (state.getValue(OCCUPIED))
        {
            if (!kickVillagerOutOfBed(level, pos))
            {
                player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }
        else if (player instanceof ServerPlayer serverPlayer)
        {
            if (level.isThundering() && TFCConfig.SERVER.thatchBedNoSleepInThunderstorms.get())
            {
                player.displayClientMessage(Component.translatable("tfc.thatch_bed.thundering"), true);
                return ItemInteractionResult.SUCCESS;
            }
            final boolean willSleep = TFCConfig.SERVER.enableThatchBedSleeping.get();
            final boolean spawnPoint = TFCConfig.SERVER.enableThatchBedSpawnSetting.get();

            // if we can set spawn but not sleep, we have to set spawn ourselves
            if (!willSleep)
            {
                if (spawnPoint)
                {
                    player.displayClientMessage(Component.translatable("tfc.thatch_bed.use_no_sleep_spawn"), true);
                    serverPlayer.setRespawnPosition(level.dimension(), pos, 0, false, false);
                    return ItemInteractionResult.SUCCESS;
                }
                // no spawn, no sleep, do nothing
                player.displayClientMessage(Component.translatable("tfc.thatch_bed.use_no_sleep_no_spawn"), true);
                return ItemInteractionResult.SUCCESS;
            }

            final BlockPos lastRespawnPos = serverPlayer.getRespawnPosition();
            final ResourceKey<Level> lastRespawnDimension = serverPlayer.getRespawnDimension();
            final float lastRespawnAngle = serverPlayer.getRespawnAngle();
            player.startSleepInBed(pos).ifLeft(problem -> {
                if (problem.getMessage() != null)
                {
                    player.displayClientMessage(problem.getMessage(), true);
                }
            }).ifRight(unit -> {
                // in this case vanilla sets the spawn point in startSleepInBed
                if (spawnPoint)
                {
                    player.displayClientMessage(Component.translatable("tfc.thatch_bed.use_sleep_spawn"), true);
                }
                else
                {
                    // sleeping automagically resets your spawn position, so we have to copy over the old spawn position and then set it to that.
                    serverPlayer.setRespawnPosition(lastRespawnDimension, lastRespawnPos, lastRespawnAngle, false, false);
                    player.displayClientMessage(Component.translatable("tfc.thatch_bed.use_sleep_no_spawn"), true);
                }
            });

        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        level.sendParticles(TFCParticles.FEATHER.get(), entity.getX(), Math.max(entity.getY() + 0.1f, pos.getY() + 0.6f), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15F);
        return super.addLandingEffects(state1, level, pos, state2, entity, numberOfParticles);
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos pos)
    {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty())
        {
            return false;
        }
        list.get(0).stopSleeping();
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return BED_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        if (state.getValue(PART) == BedPart.FOOT)
        {
            pos = pos.relative(state.getValue(FACING));
        }
        return level.getBlockEntity(pos, TFCBlockEntities.THATCH_BED.get())
            .map(bed -> bed.getInventory().getStackInSlot(0).copy())
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        // provide reasonable defaults for the bed item
        super.setPlacedBy(level, pos, state, entity, stack);
        level.getBlockEntity(pos.relative(state.getValue(FACING)), TFCBlockEntities.THATCH_BED.get()).ifPresent(bed -> {
            BlockState thatch = TFCBlocks.THATCH.get().defaultBlockState();
            bed.setBed(thatch, thatch, TFCItems.HIDES.get(HideItemType.RAW).get(HideItemType.Size.LARGE).get().getDefaultInstance());
        });
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getValue(PART) == BedPart.HEAD && !Helpers.isBlock(state, newState.getBlock()))
        {
            level.getBlockEntity(pos, TFCBlockEntities.THATCH_BED.get()).ifPresent(ThatchBedBlockEntity::destroyBed);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
