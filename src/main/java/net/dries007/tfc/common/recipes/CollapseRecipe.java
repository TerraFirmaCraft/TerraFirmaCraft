/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.Support;
import net.dries007.tfc.util.events.CollapseEvent;
import net.dries007.tfc.util.tracker.Collapse;
import net.dries007.tfc.util.tracker.WorldTracker;

/**
 * This handles logic relating to block collapses.
 * The recipe itself only handles *transformations*, not actually if a block can or cannot collapse. Those are handled by tags.
 *
 * @see TFCFallingBlockEntity
 */
public class CollapseRecipe extends BlockRecipe
{
    public static final IndirectHashCollection<Block, CollapseRecipe> CACHE = IndirectHashCollection.createForRecipe(recipe -> recipe.getBlockIngredient().blocks(), TFCRecipeTypes.COLLAPSE);

    /**
     * This is the fallback recipe, which defines default behavior (no result) for any block in the {@link TFCTags.Blocks#CAN_COLLAPSE} tag. We
     * do this for multiple reasons.
     * <p>
     * Firstly, it reduces the number of default recipes we have to provide, or reduces the need for additional recipes that are added for blocks
     * that only need to signal they exist in the above tag. Secondly, it allows us to rely on this "fallback" mechanism, without the duplication
     * of data that not having a fallback requires. The {@link TFCTags.Blocks#CAN_COLLAPSE} tag thus is always thus a subset of collapsible blocks,
     * and we don't need to have a validation mechanism that the tag, and the inputs to recipes, are synced.
     */
    private static final CollapseRecipe FALLBACK = new CollapseRecipe();

    /**
     * @return {@code true} if this block is collapsible, by virtue of having a recipe, or being defined in the fallback tag.
     */
    public static boolean canCollapse(BlockState input)
    {
        return getRecipe(input) != null;
    }

    @Nullable
    public static CollapseRecipe getRecipe(BlockState input)
    {
        final @Nullable CollapseRecipe recipe = RecipeHelpers.getRecipe(CACHE, input, input.getBlock());
        return recipe == null && FALLBACK.matches(input) ? FALLBACK : recipe;
    }

