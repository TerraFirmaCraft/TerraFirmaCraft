package net.dries007.tfc.client;

import java.awt.Color;
import java.util.Random;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.NoiseGeneratorPerlin;


public class GrassColorHandler 
{
    static NoiseGeneratorPerlin NOISE_GRASS = new NoiseGeneratorPerlin(new Random("NOISE_GRASS".hashCode()), 2);
    
	public static Color january, february, march, april, may,
						june, july, august, september, october,
						november, december;

    static
    {
    	resetColors();
    }
    
    public static void resetColors()
    {
    	july = parseColor(ConfigTFC.Client.GRASS_COLOR.seasonColorSummer);
    	october = parseColor(ConfigTFC.Client.GRASS_COLOR.seasonColorAutumn);
    	january = parseColor(ConfigTFC.Client.GRASS_COLOR.seasonColorWinter);
    	april = parseColor(ConfigTFC.Client.GRASS_COLOR.seasonColorSpring);
    	june = blendWithAlphas(april, july, 0.35, 0.65);
    	september = blendWithAlphas(july, october, 0.35, 0.65);
    	december = blendWithAlphas(october, january, 0.35, 0.65);
    	march = blendWithAlphas(january, april, 0.35, 0.65);
    	august = blendWithAlphas(july, october, 0.65, 0.35);
    	november = blendWithAlphas(october, january, 0.65, 0.35);
    	february = blendWithAlphas(january, april, 0.65, 0.35);
    	may = blendWithAlphas(april, july, 0.65, 0.35);
    }
    
    
    public static Color parseColor(String s)
    {
    	String[] parts = s.split(",");
    	int red = Integer.parseInt(parts[0].trim());
    	int blue = Integer.parseInt(parts[1].trim());
    	int green = Integer.parseInt(parts[2].trim());
    	int alpha = Integer.parseInt(parts[3].trim());
    	
    	return new Color(red, blue, green, alpha);
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
    
    // Extended grass coloring
    public static int computeGrassColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
    {
        if (pos != null)
        {
            int prev = computeInitialGrassColor(state, worldIn, pos, tintIndex);
            Color prevCol = new Color(prev);
            Color black = Color.BLACK;
            Color c = getSeasonalColor();
            Color finalColor = prevCol;
            double rand = Math.random() / 10d;
            
            if(ConfigTFC.Client.GRASS_COLOR.seasonColorEnable) 
            {
            	finalColor = blendByAlpha(finalColor, c);
            }
            
            if(ConfigTFC.Client.GRASS_COLOR.noiseEnable)
            {
            	int levels = ConfigTFC.Client.GRASS_COLOR.noiseLevels;
            	float scale = ConfigTFC.Client.GRASS_COLOR.noiseScale;
            	double darkness = ConfigTFC.Client.GRASS_COLOR.noiseDarkness;
            	double value = ((NOISE_GRASS.getValue(pos.getX() / scale, pos.getZ() / scale)));
                value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1), 1) * darkness;
            	finalColor = blendByWeight(black, finalColor, value, 1-value);
            }
            
            return finalColor.getRGB();
        }
        
        return ColorizerGrass.getGrassColor(0.5, 0.5);
    }
    
    public static Color getSeasonalColor()
    {
        switch(CalendarTFC.CALENDAR_TIME.getMonthOfYear())
        {
	        case JANUARY: return january;
	        case FEBRUARY: return february;
	        case MARCH: return march;
	        case APRIL: return april;
	        case MAY: return may;
	        case JUNE: return june;
	        case JULY: return july;
	        case AUGUST: return august;
	        case SEPTEMBER: return september;
	        case OCTOBER: return october;
	        case NOVEMBER: return november;
	        case DECEMBER: return december;
	        default: return july;
        }
    }
    
    public static double remap(double value, double currentLow, double currentHigh, double newLow, double newHigh)
    {
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }
	
	public static double clamp(double value, double min, double max)
	{
        return Math.max(min, Math.min(value, max));
    }
    
    public static double curve(double start, double end, double amount, double waves)
    {
        amount = clamp(amount, 0, 1);
        amount = clamp((amount - start) / (end - start), 0, 1);
        
        return clamp(0.5 + 0.5 * Math.sin(Math.cos(Math.PI * Math.tan(90 * amount))) * Math.cos(Math.sin(Math.tan(amount))), 0, 1);
    }
	
	public static Color blendByWeight(Color c0, Color c1, double weight0, double weight1)
	{
	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	public static Color blendWithAlphas(Color c0, Color c1, double weight0, double weight1)
	{
	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = weight0 * c0.getAlpha() + weight1 * c1.getAlpha();

	    return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	public static Color blendByAlpha(Color c0, Color c1)
	{
	    double totalAlpha = c0.getAlpha() + c1.getAlpha();
	    double weight0 = c0.getAlpha() / totalAlpha;
	    double weight1 = c1.getAlpha() / totalAlpha;

	    return blendByWeight(c0, c1, weight0, weight1);
	}
}
