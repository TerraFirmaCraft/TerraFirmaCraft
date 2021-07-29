/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.tileentity.ScrapingTileEntity;
import net.dries007.tfc.util.Helpers;

public class ScrapingBlock extends DeviceBlock
{
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    private static Vector3d calculatePoint(Vector3d rayVector, Vector3d rayPoint)
    {
        Vector3d planeNormal = new Vector3d(0.0, 1.0, 0.0);
        return rayPoint.subtract(rayVector.scale(rayPoint.dot(planeNormal) / rayVector.dot(planeNormal)));
    }

    public ScrapingBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !facingState.is(TFCTags.Blocks.SCRAPING_SURFACE))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ScrapingTileEntity te = Helpers.getTileEntity(level, pos, ScrapingTileEntity.class);
        if (te != null)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem().is(TFCTags.Items.KNIVES))
            {
                Vector3d point = calculatePoint(player.getLookAngle(), hit.getLocation().subtract(new Vector3d(pos.getX(), pos.getY(), pos.getZ())));
                te.onClicked((float) point.x, (float) point.z);
                if (!level.isClientSide)
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                else
                {
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                        level.addParticle(new ItemParticleData(ParticleTypes.ITEM, cap.getStackInSlot(0)), pos.getX() + point.x, pos.getY() + 0.0625, pos.getZ() + point.z, Helpers.fastGaussian(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.fastGaussian(level.random) / 2.0D);
                    });
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
}
