from mcresources import ResourceManager

from constants import *

def generate(rm: ResourceManager):
    # Item Groups
    rm.lang('itemGroup.tfc.rock.blocks', 'TFC Rock Blocks')
    rm.lang('itemGroup.tfc.metals', 'TFC Metals')
    rm.lang('itemGroup.tfc.gems', 'TFC Gems')
    # Enums
    ## Metal Tiers
    rm.lang(dict((
            'tfc.enum.tier.tier_%s' % tier,
            'Tier %s' % tier.upper()
        ) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')))
    ## Heat
    rm.lang('tfc.enum.heat.warming', 'Warming')
    rm.lang('tfc.enum.heat.hot', 'Hot')
    rm.lang('tfc.enum.heat.very_hot', 'Very Hot')
    rm.lang('tfc.enum.heat.faint_red', 'Faint Red')
    rm.lang('tfc.enum.heat.dark_red', 'Dark Red')
    rm.lang('tfc.enum.heat.bright_red', 'Bright Red')
    rm.lang('tfc.enum.heat.orange', 'Orange')
    rm.lang('tfc.enum.heat.yellow', 'Yellow')
    rm.lang('tfc.enum.heat.yellow_white', 'Yellow White')
    rm.lang('tfc.enum.heat.white', 'White')
    rm.lang('tfc.enum.heat.brilliant_white', 'Brilliant White')
    # Tooltips
    rm.lang('tfc.tooltip.metal', '§fMetal:§7 %s')
    rm.lang('tfc.tooltip.units', '%d units')
    rm.lang('tfc.tooltip.forging', '§f - Can Work')
    rm.lang('tfc.tooltip.welding', '§f - Can Weld')
    ## Commands
    rm.lang('tfc.command.heat', 'Held item heat set to %s')
    rm.lang('tfc.command.clear_world_done', 'Cleared.')
    ## Calendar Stuff
    rm.lang('tfc.tooltip.calendar', 'Calendar')
    rm.lang('tfc.tooltip.season', 'Season : %s')
    rm.lang('tfc.tooltip.day', 'Day : %s')
    rm.lang('tfc.tooltip.date', 'Date : %s')
    rm.lang('tfc.tooltip.calendar_full_date', '%s %s %02d, %04d')
    rm.lang('tfc.tooltip.debug_times', 'PT: %d | CT: %d')