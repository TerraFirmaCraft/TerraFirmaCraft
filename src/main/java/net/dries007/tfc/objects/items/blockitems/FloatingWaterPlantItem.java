/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.blockitems;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;

public class FloatingWaterPlantItem extends BlockItem
{
    public FloatingWaterPlantItem(Block block, Properties builder)
    {
        super(block, builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        return ActionResultType.PASS;
    }

    /**
     * Copy paste from {@link net.minecraft.item.LilyPadItem}
     */
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (raytraceresult.getType() == RayTraceResult.Type.MISS)
        {
            return ActionResult.resultPass(stack);
        }
        else
        {
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
                BlockPos blockpos = blockraytraceresult.getPos();
                Direction direction = blockraytraceresult.getFace();
                if (!world.isBlockModifiable(player, blockpos) || !player.canPlayerEdit(blockpos.offset(direction), direction, stack))
                {
                    return ActionResult.resultFail(stack);
                }

                BlockPos blockpos1 = blockpos.up();
                BlockState blockstate = world.getBlockState(blockpos);
                Material material = blockstate.getMaterial();
                IFluidState ifluidstate = world.getFluidState(blockpos);
                if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && world.isAirBlock(blockpos1))
                {
                    // special case for handling block placement with water lilies
                    BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, blockpos1);
                    world.setBlockState(blockpos1, getBlock().getDefaultState(), 11);
                    if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(player, snapshot, net.minecraft.util.Direction.UP))
                    {
                        snapshot.restore(true, false);
                        return ActionResult.resultFail(stack);
                    }

                    if (player instanceof ServerPlayerEntity)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, blockpos1, stack);
                    }

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                    world.playSound(player, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResult.resultSuccess(stack);
                }
            }

            return ActionResult.resultFail(stack);
        }
    }
}
