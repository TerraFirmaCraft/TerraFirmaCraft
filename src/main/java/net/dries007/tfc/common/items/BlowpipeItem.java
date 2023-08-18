package net.dries007.tfc.common.items;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;

public class BlowpipeItem extends Item
{
    public BlowpipeItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 80;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPYGLASS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksLeft)
    {
        super.onUseTick(level, entity, stack, ticksLeft);
        if (ticksLeft % 20 == 0 && entity instanceof Player player)
        {
            level.playSound(null, entity.blockPosition(), TFCSounds.BELLOWS_BLOW.get(), SoundSource.PLAYERS, 1f, 0.8f + (float) (player.getLookAngle().y / 2f));
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int ticksLeft)
    {
        if (ticksLeft > 1)
        {
            return;
        }
        if (entity instanceof Player player)
        {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
}
