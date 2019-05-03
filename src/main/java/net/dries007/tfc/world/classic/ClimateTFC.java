/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public final class ClimateTFC
{
    private static final float[] Y_FACTOR_CACHE = new float[441];
    private static final Random rng = new Random();

    static
    {
        //internationally accepted average lapse time is 6.49 K / 1000 m, for the first 11 km of the atmosphere. I suggest graphing our temperature
        //across the 110 m against 2750 m, so that gives us a change of 1.6225 / 10 blocks, which isn't /terrible/
        //Now going to attempt exponential growth. calculations but change in temperature at 17.8475 for our system, so that should be the drop at 255.
        //therefore, change should be temp - f(x), where f(x) is an exp function roughly equal to f(x) = (x^2)/ 677.966.
        //This seems to work nicely. I like this. Since creative allows players to travel above 255, I'll see if I can't code in the rest of it.
        //The upper troposhere has no lapse rate, so we'll just use that.
        //The equation looks rather complicated, but you can see it here:
        // http://www.wolframalpha.com/input/?i=%28%28%28x%5E2+%2F+677.966%29+*+%280.5%29*%28%28%28110+-+x%29+%2B+%7C110+-+x%7C%29%2F%28110+-
        // +x%29%29%29+%2B+%28%280.5%29*%28%28%28x+-+110%29+%2B+%7Cx+-+110%7C%29%2F%28x+-+110%29%29+*+x+*+0.16225%29%29+0+to+440

        for (int y = 0; y < Y_FACTOR_CACHE.length; y += 1)
        {
            // temp = temp - (ySq / 677.966f) * (((110.01f - y) + Math.abs(110.01f - y)) / (2 * (110.01f - y)));
            // temp -= (0.16225 * y * (((y - 110.01f) + Math.abs(y - 110.01f)) / (2 * (y - 110.01f))));

            // float ySq = y * y;
            // float diff = 110.01f - y;
            // float factor = (ySq / 677.966f) * ((diff + Math.abs(diff)) / (2 * diff))
            // 		+ 0.16225f * y * ((diff - Math.abs(diff)) / (2 * diff));

            //more optimization: using an if should be more efficient (and simpler)
            float factor;
            if (y < 110)
            {
                // diff > 0
                factor = y * y / 677.966f;  // 17.85 for y=110
            }
            else
            {
                // diff <= 0
                factor = 0.16225f * y;  // 17.85 for y=110
            }
            Y_FACTOR_CACHE[y] = factor;
        }
    }

    public static boolean isSwamp(World world, BlockPos pos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, pos);
        return data.isInitialized() && data.getRainfall() >= 375f && data.getFloraDiversity() >= 0.5f && data.getFloraDensity() >= 0.5f && world.getBiome(pos).getHeightVariation() < 0.15;
    }

    public static float getBaseTemp(World world, BlockPos pos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, pos);
        if (!data.isInitialized()) return Float.NaN;
        return data.getBaseTemp();
    }

    public static float getAverageBiomeTemp(World world, BlockPos pos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, pos);
        if (!data.isInitialized()) return Float.NaN;
        return data.getAverageTemp();
    }

    public static float getHeightAdjustedBiomeTemp(World world, BlockPos pos)
    {
        float temp = adjustTempByHeight(pos.getY(), getAverageBiomeTemp(world, pos));
        if (temp <= 0 || !world.canBlockSeeSky(pos)) return temp;
        return temp - (temp * (0.25f * (1 - (world.getLight(pos) / 15f))));
    }

    public static float getTemp(World world, BlockPos pos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, pos);
        if (!data.isInitialized()) return Float.NaN;
        return getTemp(data.getBaseTemp(), world.getSeed(), pos.getZ());
    }

    public static float getHeightAdjustedTemp(World world, BlockPos pos)
    {
        float temp = adjustTempByHeight(pos.getY(), getTemp(world, pos));
        if (temp <= 0 || !world.canBlockSeeSky(pos)) return temp;
        return temp - (temp * (0.25f * (1 - (world.getLight(pos) / 15f))));
    }

    public static float adjustTempByHeight(int y, float temp)
    {
        if (y > WorldTypeTFC.SEALEVEL)
        {
            int i = y - WorldTypeTFC.SEALEVEL;
            if (i >= Y_FACTOR_CACHE.length)
            {
                i = Y_FACTOR_CACHE.length - 1;
            }
            temp -= Y_FACTOR_CACHE[i];
        }
        return temp;
    }

    /**
     * Range 0 - 1
     *
     * @param chunkZ the chunk z position
     * @return the latitude factor for temperature calculation
     */
    public static float latitudeFactor(int chunkZ)
    {
        if (!ConfigTFC.WORLD.cyclicTemperatureRegions)
        {
            chunkZ = MathHelper.clamp(chunkZ, -1250, 1250);
        }
        return 0.5f + 0.5f * ConfigTFC.WORLD.hemisphereType * (float) Math.sin(Math.PI * chunkZ / ConfigTFC.WORLD.zTemperatureModifier);
    }

    /**
     * Range -32 to 35
     *
     * @param baseTemp The base temp for the current location
     * @param month    The month (from CalendarTFC)
     * @return the month factor for temp calculation
     */
    public static float monthTemp(float baseTemp, CalendarTFC.Month month, int z)
    {
        //return month.getTempMod() + 0.2 * baseTemp;
        return (41f - month.getTempMod() * 1.1f * (1 - 0.8f * latitudeFactor(z))) + 0.2f * baseTemp;
    }

    /**
     * Range -40 to 40
     *
     * @param baseTemp The base temp for the current location
     * @param z        The z-coordinate of the location that is being queried
     * @return The month adjusted temperature. This gets the base temperature, before daily / hourly changes
     */
    public static float getMonthAdjTemp(float baseTemp, int z)
    {
        final float currentMonthFactor = monthTemp(baseTemp, CalendarTFC.getMonthOfYear(), z);
        final float nextMonthFactor = monthTemp(baseTemp, CalendarTFC.getMonthOfYear().next(), z);

        final float delta = (float) CalendarTFC.getDayOfMonth() / CalendarTFC.getDaysInMonth();
        // Affine combination to smooth temperature transition
        return currentMonthFactor * (1 - delta) + nextMonthFactor * delta;
    }

    /**
     * Get the exact temperature for a location, including day + hour variation
     *
     * @param baseTemp The base temperature, either from {@link ChunkDataTFC} or {@link ClimateRenderHelper}
     * @param seed     The world seed
     * @param z        the z-coordinate
     * @return A temperature, in the approximate range -35 to 35
     */
    private static float getTemp(float baseTemp, long seed, int z)
    {
        int h = (int) ((CalendarTFC.getTotalHours() - 6) % CalendarTFC.HOURS_IN_DAY);
        if (h < 0) h += CalendarTFC.HOURS_IN_DAY;

        float hourMod;
        if (h < 12) hourMod = ((float) h / 11) * 0.3f;
        else hourMod = 0.3f - ((((float) h - 12) / 11) * 0.3f);

        long day = CalendarTFC.getTotalDays();
        rng.setSeed(seed + day);
        final float dailyTemp = (rng.nextInt(200) - 100) / 20f;

        return getMonthAdjTemp(baseTemp, z) + dailyTemp + (hourMod * (baseTemp + dailyTemp));
    }

    private ClimateTFC() {}
}
