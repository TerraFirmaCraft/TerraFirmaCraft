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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.Support;

/**
 * This handles all logic for land slides (sideways gravity affected blocks)
 * The recipe only handles landslide *transformations*, not if a block is affected by landslides. That is determined by tag.
 *
 * @see TFCFallingBlockEntity
 */
public class LandslideRecipe extends BlockRecipe
{
    public static final IndirectHashCollection<Block, LandslideRecipe> CACHE = IndirectHashCollection.createForRecipe(recipe -> recipe.getBlockIngredient().blocks(), TFCRecipeTypes.LANDSLIDE);

    @Nullable
    public static LandslideRecipe getRecipe(BlockState state)
    {
        return RecipeHelpers.getRecipe(CACHE, state, state.getBlock());
    }

    /**
     * Tries to cause a landslide from a given block
     *
     * @param state {@code level.getBlockState(pos)}
     * @return true if a landslide actually occurred
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean tryLandslide(Level level, BlockPos pos, BlockState state)
    {
        if (!level.isClientSide() && TFCConfig.SERVER.enableBlockLandslides.get())
        {
            final BlockPos fallPos = getLandslidePos(level, pos, state);
            if (fallPos != null)
            {
                final LandslideRecipe recipe = getRecipe(state);
                if (recipe != null)
                {
                    final BlockState fallingState = recipe.assembleBlock(state);
                    if (!fallPos.equals(pos))
                    {
                        level.removeBlock(pos, false); // Remove the original position, which would be the falling block
                        if (!FluidHelpers.isAirOrEmptyFluid(level.getBlockState(fallPos)))
                        {
                            level.destroyBlock(fallPos, true); // Destroy the block that currently occupies the pos we are going to move sideways into
                        }
                    }
                    if (TFCConfig.SERVER.farmlandMakesTheBestRaceTracks.get())
                    {
                        // This is funny, but technically a bug. So it's left here as a disabled-by-default easter egg.
                        // By setting the block and updating, farmland below will turn into a solid block, and then this falling block will attempt falling again, proceeding in a cycle.
                        // We avoid that by not causing a block update.
                        level.setBlockAndUpdate(fallPos, fallingState);
                    }
                    else
                    {
                        level.setBlock(fallPos, fallingState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }
                    level.playSound(null, pos, TFCSounds.DIRT_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
                    level.addFreshEntity(new TFCFallingBlockEntity(level, fallPos.getX() + 0.5, fallPos.getY(), fallPos.getZ() + 0.5, fallingState, 0.8f, 10));
                }
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static BlockPos getLandslidePos(Level level, BlockPos pos, BlockState fallingState)
    {
        if (Support.isSupported(level, pos))
        {
            return null;
        }
        else if (TFCFallingBlockEntity.canFallThrough(level, pos.below(), Direction.DOWN, fallingState))
        {
            return pos;
        }
        else
        {
            // Check if supported by at least two horizontals, or one on top
            if (!isSupportedOnSide(level, pos, Direction.UP))
            {
                int supportedDirections = 0;
                List<BlockPos> possibleDirections = new ArrayList<>();
                for (Direction side : Direction.Plane.HORIZONTAL)
                {
                    if (isSupportedOnSide(level, pos, side))
                    {
                        supportedDirections++;
                        if (supportedDirections >= 2)
                        {
                            // Supported by at least two sides, don't fall
                            return null;
                        }
                    }
                    else
                    {
                        // In order to fall in a direction, we need both the block immediately next to, and the one below to be open
                        // The one adjacent needs to be breakable, wheras the one below just needs to be unstable
                        final BlockPos posSide = pos.relative(side), posSideBelow = posSide.below();
                        if (TFCFallingBlockEntity.canFallThrough(level, posSide, side, fallingState) && TFCFallingBlockEntity.canFallThrough(level, posSideBelow, Direction.DOWN))
                        {
                            possibleDirections.add(posSide);
                        }
                    }
                }

                if (!possibleDirections.isEmpty())
                {
                    return possibleDirections.get(level.getRandom().nextInt(possibleDirections.size()));
                }
            }
        }
        return null;
    }

    public static boolean isSupportedOnSide(BlockGetter world, BlockPos pos, Direction side)
    {
        BlockPos sidePos = pos.relative(side);
        BlockState sideState = world.getBlockState(sidePos);
        return sideState.isFaceSturdy(world, sidePos, side.getOpposite()) || Helpers.isBlock(sideState, TFCTags.Blocks.SUPPORTS_LANDSLIDE);
    }

    public LandslideRecipe(BlockIngredient ingredient, Optional<BlockState> output)
    {
        super(ingredient, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.LANDSLIDE.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.LANDSLIDE.get();
    }
}