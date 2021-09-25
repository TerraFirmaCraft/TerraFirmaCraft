package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import net.dries007.tfc.common.blockentities.CropBlockEntity;

public class CropYieldProvider implements NumberProvider
{
    private final NumberProvider min, max;

    public CropYieldProvider(NumberProvider min, NumberProvider max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public float getFloat(LootContext context)
    {
        final BlockEntity entity = context.getParam(LootContextParams.BLOCK_ENTITY);
        if (entity instanceof CropBlockEntity crop)
        {
            return Mth.lerp(crop.getYield(), min.getFloat(context), max.getFloat(context));
        }
        return 0;
    }

    @Override
    public LootNumberProviderType getType()
    {
        return TFCLoot.CROP_YIELD;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<CropYieldProvider>
    {
        @Override
        public void serialize(JsonObject json, CropYieldProvider value, JsonSerializationContext context)
        {
            json.add("min", context.serialize(value.min));
            json.add("max", context.serialize(value.max));
        }

        @Override
        public CropYieldProvider deserialize(JsonObject json, JsonDeserializationContext context)
        {
            final NumberProvider min = GsonHelper.getAsObject(json, "min", context, NumberProvider.class);
            final NumberProvider max = GsonHelper.getAsObject(json, "max", context, NumberProvider.class);
            return new CropYieldProvider(min, max);
        }
    }
}
