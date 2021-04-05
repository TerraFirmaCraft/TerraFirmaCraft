package net.dries007.tfc.common.blocks.berrybush;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.util.Helpers;

public class StationaryBerryBushBlock extends AbstractBerryBushBlock
{
    private static final VoxelShape HALF_PLANT = box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    public StationaryBerryBushBlock(ForgeBlockProperties properties, BerryBush bush)
    {
        super(properties, bush);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isClientSide() || handIn != Hand.MAIN_HAND) return ActionResultType.FAIL;
        if (state.getValue(STAGE) == 2 && state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
        {
            ItemStack held = player.getItemInHand(handIn);
            if (held.getItem().is(Tags.Items.SHEARS))
            {
                BerryBushTileEntity te = Helpers.getTileEntity(worldIn, pos, BerryBushTileEntity.class);
                if (te != null)
                {
                    held.hurt(1, worldIn.getRandom(), null);
                    Helpers.playSound(worldIn, pos, SoundEvents.SHEEP_SHEAR);
                    if (worldIn.getRandom().nextInt(3) != 0)
                        Helpers.spawnItem(worldIn, pos, new ItemStack(defaultBlockState().getBlock()));
                    worldIn.destroyBlock(pos, true, null);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return HALF_PLANT;
    }

    @Override
    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (random.nextInt(3) != 0) return;
            if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
            }
            else if (stage == 2)
            {
                if (random.nextInt(bush.getDeathFactor()) == 0)
                {
                    for (int i = 0; i < random.nextInt(3) + 1; i++)
                        propagate(world, pos, random);
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT && !te.isGrowing())
        {
            te.addDeath();
            if (te.willDie() && random.nextInt(5) == 0)
            {
                BlockState deadState = TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState();
                if (state.getFluidState().getType() == Fluids.WATER)
                    deadState = deadState.setValue(TFCBlockStateProperties.FRESH_WATER, TFCBlockStateProperties.FRESH_WATER.keyFor(Fluids.WATER));
                world.setBlockAndUpdate(pos, deadState.setValue(STAGE, stage));
            }
        }
    }

    private void propagate(World world, BlockPos pos, Random rand)
    {
        BlockPos.Mutable mutablePos = pos.mutable();
        for (int i = 0; i < 12; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(10) - rand.nextInt(10), 0, rand.nextInt(10) - rand.nextInt(10));
            if (world.isEmptyBlock(mutablePos))
            {
                final BlockState placeState = defaultBlockState().setValue(STAGE, 0).setValue(LIFECYCLE, Lifecycle.HEALTHY);
                if (world.isEmptyBlock(mutablePos) && ((StationaryBerryBushBlock) placeState.getBlock()).canSurvive(placeState, world, mutablePos))
                {
                    world.setBlockAndUpdate(mutablePos, placeState);
                    return;
                }
            }
        }
    }
}
