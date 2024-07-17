package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;

import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.EntityDamageResistance;

public class BuiltinEntityDamageResist extends DataManagerProvider<EntityDamageResistance>
{
    public BuiltinEntityDamageResist(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(EntityDamageResistance.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("skeletons", new EntityDamageResistance(EntityTypeTags.SKELETONS, new PhysicalDamage(1000000000, 0, -50)));
        add("zombies", new EntityDamageResistance(EntityTypeTags.ZOMBIES, new PhysicalDamage(-25, 0, 50)));
    }
}
