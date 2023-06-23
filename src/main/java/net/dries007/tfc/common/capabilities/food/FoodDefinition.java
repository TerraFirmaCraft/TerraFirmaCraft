/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.items.DynamicBowlFood;
import net.dries007.tfc.util.ItemDefinition;
import net.dries007.tfc.util.JsonHelpers;

public class FoodDefinition extends ItemDefinition
{
    public static FoodHandler getHandler(FoodDefinition definition, ItemStack stack)
    {
        return switch(definition.handlerType)
            {
                case STATIC -> new FoodHandler(definition.getData());
                case DYNAMIC -> new FoodHandler.Dynamic();
                case DYNAMIC_BOWL -> new DynamicBowlFood.DynamicBowlHandler(stack);
            };
    }

    private final FoodData data;
    private final HandlerType handlerType;

    public FoodDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        if (json.has("type"))
        {
            handlerType = JsonHelpers.getEnum(json.get("type"), HandlerType.class);
            data = FoodData.EMPTY;
        }
        else
        {
            handlerType = HandlerType.STATIC;
            data = FoodData.read(json);
        }
    }

    public FoodDefinition(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
        this.data = FoodData.decode(buffer);
        this.handlerType = buffer.readEnum(HandlerType.class);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        data.encode(buffer);
        buffer.writeEnum(handlerType);
    }

    public FoodData getData()
    {
        return data;
    }

    public HandlerType getHandlerType()
    {
        return handlerType;
    }

    public enum HandlerType
    {
        STATIC,
        DYNAMIC,
        DYNAMIC_BOWL;
    }
}
