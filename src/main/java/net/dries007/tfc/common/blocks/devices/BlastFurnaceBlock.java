/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MultiBlock;

public class BlastFurnaceBlock extends DeviceBlock implements IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final MultiBlock BLAST_FURNACE_CHIMNEY;

    static
    {
        BLAST_FURNACE_CHIMNEY = new MultiBlock()
            .match(new BlockPos(0, 0, 0), state -> state.isAir() || Helpers.isBlock(state, TFCBlocks.MOLTEN.get()))
            .match(new BlockPos(0, 0, 1), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(0, 0, -1), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(1, 0, 0), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(-1, 0, 0), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(0, 0, -2), matchSheet(Direction.SOUTH))
            .match(new BlockPos(0, 0, 2), matchSheet(Direction.NORTH))
            .match(new BlockPos(2, 0, 0), matchSheet(Direction.WEST))
            .match(new BlockPos(-2, 0, 0), matchSheet(Direction.EAST))
            .match(new BlockPos(-1, 0, -1), matchSheet(Direction.SOUTH, Direction.EAST))
            .match(new BlockPos(1, 0, -1), matchSheet(Direction.SOUTH, Direction.WEST))
            .match(new BlockPos(-1, 0, 1), matchSheet(Direction.NORTH, Direction.EAST))
            .match(new BlockPos(1, 0, 1), matchSheet(Direction.NORTH, Direction.WEST));
    }

    public static boolean isBlastFurnaceInsulationBlock(BlockState state)
    {
        return state.is(TFCTags.Blocks.BLAST_FURNACE_INSULATION);
    }

    /**
     * @param pos The position of the blast furnace.
     * @return The number of layers of chimney present in the blast furnace, in the range [0, 4].
     */
    public static int getChimneyLevels(Level level, BlockPos pos)
    {
        final int maxHeight = TFCConfig.SERVER.blastFurnaceMaxChimneyHeight.get();
        for (int i = 0; i < maxHeight; i++)
        {
            final BlockPos center = pos.above(i + 1);
            if (!BLAST_FURNACE_CHIMNEY.test(level, center))
            {
                return i;
            }
        }
        return maxHeight;
    }

    private static BiPredicate<LevelAccessor, BlockPos> matchSheet(Direction face)
    {
        return (level, pos) -> {
            final BlockState state = level.getBlockState(pos);
            final SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
            return Helpers.isBlock(state, TFCBlocks.SHEET_PILE.get())
                && pile != null
                && isTier3SheetOrHigherInDirection(state, pile, face);
        };
    }

    private static BiPredicate<LevelAccessor, BlockPos> matchSheet(Direction face, Direction secondFace)
    {
        return (level, pos) -> {
            final BlockState state = level.getBlockState(pos);
            final SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
            return Helpers.isBlock(state, TFCBlocks.SHEET_PILE.get())
                && pile != null
                && isTier3SheetOrHigherInDirection(state, pile, face)
                && isTier3SheetOrHigherInDirection(state, pile, secondFace);
        };
    }

    private static boolean isTier3SheetOrHigherInDirection(BlockState state, SheetPileBlockEntity pile, Direction face)
    {
        return state.getValue(DirectionPropertyBlock.getProperty(face)) && pile.getOrCacheMetal(face).tier() >= 3;
    }

    public BlastFurnaceBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);

        registerDefaultState(getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final BlastFurnaceBlockEntity blastFurnace = level.getBlockEntity(pos, TFCBlockEntities.BLAST_FURNACE.get()).orElse(null);
        if (blastFurnace != null)
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, blastFurnace, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean canAcceptAir(Level level, BlockPos pos, BlockState state)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.BLAST_FURNACE.get())
            .map(BlastFurnaceBlockEntity::hasTuyere)
            .orElse(false);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (!state.getValue(LIT)) return;
        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();
        if (random.nextDouble() < 0.1)
        {
            level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }
        level.addParticle(ParticleTypes.SMALL_FLAME, x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0, 0, 0);
    }

    @Override
    public void intakeAir(Level level, BlockPos pos, BlockState state, int amount)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BLAST_FURNACE.get()).ifPresent(blastFurnace -> blastFurnace.intakeAir(amount));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT));
    }
}
