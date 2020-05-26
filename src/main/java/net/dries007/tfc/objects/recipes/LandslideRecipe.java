package net.dries007.tfc.objects.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.objects.entities.TFCFallingBlockEntity;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.support.SupportManager;

/**
 * This handles all logic for land slides (sideways gravity affected blocks)
 * The recipe only handles landslide *transformations*, not if a block is affected by landslides. That is determined by tag.
 *
 * @see SupportManager
 * @see TFCFallingBlockEntity
 */
public class LandslideRecipe extends SimpleBlockRecipe
{
    public static final IndirectHashCollection<Block, LandslideRecipe> CACHE = new IndirectHashCollection<>(recipe -> recipe.getBlockIngredient().getValidBlocks());
    private static final Random RANDOM = new Random();

    public static Optional<LandslideRecipe> getRecipe(World world, BlockRecipeWrapper wrapper)
    {
        return CACHE.getAll(wrapper.getState().getBlock()).stream().filter(recipe -> recipe.matches(wrapper, world)).findFirst();
    }

    /**
     * Tries to cause a land slide from a given block
     *
     * @return true if a land slide actually occurred
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean tryLandslide(World world, BlockPos pos, BlockState state)
    {
        if (!world.isRemote())
        {
            if (TFCConfig.SERVER.enableBlockLandslides.get())
            {
                BlockPos fallPos = getLandSlidePos(world, pos);
                if (fallPos != null)
                {
                    BlockRecipeWrapper wrapper = new BlockRecipeWrapper(world, pos, state);
                    getRecipe(world, wrapper).ifPresent(recipe -> {
                        BlockState fallingState = recipe.getBlockCraftingResult(wrapper);
                        if (!fallPos.equals(pos))
                        {
                            world.removeBlock(pos, false);
                        }
                        world.setBlockState(fallPos, fallingState);
                        world.playSound(null, pos, TFCSounds.DIRT_SLIDE_SHORT.get(), SoundCategory.BLOCKS, 0.4f, 1.0f);
                        world.addEntity(new TFCFallingBlockEntity(world, fallPos.getX() + 0.5, fallPos.getY() + 0.5, fallPos.getZ() + 0.5, fallingState));
                    });
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public static BlockPos getLandSlidePos(World world, BlockPos pos)
    {
        if (SupportManager.isSupported(world, pos))
        {
            return null;
        }
        else if (TFCFallingBlockEntity.canFallThrough(world, pos.down()))
        {
            return pos;
        }
        else
        {
            // Check if supported by at least two horizontals, or one on top
            if (!isSupportedOnSide(world, pos, Direction.UP))
            {
                int supportedDirections = 0;
                List<BlockPos> possibleDirections = new ArrayList<>();
                for (Direction side : Direction.Plane.HORIZONTAL)
                {
                    if (isSupportedOnSide(world, pos, side))
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
                        BlockPos posSide = pos.offset(side);
                        if (TFCFallingBlockEntity.canFallThrough(world, posSide) && TFCFallingBlockEntity.canFallThrough(world, posSide.down()))
                        {
                            possibleDirections.add(posSide);
                        }
                    }
                }

                if (!possibleDirections.isEmpty())
                {
                    return possibleDirections.get(RANDOM.nextInt(possibleDirections.size()));
                }
            }
        }
        return null;
    }

    public static boolean isSupportedOnSide(IBlockReader world, BlockPos pos, Direction side)
    {
        BlockPos sidePos = pos.offset(side);
        BlockState sideState = world.getBlockState(sidePos);
        return sideState.isSolidSide(world, sidePos, side.getOpposite()) || TFCTags.SUPPORTS_LANDSLIDE.contains(sideState.getBlock());
    }

    private LandslideRecipe(ResourceLocation id, IBlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        super(id, ingredient, outputState, copyInputState);
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.LANDSLIDE.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.LANDSLIDE;
    }

    public static class Serializer extends SimpleBlockRecipe.Serializer<LandslideRecipe>
    {
        @Override
        protected LandslideRecipe create(ResourceLocation id, IBlockIngredient ingredient, BlockState state, boolean copyInputState)
        {
            return new LandslideRecipe(id, ingredient, state, copyInputState);
        }
    }
}
