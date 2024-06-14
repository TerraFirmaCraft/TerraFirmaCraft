/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;

public final class KnappingType
{
    public static final DataManager<KnappingType> MANAGER = new DataManager<>(Helpers.identifier("knapping_types"), "knapping_types", KnappingType::new, KnappingType::new, KnappingType::encode, Packet::new);

    @Nullable
    public static KnappingType get(Player player)
    {
        final ItemStack stack = player.getMainHandItem();
        for (KnappingType type : MANAGER.getValues())
        {
            if (type.inputItem.test(stack))
            {
                return type;
            }
        }
        return null;
    }

    private final ResourceLocation id;

    private final ItemStackIngredient inputItem;
    private final int amountToConsume;
    private final SoundEvent clickSound;
    private final boolean consumeAfterComplete;
    private final boolean useDisabledTexture;
    private final boolean spawnsParticles;

    private final ItemStack jeiIconItem;

    KnappingType(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.inputItem = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "input"));

        this.amountToConsume = json.has("amount_to_consume") ? JsonHelpers.getAsInt(json, "amount_to_consume") : inputItem.count();
        this.clickSound = JsonHelpers.getRegistryEntry(json, "click_sound", BuiltInRegistries.SOUND_EVENT);
        this.consumeAfterComplete = JsonHelpers.getAsBoolean(json, "consume_after_complete");
        this.useDisabledTexture = JsonHelpers.getAsBoolean(json, "use_disabled_texture");
        this.spawnsParticles = JsonHelpers.getAsBoolean(json, "spawns_particles");

        this.jeiIconItem = JsonHelpers.getItemStack(json, "jei_icon_item");
    }

    KnappingType(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;

        this.inputItem = ItemStackIngredient.fromNetwork(buffer);

        this.amountToConsume = buffer.readVarInt();
        this.clickSound = BuiltInRegistries.SOUND_EVENT.byIdOrThrow(buffer.readVarInt());
        this.consumeAfterComplete = buffer.readBoolean();
        this.useDisabledTexture = buffer.readBoolean();
        this.spawnsParticles = buffer.readBoolean();

        this.jeiIconItem = buffer.readItem();
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public ItemStackIngredient inputItem()
    {
        return inputItem;
    }

    public boolean consumeAfterComplete()
    {
        return consumeAfterComplete;
    }

    public int amountToConsume()
    {
        return amountToConsume;
    }

    public boolean spawnsParticles()
    {
        return spawnsParticles;
    }

    public boolean usesDisabledTexture()
    {
        return useDisabledTexture;
    }

    public SoundEvent clickSound()
    {
        return clickSound;
    }

    public ItemStack jeiIcon()
    {
        return jeiIconItem;
    }

    void encode(FriendlyByteBuf buffer)
    {
        inputItem.toNetwork(buffer);

        buffer.writeVarInt(amountToConsume);
        buffer.writeVarInt(BuiltInRegistries.SOUND_EVENT.getId(clickSound));
        buffer.writeBoolean(consumeAfterComplete);
        buffer.writeBoolean(useDisabledTexture);
        buffer.writeBoolean(spawnsParticles);

        buffer.writeItem(jeiIconItem);
    }

    public static final class Packet extends DataManagerSyncPacket<KnappingType> {}
}
