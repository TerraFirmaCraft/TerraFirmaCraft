package net.dries007.tfc.util.support;

import java.util.Optional;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.util.data.DataManager;

public class SupportManager extends DataManager<Support>
{
    public static final SupportManager INSTANCE = new SupportManager();

    private int maxSupportUp, maxSupportDown, maxSupportHorizontal;

    private SupportManager()
    {
        super(new GsonBuilder().create(), "supports", "support");
    }

    public Optional<Support> get(BlockState state)
    {
        return getValues().stream().filter(support -> support.matches(state)).findFirst();
    }

    public Iterable<BlockPos> getMaximumSupportedAreaAround(BlockPos minPoint, BlockPos maxPoint)
    {
        return BlockPos.getAllInBoxMutable(minPoint.add(-maxSupportHorizontal, -maxSupportDown, -maxSupportHorizontal), maxPoint.add(maxSupportHorizontal, maxSupportUp, maxSupportHorizontal));
    }

    @Override
    protected Support read(ResourceLocation id, JsonObject obj)
    {
        return new Support(id, obj);
    }

    @Override
    protected void postProcess()
    {
        // Calculate the maximum support radius, used for searching supported areas
        maxSupportUp = 0;
        maxSupportDown = 0;
        maxSupportHorizontal = 0;
        for (Support support : getValues())
        {
            maxSupportUp = Math.max(support.getSupportUp(), maxSupportUp);
            maxSupportDown = Math.max(support.getSupportDown(), maxSupportDown);
            maxSupportHorizontal = Math.max(support.getSupportHorizontal(), maxSupportHorizontal);
        }
        super.postProcess();
    }
}
