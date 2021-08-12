/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class FruitTreeSaplingBlock extends BushBlock implements IForgeBlockExtension
{
    private static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    protected final Supplier<? extends Block> block;
    protected final int treeGrowthDays;
    private final ForgeBlockProperties properties;

    public FruitTreeSaplingBlock(ForgeBlockProperties properties, Supplier<? extends Block> block, int treeGrowthDays)
    {
        super(properties.properties());
        this.properties = properties;
        this.block = block;
        this.treeGrowthDays = treeGrowthDays;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        int saplings = state.getValue(SAPLINGS);
        if (!worldIn.isClientSide() && handIn == InteractionHand.MAIN_HAND && saplings < 4)
        {
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            //ItemStack off = player.getItemInHand(Hand.OFF_HAND);
            //todo: require knife in offhand
            if (defaultBlockState().getBlock().asItem() == held.getItem() && state.hasProperty(TFCBlockStateProperties.SAPLINGS))
            {
                if (saplings > 2 && worldIn.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
                    return InteractionResult.FAIL;
                if (!player.isCreative())
                    held.shrink(1);
                worldIn.setBlockAndUpdate(pos, state.setValue(SAPLINGS, saplings + 1));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SeasonalPlantBlock.PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            if (!world.isClientSide() && te.getTicksSinceUpdate() > (long) ICalendar.TICKS_IN_DAY * treeGrowthDays)
            {
                ChunkData data = ChunkData.get(world, pos);
                // todo: better climate checks
                /*if (!tree.getBase().isValidConditions(data.getAverageTemp(pos), data.getRainfall(pos)))
                {
                    world.setBlockAndUpdate(pos, TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState());
                }
                else*/
                {
                    boolean onBranch = world.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
                    world.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(PipeBlock.DOWN, true).setValue(TFCBlockStateProperties.SAPLINGS, onBranch ? 3 : state.getValue(SAPLINGS)).setValue(TFCBlockStateProperties.STAGE_3, onBranch ? 1 : 0));
                    TickCounterTileEntity newTE = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
                    if (newTE != null)
                    {
                        newTE.resetCounter();
                    }
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos downPos = pos.below();
        BlockState downState = worldIn.getBlockState(downPos);
        if (downState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
        {
            if (downState.getValue(FruitTreeBranchBlock.STAGE) > 1)
            {
                return false;
            }
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (downState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(d)))
                {
                    return true;
                }
            }
            return false;
        }
        return super.canSurvive(state, worldIn, pos) || downState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(worldIn, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SAPLINGS));
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
