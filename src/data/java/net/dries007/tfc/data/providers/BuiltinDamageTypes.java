package net.dries007.tfc.data.providers;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

import net.dries007.tfc.common.TFCDamageTypes;

public class BuiltinDamageTypes
{
    private final BootstrapContext<DamageType> context;

    public BuiltinDamageTypes(BootstrapContext<DamageType> context)
    {
        this.context = context;

        register(TFCDamageTypes.CORAL, 0.1f, DamageEffects.HURT);
        register(TFCDamageTypes.DEHYDRATION, 0f, DamageEffects.HURT);
        register(TFCDamageTypes.GRILL, 0.1f, DamageEffects.BURNING);
        register(TFCDamageTypes.PLUCK, 0f, DamageEffects.HURT);
        register(TFCDamageTypes.POT, 0.1f, DamageEffects.BURNING);
    }

    private void register(ResourceKey<DamageType> type, float exhaustion, DamageEffects effects)
    {
        context.register(type, new DamageType(type.location().getNamespace() + "." + type.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion, effects, DeathMessageType.DEFAULT));
    }
}
