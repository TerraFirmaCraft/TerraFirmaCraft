/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.util.tooltip.Tooltips;

public record ForgingBonusComponent(
    ForgingBonus type,
    Optional<String> author
)
{
    public static final Codec<ForgingBonusComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ForgingBonus.CODEC.fieldOf("type").forGetter(c -> c.type),
        Codec.sizeLimitedString(32).optionalFieldOf("author").forGetter(c -> c.author)
    ).apply(i, ForgingBonusComponent::new));

    public static final StreamCodec<ByteBuf, ForgingBonusComponent> STREAM_CODEC = StreamCodec.composite(
        ForgingBonus.STREAM_CODEC, c -> c.type,
        ByteBufCodecs.optional(ByteBufCodecs.stringUtf8(32)), c -> c.author,
        ForgingBonusComponent::new
    );

    private static final ForgingBonusComponent EMPTY = new ForgingBonusComponent(ForgingBonus.NONE, Optional.empty());

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final @Nullable ForgingBonusComponent component = stack.get(TFCComponents.FORGING_BONUS);
        if (component != null && component.type != ForgingBonus.NONE)
        {
            final MutableComponent name = component.type.getDisplayName();
            tooltips.add(component.author
                .map(author -> Tooltips.author(name, author))
                .orElse(name));
        }
    }

    /**
     * Mimics unbreaking-like effects for items with a forging bonus. This hooks into the method enchantments use to apply durability
     * based effects, in {@link EnchantmentHelper#processDurabilityChange}, and modifies the amount of damage taken. We base the system
     * off of how enchantments work - each durability point of damage has a chance to not apply (binomial distribution)
     */
    public static int applyLikeUnbreaking(ItemStack stack, RandomSource random, int originalDamage)
    {
        final ForgingBonus bonus = get(stack);

        int damage = originalDamage;
        if (bonus != ForgingBonus.NONE)
        {
            for (int i = 0; i < damage; i++)
            {
                if (random.nextFloat() < bonus.durability())
                {
                    damage--;
                }
            }
        }
        return damage;
    }

    public static ItemStack copy(ItemStack from, ItemStack to)
    {
        final ForgingBonusComponent component = from.get(TFCComponents.FORGING_BONUS);
        if (component != null)
        {
            to.set(TFCComponents.FORGING_BONUS, component);
        }
        return to;
    }

    /**
     * Get the forging bonus currently attached to an item stack. If the stack has no bonus, {@link ForgingBonus#NONE} will be returned.
     */
    public static ForgingBonus get(ItemStack stack)
    {
        return stack.getOrDefault(TFCComponents.FORGING_BONUS, EMPTY).type;
    }

    /**
     * Set the forging bonus on an item stack
     */
    public static void set(ItemStack stack, ForgingBonus bonus)
    {
        set(stack, bonus, null);
    }

    public static void set(ItemStack stack, ForgingBonus bonus, @Nullable Player player)
    {
        if (bonus != ForgingBonus.NONE)
        {
            stack.set(TFCComponents.FORGING_BONUS, new ForgingBonusComponent(
                bonus,
                player == null
                    ? Optional.empty()
                    : Optional.of(player.getName().getString())
            ));
        }
    }
}
