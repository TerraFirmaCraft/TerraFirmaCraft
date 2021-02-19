/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.gen;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor
{
    @Invoker("func_242705_a")//create structure
    void invoke$func_242705_a(StructureFeature<?, ?> structureFeature, DynamicRegistries dynamicRegistries, StructureManager structureManager, IChunk chunk, TemplateManager templateManager, long seed, ChunkPos chunkPos, Biome biome);
}
