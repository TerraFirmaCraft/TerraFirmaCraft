package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;

public final class KnappingType
{
    public static final DataManager<KnappingType> MANAGER = new DataManager<>(Helpers.identifier("knapping_types"), "knapping_types", KnappingType::new, KnappingType::new, KnappingType::encode, Packet::new);

    public static final ResourceLocation ROCK = Helpers.identifier("rock");
    public static final ResourceLocation CLAY = Helpers.identifier("clay");
    public static final ResourceLocation FIRE_CLAY = Helpers.identifier("fire_clay");
    public static final ResourceLocation LEATHER = Helpers.identifier("leather");

    @Nullable
    public static KnappingType get(Player player)
    {
        final ItemStack mainHandStack = player.getMainHandItem();
        final ItemStack offHandStack = player.getOffhandItem();

        final Inventory inventory = player.getInventory();

        for (KnappingType type : MANAGER.getValues())
        {
            if (type.inputItem.test(mainHandStack) && (type.offHandInputItem == null || type.offHandInputItem.test(offHandStack)))
            {
                // Both hands match the input - these must be disjoint, so we don't check anything else after this
                // Check the inventory condition, and if we fail, display a client message
                if (type.inventoryInputItem == null || containsInInventory(inventory, type.inventoryInputItem))
                {
                    return type;
                }
                else if (type.missingInventoryItemTranslationKey != null)
                {
                    player.displayClientMessage(Helpers.translatable(type.missingInventoryItemTranslationKey), true);
                }

                // Since both hands match, don't bother checking any others
                break;
            }
        }
        return null;
    }

    private static boolean containsInInventory(Inventory inventory, ItemStackIngredient ingredient)
    {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++)
        {
            if (ingredient.test(inventory.getItem(slot)))
            {
                return true;
            }
        }
        return false;
    }

    private final ResourceLocation id;

    private final ItemStackIngredient inputItem;
    private final @Nullable ItemStackIngredient offHandInputItem;
    private final @Nullable ItemStackIngredient inventoryInputItem;

    private final @Nullable String missingInventoryItemTranslationKey;

    private final SoundEvent clickSound;
    private final boolean consumeAfterComplete;
    private final boolean useDisabledTexture;
    private final boolean spawnsParticles;

    private final ItemStack jeiIconItem;

    KnappingType(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.inputItem = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "input"));
        this.offHandInputItem = json.has("off_hand_input") ? ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "off_hand_input")) : null;
        this.inventoryInputItem = json.has("inventory_input") ? ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "inventory_input")) : null;
        this.missingInventoryItemTranslationKey = json.has("when_inventory_input_missing") ? JsonHelpers.getAsString(json, "when_inventory_input_missing") : null;

        this.clickSound = JsonHelpers.getRegistryEntry(json, "click_sound", ForgeRegistries.SOUND_EVENTS);
        this.consumeAfterComplete = JsonHelpers.getAsBoolean(json, "consume_after_complete");
        this.useDisabledTexture = JsonHelpers.getAsBoolean(json, "use_disabled_texture");
        this.spawnsParticles = JsonHelpers.getAsBoolean(json, "spawns_particles");

        this.jeiIconItem = JsonHelpers.getItemStack(json, "jei_icon_item");
    }

    KnappingType(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;

        this.inputItem = ItemStackIngredient.fromNetwork(buffer);
        this.offHandInputItem = Helpers.decodeNullable(buffer, ItemStackIngredient::fromNetwork);
        this.inventoryInputItem = Helpers.decodeNullable(buffer, ItemStackIngredient::fromNetwork);
        this.missingInventoryItemTranslationKey = Helpers.decodeNullable(buffer, FriendlyByteBuf::readUtf);

        this.clickSound = buffer.readRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS);
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
        return inputItem.count();
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
        Helpers.encodeNullable(offHandInputItem, buffer, ItemStackIngredient::toNetwork);
        Helpers.encodeNullable(inventoryInputItem, buffer, ItemStackIngredient::toNetwork);
        Helpers.encodeNullable(missingInventoryItemTranslationKey, buffer, (e, b) -> b.writeUtf(e));

        buffer.writeRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS, clickSound);
        buffer.writeBoolean(consumeAfterComplete);
        buffer.writeBoolean(useDisabledTexture);
        buffer.writeBoolean(spawnsParticles);

        buffer.writeItem(jeiIconItem);
    }

    public static final class Packet extends DataManagerSyncPacket<KnappingType> {}
}
