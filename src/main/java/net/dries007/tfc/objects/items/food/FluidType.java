/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.food;

public enum FluidType
{

	NONE(0), FRESH(1, 50), SALTY(2), HOT(3);

	private final int id;
	private final int thirstAmount;

	private FluidType(int id, int thirstAmount)
	{
		this.id = id;
		this.thirstAmount = thirstAmount;
	}

	private FluidType(int id)
	{
		this(id, 0);
	}

	public int getID()
	{
		return this.id;
	}

	public int getThirst()
	{
		return this.thirstAmount;
	}

	public static FluidType fromId(int id)
	{
		for (final FluidType type : FluidType.values())
		{
			if (type.id == id)
			{
				return type;
			}
		}
		return null;
	}

}
