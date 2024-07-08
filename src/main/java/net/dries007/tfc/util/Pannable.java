/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Pannable extends BlockDefinition
{
    public static final DataManager<Pannable> MANAGER = new DataManager<>(Helpers.identifier("panning"), "panning", Pannable::new, Pannable::new, Pannable::encode, Pannable.Packet::new);
    public static final IndirectHashCollection<Block, Pannable> CACHE = IndirectHashCollection.create(s -> s.ingredient.blocks(), MANAGER::getValues);

    @Nullable
    public static Pannable get(BlockState state)
    {
        for (Pannable pannable : CACHE.getAll(state.getBlock()))
        {
            if (pannable.ingredient.test(state))
            {
                return pannable;
            }
        }
        return null;
    }

    private final ResourceLocation lootTable;
    private final ResourceLocation[] modelStages;

    public Pannable(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        final JsonArray array = JsonHelpers.getAsJsonArray(json, "model_stages");
        final List<ResourceLocation> list = new ArrayList<>(array.size());
        for (JsonElement element : array)
        {
            list.add(Helpers.resourceLocation(element.getAsString()));
        }
        this.modelStages = list.toArray(new ResourceLocation[0]);
        this.lootTable = JsonHelpers.getResourceLocation(json, "loot_table");
    }

    public Pannable(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, buffer);
        final int size = buffer.readVarInt();
        this.modelStages = new ResourceLocation[size];
        for (int i = 0; i < size; i++)
        {
            modelStages[i] = buffer.readResourceLocation();
        }
        this.lootTable = buffer.readResourceLocation();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        buffer.writeVarInt(modelStages.length);
        for (ResourceLocation res : modelStages)
        {
            buffer.writeUtf(res.toString());
        }
        buffer.writeUtf(lootTable.toString());
    }

    public ResourceLocation getLootTable()
    {
        return lootTable;
    }

    public ResourceLocation[] getModelStages()
    {
        return modelStages;
    }

    public static class Packet extends DataManagerSyncPacket<Pannable> {}
}
