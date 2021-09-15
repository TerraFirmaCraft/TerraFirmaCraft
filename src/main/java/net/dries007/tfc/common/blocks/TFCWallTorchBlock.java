/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TFCWallTorchBlock extends WallTorchBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final ExtendedProperties properties;

    public TFCWallTorchBlock(ExtendedProperties properties, ParticleOptions particleData)
    {
        super(properties.properties(), particleData);
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return TFCBlocks.TORCH.get().use(state, world, pos, player, hand, result);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand)
    {
        TFCTorchBlock.onRandomTick(world, pos, TFCBlocks.DEAD_WALL_TORCH.get().defaultBlockState());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TFCBlocks.TORCH.get().setPlacedBy(worldIn, pos, state, placer, stack);
    }
}
