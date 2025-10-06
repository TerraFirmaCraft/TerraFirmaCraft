### Changes
 - Changes to snow accumulation
   - Made snow accumulation more efficient
   - Added config for how much snow should "catch up" when loading unloaded chunks. By default, this value is 64, or 1/4 of the chunk, but it can be increased up to 256 to fully cover chunks in winter
 - Fixed shorter ryegrass using the bluegrass model
 - Re-scaled wind speeds to more believable levels
 - Minor changes in internals and API

### Bug Fixes
- Fixed shorter ryegrass using the bluegrass model
- Fixed typo in the name of a butterfly
- Fixed jellyfish rendering causing server crashes (closes #3159)
- Fixed firebox sound playing while not lit
- Fixed fruit preserves
- Fixed some meats missing tags (closes #3200)