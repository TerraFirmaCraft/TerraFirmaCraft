/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class LoomBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.loom");
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
        if (!level.isClientSide)
        {
            loom.checkForLastTickSync();
        }
        if (loom.recipe != null)
        {
            if (loom.lastTexture == null)
            {
                loom.lastTexture = loom.recipe.getInProgressTexture();
            }
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
                        loom.inventory.setStackInSlot(SLOT_OUTPUT, recipe.assemble(loom.inventory.getStackInSlot(SLOT_RECIPE)));
                    }
                    loom.markForSync();
                }
            }
        }
    }

    @Nullable private LoomRecipe recipe = null;
    @Nullable private ResourceLocation lastTexture;

    private int progress = 0;
    private long lastPushed = 0L;
    private boolean needsProgressUpdate = false;
    private boolean needsRecipeUpdate = false;

    public LoomBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOOM.get(), pos, state, defaultInventory(2), NAME);

        if (TFCConfig.SERVER.loomEnableAutomation.get())
        {
            sidedInventory.on(new PartialItemHandler(inventory).insert(SLOT_RECIPE), Direction.Plane.HORIZONTAL);
            sidedInventory.on(new PartialItemHandler(inventory).extract(SLOT_OUTPUT), Direction.DOWN);
        }
    }

    public InteractionResult onRightClick(Player player)
    {
        assert level != null;
        final ItemStack heldItem = player.getMainHandItem();

        // when shifting we let you remove items
        if (player.isShiftKeyDown())
        {
            if (!inventory.getStackInSlot(SLOT_RECIPE).isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(SLOT_RECIPE, Integer.MAX_VALUE, false));
                clearRecipe();
                markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }
        // loom is complete
        if (!inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.getStackInSlot(SLOT_OUTPUT).copy());
            inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
            markForSync();
            clearRecipe();
            return InteractionResult.SUCCESS;
        }

        // Loom is empty, initialize
        final ItemStack recipeItem = inventory.getStackInSlot(SLOT_RECIPE);
        if (recipeItem.isEmpty() && isItemValid(SLOT_RECIPE, heldItem))
        {
            inventory.setStackInSlot(SLOT_RECIPE, heldItem.split(1));
            updateCachedRecipe();
            markForSync();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        // Loom is not empty, add items.
        if (!recipeItem.isEmpty() && heldItem.getItem() == recipeItem.getItem() && recipe != null && recipe.getInputCount() > recipeItem.getCount())
        {
            inventory.getStackInSlot(SLOT_RECIPE).grow(1);
            heldItem.shrink(1);
            markForSync();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Push the loom
        if (recipe != null && heldItem.isEmpty() && recipe.getInputCount() == inventory.getStackInSlot(SLOT_RECIPE).getCount() && progress < recipe.getStepCount() && !needsProgressUpdate)
        {
            final long time = level.getGameTime() - lastPushed;
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
        final int time = (int) (level.getGameTime() - lastPushed);
        if (time < 20)
        {
            return Math.sin((Math.PI / 20) * time) * 0.23125;
        }
        return 0;
    }

    private void clearRecipe()
    {
        progress = 0;
        recipe = null;
        lastTexture = null;
        markForSync();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = true;
        markForSync();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        assert level != null;
        if (slot == SLOT_RECIPE)
        {
            if (!inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
            {
                return false;
            }
            return LoomRecipe.getRecipe(stack) != null;
        }
        return true;
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putInt("progress", progress);
        if (lastTexture != null)
        {
            tag.putString("lastTexture", lastTexture.toString());
        }
        tag.putLong("lastPushed", lastPushed);
        super.saveAdditional(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        progress = tag.getInt("progress");
        lastTexture = tag.contains("lastTexture", Tag.TAG_STRING) ? Helpers.resourceLocation(tag.getString("lastTexture")) : null;
        needsRecipeUpdate = true;
        lastPushed = tag.getLong("lastPushed");
        super.loadAdditional(tag);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        if (slot == SLOT_OUTPUT)
        {
            return 64;
        }
        return recipe != null ? recipe.getInputCount() : 1;
    }

    @Nullable
    public ResourceLocation getLastTexture()
    {
        return lastTexture;
    }

    private void updateCachedRecipe()
    {
        assert level != null;
        this.recipe = LoomRecipe.getRecipe(inventory.getStackInSlot(SLOT_RECIPE));
        if (recipe == null && progress > 0)
        {
            progress = 0;
            markForSync();
        }
        if (inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
        {
            lastTexture = null;
            markForSync();
        }
    }
}
