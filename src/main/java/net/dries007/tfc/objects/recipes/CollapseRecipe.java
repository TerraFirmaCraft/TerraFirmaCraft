package net.dries007.tfc.objects.recipes;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.objects.entities.TFCFallingBlockEntity;
import net.dries007.tfc.util.support.SupportManager;

/**
 * This handles all logic relating to block collapses:
 * - Triggering collapses via {@link CollapseRecipe#tryTriggerCollapse(World, BlockPos)}
 * - Checking if blocks can start collapses via {@link CollapseRecipe#canStartCollapse(IWorld, BlockPos)}
 * - Checking if blocks can actually collapse via {@link CollapseRecipe#canCollapse(IWorld, BlockPos)}
 * - Directly causing collapses via {@link CollapseRecipe#startCollapse(World, BlockPos)}
 * - Finding what blocks convert to when collapsing (the point of this recipe)
 */
public class CollapseRecipe extends SimpleBlockRecipe
{
    private static final Random RANDOM = new Random();

    /**
     * Called to attempt to trigger a collapse, from when a player mines a block.
     */
    public static boolean tryTriggerCollapse(World world, BlockPos pos)
    {
        if (world.isRemote() || !world.isAreaLoaded(pos, 32))
        {
            return false;
        }
        if (RANDOM.nextFloat() < TFCConfig.SERVER.collapseTriggerChance.get())
        {
            // Random radius
            int radX = (RANDOM.nextInt(5) + 4) / 2;
            int radY = (RANDOM.nextInt(3) + 2) / 2;
            int radZ = (RANDOM.nextInt(5) + 4) / 2;
            for (BlockPos checking : findUnsupportedPositions(world, pos.add(-radX, -radY, -radZ), pos.add(radX, radY, radZ))) // 9x5x9 max
            {
                // Check the area for a block collapse!
                if (canStartCollapse(world, checking))
                {
                    // Trigger collapse!
                    startCollapse(world, checking);
                    // todo: sound effect
                    //worldIn.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return true; // Don't need to check other blocks
                }
            }
        }
        return false;
    }

    /**
     * This is an optimized way to check for blocks that aren't supported during a cave in, instead of checking every single block individually and calling BlockSupper#isBeingSupported
     */
    public static Set<BlockPos> findUnsupportedPositions(IWorld worldIn, BlockPos from, BlockPos to)
    {
        Set<BlockPos> listSupported = new HashSet<>();
        Set<BlockPos> listUnsupported = new HashSet<>();
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());
        for (BlockPos searchingPoint : SupportManager.INSTANCE.getMaximumSupportedAreaAround(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)))
        {
            if (!listSupported.contains(searchingPoint))
            {
                listUnsupported.add(searchingPoint.toImmutable()); // Adding blocks that wasn't found supported
            }
            BlockState supportState = worldIn.getBlockState(searchingPoint);
            SupportManager.INSTANCE.get(supportState).ifPresent(support -> {
                for (BlockPos supported : support.getSupportedArea(searchingPoint))
                {
                    listSupported.add(supported.toImmutable()); // Adding all supported blocks by this support
                    listUnsupported.remove(supported); // Remove if this block was added earlier
                }
            });
        }
        // Searching point wasn't from points between from <-> to but
        // Time to remove the outsides that were added for convenience
        listUnsupported.removeIf(content -> content.getX() < minX || content.getX() > maxX || content.getY() < minY || content.getY() > maxY || content.getZ() < minZ || content.getZ() > maxZ);
        return listUnsupported;
    }

    public static boolean canStartCollapse(IWorld world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return TFCTags.CAN_START_COLLAPSE.contains(state.getBlock()) && canCollapse(world, pos);
    }

    public static boolean canCollapse(IWorld world, BlockPos pos)
    {
        return world.getBlockState(pos.down()).getMaterial().isReplaceable();
    }

    public static void startCollapse(World world, BlockPos centerPos)
    {
        // When starting a collapse, support checks are ignored completely
        int radiusH = TFCConfig.SERVER.collapseMinRadius.get() + RANDOM.nextInt(TFCConfig.SERVER.collapseRadiusVariance.get());
        for (BlockPos pos : BlockPos.getAllInBoxMutable(centerPos.add(-radiusH, -4, -radiusH), centerPos.add(radiusH, 1, radiusH)))
        {
            if (canCollapse(world, pos))
            {
                double distance = centerPos.distanceSq(pos);
                double chance = TFCConfig.SERVER.collapsePropagateChance.get() - 0.01 * Math.sqrt(distance);
                if (RANDOM.nextFloat() < chance)
                {
                    BlockRecipeWrapper wrapper = new BlockRecipeWrapper(world, pos);
                    RecipeCache.INSTANCE.get(TFCRecipeTypes.COLLAPSE, world, wrapper).ifPresent(recipe -> {
                        BlockState state = recipe.getBlockCraftingResult(wrapper);
                        world.setBlockState(pos, state); // Required as the falling block entity will replace the block in it's first tick
                        world.addEntity(new TFCFallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state));
                    });
                }
            }
        }
    }

    public CollapseRecipe(ResourceLocation id, IBlockIngredient ingredient, BlockState outputState)
    {
        super(id, ingredient, outputState);
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.COLLAPSE.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.COLLAPSE;
    }

    public static class Serializer extends SimpleBlockRecipe.Serializer<CollapseRecipe>
    {
        @Override
        protected CollapseRecipe create(ResourceLocation id, IBlockIngredient ingredient, BlockState state)
        {
            return new CollapseRecipe(id, ingredient, state);
        }
    }
}
