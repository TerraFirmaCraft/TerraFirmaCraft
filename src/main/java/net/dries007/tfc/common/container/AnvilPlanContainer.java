/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class AnvilPlanContainer extends BlockEntityContainer<AnvilBlockEntity> implements ButtonHandlerContainer
{
    public static AnvilPlanContainer create(AnvilBlockEntity anvil, Inventory playerInventory, int windowId)
    {
        return new AnvilPlanContainer(windowId, anvil).init(playerInventory, 0);
    }

    protected AnvilPlanContainer(int windowId, AnvilBlockEntity anvil)
    {
        super(TFCContainerTypes.ANVIL_PLAN.get(), windowId, anvil);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        if (extraNBT != null && player != null)
        {
            final ResourceLocation recipeId = new ResourceLocation(extraNBT.getString("recipe"));
            final AnvilRecipe recipe = Helpers.getRecipes(player.level, TFCRecipeTypes.ANVIL).get(recipeId);

            blockEntity.chooseRecipe(recipe);

            if (player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, blockEntity.anvilProvider(), blockEntity.getBlockPos());
            }
        }
    }
}
