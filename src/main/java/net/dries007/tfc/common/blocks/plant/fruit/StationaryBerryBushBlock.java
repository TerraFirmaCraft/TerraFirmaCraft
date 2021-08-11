/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.util.Helpers;

public class StationaryBerryBushBlock extends SeasonalPlantBlock
{
    private static final VoxelShape HALF_PLANT = box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    private final int deathChance;

    public StationaryBerryBushBlock(ForgeBlockProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, int deathChance)
    {
        super(properties, productItem, stages);

        this.deathChance = deathChance;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (worldIn.isClientSide() || handIn != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
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
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return HALF_PLANT;
    }

    @Override
    public void cycle(BerryBushTileEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
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
                if (random.nextInt(deathChance) == 0)
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

    private void propagate(Level world, BlockPos pos, Random rand)
    {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
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
