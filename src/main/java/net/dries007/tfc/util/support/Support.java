/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.support;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.objects.recipes.IBlockIngredient;

public class Support
{
    private final ResourceLocation id;
    private final int supportUp, supportDown, supportHorizontal;
    private final IBlockIngredient ingredient;

    public Support(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
        this.supportUp = JSONUtils.getInt(json, "support_up", 0);
        this.supportDown = JSONUtils.getInt(json, "support_down", 0);
        this.supportHorizontal = JSONUtils.getInt(json, "support_horizontal", 0);

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
        return BlockPos.getAllInBoxMutable(center.add(-supportHorizontal, -supportDown, -supportHorizontal), center.add(supportHorizontal, supportUp, supportHorizontal));
    }
}
