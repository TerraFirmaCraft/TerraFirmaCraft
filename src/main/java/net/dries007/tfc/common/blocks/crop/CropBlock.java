package net.dries007.tfc.common.blocks.crop;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Fertilizer;
import net.dries007.tfc.util.climate.ClimateRange;

public abstract class CropBlock extends net.minecraft.world.level.block.CropBlock implements HoeOverlayBlock, ICropBlock, IForgeBlockExtension, EntityBlockExtension
{
    public static final VoxelShape HALF_SHAPE = box(2, 0, 2, 14, 8, 14);
    public static final VoxelShape FULL_SHAPE = box(2, 0, 2, 14, 16, 14);

    protected final FarmlandBlockEntity.NutrientType primaryNutrient;
    protected final Supplier<? extends Block> dead;
    protected final Supplier<? extends Item> seeds;
    protected final Supplier<ClimateRange> climateRange;

    private final ExtendedProperties extendedProperties;
    private final int maxAge;

    protected CropBlock(ExtendedProperties properties, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange)
    {
        super(properties.properties());

        this.extendedProperties = properties;
        this.maxAge = maxAge;

        this.dead = dead;
        this.seeds = seeds;
        this.primaryNutrient = primaryNutrient;
        this.climateRange = climateRange;

        registerDefaultState(defaultBlockState().setValue(getAgeProperty(), 0));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return extendedProperties;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return FULL_SHAPE; // todo
    }

    @Override
    public abstract IntegerProperty getAgeProperty();

    @Override
    public int getMaxAge()
    {
        return maxAge;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        tick(state, level, pos, random);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.FARMLAND);
    }

    @Override
    protected ItemLike getBaseSeedId()
    {
        return seeds.get();
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random rand, BlockPos pos, BlockState state)
    {
        // No-op
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(getAgeProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        // Handle fertilizer applied directly to the crop
        if (!level.isClientSide())
        {
            ItemStack stack = player.getItemInHand(hand);
            Fertilizer fertilizer = Fertilizer.get(stack);
            if (fertilizer != null)
            {
                level.getBlockEntity(pos.below(), TFCBlockEntities.FARMLAND.get()).ifPresent(farmland -> {
                    farmland.addNutrients(fertilizer);
                    stack.shrink(1);
                });
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (!level.isClientSide())
        {
            if (canSurvive(state, level, pos))
            {
                level.getBlockEntity(pos, TFCBlockEntities.CROP.get()).ifPresent(crop -> growthTick(level, pos, state, crop));
            }
            else
            {
                // Cannot survive here (e.g. no farmland below)
                level.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();
        final BlockPos sourcePos = pos.below();

        text.add(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.add(FarmlandBlock.getTemperatureTooltip(level, pos, range, false));

        if (isDebug)
        {
            level.getBlockEntity(pos, TFCBlockEntities.CROP.get())
                .ifPresent(crop -> text.add(new TextComponent(String.format("[Debug] Growth = %.2f Yield = %.2f", crop.getGrowth(), crop.getYield()))));
        }
    }

    @Override
    public void growthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        // Only perform growth ticks on server.
        if (!level.isClientSide() && CropHelpers.growthTick(level, pos, state, crop))
        {
            postGrowthTick(level, pos, state, crop);
        }
    }

    @Override
    public ClimateRange getClimateRange()
    {
        return climateRange.get();
    }

    @Override
    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return primaryNutrient;
    }

    protected abstract void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop);
}