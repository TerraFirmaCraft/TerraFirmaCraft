/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class ComposterBlockEntity extends TickCounterBlockEntity implements Infestable
{
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
        if (green >= 4 && brown >= 4 & !isRotten())
        {
            assert level != null;
            if (getTicksSinceUpdate() > getReadyTicks())
            {
                setState(TFCComposterBlock.CompostType.READY);
                markForSync();
            }
        }
    }

    public long getReadyTicks()
    {
        assert level != null;
        final float rainfall = Climate.getRainfall(level, getBlockPos());
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
        return readyTicks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        green = nbt.getInt("green");
        brown = nbt.getInt("brown");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("green", getGreen());
        nbt.putInt("brown", getBrown());
        super.saveAdditional(nbt);
    }

    public InteractionResult use(ItemStack stack, Player player, boolean client)
    {
        assert level != null;
        final boolean rotten = isRotten();
        final BlockPos pos = getBlockPos();
        if (player.blockPosition().equals(pos)) return InteractionResult.FAIL;
        if (stack.isEmpty() && player.isShiftKeyDown()) // extract compost
        {
            if (brown == 4 && green == 4 && isReady()) Helpers.spawnItem(level, pos.above(), new ItemStack(rotten ? TFCItems.ROTTEN_COMPOST.get() : TFCItems.COMPOST.get()));
            reset();
            Helpers.playSound(level, pos, SoundEvents.ROOTED_DIRT_BREAK);
            return finishUse(client);
        }
        else if (rotten)
        {
            if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.rotten"), true);
            return finishUse(client);
        }
        else if (Helpers.isItem(stack, TFCTags.Items.COMPOST_POISONS))
        {
            if (!client) setState(TFCComposterBlock.CompostType.ROTTEN);
            return finishUse(client);
        }
        else if (green <= 4 && Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS))
        {
            if (green == 4)
            {
                if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.too_many_greens"), true);
            }
            else
            {
                green += 1;
                if (!client)
                {
                    if (!player.isCreative()) stack.shrink(1);
                    Helpers.playSound(level, pos, SoundEvents.HOE_TILL);
                    resetCounter();
                }
            }
            return finishUse(client);
        }
        else if (brown <= 4 && Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS))
        {
            if (brown == 4)
            {
                if (!client) player.displayClientMessage(Helpers.translatable("tfc.composter.too_many_browns"), true);
            }
            else
            {
                brown += 1;
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
            setState(green + brown);
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

    @Override
    public int getInfestation()
    {
        return isRotten() ? 5 : 0;
    }
}
