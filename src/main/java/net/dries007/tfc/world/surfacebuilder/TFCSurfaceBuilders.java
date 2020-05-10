package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCSurfaceBuilders
{
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = new DeferredRegister<>(ForgeRegistries.SURFACE_BUILDERS, MOD_ID);


}
