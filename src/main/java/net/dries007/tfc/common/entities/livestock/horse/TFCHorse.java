package net.dries007.tfc.common.entities.livestock.horse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;

public class TFCHorse extends Horse
{
    public TFCHorse(EntityType<? extends Horse> type, Level level)
    {
        super(type, level);
    }
}
