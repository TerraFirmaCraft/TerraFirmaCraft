/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.SewingTableBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.recipes.SewingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;
import net.dries007.tfc.util.ArrayContainerData;
import net.dries007.tfc.util.Helpers;

public class SewingTableContainer extends Container implements ISlotCallback, ButtonHandlerContainer
{
    public static SewingTableContainer create(Inventory playerInventory, int windowId, ContainerLevelAccess access)
    {
        return new SewingTableContainer(playerInventory, windowId, access).init(playerInventory, 30);
    }

    private static boolean isWool(ItemStack item)
    {
        return Helpers.isItem(item, TFCTags.Items.SEWING_LIGHT_CLOTH);
    }

    private static boolean isBurlap(ItemStack item)
    {
        return Helpers.isItem(item, TFCTags.Items.SEWING_DARK_CLOTH);
    }

    private static boolean isString(ItemStack item)
    {
        return Helpers.isItem(item, Tags.Items.STRING);
    }

    private static boolean isNeedle(ItemStack item)
    {
        return Helpers.isItem(item, TFCTags.Items.SEWING_NEEDLES);
    }

    public static final int NUM_SLOTS = 5;
    public static final int SLOT_YARN = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_INPUT_1 = 2;
    public static final int SLOT_INPUT_2 = 3;
    public static final int SLOT_RESULT = 4;
    public static final int BURLAP_ID = 0;
    public static final int WOOL_ID = 1;
    public static final int REMOVE_ID = 2;
    public static final int NEEDLE_ID = 3;
    public static final int PLACE_STITCH_ID = 4;
    public static final int RECIPE_ID = 5;
    public static final int SQUARES_PER_CLOTH = 8;
    public static final int STITCHES_PER_YARN = 16;

    public static final int MAX_SQUARES = 32;
    public static final int MAX_STITCHES = 45;
    public static final int PLACED_SLOTS_OFFSET = 100;

    private final ItemStackHandler inventory;
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    private final DataSlot activeMaterialData = DataSlot.standalone();
    private final ArrayContainerData placedMaterialData = new ArrayContainerData(MAX_SQUARES);
    private final ArrayContainerData stitchData = new ArrayContainerData(MAX_STITCHES);
    private final DataSlot burlapCount = DataSlot.standalone();
    private final DataSlot woolCount = DataSlot.standalone();
    private final DataSlot stringCount = DataSlot.standalone();
    private final DataSlot usedBurlap = DataSlot.standalone();
    private final DataSlot usedWool = DataSlot.standalone();
    private final DataSlot usedString = DataSlot.standalone();
    private final Input input = new Input(this);

    public SewingTableContainer(Inventory playerInventory, int windowId)
    {
        this(playerInventory, windowId, ContainerLevelAccess.NULL);
    }

