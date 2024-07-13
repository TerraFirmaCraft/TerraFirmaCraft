/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.render.blockentity.JavelinItemRenderer;
import net.dries007.tfc.common.entities.misc.ThrownJavelin;
import net.dries007.tfc.util.Helpers;

/**
 * Modern implementation is based on {@link net.minecraft.world.item.TridentItem} rather than TFC Classic
 */
public class JavelinItem extends SwordItem
{
    private final ResourceLocation textureLocation;
    private final float thrownDamage;

    public JavelinItem(Tier tier, float attackDamage, float thrownDamage, float attackSpeed, Properties properties, String name)
    {
        this(tier, (int) attackDamage, thrownDamage, attackSpeed, properties, Helpers.identifier("textures/entity/projectiles/" + name + "_javelin.png"));
    }

    public JavelinItem(Tier tier, float attackDamage, float thrownDamage, float attackSpeed, Properties properties, ResourceLocation name)
    {
        super(tier, (int) attackDamage, attackSpeed, properties);
        this.thrownDamage = thrownDamage;
        this.textureLocation = name;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
    {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int ticksLeft)
    {
        if (entity instanceof Player player)
        {
            int i = this.getUseDuration(stack) - ticksLeft;
            if (i >= 10)
            {
                if (!level.isClientSide)
                {
                    Helpers.damageItem(stack, player, entity.getUsedItemHand());

                    ThrownJavelin javelin = new ThrownJavelin(level, player, stack);
                    javelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                    if (player.getAbilities().instabuild)
                    {
                        javelin.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(javelin);
                    level.playSound(null, javelin, TFCSounds.JAVELIN_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild)
                    {
                        player.getInventory().removeItem(stack);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (held.getDamageValue() >= held.getMaxDamage() - 1)
        {
            return InteractionResultHolder.fail(held);
        }
        else
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced)
    {
        tooltip.add(Component.translatable("tfc.tooltip.javelin.thrown_damage", String.format("%.0f", getThrownDamage())).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction)
    {
        return super.canPerformAction(stack, toolAction) && toolAction != ItemAbilities.SWORD_SWEEP;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions() {
            private final Supplier<JavelinItemRenderer> renderer = Suppliers.memoize(() -> new JavelinItemRenderer(getTextureLocation()));
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return renderer.get();
            }
        });
    }

    public ResourceLocation getTextureLocation()
    {
        return textureLocation;
    }

    public float getThrownDamage()
    {
        return thrownDamage;
    }
}