package net.dries007.tfc.mixin.world.gen.blockstateprovider;

import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("rawtypes")
@Mixin(BlockStateProviderType.class)
public interface BlockStateProviderTypeAccessor
{
    @Invoker("<init>")
    static BlockStateProviderType invoke$new(Codec<?> codec)
    {
        return null;
    }
}
