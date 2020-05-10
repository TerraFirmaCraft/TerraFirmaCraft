package net.dries007.tfc.world.vein;

import com.google.gson.GsonBuilder;

import net.dries007.tfc.command.ClearWorldCommand;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.TypedDataManager;
import net.dries007.tfc.world.feature.VeinsFeature;

public class VeinTypeManager extends TypedDataManager<VeinType<?>>
{
    public static final VeinTypeManager INSTANCE = new VeinTypeManager();

    private VeinTypeManager()
    {
        super(new GsonBuilder().create(), "veins", "vein type");

        register(Helpers.identifier("cluster"), ClusterVeinType::new);
        register(Helpers.identifier("disc"), DiscVeinType::new);
        register(Helpers.identifier("pipe"), PipeVeinType::new);
    }

    @Override
    protected void postProcess()
    {
        super.postProcess();
        ClearWorldCommand.resetVeinStates();
        VeinsFeature.resetChunkRadius();
    }
}
