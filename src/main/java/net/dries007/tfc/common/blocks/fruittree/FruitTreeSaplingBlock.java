package net.dries007.tfc.common.blocks.fruittree;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class FruitTreeSaplingBlock extends BushBlock implements IForgeBlockProperties
{
    private static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    protected final Supplier<? extends Block> block;
    protected final FruitTree tree;
    private final ForgeBlockProperties properties;

    public FruitTreeSaplingBlock(ForgeBlockProperties properties, FruitTree tree, Supplier<? extends Block> block)
    {
        super(properties.properties());
        this.properties = properties;
        this.block = block;
        this.tree = tree;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        int saplings = state.getValue(SAPLINGS);
        if (!worldIn.isClientSide() && handIn == Hand.MAIN_HAND && saplings < 4)
        {
            ItemStack held = player.getItemInHand(Hand.MAIN_HAND);
            //ItemStack off = player.getItemInHand(Hand.OFF_HAND);
            //todo: require knife in offhand
            if (defaultBlockState().getBlock().asItem() == held.getItem() && state.hasProperty(TFCBlockStateProperties.SAPLINGS))
            {
                if (saplings > 2 && worldIn.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
                    return ActionResultType.FAIL;
                if (!player.isCreative())
                    held.shrink(1);
                worldIn.setBlockAndUpdate(pos, state.setValue(SAPLINGS, saplings + 1));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return AbstractBerryBushBlock.PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            if (!world.isClientSide() && te.getTicksSinceUpdate() > ICalendar.TICKS_IN_DAY * tree.getSaplingDays())
            {
                ChunkData data = ChunkData.get(world, pos);
                if (!tree.getBase().isValidConditions(data.getRainfall(pos), data.getAverageTemp(pos)))
                {
                    world.setBlockAndUpdate(pos, TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState());
                }
                else
                {
                    boolean onBranch = world.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
                    world.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(SixWayBlock.DOWN, true).setValue(TFCBlockStateProperties.SAPLINGS, onBranch ? 3 : state.getValue(SAPLINGS)).setValue(TFCBlockStateProperties.STAGE_3, onBranch ? 1 : 0));
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
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
                if (downState.getValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(d)))
                {
                    return true;
                }
            }
            return false;
        }
        return super.canSurvive(state, worldIn, pos) || downState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(worldIn, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SAPLINGS);
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
