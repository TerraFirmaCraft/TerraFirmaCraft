package net.dries007.tfc.world.classic;

import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * TODO: *important* store some of this (perhaps bio/non height adjusted temp?) to chunkdata
 */
public final class ClimateTFC
{
    private ClimateTFC() {}

    public static final float MAX_TEMP = 52;

    private static final int MAX_Z = 30000;
    private static final float[] Y_FACTOR_CACHE = new float[441];
    private static final float[] Z_FACTOR_CACHE = new float[MAX_Z + 1];
    private static final float[][] MONTH_TEMP_CACHE = new float[12][MAX_Z + 1];

    private static final Random rng = new Random();

    public static float getHeightAdjustedTemp(World world, BlockPos pos)
    {
        float temp = getTemp(world, pos);
        temp += getTemp(world, pos.add(1, 0, 0));
        temp += getTemp(world, pos.add(-1, 0, 0));
        temp += getTemp(world, pos.add(0, 0, 1));
        temp += getTemp(world, pos.add(0, 0, -1));
        temp /= 5;
        temp = adjustHeightToTemp(pos.getY(), temp);
        if (temp <= 0 || !world.canBlockSeeSky(pos)) return temp;
        return temp - (temp * (0.25f*(1-(world.getLight(pos)/15f))));
    }

    public static float getTemp(World world, BlockPos pos)
    {
        return getTemp(world, pos, CalenderTFC.getTotalDays(), CalenderTFC.getTotalHours(), false);
    }

    public static float getBioTemperatureHeight(World world, BlockPos pos)
    {
        float temp = 0;
        for (int i = 0; i < 12; i++)
        {
            float t = adjustHeightToTemp(pos.getY(), getTemp(world, pos, i * CalenderTFC.getDaysInMonth(), 0, true));
            temp += t;
        }
        return temp / 12;
    }

    private static float getTemp(World world, BlockPos pos, long day, long hour, boolean bio)
    {
//        int x = pos.getX();
//        int y = pos.getY();
        int z = pos.getZ();

        if (z < 0) z = -z;
        if (z > MAX_Z) z = MAX_Z;

        final float zMod = Z_FACTOR_CACHE[z];
        final float zTemp = zMod * MAX_TEMP - 20 + ((zMod - 0.5f) * 10);

        float hourMod = 0.2f;
        float dailyTemp = 0;
        if (!bio)
        {
            int h = (int) ((hour - 6) % CalenderTFC.HOURS_IN_DAY);
            if (h < 0) h += CalenderTFC.HOURS_IN_DAY;

            if (h < 12) hourMod = ((float)h / 11) * 0.3F;
            else hourMod = 0.3F - ((((float)h-12) / 11) * 0.3F);

            rng.setSeed(world.getSeed() + day);
            dailyTemp = (rng.nextInt(200)-100) / 20F;
        }

        final float rainMod = (1f - (ChunkDataTFC.getRainfall(world, pos) / 4000f))*zMod;

        final float monthTemp = MONTH_TEMP_CACHE[CalenderTFC.getSeasonFromDayOfYear(day, z > 0)][z];
        final float lastMonthTemp = MONTH_TEMP_CACHE[CalenderTFC.getSeasonFromDayOfYear(day - CalenderTFC.getDaysInMonth(), z > 0)][z];

        final float monthDelta = ((monthTemp - lastMonthTemp) * CalenderTFC.getDayOfMonthFromDayOfYear(day)) / CalenderTFC.getDaysInMonth();

        float temp = lastMonthTemp + monthDelta + dailyTemp + (hourMod*(zTemp + dailyTemp));

        if(temp >= 12) temp += (8*rainMod)*zMod;
        else temp -= (8*rainMod)*zMod;

        return temp;
    }

    public static float adjustHeightToTemp(int y, float temp)
    {
        //internationally accepted average lapse time is 6.49 K / 1000 m, for the first 11 km of the atmosphere. I suggest graphing our temperature
        //across the 110 m against 2750 m, so that gives us a change of 1.6225 / 10 blocks, which isn't /terrible/
        //Now going to attemp exonential growth. calculations but change in temperature at 17.8475 for our system, so that should be the drop at 255.
        //therefore, change should be temp - f(x), where f(x) is an exp function roughly equal to f(x) = (x^2)/ 677.966.
        //This seems to work nicely. I like this. Since creative allows players to travel above 255, I'll see if I can't code in the rest of it.
        //The upper troposhere has no lapse rate, so we'll just use that.
        //The equation looks rather complicated, but you can see it here:
        // http://www.wolframalpha.com/input/?i=%28%28%28x%5E2+%2F+677.966%29+*+%280.5%29*%28%28%28110+-+x%29+%2B+%7C110+-+x%7C%29%2F%28110+-
        // +x%29%29%29+%2B+%28%280.5%29*%28%28%28x+-+110%29+%2B+%7Cx+-+110%7C%29%2F%28x+-+110%29%29+*+x+*+0.16225%29%29+0+to+440
        if(y > WorldTypeTFC.SEALEVEL)
        {
            int i = y - WorldTypeTFC.SEALEVEL;
            if (i >= Y_FACTOR_CACHE.length) {
                i = Y_FACTOR_CACHE.length - 1;
            }
            temp -= Y_FACTOR_CACHE[i];
        }
        return temp;
    }

    static
    {
        //internationally accepted average lapse time is 6.49 K / 1000 m, for the first 11 km of the atmosphere. I suggest graphing our temperature
        //across the 110 m against 2750 m, so that gives us a change of 1.6225 / 10 blocks, which isn't /terrible/
        //Now going to attemp exonential growth. calculations but change in temperature at 17.8475 for our system, so that should be the drop at 255.
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

        for (int zCoord = 0; zCoord < MAX_Z + 1; ++zCoord)
        {
            float factor = (MAX_Z- (float) zCoord) / MAX_Z;
            Z_FACTOR_CACHE[zCoord] = factor;

            for (int month = 0; month < 12; ++month)
            {
                final float MAXTEMP = 35F;

                double angle = factor * (Math.PI / 2);
                double latitudeFactor = Math.cos(angle);

                switch(month)
                {
                    case 10:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP-13.5*latitudeFactor - (latitudeFactor*55)); break;
                    case 9:
                    case 11:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -12.5*latitudeFactor- (latitudeFactor*53)); break;
                    case 0:
                    case 8:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -10*latitudeFactor- (latitudeFactor*46)); break;
                    case 1:
                    case 7:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -7.5*latitudeFactor- (latitudeFactor*40)); break;
                    case 2:
                    case 6:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP - 5*latitudeFactor- (latitudeFactor*33)); break;
                    case 3:
                    case 5:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -2.5*latitudeFactor- (latitudeFactor*27)); break;
                    case 4:
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -1.5*latitudeFactor- (latitudeFactor*27)); break;
                }
            }
        }
    }
}
