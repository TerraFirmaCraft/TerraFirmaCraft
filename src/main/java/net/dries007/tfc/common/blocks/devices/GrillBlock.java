/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity;
import net.dries007.tfc.common.tileentity.GrillTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.TFCDamageSources;

import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_START;

public class GrillBlock extends FirepitBlock
{
    private static final VoxelShape GRILL_SHAPE = VoxelShapes.or(
        BASE_SHAPE,
        box(2, 9.5, 3, 14, 10, 13),
        box(2, 0, 13, 3, 11, 14),
        box(13, 0, 13, 14, 11, 14),
        box(2, 0, 2, 3, 11, 3),
        box(13, 0, 2, 14, 11, 3));

    public GrillBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        super.animateTick(state, world, pos, rand);
        if (!state.getValue(LIT)) return;
        GrillTileEntity te = Helpers.getTileEntity(world, pos, GrillTileEntity.class);
        if (te != null)
        {
            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
                {
                    if (!cap.getStackInSlot(i).isEmpty())
                    {
                        double x = pos.getX() + 0.5D;
                        double y = pos.getY() + 0.5D;
                        double z = pos.getZ() + 0.5D;
                        world.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.25F, rand.nextFloat() * 0.7F + 0.4F, false);
                        world.addParticle(ParticleTypes.SMOKE, x + rand.nextFloat() / 2 - 0.25, y + 0.1D, z + rand.nextFloat() / 2 - 0.25, 0.0D, 0.1D, 0.0D);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        final AbstractFirepitTileEntity<?> firepit = Helpers.getTileEntity(world, pos, AbstractFirepitTileEntity.class);
        if (firepit != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                if (!world.isClientSide)
                {
                    if (state.getValue(LIT))
                    {
                        player.hurt(TFCDamageSources.GRILL, 1.0F);
                        Helpers.playSound(world, pos, SoundEvents.LAVA_EXTINGUISH);
                    }
                    else
                    {
                        AbstractFirepitTileEntity.convertTo(world, pos, state, firepit, TFCBlocks.FIREPIT.get());
                    }
                }
                return ActionResultType.SUCCESS;
            }
            else if (stack.getItem().is(TFCTags.Items.EXTINGUISHER))
            {
                firepit.extinguish(state);
                return ActionResultType.SUCCESS;
            }
            else
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, firepit, pos);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return GRILL_SHAPE;
    }

    @Override
    protected double getParticleHeightOffset()
    {
        return 0.8D;
    }
}
