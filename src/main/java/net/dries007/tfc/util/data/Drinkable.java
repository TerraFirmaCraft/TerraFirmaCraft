/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Drinkable(
    FluidIngredient ingredient,
    float consumeChance,
    boolean mayDrinkWhenFull,
    FoodData food,
    List<Effect> effects
) {
    public static final Codec<Drinkable> CODEC = RecordCodecBuilder.create(i -> i.group(
        FluidIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codec.FLOAT.optionalFieldOf("consume_chance", 0f).forGetter(c -> c.consumeChance),
        Codec.BOOL.optionalFieldOf("may_drink_when_full", false).forGetter(c -> c.mayDrinkWhenFull),
        FoodData.MAP_CODEC.forGetter(c -> c.food),
        RecordCodecBuilder.<Effect>create(j -> j.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(c -> c.type),
            Codec.INT.fieldOf("duration").forGetter(c -> c.duration),
            Codec.INT.fieldOf("amplifier").forGetter(c -> c.amplifier),
            Codec.FLOAT.fieldOf("chance").forGetter(c -> c.chance)
        ).apply(j, Effect::new)).listOf().optionalFieldOf("effects", List.of()).forGetter(c -> c.effects)
    ).apply(i, Drinkable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Drinkable> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, c -> c.ingredient,
        ByteBufCodecs.FLOAT, c -> c.consumeChance,
        ByteBufCodecs.BOOL, c -> c.mayDrinkWhenFull,
        FoodData.STREAM_CODEC, c -> c.food,
        StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), c -> c.type,
            ByteBufCodecs.VAR_INT, c -> c.duration,
            ByteBufCodecs.VAR_INT, c -> c.amplifier,
            ByteBufCodecs.FLOAT, c -> c.chance,
            Effect::new
        ).apply(ByteBufCodecs.list()), c -> c.effects,
        Drinkable::new
    );

    public static final DataManager<Drinkable> MANAGER = new DataManager<>(Helpers.identifier("drinkable"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Fluid, Drinkable> CACHE = IndirectHashCollection.create(c -> RecipeHelpers.fluidKeys(c.ingredient), MANAGER::getValues);

    /** Amount of mB drank when drinking by hand on a source block */
    private static final int HAND_DRINK_MB = 25;

    @Nullable
    public static Drinkable get(Fluid fluid)
    {
        for (Drinkable drinkable : CACHE.getAll(fluid))
        {
            if (drinkable.ingredient.test(new FluidStack(fluid, 1)))
            {
                return drinkable;
            }
        }
        return null;
    }

    /**
     * Attempt to drink from a fluid source block.
     * Called by TFC through both right click block and right click empty events.
     */
    public static InteractionResult attemptDrink(Level level, Player player, boolean doDrink)
    {
        final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK)
        {
            final BlockPos pos = hit.getBlockPos();
            final BlockState state = level.getBlockState(pos);
            final Fluid fluid = state.getFluidState().getType();
            final IPlayerInfo info = IPlayerInfo.get(player);
            if (info.canDrink())
            {
                final Drinkable drinkable = get(fluid);
                if (drinkable != null && (info.getThirst() < PlayerInfo.MAX_THIRST || drinkable.food.water() == 0 || drinkable.mayDrinkWhenFull))
                {
                    if (!level.isClientSide && doDrink)
                    {
                        doDrink(level, player, state, pos, info, drinkable);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private static void doDrink(Level level, Player player, BlockState state, BlockPos pos, IPlayerInfo info, Drinkable drinkable)
    {
        assert !level.isClientSide;

        info.onDrink();
        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);

        drinkable.onDrink(player, HAND_DRINK_MB);

        if (drinkable.consumeChance > 0 && drinkable.consumeChance > level.getRandom().nextFloat())
        {
            final BlockState emptyState = FluidHelpers.isAirOrEmptyFluid(state) ? Blocks.AIR.defaultBlockState() : FluidHelpers.fillWithFluid(state, Fluids.EMPTY);
            if (emptyState != null)
            {
                level.setBlock(pos, emptyState, 3);
            }
        }
    }

    /**
     * Applies effect from drinking a given amoung of {@code mB} of the fluid represented by this drinkable.
     * @param player The player doing the drinking
     * @param mB     The amount of fluid that is being drank, in mB. This will scale certain effects proportional to the volume. 25mB is a reference for amount drank when
     *               right-clicking a fluid source with an open hand, which is also the amount that the drinkable JSON is defined as.
     */
    public void onDrink(Player player, int mB)
    {
        assert !player.level().isClientSide;

        final float multiplier = mB / (float) HAND_DRINK_MB;
        final RandomSource random = player.getRandom();
        final IPlayerInfo info = IPlayerInfo.get(player);

        info.eat(food.mul(multiplier));

        for (Drinkable.Effect effect : effects)
        {
            // Multiplier affects the chance that a specific effect will be applied, but does not affect the effect itself.
            // This is consistent with the probability of drinking N times P(at least one effect) = 1 - (1 - P(effect))^N
            if (1 - Math.pow(1 - effect.chance(), multiplier) > random.nextFloat())
            {
                player.addEffect(new MobEffectInstance(effect.type(), effect.duration(), effect.amplifier(), false, false, true));
            }
        }

        player.setSprinting(false);
    }

    public record Effect(Holder<MobEffect> type, int duration, int amplifier, float chance) {}
}
