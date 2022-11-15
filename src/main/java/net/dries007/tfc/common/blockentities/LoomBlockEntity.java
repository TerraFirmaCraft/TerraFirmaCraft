/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LoomBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.loom");
    private static final int SLOT_RECIPE = 0;
    private static final int SLOT_OUTPUT = 1;

    public static void tick(Level level, BlockPos pos, BlockState state, LoomBlockEntity loom)
    {
        // No access to the level when loading NBT, so it has to happen on the first tick
        if (loom.needsRecipeUpdate)
        {
            loom.updateCachedRecipe();
            loom.needsRecipeUpdate = false;
        }
        if (loom.recipe != null)
        {
            final LoomRecipe recipe = loom.recipe; // Avoids NPE on slot changes
            if (loom.needsProgressUpdate)
            {
                if (level.getGameTime() - loom.lastPushed >= 20)
                {
                    loom.needsProgressUpdate = false;
                    loom.progress++;

                    if (loom.progress == recipe.getStepCount())
                    {
                        loom.inventory.setStackInSlot(SLOT_RECIPE, ItemStack.EMPTY);
                        loom.inventory.setStackInSlot(SLOT_OUTPUT, recipe.assemble(new ItemStackInventory(loom.inventory.getStackInSlot(SLOT_RECIPE))));
                    }
                    loom.markForSync();
                }
            }
        }
    }


    @Nullable private LoomRecipe recipe = null;
    @Nullable private ResourceLocation recipeId;

    private int progress = 0;
    private long lastPushed = 0L;
    private boolean needsProgressUpdate = false;
    private boolean needsRecipeUpdate = false;

    public LoomBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOOM.get(), pos, state, defaultInventory(2), NAME);
    }

    public InteractionResult onRightClick(Player player)
    {
        assert level != null;
        ItemStack heldItem = player.getMainHandItem();

        // when shifting we let you remove items
        if (player.isShiftKeyDown())
        {
            if (!inventory.getStackInSlot(SLOT_RECIPE).isEmpty() && progress == 0)
            {
                if (heldItem.isEmpty() && !level.isClientSide)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(SLOT_RECIPE, 1, false));
                    if (inventory.getStackInSlot(SLOT_RECIPE).isEmpty())
                    {
                        clearRecipe();
                    }
                    markForSync();
                }
                return InteractionResult.SUCCESS;
            }
        }
        else
        {
            // everything is empty let's initialize
            if (inventory.getStackInSlot(SLOT_RECIPE).isEmpty() && inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
            {
                // This will always be null for clients on servers unless the recipe cache is reloaded for them
                LoomRecipe foundRecipe = LoomRecipe.getRecipe(level, new ItemStackInventory(heldItem));
                if (foundRecipe != null)
                {
                    if (!level.isClientSide)
                    {
                        inventory.setStackInSlot(SLOT_RECIPE, heldItem.split(1));
                        recipeId = foundRecipe.getId();
                        recipe = foundRecipe;
                        markForSync();
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else if (!inventory.getStackInSlot(SLOT_RECIPE).isEmpty()) // we are holding something that can be added to the current loom inventory
            {
                if (heldItem.sameItem(inventory.getStackInSlot(SLOT_RECIPE)) && recipe != null && recipe.getInputCount() > inventory.getStackInSlot(SLOT_RECIPE).getCount())
                {
                    if (!level.isClientSide)
                    {
                        heldItem.shrink(1);
                        inventory.getStackInSlot(SLOT_RECIPE).grow(1);
                        markForSync();
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            // actual pushing function of the loom
            if (recipe != null && heldItem.isEmpty() && recipe.getInputCount() == inventory.getStackInSlot(SLOT_RECIPE).getCount() && progress < recipe.getStepCount() && !needsProgressUpdate)
            {
                long time = level.getGameTime() - lastPushed;
                // This acts strangely when set to just 'time < 20', for some reason
                // Animation will mess up if right click is held down, even if animation is sped up
                if (time <= 20) // we only let you update once a second
                {
                    return InteractionResult.PASS;
                }
                level.playSound(null, worldPosition, TFCSounds.LOOM_WEAVE.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
                lastPushed = level.getGameTime();
                needsProgressUpdate = true;
                markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide); // we want to swing the player's arm
            }

            if (!inventory.getStackInSlot(SLOT_OUTPUT).isEmpty() && heldItem.isEmpty()) // loom is complete
            {
                if (!level.isClientSide)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inventory.getStackInSlot(SLOT_OUTPUT).copy());
                    inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
                    markForSync();
                }
                progress = 0;
                clearRecipe();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean currentBoolean()
    {
        return progress % 2 == 0;
    }

    public int getCount()
    {
        return inventory.getStackInSlot(SLOT_RECIPE).getCount();
    }

    public int getProgress()
    {
        return progress;
    }

    @Nullable
    public LoomRecipe getRecipe()
    {
        return recipe;
    }

    public double getAnimPos()
    {
        assert level != null;
        if (recipe == null) return 0;
        int time = (int) (level.getGameTime() - lastPushed);
        if (time < 20)
        {
            return Math.sin((Math.PI / 20) * time) * 0.23125;
        }
        return 0;
    }

    private void clearRecipe()
    {
        recipe = null;
        recipeId = null;
        markForSync();
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putInt("progress", progress);
        if (recipeId != null)
        {
            tag.putString("recipe", recipeId.toString());
        }
        super.saveAdditional(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        progress = tag.getInt("progress");
        recipeId = tag.contains("recipe") ? new ResourceLocation(tag.getString("recipe")) : null;

        updateCachedRecipe();
        super.loadAdditional(tag);
    }

    private void updateCachedRecipe()
    {
        recipe = null;
        if (level == null)
        {
            // On first load, but not on sync
            needsRecipeUpdate = true;
        }
        else if (recipeId != null)
        {
            recipe = level.getRecipeManager().byKey(recipeId)
                .map(r -> r instanceof LoomRecipe lr ? lr : null)
                .orElse(null);
        }
    }
}
