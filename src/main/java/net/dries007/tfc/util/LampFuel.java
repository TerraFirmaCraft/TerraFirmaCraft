/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public final class LampFuel
{
    public static final DataManager<LampFuel> MANAGER = new DataManager<>(Helpers.identifier("lamp_fuels"), "lamp_fuel", LampFuel::new, LampFuel::new, LampFuel::encode, Packet::new);
    public static final IndirectHashCollection<Fluid, LampFuel> CACHE = IndirectHashCollection.create(s -> s.getFluidIngredient().getMatchingFluids(), MANAGER::getValues);

    @Nullable
    public static LampFuel get(Fluid fluid, BlockState state)
    {
        for (LampFuel fuel : CACHE.getAll(fluid))
        {
            if (fuel.matches(fluid, state))
            {
                return fuel;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final FluidIngredient fluidIngredient;
    private final BlockIngredient validLamps;
    private final int burnRate;

    public LampFuel(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.fluidIngredient = FluidIngredient.fromJson(JsonHelpers.get(json, "fluid"));
        this.validLamps = BlockIngredients.fromJson(JsonHelpers.get(json, "valid_lamps"));
        this.burnRate = JsonHelpers.getAsInt(json, "burn_rate");
    }

    public LampFuel(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;
        this.fluidIngredient = FluidIngredient.fromNetwork(buffer);
        this.validLamps = BlockIngredients.fromNetwork(buffer);
        this.burnRate = buffer.readVarInt();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        fluidIngredient.toNetwork(buffer);
        validLamps.toNetwork(buffer);
        buffer.writeVarInt(burnRate);
    }

    private boolean matches(Fluid fluid, BlockState state)
    {
        return getFluidIngredient().test(fluid) && getValidLamps().test(state);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    /**
     * @return The burn rate of this lamp, in ticks per mB.
     */
    public int getBurnRate()
    {
        return burnRate;
    }

    public FluidIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    public BlockIngredient getValidLamps()
    {
        return validLamps;
    }

    public static class Packet extends DataManagerSyncPacket<LampFuel> { }
}
