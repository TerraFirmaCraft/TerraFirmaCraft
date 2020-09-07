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

    @Override
    default void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        getDelegate().addTileEntity(pos, tileEntityIn);
    }

    @Override
    default void addEntity(Entity entityIn)
    {
        getDelegate().addEntity(entityIn);
    }

    @Nullable
    @Override
    default ChunkSection getLastExtendedBlockStorage()
    {
        return getDelegate().getLastExtendedBlockStorage();
    }

    @Override
    default int getTopFilledSegment()
    {
        return getDelegate().getTopFilledSegment();
    }

    @Override
    default Set<BlockPos> getTileEntitiesPos()
    {
        return getDelegate().getTileEntitiesPos();
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
    default Heightmap getHeightmap(Heightmap.Type typeIn)
    {
        return getDelegate().getHeightmap(typeIn);
    }

    @Override
    default int getTopBlockY(Heightmap.Type heightmapType, int x, int z)
    {
        return getDelegate().getTopBlockY(heightmapType, x, z);
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
    default Map<String, StructureStart> getStructureStarts()
    {
        return getDelegate().getStructureStarts();
    }

    @Override
    default void setStructureStarts(Map<String, StructureStart> structureStartsIn)
    {
        getDelegate().setStructureStarts(structureStartsIn);
    }

    @Override
    default boolean isEmptyBetween(int startY, int endY)
    {
        return getDelegate().isEmptyBetween(startY, endY);
    }

    @Nullable
    @Override
    default BiomeContainer getBiomes()
    {
        return getDelegate().getBiomes();
    }

    @Nullable
    @Override
    default TileEntity getTileEntity(BlockPos pos)
    {
        return getDelegate().getTileEntity(pos);
    }

    @Override
    default void setModified(boolean modified)
    {
        getDelegate().setModified(modified);
    }

    @Override
    default BlockState getBlockState(BlockPos pos)
    {
        return getDelegate().getBlockState(pos);
    }

    @Override
    default boolean isModified()
    {
        return getDelegate().isModified();
    }

    @Override
    default IFluidState getFluidState(BlockPos pos)
    {
        return getDelegate().getFluidState(pos);
    }

    @Override
    default ChunkStatus getStatus()
    {
        return getDelegate().getStatus();
    }

    @Override
    default int getLightValue(BlockPos pos)
    {
        return getDelegate().getLightValue(pos);
    }

    @Override
    default void removeTileEntity(BlockPos pos)
    {
        getDelegate().removeTileEntity(pos);
    }

    @Override
    default int getMaxLightLevel()
    {
        return getDelegate().getMaxLightLevel();
    }

    @Override
    default void markBlockForPostprocessing(BlockPos pos)
    {
        getDelegate().markBlockForPostprocessing(pos);
    }

    @Override
    default int getHeight()
    {
        return getDelegate().getHeight();
    }

    @Override
    default ShortList[] getPackedPositions()
    {
        return getDelegate().getPackedPositions();
    }

    @Override
    default BlockRayTraceResult rayTraceBlocks(RayTraceContext context)
    {
        return getDelegate().rayTraceBlocks(context);
    }

    @Override
    default void func_201636_b(short packedPosition, int index)
    {
        getDelegate().func_201636_b(packedPosition, index);
    }

    @Nullable
    @Override
    default BlockRayTraceResult rayTraceBlocks(Vec3d startVec, Vec3d endVec, BlockPos pos, VoxelShape shape, BlockState state)
    {
        return getDelegate().rayTraceBlocks(startVec, endVec, pos, shape, state);
    }

    @Override
    default void addTileEntity(CompoundNBT nbt)
    {
        getDelegate().addTileEntity(nbt);
    }

    @Nullable
    @Override
    default StructureStart getStructureStart(String stucture)
    {
        return getDelegate().getStructureStart(stucture);
    }

    @Nullable
    @Override
    default CompoundNBT getDeferredTileEntity(BlockPos pos)
    {
        return getDelegate().getDeferredTileEntity(pos);
    }

    @Override
    default void putStructureStart(String structureIn, StructureStart structureStartIn)
    {
        getDelegate().putStructureStart(structureIn, structureStartIn);
    }

    @Nullable
    @Override
    default CompoundNBT getTileEntityNBT(BlockPos pos)
    {
        return getDelegate().getTileEntityNBT(pos);
    }

    @Override
    default LongSet getStructureReferences(String structureIn)
    {
        return getDelegate().getStructureReferences(structureIn);
    }

    @Override
    default Stream<BlockPos> getLightSources()
    {
        return getDelegate().getLightSources();
    }

    @Override
    default void addStructureReference(String structure, long reference)
    {
        getDelegate().addStructureReference(structure, reference);
    }

    @Override
    default ITickList<Block> getBlocksToBeTicked()
    {
        return getDelegate().getBlocksToBeTicked();
    }

    @Override
    default Map<String, LongSet> getStructureReferences()
    {
        return getDelegate().getStructureReferences();
    }

    @Override
    default ITickList<Fluid> getFluidsToBeTicked()
    {
        return getDelegate().getFluidsToBeTicked();
    }

    @Override
    default void setStructureReferences(Map<String, LongSet> structureReferences)
    {
        getDelegate().setStructureReferences(structureReferences);
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
    default boolean hasLight()
    {
        return getDelegate().hasLight();
    }

    @Override
    default void setLight(boolean lightCorrectIn)
    {
        getDelegate().setLight(lightCorrectIn);
    }

    @Nullable
    @Override
    default IWorld getWorldForge()
    {
        return getDelegate().getWorldForge();
    }


}
