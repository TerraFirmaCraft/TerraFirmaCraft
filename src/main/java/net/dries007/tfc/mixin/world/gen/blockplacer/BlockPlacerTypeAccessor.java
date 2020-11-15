package net.dries007.tfc.mixin.world.gen.blockplacer;

import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("rawtypes")
@Mixin(BlockPlacerType.class)
public interface BlockPlacerTypeAccessor
{
    @Invoker("<init>")
    static BlockPlacerType invoke$new(Codec<?> codec)
    {
        return null;
    }
}
