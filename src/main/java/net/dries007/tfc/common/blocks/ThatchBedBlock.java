/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static net.dries007.tfc.common.blocks.GroundcoverBlock.FACING;

public class ThatchBedBlock extends BedBlock
{
    private static final VoxelShape BED_SHAPE = Block.makeCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F);

    public ThatchBedBlock(Properties properties)
    {
        super(DyeColor.YELLOW, properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote())
        {
            if (canSpawnInBlock())
            {
                if (!worldIn.isThundering())
                {
                    player.sendStatusMessage(new TranslationTextComponent("tfc.thatch_bed.use"), true);
                }
                else
                {
                    player.sendStatusMessage(new TranslationTextComponent("tfc.thatch_bed.thundering"), true);
                }
                return ActionResultType.SUCCESS;
            }
            else
            {
                worldIn.createExplosion(null, DamageSource.IN_FIRE, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Explosion.Mode.DESTROY);
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return BED_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return null; // Need to override as the super class is a ITileEntityProvider
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction facing = state.get(FACING);
        if (!(world.getBlockState(pos.offset(facing)).isIn(TFCBlocks.THATCH_BED.get())) || world.getBlockState(pos.down()).isAir(world, pos))
        {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return false; // Need to override as the super class is a ITileEntityProvider
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }
}
