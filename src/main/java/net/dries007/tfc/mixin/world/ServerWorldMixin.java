package net.dries007.tfc.mixin.world;

import java.util.function.Supplier;

import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World
{
    private ServerWorldMixin(ISpawnWorldInfo worldInfo, RegistryKey<World> dimensionIn, DimensionType dimensionType, Supplier<IProfiler> profilerIn, boolean isRemote, boolean isDebug, long seed)
    {
        super(worldInfo, dimensionIn, dimensionType, profilerIn, isRemote, isDebug, seed);
    }

    @Override
    public int getSeaLevel()
    {
        return ((ServerWorld) (Object) this).getChunkSource().getGenerator().getSeaLevel();
    }
}
