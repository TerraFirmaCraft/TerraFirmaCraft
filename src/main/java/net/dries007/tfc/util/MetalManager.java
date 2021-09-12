package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.world.level.material.Fluid;

public class MetalManager extends DataManager.Instance<Metal>
{
    private final Map<Fluid, Metal> metalsFromFluids;

    MetalManager()
    {
        super(Metal::new, "metals", "metal");

        this.metalsFromFluids = new HashMap<>();
    }

    /**
     * Reverse lookup for metals attached to fluids.
     * For the other direction, see {@link Metal#getFluid()}.
     *
     * @param fluid The fluid, can be empty.
     * @return A metal if it exists, and null if it doesn't.
     */
    @Nullable
    public Metal getMetal(Fluid fluid)
    {
        return metalsFromFluids.get(fluid);
    }

    @Override
    protected void postProcess()
    {
        metalsFromFluids.clear();
        for (Metal metal : getValues())
        {
            metalsFromFluids.put(metal.getFluid(), metal);
        }
    }
}
