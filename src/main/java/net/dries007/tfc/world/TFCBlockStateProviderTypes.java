package net.dries007.tfc.world;

import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unchecked")
public class TFCBlockStateProviderTypes
{
    public static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, MOD_ID);

    public static final RegistryObject<BlockStateProviderType<FacingHorizontalBlockStateProvider>> FACING_PROVIDER = BLOCK_STATE_PROVIDER_TYPES.register("facing_random", () -> new BlockStateProviderType<>(FacingHorizontalBlockStateProvider.CODEC));
}
