package net.dries007.tfc.common.blockentities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.util.Fertilizer;
import net.dries007.tfc.util.Helpers;

public interface IFarmland
{
    static void addNutrientParticles(ServerLevel level, BlockPos pos, Fertilizer fertilizer)
    {
        final float n = fertilizer.getNitrogen(), p = fertilizer.getPhosphorus(), k = fertilizer.getPotassium();
        for (int i = 0; i < (int) (n > 0 ? Mth.clamp(n * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.NITROGEN.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
        for (int i = 0; i < (int) (p > 0 ? Mth.clamp(p * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.PHOSPHORUS.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
        for (int i = 0; i < (int) (k > 0 ? Mth.clamp(k * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.POTASSIUM.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
    }

    float getNutrient(FarmlandBlockEntity.NutrientType type);

    void addNutrient(FarmlandBlockEntity.NutrientType type, float value);

    void setNutrient(FarmlandBlockEntity.NutrientType type, float value);

    default void addNutrients(Fertilizer fertilizer)
    {

    }

    /**
     * Consume up to {@code amount} of nutrient {@code type}.
     * Other nutrients may be changed during this call as a result.
     * @return The amount of nutrient {@code type} that was actually consumed.
     */
    default float consumeNutrientAndResupplyOthers(NutrientType type, float amount)
    {
        return 0f;
    }

    default void addTooltipInfo(List<Component> text)
    {
        text.add(Helpers.translatable("tfc.tooltip.farmland.nutrients", format(getNutrient(NutrientType.NITROGEN)), format(getNutrient(NutrientType.PHOSPHOROUS)), format(getNutrient(NutrientType.POTASSIUM))));
    }

    private String format(float value)
    {
        return String.format("%.2f", value * 100);
    }


    enum NutrientType
    {
        NITROGEN, PHOSPHOROUS, POTASSIUM;

        public static final NutrientType[] VALUES = values();
    }
}
