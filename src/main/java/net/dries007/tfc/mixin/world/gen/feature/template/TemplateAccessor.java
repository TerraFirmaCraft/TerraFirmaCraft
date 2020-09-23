package net.dries007.tfc.mixin.world.gen.feature.template;

import java.util.List;

import net.minecraft.world.gen.feature.template.Template;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Template.class)
public interface TemplateAccessor
{
    @Accessor("palettes")
    List<Template.Palette> accessor$getPalettes();
}
