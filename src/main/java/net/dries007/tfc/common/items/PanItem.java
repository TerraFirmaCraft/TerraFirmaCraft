package net.dries007.tfc.common.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.loot.TFCLoot;


public class PanItem extends Item
{
    public static final int USE_TIME = 120;

    @Nullable
    public static BlockState readState(ItemStack stack)
    {
        final CompoundTag tag = stack.getTagElement("state");
        if (tag != null)
        {
            return NbtUtils.readBlockState(tag);
        }
        return null;
    }

    public PanItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return USE_TIME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int countLeft)
    {
        if (countLeft % 16 == 0 && !level.isClientSide)
        {
            entity.playSound(SoundEvents.SAND_BREAK, 1F, 1F);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (entity instanceof Player player && !level.isClientSide)
        {
            final BlockState state = readState(stack);
            if (state != null)
            {
                Helpers.dropWithContext(level, state, entity.blockPosition(), ctx -> ctx.withParameter(TFCLoot.PANNED, true));
                player.awardStat(Stats.ITEM_USED.get(this));
                return new ItemStack(TFCItems.EMPTY_PAN.get()); // MC calls setItemInHand to place this in the hand
            }
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag)
    {
        final BlockState state = readState(stack);
        if (state != null)
        {
            text.add(new TranslatableComponent("tfc.tooltip.pan.contents").append(state.getBlock().getName()));
        }
    }
}
