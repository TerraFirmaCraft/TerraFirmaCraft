/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import org.jetbrains.annotations.NotNull;

public class ComposterBlockEntity extends InventoryBlockEntity<ItemStackHandler> {
    public static final int MAX_AMOUNT = 16;

    protected long lastUpdateTick = Integer.MIN_VALUE;
    private int green, brown;

    public ComposterBlockEntity(BlockPos pos, BlockState state) {
        this(TFCBlockEntities.COMPOSTER.get(), pos, state);
    }

    public ComposterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, defaultInventory(1));
        if (TFCConfig.SERVER.composterEnableAutomation.get()) {
            sidedInventory.on(new PartialItemHandler(inventory).extractAll(), Direction.DOWN);
        }
    }

    public void randomTick() {
        assert level != null;
        if (green >= MAX_AMOUNT && brown >= MAX_AMOUNT && !isRotten()) {
            if (getTicksSinceUpdate() > getReadyTicks()) {
                inventory.setStackInSlot(0, new ItemStack(TFCItems.COMPOST.get()));
                setState(TFCComposterBlock.CompostType.READY);
                markForSync();
            }
        }
        if (isRotten()) {
            Helpers.tickInfestation(level, getBlockPos(), 5, null);
        }
    }
//  fix Implicit cast from 'float' to 'long'
    public long getReadyTicks() {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(getBlockPos());
        float rainfall = Climate.getRainfall(level, cursor);
        long readyTicks = TFCConfig.SERVER.composterTicks.get();
        readyTicks = (long) (readyTicks * getRainfallAdjustmentFactor(rainfall));
        cursor.move(0, 1, 0);
        if (Helpers.isBlock(level.getBlockState(cursor), BlockTags.SNOW)) {
            readyTicks = (long) (readyTicks * 0.9f);
        }
        return adjustForNearbyComposters(cursor, readyTicks);
    }

    private long adjustForNearbyComposters(BlockPos.MutableBlockPos cursor, long readyTicks) {
        assert level != null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            cursor.setWithOffset(getBlockPos(), direction);
            if (level.getBlockState(cursor).getBlock() instanceof TFCComposterBlock) {
                readyTicks = (long) (readyTicks * 1.05f);
            }
        }
        return readyTicks;
    }

    // inverted trapezoid wave
    // extracted and fix converting to long
    private float getRainfallAdjustmentFactor(float rainfall) {
        if (rainfall < 150f) {
            return (150f - rainfall) / 50f + 1f;
        } else if (rainfall > 350f) {
            return (rainfall - 350f) / 50f + 1f;
        }
        return 1f;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        green = nbt.getInt("green");
        brown = nbt.getInt("brown");
        lastUpdateTick = nbt.getLong("tick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("green", getGreen());
        nbt.putInt("brown", getBrown());
        nbt.putLong("tick", lastUpdateTick);
        super.saveAdditional(nbt, provider);
    }

    public ItemInteractionResult use(ItemStack stack, Player player, boolean client) {
        assert level != null;
        final boolean rotten = isRotten();
        final BlockPos pos = getBlockPos();
        if (player.blockPosition().equals(pos)) return ItemInteractionResult.FAIL;
        final Compost compost = getCompost(stack);
        if (stack.isEmpty() && player.isShiftKeyDown()) {
            return handleExtractCompost(client, pos);
        }else if (rotten) {
            if (!client) player.displayClientMessage(Component.translatable("tfc.composter.rotten"), true);
            return finishUse(client);
        }

        return handleCompostAddition(stack, player, client, compost, pos);
    }

    private @NotNull ItemInteractionResult handleCompostAddition(ItemStack stack, Player player, boolean client, Compost compost, BlockPos pos) {
        assert level != null;
        if (canAddCompose(AdditionType.POISON, 0)) {
            return setPoisonState(stack, player, client, pos);
        }
        if (canAddCompose(AdditionType.GREEN, green)) {
            if (green == MAX_AMOUNT) {
                if (!client) getTooManyMessage(AdditionType.GREEN, player);
            } else {
                green = Math.min((green + compost.amount), MAX_AMOUNT);
                shrinkAndSoundEffect(stack, player, client, pos);
            }
            return finishUse(client);
        }
        if (brown <= MAX_AMOUNT && compost.type == AdditionType.BROWN) {
            if (brown == MAX_AMOUNT) {
                if (!client) getTooManyMessage(AdditionType.BROWN, player);
            } else {
                brown = Math.min(brown + compost.amount, MAX_AMOUNT);
                shrinkAndSoundEffect(stack, player, client, pos);
            }
            return finishUse(client);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    private void getTooManyMessage(AdditionType type, Player player) {
        if (type == AdditionType.GREEN) {
            player.displayClientMessage(Component.translatable("tfc.composter.too_many_greens"), true);
        }
        player.displayClientMessage(Component.translatable("tfc.composter.too_many_browns"), true);
    }

    private @NotNull ItemInteractionResult setPoisonState(ItemStack stack, Player player, boolean client, BlockPos pos) {
        assert level != null;
        if (!client) setState(TFCComposterBlock.CompostType.ROTTEN);
        if (!player.isCreative()) stack.shrink(1);
        inventory.setStackInSlot(0, new ItemStack(TFCItems.ROTTEN_COMPOST.get()));
        Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
        return finishUse(client);
    }

    private boolean canAddCompose(AdditionType type, int amount) {
        return (type == AdditionType.GREEN && amount < MAX_AMOUNT) ||
            (type == AdditionType.BROWN && amount < MAX_AMOUNT) ||
            (type == AdditionType.POISON && amount == 0);
    }

    private void shrinkAndSoundEffect(ItemStack stack, Player player, boolean client, BlockPos pos) {
        assert level != null;
        if (!client && !player.isCreative()) {
            stack.shrink(1);
            Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
            resetCounter();
        }
    }

    private @NotNull ItemInteractionResult handleExtractCompost(boolean client, BlockPos pos) {
        assert level != null;
        if (brown == MAX_AMOUNT && green == MAX_AMOUNT) {
            Helpers.spawnItem(level, pos.above(), inventory.extractItem(0, 1, false));
        }
        reset();
        Helpers.playSound(level, pos, SoundEvents.ROOTED_DIRT_BREAK);
        return finishUse(client);
    }

    public void resetCounter() {
        lastUpdateTick = Calendars.SERVER.getTicks();
        setChanged();
    }

    public long getTicksSinceUpdate() {
        assert level != null;
        return Calendars.get(level).getTicks() - lastUpdateTick;
    }

    @Override
    public void setAndUpdateSlots(int slot) {
        super.setAndUpdateSlots(slot);
        if (inventory.getStackInSlot(slot).isEmpty())
            reset();
    }

    public ItemInteractionResult finishUse(boolean client) {
        if (!client) {
            int stage = (green + brown) / 4;
            if (green + brown > 0) {
                stage = Math.max(stage, 1);
            }
            setState(stage);
            markForSync();
        }
        return ItemInteractionResult.sidedSuccess(client);
    }

    public int getGreen() {
        return green;
    }

    public int getBrown() {
        return brown;
    }

    public void reset() {
        green = brown = 0;
        resetCounter();
        setState(TFCComposterBlock.CompostType.NORMAL, 0);
    }

    public boolean isRotten() {
        assert level != null;
        return level.getBlockState(getBlockPos()).getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.ROTTEN;
    }

    public boolean isReady() {
        assert level != null;
        return level.getBlockState(getBlockPos()).getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.READY;
    }

    public void setState(TFCComposterBlock.CompostType type) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.TYPE, type));
    }

    public void setState(TFCComposterBlock.CompostType type, int stage) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.TYPE, type).setValue(TFCComposterBlock.STAGE, stage));
    }

    public void setState(int stage) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.STAGE, stage));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() == TFCItems.COMPOST.get() || stack.getItem() == TFCItems.ROTTEN_COMPOST.get();
    }

    @Override
    public int getSlotStackLimit(int slot) {
        return 1;
    }

    /**
     * Browns are checked first as some plants are in the plants tag, but we want them to be brown
     */
    public Compost getCompost(ItemStack stack) {
        final boolean rotten = FoodCapability.isRotten(stack);
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_POISONS)) {
            return new Compost(AdditionType.POISON, 0);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_LOW)) {
            return new Compost(AdditionType.BROWN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_MEDIUM)) {
            return new Compost(AdditionType.BROWN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_HIGH)) {
            return new Compost(AdditionType.BROWN, rotten ? 2 : 4);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_LOW)) {
            return new Compost(AdditionType.GREEN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_MEDIUM)) {
            return new Compost(AdditionType.GREEN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_HIGH)) {
            return new Compost(AdditionType.GREEN, rotten ? 2 : 4);
        }
        return new Compost(AdditionType.NONE, 0);
    }

    public record Compost(AdditionType type, int amount) {
    }

    public enum AdditionType {
        NONE,
        GREEN,
        BROWN,
        POISON
    }
}
