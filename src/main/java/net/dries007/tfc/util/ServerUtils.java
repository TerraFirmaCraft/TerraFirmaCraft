package net.dries007.tfc.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static com.google.common.math.DoubleMath.mean;

public class ServerUtils
{
    public ServerUtils() {}

    public double getTPS(World world, int dimId)
    {
        double worldTickTime = (double) mean((long[]) world.getMinecraftServer().worldTickTimes.get(dimId)) * 1.0E-6D;
        return Math.min(1000.0D / worldTickTime, 20.0D);

    }
}