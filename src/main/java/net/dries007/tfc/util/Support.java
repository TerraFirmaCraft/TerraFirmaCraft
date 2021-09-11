/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;

public class Support
{
    public static final SupportManager MANAGER = new SupportManager();

    /**
     * Finds all unsupported positions in a large area. It's more efficient than checking each block individually and calling {@link Support#isSupported(BlockGetter, BlockPos)}
     */
    public static Set<BlockPos> findUnsupportedPositions(BlockGetter worldIn, BlockPos from, BlockPos to)
    {
        Set<BlockPos> listSupported = new HashSet<>();
        Set<BlockPos> listUnsupported = new HashSet<>();
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());
        for (BlockPos searchingPoint : MANAGER.getMaximumSupportedAreaAround(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)))
        {
            if (!listSupported.contains(searchingPoint))
            {
                listUnsupported.add(searchingPoint.immutable()); // Adding blocks that wasn't found supported
            }
            BlockState supportState = worldIn.getBlockState(searchingPoint);
            MANAGER.get(supportState).ifPresent(support -> {
                for (BlockPos supported : support.getSupportedArea(searchingPoint))
                {
                    listSupported.add(supported.immutable()); // Adding all supported blocks by this support
                    listUnsupported.remove(supported); // Remove if this block was added earlier
                }
            });
        }
        // Searching point wasn't from points between from <-> to but
        // Time to remove the outsides that were added for convenience
        listUnsupported.removeIf(content -> content.getX() < minX || content.getX() > maxX || content.getY() < minY || content.getY() > maxY || content.getZ() < minZ || content.getZ() > maxZ);
        return listUnsupported;
    }

    public static boolean isSupported(BlockGetter world, BlockPos pos)
    {
        for (BlockPos supportPos : MANAGER.getMaximumSupportedAreaAround(pos, pos))
        {
            BlockState supportState = world.getBlockState(supportPos);
            if (MANAGER.get(supportState).map(support -> support.canSupport(supportPos, pos)).orElse(false))
            {
                return true;
            }
        }
        return false;
    }

    private final ResourceLocation id;
    private final int supportUp, supportDown, supportHorizontal;
    private final BlockIngredient ingredient;

    public Support(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.ingredient = BlockIngredients.fromJson(JsonHelpers.get(json, "ingredient"));
        this.supportUp = GsonHelper.getAsInt(json, "support_up", 0);
        this.supportDown = GsonHelper.getAsInt(json, "support_down", 0);
        this.supportHorizontal = GsonHelper.getAsInt(json, "support_horizontal", 0);

        if (supportUp < 0 || supportDown < 0 || supportHorizontal < 0)
        {
            throw new JsonParseException("Support values must be nonnegative.");
        }
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public int getSupportUp()
    {
        return supportUp;
    }

    public int getSupportDown()
    {
        return supportDown;
    }

    public int getSupportHorizontal()
    {
        return supportHorizontal;
    }

    public boolean matches(BlockState state)
    {
        return ingredient.test(state);
    }

    public boolean canSupport(BlockPos supportPos, BlockPos testPos)
    {
        BlockPos diff = supportPos.subtract(testPos);
        return Math.abs(diff.getX()) <= supportHorizontal && -supportDown <= diff.getY() && diff.getY() <= supportUp && Math.abs(diff.getZ()) <= supportHorizontal;
    }

    public Iterable<BlockPos> getSupportedArea(BlockPos center)
    {
        return BlockPos.betweenClosed(center.offset(-supportHorizontal, -supportDown, -supportHorizontal), center.offset(supportHorizontal, supportUp, supportHorizontal));
    }
}