    /**
     * Called to attempt to trigger a collapse, from when a player mines a block.
     *
     * @return true if a collapse occurred.
     */
    public static boolean tryTriggerCollapse(Level level, BlockPos pos)
    {
        final RandomSource random = level.getRandom();
        if (!level.isClientSide() && level.isAreaLoaded(pos, 32))
        {
            final boolean realCollapse = random.nextFloat() < TFCConfig.SERVER.collapseTriggerChance.get(),
                fakeCollapse = !realCollapse && random.nextFloat() < TFCConfig.SERVER.collapseFakeTriggerChance.get();
            if (realCollapse || fakeCollapse)
            {
                // Random radius
                final int radX = (random.nextInt(5) + 4) / 2;
                final int radY = (random.nextInt(3) + 2) / 2;
                final int radZ = (random.nextInt(5) + 4) / 2;

                final List<BlockPos> fakeCollapseStarts = new ArrayList<>();
                for (BlockPos checking : Support.findUnsupportedPositions(level, pos.offset(-radX, -radY, -radZ), pos.offset(radX, radY, radZ))) // 9x5x9 max
                {
                    // Exclude the position being mined, as it's done before the mining is completed, which is unintuitive
                    if (!checking.equals(pos) && canStartCollapse(level, checking))
                    {
                        if (fakeCollapse)
                        {
                            fakeCollapseStarts.add(checking.immutable());
                            continue;
                        }
                        if (startCollapse(level, checking))
                        {
                            level.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                        }
                        return true; // Don't need to check other blocks, regardless of if we managed to collapse any blocks.
                    }
                }

                if (!fakeCollapseStarts.isEmpty())
                {
                    // Play sound
                    level.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG_FAKE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

                    final List<BlockPos> startsToDisplay = fakeCollapseStarts.size() < 4 ?
                        fakeCollapseStarts :
                        Helpers.uniqueRandomSample(fakeCollapseStarts, Math.min(12, 3 + random.nextInt(fakeCollapseStarts.size() - 3)), random);
                    // Use startsToDisplay instead of fakeCollapseStarts to match the behavior of real collapses only providing 'effected' blocks
                    NeoForge.EVENT_BUS.post(new CollapseEvent(level, pos, startsToDisplay, 0D, true));
                    for (BlockPos start : startsToDisplay)
                    {
                        final BlockState fakeStartState = level.getBlockState(start);
                        level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, start, Block.getId(fakeStartState));
                    }

                    return false; // No collapse actually occurred, just a fake one
                }
            }
        }
        return false;
    }

    /**
     * Checks if a single block is possible to be the locus of a collapse
     */
    public static boolean canStartCollapse(LevelAccessor level, BlockPos pos)
    {
        final BlockPos posBelow = pos.below();
        final BlockState state = level.getBlockState(pos);
        final BlockState stateBelow = level.getBlockState(posBelow);
        return Helpers.isBlock(state, TFCTags.Blocks.CAN_START_COLLAPSE)
            // If we can directly fall into this block (same as a single block collapse check), we can start a collapse
            && (TFCFallingBlockEntity.canFallThrough(level, posBelow, stateBelow, Direction.DOWN, state)
            // Or, if the block directly below isn't quite a solid block - stuff like upwards facing slabs n stairs can still cause collapses to start, since we can forcibly break them
            || !stateBelow.isCollisionShapeFullBlock(level, posBelow)
            // Finally, we want to include blocks that have a non-solid-supporting full block below them. As usually, these blocks can collapse themselves, and aren't alone enough to prevent a collapse.
            || Helpers.isBlock(stateBelow, TFCTags.Blocks.NOT_SOLID_SUPPORTING));
    }

    /**
     * Starts a collapse from a given location
     * - at this point, any supports are ignored completely.
     * - many more blocks can collapse, even if they can't trigger or start collapses.
     * - this is much more in-depth than previous implementations, and searches aggressively for next-tick collapse blocks
     *
     * @return {@code true} if any blocks started collapsing.
     */
    public static boolean startCollapse(Level level, BlockPos centerPos)
    {
        final RandomSource random = level.getRandom();
        final int radius = TFCConfig.SERVER.collapseMinRadius.get() + random.nextInt(TFCConfig.SERVER.collapseRadiusVariance.get());
        final int radiusSquared = radius * radius;
        final List<BlockPos> secondaryPositions = new ArrayList<>();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        TerraFirmaCraft.LOGGER.info("Collapse started at pos {}, with the block column {} -> (start: {}) -> {}",
            centerPos,
            level.getBlockState(centerPos.above()),
            level.getBlockState(centerPos),
            level.getBlockState(centerPos.below()));

        // Initially only scan on the bottom layer, and advance upwards
        for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-radius, -4, -radius), centerPos.offset(radius, -4, radius)))
        {
            boolean foundEmpty = false; // If we've found a space to collapse into
            for (int y = 0; y <= 8; y++)
            {
                final BlockPos posAt = cursor.setWithOffset(pos, 0, y, 0);
                final BlockState stateAt = level.getBlockState(posAt);
                if (foundEmpty && canCollapse(stateAt))
                {
                    // Check for a possible collapse
                    if (posAt.distSqr(centerPos) < radiusSquared && random.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                    {
                        // Trigger destruction, since our previous check only was 'non-full-blocks'
                        if (collapseBlock(level, posAt, stateAt, true))
                        {
                            // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                            secondaryPositions.add(posAt.above());
                            break;
                        }
                    }
                }
                // Any non-solid block below might be a candidate for a collapse, since we just break stuff like slabs and stairs that would otherwise count as a solid surface above.
                foundEmpty = !stateAt.isCollisionShapeFullBlock(level, posAt);
            }
        }

        if (!secondaryPositions.isEmpty())
        {
            WorldTracker.get(level).addCollapseData(new Collapse(centerPos, secondaryPositions, radiusSquared));
        }

        return !secondaryPositions.isEmpty();
    }

    /**
     * Responsible for actually collapsing a singular block. Queries the collapse recipe, replaces the block, and spawns the falling block entity
     *
     * @return true if the collapse actually occurred
     */
    public static boolean collapseBlock(Level level, BlockPos pos, BlockState state)
    {
        return collapseBlock(level, pos, state, false);
    }

    public static boolean collapseBlock(Level level, BlockPos pos, BlockState state, boolean destroyBlockBelow)
    {
        final CollapseRecipe recipe = getRecipe(state);
        if (recipe != null)
        {
            final BlockPos posBelow = pos.below();
            if (destroyBlockBelow && !TFCFallingBlockEntity.canFallThrough(level, posBelow, Direction.DOWN, Blocks.BEDROCK.defaultBlockState()))
            {
                // If we cannot fall through the pos below, yet we're trying to collapse this block, it's because we identified it as one we can bust through
                // So, once we know we are actually collapsing, now we break the block below.
                // If this check passes, it means the collapsing block will break the block below during it's collapse, so this extra destruction isn't needed.
                level.destroyBlock(posBelow, true);
            }
            final BlockState collapseState = recipe.assembleBlock(state);
            level.setBlockAndUpdate(pos, collapseState); // Required as the falling block entity will replace the block in it's first tick
            level.addFreshEntity(new TFCFallingBlockEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, collapseState, 2.0f, 20));
            return true;
        }
        return false;
    }

    public CollapseRecipe(BlockIngredient ingredient, BlockState output)
    {
        super(ingredient, Optional.of(output));
    }

    CollapseRecipe(BlockIngredient ingredient, Optional<BlockState> output)
    {
        super(ingredient, output);
    }

    CollapseRecipe()
    {
        super(BlockIngredient.of(TFCTags.Blocks.CAN_COLLAPSE), Optional.empty());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.COLLAPSE.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.COLLAPSE.get();
    }
}