package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCDecorators
{
    public static final DeferredRegister<Placement<?>> DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, MOD_ID);

    public static final RegistryObject<FlatEnoughDecorator> FLAT_ENOUGH = DECORATORS.register("flat_enough", () -> new FlatEnoughDecorator(FlatEnoughConfig.CODEC));
}
