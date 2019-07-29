/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

public enum Gem
{
	AGATE, AMETHYST, BERYL, DIAMOND, EMERALD, GARNET, JADE, JASPER, OPAL, RUBY, SAPPHIRE, TOPAZ, TOURMALINE;

	public enum Grade
	{
		NORMAL, FLAWED, FLAWLESS, CHIPPED, EXQUISITE;

		public static Grade fromMeta(int meta)
		{
			return values()[meta];
		}

		public int getMeta()
		{
			return this.ordinal();
		}
	}
}
