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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class ComposterBlockEntity extends TickCounterBlockEntity
{
    public static final int MAX_AMOUNT = 16;

    private int green, brown;

    public ComposterBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.COMPOSTER.get(), pos, state);
    }

    public ComposterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void randomTick()
    {
        assert level != null;
        if (green >= MAX_AMOUNT && brown >= MAX_AMOUNT & !isRotten())
        {
            if (getTicksSinceUpdate() > getReadyTicks())
            {
                setState(TFCComposterBlock.CompostType.READY);
                markForSync();
            }
        }
        if (isRotten())
        {
            Helpers.tickInfestation(level, getBlockPos(), 5, null);
        }
    }

    public long getReadyTicks()
    {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(getBlockPos());
        final float rainfall = Climate.getRainfall(level, cursor);
        long readyTicks = TFCConfig.SERVER.composterTicks.get();
        if (TFCConfig.SERVER.composterRainfallCheck.get())
        {
            if (rainfall < 150f) // inverted trapezoid wave
            {
                readyTicks *= (long) ((150f - rainfall) / 50f + 1f);
            }
            else if (rainfall > 350f)
            {
                readyTicks *= (long) ((rainfall - 350f) / 50f + 1f);
            }
        }
        cursor.move(0, 1, 0);
        if (Helpers.isBlock(level.getBlockState(cursor), TFCTags.Blocks.SNOW))
        {
            readyTicks *= 0.9f;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            cursor.setWithOffset(getBlockPos(), direction);
            if (level.getBlockState(cursor).getBlock() instanceof TFCComposterBlock)
            {
                readyTicks *= 1.05f;
            }
        }
        return readyTicks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        green = nbt.getInt("green");
        brown = nbt.getInt("brown");
        // todo: remove after some kind of grace period?
        if (!nbt.contains("legacyFixed", Tag.TAG_BYTE))
        {
            green = Mth.clamp(green * 4, 0, MAX_AMOUNT);
            brown = Mth.clamp(brown * 4, 0, MAX_AMOUNT);
        }
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("green", getGreen());
        nbt.putInt("brown", getBrown());
        nbt.putBoolean("legacyFixed", true);
        super.saveAdditional(nbt);
    }

    public InteractionResult use(ItemStack stack, Player player, boolean client)
    {
        assert level != null;
        final boolean rotten = isRotten();
        final BlockPos pos = getBlockPos();
        if (player.blockPosition().equals(pos)) return InteractionResult.FAIL;
        final Compost compost = getCompost(stack);
        if (stack.isEmpty() && player.isShiftKeyDown()) // extract compost
        {
            if (brown == MAX_AMOUNT && green == MAX_AMOUNT)
            {
                if (isReady())
                {
                    Helpers.spawnItem(level, pos.above(), new ItemStack(TFCItems.COMPOST.get()));
                }
                else if (rotten)
                {
                    Helpers.spawnItem(level, pos.above(), new ItemStack(TFCItems.ROTTEN_COMPOST.get()));
                }
            }
            reset();
            Helpers.playSound(level, pos, SoundEvents.ROOTED_DIRT_BREAK);
            return finishUse(client);
        }
        else if (rotten)
        {
            if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.rotten"), true);
            return finishUse(client);
        }
        else if (compost.type == AdditionType.POISON)
        {
            if (!client) setState(TFCComposterBlock.CompostType.ROTTEN);
            return finishUse(client);
        }
        else if (green <= MAX_AMOUNT && compost.type == AdditionType.GREEN)
        {
            if (green == MAX_AMOUNT)
            {
                if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.too_many_greens"), true);
            }
            else
            {
                green = Math.min(green + compost.amount, MAX_AMOUNT);
                if (!client)
                {
                    if (!player.isCreative()) stack.shrink(1);
                    Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
                    resetCounter();
                }
            }
            return finishUse(client);
        }
        else if (brown <= MAX_AMOUNT && compost.type == AdditionType.BROWN)
        {
            if (brown == MAX_AMOUNT)
            {
                if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.too_many_browns"), true);
            }
            else
            {
                brown = Math.min(brown + compost.amount, MAX_AMOUNT);
                if (!client)
                {
                    if (!player.isCreative()) stack.shrink(1);
                    Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
                    resetCounter();
                }
            }
            return finishUse(client);
        }
        return InteractionResult.PASS;
    }

    public InteractionResult finishUse(boolean client)
    {
        if (!client)
        {
            int stage = (green + brown) / 4;
            if (green + brown > 0)
            {
                stage = Math.max(stage, 1);
            }
            setState(stage);
            markForSync();
        }
        return InteractionResult.sidedSuccess(client);
    }

    public int getGreen()
    {
        return green;
    }

    public int getBrown()
    {
        return brown;
    }

    public void reset()
    {
        green = brown = 0;
        resetCounter();
        setState(TFCComposterBlock.CompostType.NORMAL, 0);
    }

    public boolean isRotten()
    {
        assert level != null;
        return level.getBlockState(getBlockPos()).getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.ROTTEN;
    }

    public boolean isReady()
    {
        assert level != null;
        return level.getBlockState(getBlockPos()).getValue(TFCComposterBlock.TYPE) == TFCComposterBlock.CompostType.READY;
    }

    public void setState(TFCComposterBlock.CompostType type)
    {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.TYPE, type));
    }

    public void setState(TFCComposterBlock.CompostType type, int stage)
    {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.TYPE, type).setValue(TFCComposterBlock.STAGE, stage));
    }

    public void setState(int stage)
    {
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(TFCComposterBlock.STAGE, stage));
    }

    /**
     * Browns are checked first as some plants are in the plants tag, but we want them to be brown
     */
    public Compost getCompost(ItemStack stack)
    {
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_POISONS))
        {
            return new Compost(AdditionType.POISON, 0);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_LOW))
        {
            return new Compost(AdditionType.BROWN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS))
        {
            return new Compost(AdditionType.BROWN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_HIGH))
        {
            return new Compost(AdditionType.BROWN, 4);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_LOW))
        {
            return new Compost(AdditionType.GREEN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS))
        {
            return new Compost(AdditionType.GREEN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_HIGH))
        {
            return new Compost(AdditionType.GREEN, 4);
        }
        return new Compost(AdditionType.NONE, 0);
    }

    public record Compost(AdditionType type, int amount) {}

    public enum AdditionType
    {
        NONE,
        GREEN,
        BROWN,
        POISON
    }
}
