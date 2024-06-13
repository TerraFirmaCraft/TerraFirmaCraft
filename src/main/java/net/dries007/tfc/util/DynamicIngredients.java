/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.network.DataManagerSyncPacket;

public final class DynamicIngredients extends ItemDefinition
{
    public static final RegisteredDataManager<DynamicIngredients> MANAGER = new RegisteredDataManager<>(DynamicIngredients::new, DynamicIngredients::new, Helpers.identifier("ingredients"), "dynamic ingredient", DynamicIngredients::new, DynamicIngredients::encode, Packet::new);

    public static final Holder CAN_LOG = register("can_log"); // Axes that can cut down trees
    public static final Holder CAN_LOG_BADLY = register("can_log_badly"); // Axes that have only 60% return when cutting down trees

    public static final Holder CAN_ANVIL = register("can_anvil");
    public static final Holder CAN_EDIT_GEARBOXES = register("can_edit_gearboxes");

    public static final Holder CAN_SCRAPE = register("can_scrape");

    private static Holder register(String name)
    {
        return new Holder(MANAGER.register(Helpers.identifier(name)));
    }

    private DynamicIngredients(ResourceLocation id)
    {
        super(id, Ingredient.EMPTY);
    }

    private DynamicIngredients(ResourceLocation id, JsonObject json)
    {
        super(id, json);
    }

    private DynamicIngredients(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
    }

    private void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
    }

    public static class Packet extends DataManagerSyncPacket<RegisteredDataManager.Entry<DynamicIngredients>> {}

    public record Holder(RegisteredDataManager.Entry<DynamicIngredients> inner)
    {
        public Ingredient ingredient()
        {
            return inner.get().ingredient;
        }

        public boolean matches(ItemStack stack)
        {
            return inner.get().matches(stack);
        }
    }
}
