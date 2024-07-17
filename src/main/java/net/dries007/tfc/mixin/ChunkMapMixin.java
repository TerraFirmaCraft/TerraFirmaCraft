/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.concurrent.Executor;
import java.util.function.Supplier;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.ChunkMapBridge;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements ChunkMapBridge
{
    @Mutable @Shadow @Final private WorldGenContext worldGenContext;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void createRandomStateExtension(ServerLevel level, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer fixerUpper, StructureTemplateManager structureManager, Executor dispatcher, BlockableEventLoop<Runnable> mainThreadExecutor, LightChunkGetter lightChunk, ChunkGenerator generator, ChunkProgressListener progressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> overworldDataStorage, int viewDistance, boolean sync, CallbackInfo ci)
    {
        if (generator instanceof ChunkGeneratorExtension ex)
        {
            ex.initRandomState((ChunkMap) (Object) this, level);
        }
    }

    @Override
    public void tfc$updateGenerator(@NotNull ChunkGenerator generator)
    {
        worldGenContext = new WorldGenContext(
            worldGenContext.level(),
            generator,
            worldGenContext.structureManager(),
            worldGenContext.lightEngine(),
            worldGenContext.mainThreadMailBox()
        );
    }
}
