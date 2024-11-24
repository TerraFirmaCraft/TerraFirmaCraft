/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
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

public class ComposterBlockEntity extends InventoryBlockEntity<ItemStackHandler> {
    public static final byte MAX_AMOUNT = 16;

    protected long lastUpdateTick = Integer.MIN_VALUE;
    private byte green, brown;

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
        boolean rotten = isRotten();
        if (rotten) {
            Helpers.tickInfestation(level, getBlockPos(), 5, null);
            return;
        }

        if (green >= MAX_AMOUNT && brown >= MAX_AMOUNT) {
            if (getTicksSinceUpdate() > getReadyTicks()) {
                inventory.setStackInSlot(0, new ItemStack(TFCItems.COMPOST.get()));
                setState(TFCComposterBlock.CompostType.READY);
                markForSync();
            }
        }
    }

    public long getReadyTicks() {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final float rainfall = Climate.getRainfall(level, cursor);
        long readyTicks = TFCConfig.SERVER.composterTicks.get();

        // inverted trapezoid wave
        if (rainfall < 150f) {
            readyTicks *= (long) ((150f - rainfall) / 50f + 1f);
        } else if (rainfall > 350f) {
            readyTicks *= (long) ((rainfall - 350f) / 50f + 1f);
        }

        cursor.set(getBlockPos()).move(Direction.UP);
        if (Helpers.isBlock(level.getBlockState(cursor), BlockTags.SNOW)) {
            readyTicks = (long) (readyTicks * 0.9f);
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            cursor.setWithOffset(getBlockPos(), direction);
            if (level.getBlockState(cursor).getBlock() instanceof TFCComposterBlock) {
                readyTicks = (long) (readyTicks * 1.05f);
            }
        }
        return readyTicks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        green = nbt.getByte("green");
        brown = nbt.getByte("brown");
        lastUpdateTick = nbt.getLong("tick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putByte("green", getGreen());
        nbt.putByte("brown", getBrown());
        nbt.putLong("tick", lastUpdateTick);
        super.saveAdditional(nbt, provider);
    }

    public ItemInteractionResult use(ItemStack stack, Player player, boolean client) {
        assert level != null;
        final boolean rotten = isRotten();
        final BlockPos pos = getBlockPos();
        final Compost compost = getCompost(stack);

        if (player.blockPosition().equals(pos)) return ItemInteractionResult.FAIL;

        if (rotten) {
            if (!client) player.displayClientMessage(Component.translatable("tfc.composter.rotten"), true);
            return finishUse(client);
        }

        // extract compost
        if (stack.isEmpty() && player.isShiftKeyDown()) {
            if (brown == MAX_AMOUNT && green == MAX_AMOUNT) {
                Helpers.spawnItem(level, pos.above(), inventory.extractItem(0, 1, false));
            }
            reset();
            Helpers.playSound(level, pos, SoundEvents.ROOTED_DIRT_BREAK);
            return finishUse(client);
        }

        return switch (compost.type) {
            case POISON -> {
                handlePoisonCompost(client, player, stack, pos);
                yield finishUse(client);
            }
            case BROWN -> {
                handleCompostAddition(player, stack, client, "tfc.composter.too_many_browns",
                    () -> brown += compost.amount, pos, brown);
                yield finishUse(client);
            }
            case GREEN -> {
                handleCompostAddition(player, stack, client, "tfc.composter.too_many_greens",
                    () -> green += compost.amount, pos, green);
                yield finishUse(client);
            }
            default -> finishUse(client);
        };
    }

    private void handleCompostAddition(Player player, ItemStack stack, boolean client, String tooManyMessage,
                                       Runnable incrementAction, BlockPos pos, byte currentAmount) {
        assert level != null;
        if (currentAmount == MAX_AMOUNT) {
            if (!client) player.displayClientMessage(Component.translatable(tooManyMessage), true);
        } else {
            if (!client) {
                if (!player.isCreative()) stack.shrink(1);
                incrementAction.run();
                Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
                resetCounter();
            }
        }
    }

    private void handlePoisonCompost(boolean client, Player player, ItemStack stack, BlockPos pos) {
        assert level != null;
        if (!client) {
            setState(TFCComposterBlock.CompostType.ROTTEN);
            inventory.setStackInSlot(0, new ItemStack(TFCItems.ROTTEN_COMPOST.get()));
        }
        if (!player.isCreative()) stack.shrink(1);
        Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
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

    public byte getGreen() {
        return green;
    }

    public byte getBrown() {
        return brown;
    }

    public void reset() {
        green = brown = 0;
        resetCounter();
        setState(TFCComposterBlock.CompostType.NORMAL, 0);
    }
    //Caching the block state
    private BlockState getCurrentState() {
        assert level != null;
        return level.getBlockState(getBlockPos());
    }

    public boolean isRotten() {
        assert level != null;
        return getCurrentState().getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.ROTTEN;
    }

    public boolean isReady() {
        assert level != null;
        return getCurrentState().getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.READY;
    }

    public void setState(TFCComposterBlock.CompostType type) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(),getCurrentState().setValue(TFCComposterBlock.TYPE, type));
    }

    public void setState(TFCComposterBlock.CompostType type, int stage) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), getCurrentState().setValue(TFCComposterBlock.TYPE, type).setValue(TFCComposterBlock.STAGE, stage));
    }

    public void setState(int stage) {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(),getCurrentState().setValue(TFCComposterBlock.STAGE, stage));
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
            return new Compost(AdditionType.POISON, (byte) 0);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_LOW)) {
            return new Compost(AdditionType.BROWN, (byte) 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_MEDIUM)) {
            return new Compost(AdditionType.BROWN, (byte) 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_HIGH)) {
            return new Compost(AdditionType.BROWN, (byte) (rotten ? 2 : 4));
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_LOW)) {
            return new Compost(AdditionType.GREEN, (byte) 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_MEDIUM)) {
            return new Compost(AdditionType.GREEN, (byte) 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_HIGH)) {
            return new Compost(AdditionType.GREEN, (byte) (rotten ? 2 : 4));
        }
        return new Compost(AdditionType.NONE, (byte) 0);
    }

    public record Compost(AdditionType type, byte amount) {
    }

    public enum AdditionType {
        NONE,
        GREEN,
        BROWN,
        POISON
    }
}