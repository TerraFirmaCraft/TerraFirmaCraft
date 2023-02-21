/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class Drinkable extends FluidDefinition
{
    public static final DataManager<Drinkable> MANAGER = new DataManager<>(Helpers.identifier("drinkables"), "drinkable", Drinkable::new, Drinkable::new, Drinkable::encode, Packet::new);
    public static final IndirectHashCollection<Fluid, Drinkable> CACHE = IndirectHashCollection.create(Drinkable::getFluids, MANAGER::getValues);

    /** Amount of mB drank when drinking by hand on a source block */
    private static final int HAND_DRINK_MB = 25;

    @Nullable
    public static Drinkable get(Fluid fluid)
    {
        for (Drinkable drinkable : CACHE.getAll(fluid))
        {
            if (drinkable.matches(fluid))
            {
                return drinkable;
            }
        }
        return null;
    }

    @Deprecated(forRemoval = true)
    public static void drinkFromPotion(ItemStack stack, Level level, LivingEntity entity)
    {
        // todo: this method is unused as vanilla glass bottles need a lot of work to make them usable fluid handlers
        // We just disable them as they break progression and are objectively better than jugs.
        if (entity instanceof Player player && !level.isClientSide)
        {
            // Unless there is more need, we only handle water potions.
            // There is no nice API way to translate Potion -> Fluid, so any further functionality would require 1) addon for TFC, 2) mod to add potion fluids, 3) datapack to add the TFC drinkables.
            final Potion potion = PotionUtils.getPotion(stack);
            if (potion == Potions.WATER)
            {
                final Drinkable drink = Drinkable.get(Fluids.WATER);
                if (drink != null)
                {
                    drink.onDrink(player, 100);
                }
            }
        }
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
            final float thirst = player.getFoodData() instanceof TFCFoodData data ? data.getThirst() : TFCFoodData.MAX_THIRST;
            final LazyOptional<PlayerData> playerData = player.getCapability(PlayerDataCapability.CAPABILITY);
            if (playerData.map(p -> p.getLastDrinkTick() + 10 < Calendars.get(level).getTicks()).orElse(false))
            {
                final Drinkable drinkable = get(fluid);
                if (drinkable != null && (thirst < TFCFoodData.MAX_THIRST || drinkable.getThirst() == 0))
                {
                    if (!level.isClientSide && doDrink)
                    {
                        doDrink(level, player, state, pos, playerData, drinkable);
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

    private static void doDrink(Level level, Player player, BlockState state, BlockPos pos, LazyOptional<PlayerData> playerData, Drinkable drinkable)
    {
        assert !level.isClientSide;

        playerData.ifPresent(p -> p.setLastDrinkTick(Calendars.SERVER.getTicks()));
        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);

        drinkable.onDrink(player, HAND_DRINK_MB);

        if (drinkable.getConsumeChance() > 0 && drinkable.getConsumeChance() > level.getRandom().nextFloat())
        {
            final BlockState emptyState = FluidHelpers.isAirOrEmptyFluid(state) ? Blocks.AIR.defaultBlockState() : FluidHelpers.fillWithFluid(state, Fluids.EMPTY);
            if (emptyState != null)
            {
                level.setBlock(pos, emptyState, 3);
            }
        }
    }

    private final float consumeChance;
    private final int thirst;
    private final int intoxication;
    private final List<Effect> effects;
    @Nullable private final FoodData food;

    private Drinkable(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        this.consumeChance = JsonHelpers.getAsFloat(json, "consume_chance", 0);
        this.thirst = JsonHelpers.getAsInt(json, "thirst", 0);
        this.intoxication = JsonHelpers.getAsInt(json, "intoxication", 0);
        this.food = json.has("food") ? FoodData.read(json.getAsJsonObject("food")) : null;

        final ImmutableList.Builder<Effect> builder = new ImmutableList.Builder<>();
        if (json.has("effects"))
        {
            JsonArray array = JsonHelpers.getAsJsonArray(json, "effects");
            for (JsonElement e : array)
            {
                final JsonObject effectJson = JsonHelpers.convertToJsonObject(e, "effect");
                final MobEffect type = JsonHelpers.getRegistryEntry(effectJson, "type", ForgeRegistries.MOB_EFFECTS);
                final int duration = JsonHelpers.getAsInt(effectJson, "duration", 20);
                final int amplifier = JsonHelpers.getAsInt(effectJson, "amplifier", 0);
                final float chance = (float) JsonHelpers.getAsDouble(effectJson, "chance", 1);

                builder.add(new Effect(type, duration, amplifier, chance));
            }
        }
        this.effects = builder.build();
    }

    private Drinkable(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, FluidIngredient.fromNetwork(buffer));

        this.consumeChance = buffer.readFloat();
        this.thirst = buffer.readVarInt();
        this.intoxication = buffer.readVarInt();
        this.food = Helpers.decodeNullable(buffer, FoodData::decode);

        this.effects = Helpers.decodeAll(buffer, new ArrayList<>(), Effect::fromNetwork);
    }

    /**
     * @param player The player doing the drinking
     * @param mB     The amount of fluid that is being drank, in mB. This will scale certain effects proportional to the volume. 25mB is a reference for amount drank when right-clicking a fluid source with an open hand, which is also the amount that the drinkable JSON is defined as.
     */
    public void onDrink(Player player, int mB)
    {
        assert !player.level.isClientSide;

        final float multiplier = mB / 25f;
        final Random random = player.getRandom();

        if (player.getFoodData() instanceof TFCFoodData foodData)
        {
            foodData.addThirst(thirst * multiplier);
        }

        if (intoxication > 0)
        {
            player.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(p -> p.addIntoxicatedTicks((long) (intoxication * multiplier)));
        }

        if (food != null && player.getFoodData() instanceof TFCFoodData data)
        {
            data.eat(food);
        }

        for (Drinkable.Effect effect : effects)
        {
            // Multiplier affects the chance that a specific effect will be applied, but does not affect the effect itself.
            // This is consistent with the probability of drinking N times P(at least one effect) = 1 - (1 - P(effect))^N
            if (1 - Math.pow(1 - effect.chance(), multiplier) > random.nextFloat())
            {
                player.addEffect(new MobEffectInstance(effect.type(), effect.duration(), effect.amplifier(), false, false, true));
            }
        }
    }

    public float getConsumeChance()
    {
        return consumeChance;
    }

    public int getThirst()
    {
        return thirst;
    }

    public int getIntoxication()
    {
        return intoxication;
    }

    @Nullable
    public FoodData getFoodStats()
    {
        return food;
    }

    public Collection<Effect> getEffects()
    {
        return effects;
    }

    private void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);

        buffer.writeFloat(consumeChance);
        buffer.writeVarInt(thirst);
        buffer.writeVarInt(intoxication);
        Helpers.encodeNullable(food, buffer, FoodData::encode);

        Helpers.encodeAll(buffer, effects, Effect::toNetwork);
    }

    public record Effect(MobEffect type, int duration, int amplifier, float chance)
    {
        public static Effect fromNetwork(FriendlyByteBuf buffer)
        {
            final MobEffect type = buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
            final int duration = buffer.readVarInt();
            final int amplifier = buffer.readVarInt();
            final float chance = buffer.readFloat();
            return new Effect(type, duration, amplifier, chance);
        }

        public void toNetwork(FriendlyByteBuf buffer)
        {
            buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, type);
            buffer.writeVarInt(duration);
            buffer.writeVarInt(amplifier);
            buffer.writeFloat(chance);
        }
    }

    public static class Packet extends DataManagerSyncPacket<Drinkable> {}
}
