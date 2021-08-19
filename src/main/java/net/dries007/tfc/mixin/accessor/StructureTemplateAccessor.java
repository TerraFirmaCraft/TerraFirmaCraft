package net.dries007.tfc.mixin.accessor;

import java.util.List;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor
{
    @Accessor("palettes")
    List<StructureTemplate.Palette> accessor$getPalettes();
}
