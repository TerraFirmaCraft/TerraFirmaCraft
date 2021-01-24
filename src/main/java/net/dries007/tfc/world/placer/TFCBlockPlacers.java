package net.dries007.tfc.world.placer;

import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBlockPlacers
{
    public static final DeferredRegister<BlockPlacerType<?>> BLOCK_PLACER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_PLACER_TYPES, MOD_ID);

    public static final RegistryObject<BlockPlacerType<TallPlantPlacer>> TALL_PLANT = register("tall_plant", TallPlantPlacer.CODEC);
    public static final RegistryObject<BlockPlacerType<EmergentPlantPlacer>> EMERGENT_PLANT = register("emergent", EmergentPlantPlacer.CODEC);
    public static final RegistryObject<BlockPlacerType<WaterPlantPlacer>> WATER_PLANT = register("water_plant", WaterPlantPlacer.CODEC);

    public static final RegistryObject<BlockPlacerType<UndergroundPlacer>> UNDERGROUND = register("underground", UndergroundPlacer.CODEC);

    private static <P extends BlockPlacer> RegistryObject<BlockPlacerType<P>> register(String name, Codec<P> codec)
    {
        return BLOCK_PLACER_TYPES.register(name, () -> new BlockPlacerType<>(codec));
    }
}
