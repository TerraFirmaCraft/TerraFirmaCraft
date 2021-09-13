/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.inventory.BlockInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Support;
import net.dries007.tfc.util.SupportManager;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.tracker.Collapse;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

/**
 * This handles logic relating to block collapses.
 * The recipe itself only handles *transformations*, not actually if a block can or cannot collapse. Those are handled by tags.
 *
 * @see SupportManager
 * @see TFCFallingBlockEntity
 */
public class CollapseRecipe extends SimpleBlockRecipe
{
    public static final IndirectHashCollection<Block, CollapseRecipe> CACHE = new IndirectHashCollection<>(recipe -> recipe.getBlockIngredient().getValidBlocks());
    private static final Random RANDOM = new Random();

    @Nullable
    public static CollapseRecipe getRecipe(Level world, BlockInventory wrapper)
    {
        for (CollapseRecipe recipe : CACHE.getAll(wrapper.getState().getBlock()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Called to attempt to trigger a collapse, from when a player mines a block.
     *
     * @return true if a collapse occurred.
     */
    public static boolean tryTriggerCollapse(Level world, BlockPos pos)
    {
        if (!world.isClientSide() && world.isAreaLoaded(pos, 32))
        {
            if (RANDOM.nextFloat() < TFCConfig.SERVER.collapseTriggerChance.get())
            {
                // Random radius
                int radX = (RANDOM.nextInt(5) + 4) / 2;
                int radY = (RANDOM.nextInt(3) + 2) / 2;
                int radZ = (RANDOM.nextInt(5) + 4) / 2;
                for (BlockPos checking : Support.findUnsupportedPositions(world, pos.offset(-radX, -radY, -radZ), pos.offset(radX, radY, radZ))) // 9x5x9 max
                {
                    if (canStartCollapse(world, checking))
                    {
                        startCollapse(world, checking);
                        world.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                        return true; // Don't need to check other blocks
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if a single block is possible to be the locus of a collapse
     */
    public static boolean canStartCollapse(LevelAccessor world, BlockPos pos)
    {
        return TFCTags.Blocks.CAN_START_COLLAPSE.contains(world.getBlockState(pos).getBlock()) && TFCFallingBlockEntity.canFallThrough(world, pos.below());
    }

    /**
     * Starts a collapse from a given location
     * - at this point, any supports are ignored completely.
     * - many more blocks can collapse, even if they can't trigger or start collapses.
     * - this is much more in-depth than previous implementations, and searches aggressively for next-tick collapse blocks
     */
    public static void startCollapse(Level world, BlockPos centerPos)
    {
        int radius = TFCConfig.SERVER.collapseMinRadius.get() + RANDOM.nextInt(TFCConfig.SERVER.collapseRadiusVariance.get());
        int radiusSquared = radius * radius;
        List<BlockPos> secondaryPositions = new ArrayList<>();

        // Initially only scan on the bottom layer, and advance upwards
        for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-radius, -4, -radius), centerPos.offset(radius, -4, radius)))
        {
            boolean foundEmpty = false; // If we've found a space to collapse into
            for (int y = 0; y <= 8; y++)
            {
                BlockPos posAt = pos.above(y);
                BlockState stateAt = world.getBlockState(posAt);
                if (foundEmpty && TFCTags.Blocks.CAN_COLLAPSE.contains(stateAt.getBlock()))
                {
                    // Check for a possible collapse
                    if (posAt.distSqr(centerPos) < radiusSquared && RANDOM.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                    {
                        if (collapseBlock(world, posAt, stateAt))
                        {
                            // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                            secondaryPositions.add(posAt.above());
                            break;
                        }
                    }
                }
                if (TFCFallingBlockEntity.canFallThrough(world, posAt))
                {
                    foundEmpty = true;
                }
            }
        }

        if (!secondaryPositions.isEmpty())
        {
            world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapseData(new Collapse(centerPos, secondaryPositions, radiusSquared)));
        }
    }

    /**
     * Responsible for actually collapsing a singular block. Queries the collapse recipe, replaces the block, and spawns the falling block entity
     *
     * @return true if the collapse actually occurred
     */
    public static boolean collapseBlock(Level world, BlockPos pos, BlockState state)
    {
        BlockInventory wrapper = new BlockInventory(pos, state);
        CollapseRecipe recipe = getRecipe(world, wrapper);
        if (recipe != null)
        {
            BlockState collapseState = recipe.getBlockCraftingResult(wrapper);
            world.setBlockAndUpdate(pos, collapseState); // Required as the falling block entity will replace the block in it's first tick
            world.addFreshEntity(new TFCFallingBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, collapseState));
            return true;
        }
        return false;
    }

    CollapseRecipe(ResourceLocation id, BlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        super(id, ingredient, outputState, copyInputState);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.COLLAPSE.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.COLLAPSE;
    }
}