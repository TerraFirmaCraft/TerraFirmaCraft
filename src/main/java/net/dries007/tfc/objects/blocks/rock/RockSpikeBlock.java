package net.dries007.tfc.objects.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.objects.entities.TFCFallingBlockEntity;
import net.dries007.tfc.objects.recipes.CollapseRecipe;

public class RockSpikeBlock extends Block
{
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public static final VoxelShape BASE_SHAPE = makeCuboidShape(2, 0, 2, 14, 16, 14);
    public static final VoxelShape MIDDLE_SHAPE = makeCuboidShape(4, 0, 4, 12, 16, 12);
    public static final VoxelShape TIP_SHAPE = makeCuboidShape(6, 0, 6, 10, 16, 10);

    public RockSpikeBlock()
    {
        this(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.4f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE));
    }

    public RockSpikeBlock(Properties properties)
    {
        super(properties);

        setDefaultState(stateContainer.getBaseState().with(PART, Part.BASE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(PART))
        {
            case BASE:
                return BASE_SHAPE;
            case MIDDLE:
                return MIDDLE_SHAPE;
            case TIP:
            default:
                return TIP_SHAPE;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PART);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (TFCTags.CAN_COLLAPSE.contains(this))
        {
            BlockPos downPos = pos.down();
            if (TFCFallingBlockEntity.canFallThrough(world, downPos))
            {
                // Potential to collapse from the top
                if (!world.isRemote && isUnsupported(world, pos))
                {
                    // Spike is unsupported
                    boolean collapsed = false;
                    BlockState stateAt = state;
                    while (stateAt.getBlock() == this)
                    {
                        collapsed |= CollapseRecipe.collapseBlock(world, pos, stateAt);
                        pos = pos.down();
                        stateAt = world.getBlockState(pos);
                    }
                    if (collapsed)
                    {
                        world.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundCategory.BLOCKS, 0.8f, 1.0f);
                    }
                }
            }
        }
    }

    private boolean isUnsupported(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        BlockState stateDown = world.getBlockState(pos.down());
        if (stateDown.isSolidSide(world, pos.down(), Direction.UP) || (stateDown.getBlock() == this && stateDown.get(PART).ordinal() > state.get(PART).ordinal() && !isUnsupported(world, pos.down())))
        {
            return false;
        }
        BlockState stateUp = world.getBlockState(pos.up());
        return !stateUp.isSolidSide(world, pos.up(), Direction.DOWN) && (stateUp.getBlock() != this || stateUp.get(PART).ordinal() > state.get(PART).ordinal() || isUnsupported(world, pos.up()));
    }

    public enum Part implements IStringSerializable
    {
        BASE, MIDDLE, TIP;

        @Override
        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
