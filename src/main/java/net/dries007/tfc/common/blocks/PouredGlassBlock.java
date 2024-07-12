/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class PouredGlassBlock extends ExtendedBlock
{
    public static Item getStainedGlass(DyeColor color)
    {
        return switch (color)
        {
            case WHITE -> Items.WHITE_STAINED_GLASS_PANE;
            case ORANGE -> Items.ORANGE_STAINED_GLASS_PANE;
            case MAGENTA -> Items.MAGENTA_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_STAINED_GLASS_PANE;
            case YELLOW -> Items.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Items.LIME_STAINED_GLASS_PANE;
            case PINK -> Items.PINK_STAINED_GLASS_PANE;
            case GRAY -> Items.GRAY_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_STAINED_GLASS_PANE;
            case CYAN -> Items.CYAN_STAINED_GLASS_PANE;
            case PURPLE -> Items.PURPLE_STAINED_GLASS_PANE;
            case BLUE -> Items.BLUE_STAINED_GLASS_PANE;
            case BROWN -> Items.BROWN_STAINED_GLASS_PANE;
            case GREEN -> Items.GREEN_STAINED_GLASS_PANE;
            case RED -> Items.RED_STAINED_GLASS_PANE;
            default -> Items.BLACK_STAINED_GLASS_PANE;
        };
    }

    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);
    private final Supplier<? extends Item> drop;

    public PouredGlassBlock(ExtendedProperties properties, Supplier<? extends Item> drop)
    {
        super(properties);
        this.drop = drop;
    }

    public Item getDrop()
    {
        return drop.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return BottomSupportedDeviceBlock.canSurvive(level, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return BottomSupportedDeviceBlock.canSurvive(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && (!facingState.isFaceSturdy(level, facingPos, Direction.UP) && !Helpers.isBlock(facingState, TFCTags.Blocks.BOTTOM_SUPPORT_ACCEPTED)) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
}
