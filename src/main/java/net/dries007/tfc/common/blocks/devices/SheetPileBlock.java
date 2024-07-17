/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.FluidHeat;

/**
 * A sheet pile is a collection of metal sheets (which can be made from arbitrary metals, and hence items), placed on the side of blocks.
 * The sheet pile is identified by direction properties, where the UP property indicates that a sheet is present on the UP face of the sheet
 * block, or placed against the block directly above.
 */
public class SheetPileBlock extends ExtendedBlock implements EntityBlockExtension, DirectionPropertyBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty MIRROR = TFCBlockStateProperties.MIRROR;

    public static final Map<BooleanProperty, VoxelShape> SHAPES = new ImmutableMap.Builder<BooleanProperty, VoxelShape>()
        .put(NORTH, box(0, 0, 0, 16, 16, 1))
        .put(SOUTH, box(0, 0, 15, 16, 16, 16))
        .put(EAST, box(15, 0, 0, 16, 16, 16))
        .put(WEST, box(0, 0, 0, 1, 16, 16))
        .put(UP, box(0, 15, 0, 16, 16, 16))
        .put(DOWN, box(0, 0, 0, 16, 1, 16))
        .build();

    public static void removeSheet(Level level, BlockPos pos, BlockState state, Direction face, @Nullable Player player, boolean doDrops)
    {
        final BlockState newState = state.setValue(DirectionPropertyBlock.getProperty(face), false);
        final @Nullable SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
        if (pile != null)
        {
            final ItemStack stack = pile.removeSheet(face);
            if (doDrops && (player == null || !player.isCreative()))
            {
                popResourceFromFace(level, pos, face, stack);
            }
        }

        level.playSound(null, pos, SoundEvents.METAL_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + 0.2f * level.getRandom().nextFloat());

        if (isEmptyContents(newState))
        {
            level.destroyBlock(pos, false);
        }
        else
        {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        }
    }

    public static void addSheet(LevelAccessor level, BlockPos pos, BlockState state, Direction face, ItemStack stack)
    {
        final BlockState newState = state.setValue(DirectionPropertyBlock.getProperty(face), true);

        level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).ifPresent(pile -> pile.addSheet(face, stack));

        final SoundType placementSound = state.getSoundType(level, pos, null);
        level.playSound(null, pos, state.getSoundType(level, pos, null).getPlaceSound(), SoundSource.BLOCKS, (placementSound.getVolume() + 1.0f) / 2.0f, placementSound.getPitch() * 0.8f);
    }

    /**
     * @return The targeted face, if we can find one, or the first non-empty face, if we can find one, or {@code null}, if the block is empty.
     */
    @Nullable
    public static Direction getTargetedFace(Level level, BlockState state, Player player)
    {
        return getTargetedFace(state, Helpers.rayTracePlayer(level, player, ClipContext.Fluid.NONE));
    }

    @Nullable
    private static Direction getTargetedFace(BlockState state, BlockHitResult result)
    {
        if (result.getType() == HitResult.Type.BLOCK)
        {
            final Vec3 hit = result.getLocation();
            @Nullable Direction firstDirection = null;
            for (Map.Entry<BooleanProperty, VoxelShape> entry : SHAPES.entrySet())
            {
                final BooleanProperty property = entry.getKey();
                if (state.getValue(property))
                {
                    if (firstDirection == null)
                    {
                        firstDirection = DirectionPropertyBlock.getDirection(property);
                    }
                    if (entry.getValue().bounds().move(result.getBlockPos()).inflate(0.01d).contains(hit))
                    {
                        return DirectionPropertyBlock.getDirection(property);
                    }
                }
            }
            return firstDirection;
        }
        return null;
    }

    public static VoxelShape getShapeForSingleFace(Direction direction)
    {
        return SHAPES.get(DirectionPropertyBlock.getProperty(direction));
    }

    public static int countSheets(BlockState state, Predicate<Direction> onlyTheseDirections)
    {
        int count = 0;
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (onlyTheseDirections.test(direction) && state.getValue(DirectionPropertyBlock.getProperty(direction)))
            {
                count++;
            }
        }
        return count;
    }

    public static boolean isEmptyContents(BlockState state)
    {
        for (BooleanProperty property : PROPERTIES)
        {
            if (state.getValue(property))
            {
                return false;
            }
        }
        return true;
    }

    private final Map<BlockState, VoxelShape> shapeCache;

    public SheetPileBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false)
            .setValue(FACING, Direction.NORTH)
            .setValue(MIRROR, false)
        );
        shapeCache = DirectionPropertyBlock.makeShapeCache(getStateDefinition(), SHAPES::get);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        if (!neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite()))
        {
            level.scheduleTick(currentPos, this, 0);
        }
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(direction)))
            {
                final BlockPos adjacentPos = pos.relative(direction);
                final BlockState adjacentState = level.getBlockState(adjacentPos);
                if (!adjacentState.isFaceSturdy(level, adjacentPos, direction.getOpposite()))
                {
                    // Neighbor state is not sturdy, so pop off
                    removeSheet(level, pos, state, direction, null, true);
                }
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(direction)))
            {
                final BlockPos adjacentPos = pos.relative(direction);
                final BlockState adjacentState = level.getBlockState(adjacentPos);
                if (!adjacentState.isFaceSturdy(level, adjacentPos, direction.getOpposite()))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        if (level instanceof Level realLevel)
        {
            final Direction targetFace = getTargetedFace(realLevel, state, player);
            if (targetFace != null)
            {
                return level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get())
                    .map(pile -> pile.getSheet(targetFace))
                    .orElse(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Destroys the block, including setting it to air. Called on both sides, and regardless of if a player has the correct tool to drop the block.
     * We have to manually check the harvest check here to see if we should drop anything.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        final boolean canActuallyHarvest = state.canHarvestBlock(level, pos, player);
        final Direction targetFace = getTargetedFace(level, state, player);

        playerWillDestroy(level, pos, state, player);

        if (targetFace == null)
        {
            level.destroyBlock(pos, false);
        }
        else
        {
            removeSheet(level, pos, state, targetFace, player, canActuallyHarvest);
        }

        return true;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (level.getBlockEntity(pos) instanceof SheetPileBlockEntity pile && newState.getBlock() != this)
        {
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (state.getValue(DirectionPropertyBlock.getProperty(direction)))
                {
                    popResourceFromFace(level, pos, direction, pile.removeSheet(direction));
                }
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(PROPERTIES).add(FACING).add(MIRROR));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shapeCache.get(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return DirectionPropertyBlock.rotate(state, rot).setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state; // don't flip MIRROR bit
        }

        return DirectionPropertyBlock.mirror(state, mirror).setValue(FACING, mirror.mirror(state.getValue(FACING))).cycle(MIRROR);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(new SheetPileRendering(this));
    }

    record SheetPileRendering(Block block) implements IClientBlockExtensions
    {
        /**
         * Prevent vanilla particles, and render our own. This fixes two issues:
         * <ul>
         *     <li>Particles render based on the combined bounds of the block, not on the targeted face</li>
         *     <li>Particles render using the first-available texture by face, not the targeted face</li>
         * </ul>
         * Both of these are derived from behavior in {@link ParticleEngine#crack(BlockPos, Direction)}, and what this method
         * is based on, with modifications.
         * @return {@code true} to prevent vanilla particles from rendering.
         */
        @Override
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
        {
            // All vanilla call paths provide `BlockHitResult`, why doesn't this API?
            // There's no other way to access the position here, this is terrible...
            // Also, particle calls require a client level...
            if (state.getBlock() == block &&
                !isEmptyContents(state) &&
                target instanceof BlockHitResult blockHit &&
                level instanceof ClientLevel clientLevel)
            {
                final BlockPos pos = blockHit.getBlockPos();
                final @Nullable SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
                if (pile != null)
                {
                    final @Nullable Direction targetedFace = getTargetedFace(state, blockHit);
                    if (targetedFace != null)
                    {
                        addHitEffects(clientLevel, pos, state, targetedFace, pile.getOrCacheMetal(targetedFace));
                    }
                }
            }
            return true;
        }

        private void addHitEffects(ClientLevel level, BlockPos pos, BlockState state, Direction face, FluidHeat metal)
        {
            double x = level.random.nextDouble() * 0.8 + 0.1;
            double y = level.random.nextDouble() * 0.8 + 0.1;
            double z = level.random.nextDouble() * 0.8 + 0.1;
            switch (face)
            {
                case DOWN -> y = 0.1;
                case UP -> y = 0.9;
                case NORTH -> z = 0.1;
                case SOUTH -> z = 0.9;
                case WEST -> x = 0.1;
                case EAST -> x = 0.9;
            }

            // Set the sprite directly rather than calling `updateSprite()`, so we pick up the correct particle for the face being hit
            final TextureAtlasSprite metalSprite = RenderHelpers.blockTexture(metal.textureId());
            final Particle particle = new TerrainParticle(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, 0, 0, state, pos)
            {{
                sprite = metalSprite;
            }}
                .setPower(0.2f)
                .scale(0.6f);

            Minecraft.getInstance().particleEngine.add(particle);
        }

        /**
         * Prevent vanilla destruction particles from rendering when a sheet pile is broken. As far as I can tell, there is no practical way, from this
         * method, that we can identify what sheet is being broken. And without that, we can't ever ensure we render the correct particles outside of
         * weird edge cases, so I'd rather err on the side of correctness and say "these don't generate particles on break"
         */
        @Override
        public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager)
        {
            return true;
        }
    }
}
