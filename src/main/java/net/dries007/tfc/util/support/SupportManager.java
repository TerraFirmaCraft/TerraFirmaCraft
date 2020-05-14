package net.dries007.tfc.util.support;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.dries007.tfc.util.data.DataManager;

public class SupportManager extends DataManager<Support>
{
    public static final SupportManager INSTANCE = new SupportManager();

    /**
     * Finds all unsupported positions in a large area. It's more efficient than checking each block individually and calling {@link SupportManager#isSupported(IBlockReader, BlockPos)}
     */
    public static Set<BlockPos> findUnsupportedPositions(IBlockReader worldIn, BlockPos from, BlockPos to)
    {
        Set<BlockPos> listSupported = new HashSet<>();
        Set<BlockPos> listUnsupported = new HashSet<>();
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());
        for (BlockPos searchingPoint : INSTANCE.getMaximumSupportedAreaAround(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)))
        {
            if (!listSupported.contains(searchingPoint))
            {
                listUnsupported.add(searchingPoint.toImmutable()); // Adding blocks that wasn't found supported
            }
            BlockState supportState = worldIn.getBlockState(searchingPoint);
            INSTANCE.get(supportState).ifPresent(support -> {
                for (BlockPos supported : support.getSupportedArea(searchingPoint))
                {
                    listSupported.add(supported.toImmutable()); // Adding all supported blocks by this support
                    listUnsupported.remove(supported); // Remove if this block was added earlier
                }
            });
        }
        // Searching point wasn't from points between from <-> to but
        // Time to remove the outsides that were added for convenience
        listUnsupported.removeIf(content -> content.getX() < minX || content.getX() > maxX || content.getY() < minY || content.getY() > maxY || content.getZ() < minZ || content.getZ() > maxZ);
        return listUnsupported;
    }

    public static boolean isSupported(IBlockReader world, BlockPos pos)
    {
        for (BlockPos supportPos : INSTANCE.getMaximumSupportedAreaAround(pos, pos))
        {
            BlockState supportState = world.getBlockState(supportPos);
            if (INSTANCE.get(supportState).map(support -> support.canSupport(supportPos, pos)).orElse(false))
            {
                return true;
            }
        }
        return false;
    }

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
