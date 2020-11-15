package net.dries007.tfc.world;

import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.mixin.world.gen.blockplacer.BlockPlacerTypeAccessor;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unchecked")
public class TFCBlockPlacerTypes
{
    public static final DeferredRegister<BlockPlacerType<?>> BLOCK_PLACER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_PLACER_TYPES, MOD_ID);

    public static final RegistryObject<BlockPlacerType<TallPlantPlacer>> TALL_PLANT_PLACER = BLOCK_PLACER_TYPES.register("tall_plant", () -> BlockPlacerTypeAccessor.invoke$new(TallPlantPlacer.CODEC));
    public static final RegistryObject<BlockPlacerType<EmergentPlantPlacer>> EMERGENT_PLANT_PLACER = BLOCK_PLACER_TYPES.register("emergent", () -> BlockPlacerTypeAccessor.invoke$new(EmergentPlantPlacer.CODEC));
    public static final RegistryObject<BlockPlacerType<WaterPlantPlacer>> WATER_PLANT_PLACER = BLOCK_PLACER_TYPES.register("water_plant", () -> BlockPlacerTypeAccessor.invoke$new(WaterPlantPlacer.CODEC));

}
