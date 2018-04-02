package net.dries007.tfc.world.classic;

public final class Climate
{
    private Climate() {}

    private static final float[] Y_FACTOR_CACHE = new float[441];
    private static final float[] Z_FACTOR_CACHE = new float[30001];
    private static final float[][] MONTH_TEMP_CACHE = new float[12][30001];

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

        for (int y = 0; y < Y_FACTOR_CACHE.length; y += 1) {
            // temp = temp - (ySq / 677.966f) * (((110.01f - y) + Math.abs(110.01f - y)) / (2 * (110.01f - y)));
            // temp -= (0.16225 * y * (((y - 110.01f) + Math.abs(y - 110.01f)) / (2 * (y - 110.01f))));

            // float ySq = y * y;
            // float diff = 110.01f - y;
            // float factor = (ySq / 677.966f) * ((diff + Math.abs(diff)) / (2 * diff))
            // 		+ 0.16225f * y * ((diff - Math.abs(diff)) / (2 * diff));

            //more optimization: using an if should be more efficient (and simpler)
            float factor;
            if (y < 110) {
                // diff > 0
                factor = y * y / 677.966f;  // 17.85 for y=110
            } else {
                // diff <= 0
                factor = 0.16225f * y;  // 17.85 for y=110
            }
            Y_FACTOR_CACHE[y] = factor;
        }

        for(int zCoord = 0; zCoord < 30000 + 1; ++zCoord)
        {
            float factor = (30000- (float) zCoord) / 30000;
            Z_FACTOR_CACHE[zCoord] = factor;

            for(int month = 0; month < 12; ++month)
            {
                final float MAXTEMP = 35F;

                double angle = factor * (Math.PI / 2);
                double latitudeFactor = Math.cos(angle);

                switch(month)
                {
                    case 10:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP-13.5*latitudeFactor - (latitudeFactor*55));
                        break;
                    }
                    case 9:
                    case 11:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -12.5*latitudeFactor- (latitudeFactor*53));
                        break;
                    }
                    case 0:
                    case 8:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -10*latitudeFactor- (latitudeFactor*46));
                        break;
                    }
                    case 1:
                    case 7:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -7.5*latitudeFactor- (latitudeFactor*40));
                        break;
                    }
                    case 2:
                    case 6:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP - 5*latitudeFactor- (latitudeFactor*33));
                        break;
                    }
                    case 3:
                    case 5:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -2.5*latitudeFactor- (latitudeFactor*27));
                        break;
                    }
                    case 4:
                    {
                        MONTH_TEMP_CACHE[month][zCoord] = (float)(MAXTEMP -1.5*latitudeFactor- (latitudeFactor*27));
                        break;
                    }
                }
            }
        }
    }
}
