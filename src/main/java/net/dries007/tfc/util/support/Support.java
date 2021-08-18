/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.support;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.JsonHelpers;

public class Support
{
    private final ResourceLocation id;
    private final int supportUp, supportDown, supportHorizontal;
    private final BlockIngredient ingredient;

    public Support(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.ingredient = BlockIngredient.fromJson(JsonHelpers.get(json, "ingredient"));
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