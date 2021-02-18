package net.dries007.tfc.common.blocks.fruit_tree;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.berry_bush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.plant.TFCBushBlock;
import net.dries007.tfc.common.tileentity.BranchTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class FruitTreeSaplingBlock extends TFCBushBlock implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;
    private final Supplier<? extends Block> block;
    private final FruitTree tree;

    private static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;

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
        if (!worldIn.isClientSide() && handIn == Hand.MAIN_HAND && state.getValue(SAPLINGS) < 4)
        {
            ItemStack held = player.getItemInHand(Hand.MAIN_HAND);
            //ItemStack off = player.getItemInHand(Hand.OFF_HAND);
            //todo: require knife in offhand
            if (defaultBlockState().getBlock().asItem() == held.getItem())
            {
                held.shrink(1);
                worldIn.setBlockAndUpdate(pos, state.setValue(SAPLINGS, state.getValue(SAPLINGS) + 1));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            if (!world.isClientSide() && te.getTicksSinceUpdate() > ICalendar.TICKS_IN_DAY * 7)
            {
                world.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(SixWayBlock.DOWN, true));
                BranchTileEntity branch = Helpers.getTileEntity(world, pos, BranchTileEntity.class);
                if (branch != null)
                {
                    branch.addSaplings(state.getValue(SAPLINGS));
                }
            }
        }
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
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return AbstractBerryBushBlock.PLANT_SHAPE;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SAPLINGS);
    }
}
