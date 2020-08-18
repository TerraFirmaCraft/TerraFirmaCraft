
    for var in TREES:
        # Logs
        rm.blockstate(('wood', 'log', var), variants={
            'axis=y': {'model': 'tfc:block/wood/log/%s' % var},
            'axis=z': {'model': 'tfc:block/wood/log/%s' % var, "x": 90},
            'axis=x': {'model': 'tfc:block/wood/log/%s' % var, "x": 90, "y": 90}}, use_default_model=False) \
            .with_item_model()

        rm.block_model(('wood', 'log', var), {
            'end': 'tfc:block/wood/top/%s' % var,
            'side': 'tfc:block/wood/log/%s' % var
        }, parent="block/cube_column")
        # Leaves
        rm.blockstate(('wood', 'leaves', var), variants={
            '': {'model': 'tfc:block/wood/leaves/%s' % var}
        }) \
            .with_block_model('tfc:block/wood/leaves/%s' % var, parent="block/leaves") \
            .with_item_model()
