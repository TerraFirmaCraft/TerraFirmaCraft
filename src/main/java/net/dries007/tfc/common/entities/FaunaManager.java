package net.dries007.tfc.common.entities;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.mixin.entity.EntitySpawnPlacementRegistryAccessor;
import net.dries007.tfc.util.data.DataManager;

public class FaunaManager extends DataManager<Fauna>
{
    public static final FaunaManager INSTANCE = new FaunaManager();

    private FaunaManager()
    {
        super(new GsonBuilder().create(), "fauna", "fauna", true);
    }

    @Override
    protected Fauna read(ResourceLocation id, JsonObject obj)
    {
        return new Fauna(id, obj);
    }

    @Override
    protected void postProcess()
    {
        INSTANCE.getValues().forEach(fauna -> {
            // MC will throw up if we have duplicate keys, so we have to remove first.
            EntitySpawnPlacementRegistryAccessor.getPlacementMap().remove(fauna.getEntity());
            EntitySpawnPlacementRegistry.register(fauna.getEntity(), fauna.getPlacementType(), Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, fauna.makeRules());
        });
        super.postProcess();
    }
}
