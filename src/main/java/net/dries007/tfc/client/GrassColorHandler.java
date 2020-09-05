/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.awt.*;
import java.time.Month;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;


public class GrassColorHandler
{
    public static NoiseGeneratorPerlin noiseGenerator = new NoiseGeneratorPerlin(new Random("NOISE_GRASS".hashCode()), 2);
    public static Color[] monthlyColors = new Color[12];

    static
    {
        resetColors();
    }

    public static void resetColors()
    {
        int julyCode = 0x00;
        int octoberCode = 0x00;
        int januaryCode = 0x00;
        int aprilCode = 0x00;
        try
        {
            julyCode = Integer.parseUnsignedInt(ConfigTFC.Client.GRASS_COLOR.seasonColorSummer, 16);
            octoberCode = Integer.parseUnsignedInt(ConfigTFC.Client.GRASS_COLOR.seasonColorAutumn, 16);
            januaryCode = Integer.parseUnsignedInt(ConfigTFC.Client.GRASS_COLOR.seasonColorWinter, 16);
            aprilCode = Integer.parseUnsignedInt(ConfigTFC.Client.GRASS_COLOR.seasonColorSpring, 16);
        }
        finally
        {
            monthlyColors[Month.JULY.ordinal()] = new Color(julyCode, true);
            monthlyColors[Month.OCTOBER.ordinal()] = new Color(octoberCode, true);
            monthlyColors[Month.JANUARY.ordinal()] = new Color(januaryCode, true);
            monthlyColors[Month.APRIL.ordinal()] = new Color(aprilCode, true);

            for (int i = 0; i < 12; i += 3)
            {
                monthlyColors[i + 1] = blendWithAlphas(monthlyColors[i], monthlyColors[(i + 3) % 12], 0.7);
                monthlyColors[i + 2] = blendWithAlphas(monthlyColors[i], monthlyColors[(i + 3) % 12], 0.3);
            }
        }
    }

    // Extended grass coloring
    public static int computeGrassColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
    {
        if (pos != null)
        {
            Color originalColor = new Color(computeInitialGrassColor(state, worldIn, pos, tintIndex));
            Color seasonalColor = getSeasonalColor();
            Color finalColor = originalColor;

            if (ConfigTFC.Client.GRASS_COLOR.seasonColorEnable)
            {
                finalColor = blendByAlpha(finalColor, seasonalColor);
            }

            if (ConfigTFC.Client.GRASS_COLOR.noiseEnable)
            {
                int levels = ConfigTFC.Client.GRASS_COLOR.noiseLevels;
                float scale = ConfigTFC.Client.GRASS_COLOR.noiseScale;
                double darkness = ConfigTFC.Client.GRASS_COLOR.noiseDarkness;
                double value = noiseGenerator.getValue(pos.getX() / scale, pos.getZ() / scale);
                value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1), 1) * darkness;
                finalColor = blendByWeight(Color.BLACK, finalColor, value);
            }

            return finalColor.getRGB();
        }

        return ColorizerGrass.getGrassColor(0.5, 0.5);
    }

    public static Color getSeasonalColor()
    {
        return monthlyColors[CalendarTFC.CALENDAR_TIME.getMonthOfYear().ordinal()];
    }

    public static double remap(double value, double currentLow, double currentHigh, double newLow, double newHigh)
    {
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }

    public static double curve(double start, double end, double amount, double waves)
    {
        amount = MathHelper.clamp(amount, 0, 1);
        amount = MathHelper.clamp((amount - start) / (end - start), 0, 1);

        return MathHelper.clamp(0.5 + 0.5 * MathHelper.sin(MathHelper.cos((float) (Math.PI * Math.tan(90 * amount)))) * MathHelper.cos(MathHelper.sin((float) Math.tan(amount))), 0, 1);
    }

    public static Color blendByWeight(Color c0, Color c1, double weight0)
    {
        double weight1 = 1.0d - weight0;
        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    public static Color blendWithAlphas(Color c0, Color c1, double weight0)
    {
        double weight1 = 1.0d - weight0;
        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = weight0 * c0.getAlpha() + weight1 * c1.getAlpha();

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    public static Color blendByAlpha(Color c0, Color c1)
    {
        return blendByWeight(c0, c1, 1d - (double) c1.getAlpha() / 255d);
    }

    // Default TFC grass coloring
    private static int computeInitialGrassColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
    {
        if (pos != null)
        {
            double temp = MathHelper.clamp((ClimateTFC.getMonthlyTemp(pos) + 30) / 60, 0, 1);
            double rain = MathHelper.clamp((ClimateTFC.getRainfall(pos) - 50) / 400, 0, 1);
            return ColorizerGrass.getGrassColor(temp, rain);
        }

        return ColorizerGrass.getGrassColor(0.5, 0.5);
    }
}
