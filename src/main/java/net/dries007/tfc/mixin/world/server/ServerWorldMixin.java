package net.dries007.tfc.mixin.world.server;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;

import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World
{
    private ServerWorldMixin(ISpawnWorldInfo spawnWorldInfo, RegistryKey<World> registryKey, DimensionType dimensionType, Supplier<IProfiler> profiler, boolean meh, boolean bleh, long blah)
    {
        super(spawnWorldInfo, registryKey, dimensionType, profiler, meh, bleh, blah);
    }

    /**
     * The default value is hardcoded to 63. This should query the generator's value (which we can override).
     *
     * Would be fixed upstream by https://github.com/MinecraftForge/MinecraftForge/pull/7479
     */
    @Override
    public int getSeaLevel()
    {
        return ((ServerWorld) (Object) this).getChunkSource().getGenerator().getSeaLevel();
    }

    /**
     * Hook into chunk random ticks, allow for snow placement modification.
     * Could be replaced by https://github.com/MinecraftForge/MinecraftForge/pull/7235
     */
    @Inject(method = "tickChunk", at = @At("RETURN"))
    private void inject$tickChunk(Chunk chunkIn, int randomTickSpeed, CallbackInfo ci)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (random.nextInt(16) == 0)
        {
            BlockPos pos = getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
            if (isAreaLoaded(pos, 1) && isRaining())
            {
                BlockState state = getBlockState(pos);
                if (state.is(Blocks.SNOW) && state.getValue(SnowBlock.LAYERS) < 7 && Climate.getTemperature(this, pos) < Climate.SNOW_STACKING_TEMPERATURE)
                {
                    setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) + 1));
                }
            }
        }
    }

    /**
     * Redirect a call to {@link Biome#getPrecipitation()} with one that has world and position context.
     * The position is inferred by reverse engineering {@link ServerWorld#getBlockRandomPos(int, int, int, int)}
     */
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$tickChunk$getPrecipitation(Biome biome, Chunk chunkIn)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        BlockPos pos = Helpers.getPreviousRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15, randValue).below();
        return Climate.getPrecipitation(this, pos);
    }
}
