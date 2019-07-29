/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.fluids;

public class FluidThirstConfig
{
	
	private final int thirstAmount;
	
	public FluidThirstConfig(int thirstAmount)
	{
		this.thirstAmount = thirstAmount;
	}
	
	public int getThirstAmount()
	{
		return this.thirstAmount;
	}
	
}