    public SewingTableContainer(Inventory playerInventory, int windowId, ContainerLevelAccess access)
    {
        super(TFCContainerTypes.SEWING_TABLE.get(), windowId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.inventory = new InventoryItemHandler(this, NUM_SLOTS);
        addDataSlot(activeMaterialData).set(-1);
        for (int i = 0; i < MAX_SQUARES; i++)
            placedMaterialData.set(i, -1);
        for (int i = 0; i < MAX_STITCHES; i++)
            stitchData.set(i, 0);
        addDataSlots(placedMaterialData);
        addDataSlots(stitchData);
        addDataSlot(woolCount).set(0);
        addDataSlot(burlapCount).set(0);
        addDataSlot(stringCount).set(0);
        addDataSlot(usedWool).set(0);
        addDataSlot(usedBurlap).set(0);
        addDataSlot(usedString).set(0);

        addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack stack)
            {
                if (slotIndex == SLOT_YARN || slotIndex == SLOT_INPUT_1 || slotIndex == SLOT_INPUT_2)
                {
                    woolCount.set(countItem(SewingTableContainer::isWool));
                    burlapCount.set(countItem(SewingTableContainer::isBurlap));
                    stringCount.set(inventory.getStackInSlot(SLOT_YARN).getCount());
                }
            }

            @Override
            public void dataChanged(AbstractContainerMenu container, int dataSlotIndex, int value) {}
        });
    }

    public void updateResultItem()
    {
        access.execute((level, pos) -> {
            level.getRecipeManager().getRecipeFor(TFCRecipeTypes.SEWING.get(), input, level).ifPresentOrElse(recipe -> {
                final ItemStack result = recipe.getResultItem(level.registryAccess());
                if (result.getItem() != inventory.getStackInSlot(SLOT_RESULT).getItem())
                {
                    inventory.setStackInSlot(SLOT_RESULT, result);
                    activeMaterialData.set(-1);
                }
            }, () -> inventory.setStackInSlot(SLOT_RESULT, ItemStack.EMPTY));
        });
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_YARN)
        {
            return isString(stack);
        }
        if (slot == SLOT_TOOL)
        {
            return isNeedle(stack);
        }
        if (slot == SLOT_INPUT_1 || slot == SLOT_INPUT_2)
        {
            return !isString(stack) && !isNeedle(stack);
        }
        return slot != SLOT_RESULT;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return switch (slot)
        {
            case SLOT_TOOL -> 1;
            case SLOT_INPUT_1, SLOT_INPUT_2, SLOT_YARN -> 8;
            default -> 64;
        };
    }

    public int countItem(Predicate<ItemStack> input)
    {
        int count = 0;
        final ItemStack firstItem = inventory.getStackInSlot(SLOT_INPUT_1);
        if (input.test(firstItem))
            count += firstItem.getCount();
        final ItemStack secondItem = inventory.getStackInSlot(SLOT_INPUT_2);
        if (input.test(secondItem))
            count += secondItem.getCount();
        return count;
    }

    @Override
    public void removed(Player player)
    {
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            if (slot != SLOT_RESULT)
            {
                final ItemStack stack = inventory.getStackInSlot(slot);
                giveItemStackToPlayerOrDrop(player, stack);
            }
        }
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof SewingTableBlock && player.position().distanceToSqr(pos.getCenter()) < 64, true);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        int activeMaterial = getActiveMaterial();
        if (buttonID == BURLAP_ID || buttonID == NEEDLE_ID || buttonID == WOOL_ID || buttonID == REMOVE_ID)
        {
            activeMaterial = buttonID;
        }
        else if (buttonID - PLACED_SLOTS_OFFSET >= 0 && buttonID - PLACED_SLOTS_OFFSET < MAX_SQUARES && (activeMaterial == BURLAP_ID || activeMaterial == WOOL_ID || activeMaterial == REMOVE_ID) && activeMaterial != getPlacedMaterial(buttonID - PLACED_SLOTS_OFFSET))
        {
            if (activeMaterial == BURLAP_ID)
            {
                if (getBurlapCount() <= 0)
                    return;
                usedBurlap.set(usedBurlap.get() + 1);
                access.execute((level, pos) -> Helpers.playSound(level, pos, SoundEvents.WOOL_PLACE));
            }
            else if (activeMaterial == WOOL_ID)
            {
                if (getWoolCount() <= 0)
                    return;
                usedWool.set(usedWool.get() + 1);
                access.execute((level, pos) -> Helpers.playSound(level, pos, SoundEvents.WOOL_PLACE));
            }
            else
            {
                access.execute((level, pos) -> Helpers.playSound(level, pos, SoundEvents.WOOL_BREAK));
            }
            placedMaterialData.set(buttonID - PLACED_SLOTS_OFFSET, activeMaterial == REMOVE_ID ? -1 : activeMaterial);
        }
        else if (buttonID == PLACE_STITCH_ID && extraNBT != null && !inventory.getStackInSlot(SLOT_TOOL).isEmpty())
        {
            if (getYarnCount() <= 0)
                return;
            usedString.set(usedString.get() + 1);
            stitchData.set(extraNBT.getInt("id"), extraNBT.getInt("stitchType"));
            access.execute((level, pos) -> Helpers.playSound(level, pos, SoundEvents.WOOL_HIT));
        }
        activeMaterialData.set(activeMaterial);
        updateResultItem();
        broadcastChanges();
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        if (slot == SLOT_RESULT)
        {
            int woolCount = Mth.ceil((float) usedWool.get() / SQUARES_PER_CLOTH);
            int burlapCount = Mth.ceil((float) usedBurlap.get() / SQUARES_PER_CLOTH);
            ItemStack stack1 = inventory.getStackInSlot(SLOT_INPUT_1);
            ItemStack stack2 = inventory.getStackInSlot(SLOT_INPUT_2);
            if (isWool(stack1))
            {
                final int toShrink = Math.min(woolCount, stack1.getCount());
                stack1.shrink(toShrink);
                woolCount -= toShrink;
            }
            if (woolCount > 0 && isWool(stack2))
            {
                stack2.shrink(woolCount);
            }
            if (isBurlap(stack1))
            {
                final int toShrink = Math.min(burlapCount, stack1.getCount());
                stack1.shrink(toShrink);
                burlapCount -= toShrink;
            }
            if (burlapCount > 0 && isBurlap(stack2))
            {
                stack2.shrink(burlapCount);
            }
            inventory.getStackInSlot(SLOT_YARN).shrink(Mth.ceil((float) usedString.get() / STITCHES_PER_YARN));

            usedWool.set(0);
            usedBurlap.set(0);
            usedString.set(0);
            activeMaterialData.set(-1);
            for (int i = 0; i < MAX_SQUARES; i++)
                placedMaterialData.set(i, -1);
            for (int i = 0; i < MAX_STITCHES; i++)
                stitchData.set(i, 0);
            Helpers.damageItem(inventory.getStackInSlot(SLOT_TOOL), 1);
            access.execute((level, pos) -> Helpers.playSound(level, pos, SoundEvents.UI_LOOM_TAKE_RESULT));
        }
    }

    public int getBurlapCount()
    {
        return burlapCount.get() * SQUARES_PER_CLOTH - usedBurlap.get();
    }

    public int getWoolCount()
    {
        return woolCount.get() * SQUARES_PER_CLOTH - usedWool.get();
    }

    public int getYarnCount()
    {
        return stringCount.get() * STITCHES_PER_YARN - usedString.get();
    }

    public int getActiveMaterial()
    {
        return activeMaterialData.get();
    }

    public int getPlacedMaterial(int slot)
    {
        return placedMaterialData.get(slot);
    }

    public int getStitchAt(int slot)
    {
        return stitchData.get(slot);
    }

    public boolean canPickup(int slot)
    {
        return canPickup(inventory.getStackInSlot(slot));
    }

    public boolean canPickup(ItemStack item)
    {
        if (isWool(item) && usedWool.get() > 0)
            return false;
        if (isBurlap(item) && usedBurlap.get() > 0)
            return false;
        if (isString(item) && usedString.get() > 0)
            return false;
        if (isNeedle(item) && usedString.get() > 0)
            return false;
        return true;
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch(typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, SLOT_YARN, NUM_SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        super.addContainerSlots();
        addSlot(new SewingInputSlot(this, inventory, SLOT_YARN, 8, 83));
        addSlot(new SewingInputSlot(this, inventory, SLOT_TOOL, 26, 83));
        addSlot(new SewingInputSlot(this, inventory, SLOT_INPUT_1, 62, 83));
        addSlot(new SewingInputSlot(this, inventory, SLOT_INPUT_2, 80, 83));
        addSlot(new CallbackSlot(this, inventory, SLOT_RESULT, 152, 83));
    }

    public static class SewingInputSlot extends CallbackSlot
    {
        private final SewingTableContainer callback;

        public SewingInputSlot(SewingTableContainer callback, IItemHandler inventory, int index, int x, int y)
        {
            super(callback, inventory, index, x, y);
            this.callback = callback;
        }

        @Override
        public boolean mayPickup(Player player)
        {
            return callback.canPickup(getItem()) && super.mayPickup(player);
        }
    }

    public record Input(SewingTableContainer container) implements NonEmptyInput
    {
        public boolean stitchesMatch(SewingRecipe recipe)
        {
            final int[] array = container.stitchData.getArray();
            if (array.length != MAX_STITCHES)
                return false;
            for (int i = 0; i < MAX_STITCHES; i++)
            {
                if (recipe.getStitch(i) != (array[i] == 1))
                    return false;
            }
            return true;
        }

        public boolean squaresMatch(SewingRecipe recipe)
        {
            final int[] array = container.placedMaterialData.getArray();
            if (array.length != MAX_SQUARES)
                return false;
            for (int i = 0; i < MAX_SQUARES; i++)
            {
                if (recipe.getSquare(i) != array[i])
                    return false;
            }
            return true;
        }
    }
}
