package net.dries007.tfc.common.blocks.fruittree;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.common.tileentity.FruitTreeLeavesTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class FruitTreeLeavesBlock extends AbstractBerryBushBlock implements IForgeBlockProperties, ILeavesBlock
{
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    private final ForgeBlockProperties properties;
    private final FruitTree tree;

    public FruitTreeLeavesBlock(ForgeBlockProperties properties, FruitTree tree)
    {
        super(properties, tree.getBase());
        this.properties = properties;
        this.tree = tree;
        registerDefaultState(getStateDefinition().any().setValue(PERSISTENT, false).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.block();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        FruitTreeLeavesTileEntity te = Helpers.getTileEntity(world, pos, FruitTreeLeavesTileEntity.class);
        if (te == null) return;

        Lifecycle old = state.getValue(LIFECYCLE); // have to put this in random tick to capture the old state
        if (old == Lifecycle.FLOWERING || old == Lifecycle.FRUITING)
        {
            if (!te.isOnYear() && te.isGrowing() && old == Lifecycle.FLOWERING && super.updateLifecycle(te) == Lifecycle.FRUITING)
            {
                te.addDeath();
                int probability = MathHelper.clamp(te.getDeath(), 2, 10);
                if (random.nextInt(probability) == 0)
                {
                    te.setOnYear(true);
                }
            }
        }
        else
        {
            te.setOnYear(false); // reset when we're not in season
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (TFCConfig.SERVER.enableLeavesSlowEntities.get())
        {
            Helpers.slowEntityInBlock(entityIn, 0.2f, 5);
        }
    }

    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (te.getDeath() > 10)
        {
            te.setGrowing(false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PERSISTENT);
    }

    protected Lifecycle updateLifecycle(BerryBushTileEntity te)
    {
        Lifecycle lifecycle = super.updateLifecycle(te);

        FruitTreeLeavesTileEntity fruityTE = (FruitTreeLeavesTileEntity) te;
        if (lifecycle == Lifecycle.FRUITING && !fruityTE.isOnYear())
        {
            lifecycle = Lifecycle.HEALTHY;
        }
        return lifecycle;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(PERSISTENT);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return isValid(worldIn, currentPos, stateIn) ? stateIn : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!isValid(worldIn, pos, state))
            worldIn.destroyBlock(pos, true);
    }

    private boolean isValid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        if (state.getValue(PERSISTENT)) return true;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : Direction.values())
        {
            mutablePos.set(pos).move(direction);
            if (worldIn.getBlockState(mutablePos).is(TFCTags.Blocks.FRUIT_TREE_BRANCH)) return true;
        }
        return false;
    }
}
