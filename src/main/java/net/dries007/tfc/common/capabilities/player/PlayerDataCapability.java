package net.dries007.tfc.common.capabilities.player;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.dries007.tfc.util.Helpers;

public final class PlayerDataCapability
{
    @CapabilityInject(PlayerData.class)
    public static final Capability<PlayerData> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = Helpers.identifier("player_data");

    public static void setup()
    {
        Helpers.registerSimpleCapability(PlayerData.class);
    }
}
