import os
import shutil
from typing import Tuple

import gradients

SRC = '../src/main/resources/assets/tfc/textures/colormap/'


def main():
    make('sky.png', (0, 0, '#6697E7'), (255, 0, '#7ca5f7'), (0, 255, '#dec797'), (255, 255, '#ABAAE3'), (64, 64, '#6597CE'))
    make('fog.png', (0, 0, '#8FB1E9'), (255, 0, '#b4a1e7'), (0, 255, '#EDCC97'), (255, 255, '#d7d6f6'), (64, 64, '#b0d2f7'))

    make('water.png', (0, 0, '#4882C9'), (255, 0, '#273968'))

    make('grass.png', (0, 0, '#217C3E'), (170, 0, '#557d51'), (230, 40, '#6D997A'), (47, 123, '#5D9C52'), (25, 173, '#FADA5A'), (50, 255, '#FBD259'))
    make('tall_grass.png', (0, 0, '#218239'), (170, 0, '#56875A'), (230, 40, '#729985'), (47, 113, '#39AD54'), (25, 163, '#FFE56C'), (50, 255, '#F7E656'))

    make('foliage.png', (0, 0, '#1D6233'), (255, 0, '57776D'), (0, 255, '#8EA825'), (255, 255, '#9C8733'))
    make('foliage_fall.png',
         (0, 0, '#68823E'), (60, 0, '#fbf236'), (120, 0, '#f06613'), (195, 0, '#e8201c'), (255, 0, '#7C592B'),
         (0, 127, '#68823E'), (60, 127, '#fbf236'), (160, 127, '#f18e00'), (200, 127, '#f06613'), (255, 127, '#7C592B'),
         (0, 255, '#68823E'), (90, 255, '#fbf236'), (190, 255, '#b1a145'), (230, 255, '#7C592B'))
    make('foliage_winter.png', (97, 50, '#7C592B'), (97, 255, '#7C592B'), (92, 50, '#c2ab35'), (85, 50, '#1D6233'), (85, 255, '#956d37'))

    copy('water.png', 'water_fog.png')

    print('Done')


def make(image: str, *points: Tuple[int, int, str]):
    gradients.create(os.path.join(SRC, image), 256, 256, *points)


def copy(src: str, dest: str):
    shutil.copy(os.path.join(SRC, src), os.path.join(SRC, dest))


if __name__ == '__main__':
    main()
