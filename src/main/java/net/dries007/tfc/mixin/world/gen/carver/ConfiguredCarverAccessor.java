package net.dries007.tfc.mixin.world.gen.carver;

import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfiguredCarver.class)
public interface ConfiguredCarverAccessor
{
    /**
     * The accessor has been inline / removed and it is needed to provide additional carving context
     */
    @Accessor("worldCarver")
    WorldCarver<?> accessor$getWorldCarver();
}
