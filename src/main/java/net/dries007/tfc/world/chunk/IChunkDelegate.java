/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunk;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;

public interface IChunkDelegate extends IChunk
{
    IChunk getDelegate();

    @Nullable
    @Override
    default BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving)
    {
        return getDelegate().setBlockState(pos, state, isMoving);
    }

    @Nullable
    @Override
    default ChunkSection getHighestSection()
    {
        return getDelegate().getHighestSection();
    }

    @Override
    default int getHighestSectionPosition()
    {
        return getDelegate().getHighestSectionPosition();
    }

    @Override
    default Set<BlockPos> getBlockEntitiesPos()
    {
        return getDelegate().getBlockEntitiesPos();
    }

    @Override
    default ChunkSection[] getSections()
    {
        return getDelegate().getSections();
    }

    @Override
    default Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps()
    {
        return getDelegate().getHeightmaps();
    }

    @Override
    default void setHeightmap(Heightmap.Type type, long[] data)
    {
        getDelegate().setHeightmap(type, data);
    }

    @Override
    default Heightmap getOrCreateHeightmapUnprimed(Heightmap.Type typeIn)
    {
        return getDelegate().getOrCreateHeightmapUnprimed(typeIn);
    }

    @Override
    default int getHeight(Heightmap.Type heightmapType, int x, int z)
    {
        return getDelegate().getHeight(heightmapType, x, z);
    }

    @Override
    default ChunkPos getPos()
    {
        return getDelegate().getPos();
    }

    @Override
    default void setLastSaveTime(long saveTime)
    {
        getDelegate().setLastSaveTime(saveTime);
    }

    @Override
    default Map<String, StructureStart> getAllStarts()
    {
        return getDelegate().getAllStarts();
    }

    @Override
    default void setAllStarts(Map<String, StructureStart> structureStartsIn)
    {
        getDelegate().setAllStarts(structureStartsIn);
    }

    @Override
    default boolean isYSpaceEmpty(int startY, int endY)
    {
        return getDelegate().isYSpaceEmpty(startY, endY);
    }

    @Nullable
    @Override
    default BiomeContainer getBiomes()
    {
        return getDelegate().getBiomes();
    }

    @Override
    default void setBlockEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        getDelegate().setBlockEntity(pos, tileEntityIn);
    }

    @Override
    default void addEntity(Entity entityIn)
    {
        getDelegate().addEntity(entityIn);
    }

    @Nullable
    @Override
    default TileEntity getBlockEntity(BlockPos pos)
    {
        return getDelegate().getBlockEntity(pos);
    }

    @Override
    default BlockState getBlockState(BlockPos pos)
    {
        return getDelegate().getBlockState(pos);
    }

    @Override
    default IFluidState getFluidState(BlockPos pos)
    {
        return getDelegate().getFluidState(pos);
    }    @Override
    default void setUnsaved(boolean modified)
    {
        getDelegate().setUnsaved(modified);
    }

    @Override
    default int getLightEmission(BlockPos pos)
    {
        return getDelegate().getLightEmission(pos);
    }

    @Override
    default int getMaxLightLevel()
    {
        return getDelegate().getMaxLightLevel();
    }

    @Override
    default int getMaxBuildHeight()
    {
        return getDelegate().getMaxBuildHeight();
    }    @Override
    default boolean isUnsaved()
    {
        return getDelegate().isUnsaved();
    }

    @Override
    default BlockRayTraceResult clip(RayTraceContext context)
    {
        return getDelegate().clip(context);
    }

    @Nullable
    @Override
    default BlockRayTraceResult clipWithInteractionOverride(Vec3d startVec, Vec3d endVec, BlockPos pos, VoxelShape shape, BlockState state)
    {
        return getDelegate().clipWithInteractionOverride(startVec, endVec, pos, shape, state);
    }

    @Nullable
    @Override
    default StructureStart getStartForFeature(String stucture)
    {
        return getDelegate().getStartForFeature(stucture);
    }    @Override
    default ChunkStatus getStatus()
    {
        return getDelegate().getStatus();
    }

    @Override
    default void setStartForFeature(String structureIn, StructureStart structureStartIn)
    {
        getDelegate().setStartForFeature(structureIn, structureStartIn);
    }

    @Override
    default LongSet getReferencesForFeature(String structureIn)
    {
        return getDelegate().getReferencesForFeature(structureIn);
    }

    @Override
    default void addReferenceForFeature(String structure, long reference)
    {
        getDelegate().addReferenceForFeature(structure, reference);
    }    @Override
    default void removeBlockEntity(BlockPos pos)
    {
        getDelegate().removeBlockEntity(pos);
    }

    @Override
    default Map<String, LongSet> getAllReferences()
    {
        return getDelegate().getAllReferences();
    }

    @Override
    default void setAllReferences(Map<String, LongSet> structureReferences)
    {
        getDelegate().setAllReferences(structureReferences);
    }

    @Override
    default void markPosForPostprocessing(BlockPos pos)
    {
        getDelegate().markPosForPostprocessing(pos);
    }





    @Override
    default ShortList[] getPostProcessing()
    {
        return getDelegate().getPostProcessing();
    }





    @Override
    default void addPackedPostProcess(short packedPosition, int index)
    {
        getDelegate().addPackedPostProcess(packedPosition, index);
    }


    @Override
    default void setBlockEntityNbt(CompoundNBT nbt)
    {
        getDelegate().setBlockEntityNbt(nbt);
    }


    @Nullable
    @Override
    default CompoundNBT getBlockEntityNbt(BlockPos pos)
    {
        return getDelegate().getBlockEntityNbt(pos);
    }


    @Nullable
    @Override
    default CompoundNBT getBlockEntityNbtForSaving(BlockPos pos)
    {
        return getDelegate().getBlockEntityNbtForSaving(pos);
    }


    @Override
    default Stream<BlockPos> getLights()
    {
        return getDelegate().getLights();
    }


    @Override
    default ITickList<Block> getBlockTicks()
    {
        return getDelegate().getBlockTicks();
    }


    @Override
    default ITickList<Fluid> getLiquidTicks()
    {
        return getDelegate().getLiquidTicks();
    }


    @Override
    default BitSet getCarvingMask(GenerationStage.Carving type)
    {
        return getDelegate().getCarvingMask(type);
    }

    @Override
    default UpgradeData getUpgradeData()
    {
        return getDelegate().getUpgradeData();
    }

    @Override
    default void setInhabitedTime(long newInhabitedTime)
    {
        getDelegate().setInhabitedTime(newInhabitedTime);
    }

    @Override
    default long getInhabitedTime()
    {
        return getDelegate().getInhabitedTime();
    }

    @Override
    default boolean isLightCorrect()
    {
        return getDelegate().isLightCorrect();
    }

    @Override
    default void setLightCorrect(boolean lightCorrectIn)
    {
        getDelegate().setLightCorrect(lightCorrectIn);
    }

    @Nullable
    @Override
    default IWorld getWorldForge()
    {
        return getDelegate().getWorldForge();
    }


}