### Warning
This update is expected to break any addons which add trees, including ArborFirmaCraft. If all goes well, I will have time tomorrow to release an AFC version that at a minimum won't crash, and at a maximum will take full advantage of the new rendering behavior for leaf blocks.

-Therighthon

### Changes
- Reworked how leaf blocks render when fancy graphics are enabled
  - Rotate between 4 different seasonal models: bare branches, blooming, dense leaves, and sparse leaves
  - Conifers, and deciduous trees grown in the wet tropics only rotate between dense leaves and blooming
  - Deciduous leaf colors now vary by time of year, with brighter greens in spring
  - Color changing/autumn colors are no longer tied to elevation in any way
  - Borders between areas with different seasons are less harsh (for example, an area that is just barely cold enough to lose leaves in winter)
- Improved the texture for flower cuttings

### Fixes
- Fixed leaf blocks etc. requiring a restart to update between fast/fancy graphics