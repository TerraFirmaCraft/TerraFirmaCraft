/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class PhysicalDamageTypeData implements PhysicalDamageType.Multiplier
{
    private final float piercing, slashing, crushing;

    public PhysicalDamageTypeData(ResourceLocation id, JsonObject json)
    {
        this.piercing = JsonHelpers.getAsFloat(json, "piercing", 0);
        this.slashing = JsonHelpers.getAsFloat(json, "slashing", 0);
        this.crushing = JsonHelpers.getAsFloat(json, "crushing", 0);
    }

    public PhysicalDamageTypeData(ResourceLocation id, FriendlyByteBuf buffer)
    {
        piercing = buffer.readFloat();
        slashing = buffer.readFloat();
        crushing = buffer.readFloat();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(piercing);
        buffer.writeFloat(slashing);
        buffer.writeFloat(crushing);
    }

    @Override
    public float crushing()
    {
        return crushing;
    }

    @Override
    public float piercing()
    {
        return piercing;
    }

    @Override
    public float slashing()
    {
        return slashing;
    }
}
