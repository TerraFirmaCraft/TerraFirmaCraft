package net.dries007.tfc.world.vein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;

import net.dries007.tfc.command.ClearWorldCommand;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.json.*;
import net.dries007.tfc.world.feature.VeinsFeature;

public class VeinTypeManager extends GenericJsonReloadListener<VeinType<?>>
{
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(new TypeToken<IWeighted<BlockState>>() {}.getType(), new WeightedDeserializer<>(BlockState.class))
        .registerTypeAdapter(new TypeToken<IWeighted<Indicator>>() {}.getType(), new WeightedDeserializer<>(Indicator.class))
        .registerTypeAdapter(new TypeToken<List<BlockState>>() {}.getType(), new LenientListDeserializer<>(BlockState.class, Collections::singletonList, ArrayList::new))
        .registerTypeAdapter(BlockState.class, BlockStateDeserializer.INSTANCE)
        .registerTypeAdapter(Indicator.class, VeinIndicatorDeserializer.INSTANCE)
        .registerTypeAdapter(new TypeToken<VeinType<?>>() {}.getType(), VeinTypeDeserializer.INSTANCE)
        .disableHtmlEscaping()
        .create();

    public static final VeinTypeManager INSTANCE = new VeinTypeManager();

    private VeinTypeManager()
    {
        super(GSON, "/veins", new TypeToken<VeinType<?>>() {}.getType(), "vein");
    }

    @Override
    protected void postProcess()
    {
        super.postProcess();
        ClearWorldCommand.resetVeinStates();
        VeinsFeature.resetChunkRadius();
    }
}
