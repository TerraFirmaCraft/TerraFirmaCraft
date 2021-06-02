from argparse import ArgumentParser
from typing import NamedTuple, Tuple, List, TypeVar

from PIL import Image

Point = NamedTuple('Point', x=int, y=int, r=int, g=int, b=int)
Number = TypeVar('Number', int, float)


def main():
    parser = ArgumentParser()
    parser.add_argument('spec', type=str, default='', help='Fixed points, in the form x0,y0,c0;x1,y1,c1...')
    parser.add_argument('--size', type=str, default='256x256', help='The size of image to generate, in WxH format')
    parser.add_argument('--out', type=str, default='result.png', help='Output file')

    args = parser.parse_args()
    print('Running with', args)

    try:
        w, h = map(int, args.size.split('x'))
    except:
        print('Invalid WxH specification: ' + args.size)
        return

    try:
        points = []
        steps = args.spec.split(';')
        for step in steps:
            x, y, c = step.split(',')
            points.append((int(x), int(y), c))
    except Exception as e:
        print('Invalid spec: ' + args.spec)
        print(e)
        return

    create(args.out, w, h, *points)


def create(file: str, w: int, h: int, *points: Tuple[int, int, str]):
    def point(p: Tuple[int, int, str]):
        x, y, c = p
        if not (0 <= x < w and 0 <= y < h):
            raise ValueError('Out of bounds: ' + str(p))
        if c.startswith('#'):
            c = c[1:]
        elif c.startswith('0x'):
            c = c[2:]
        c = int(c, base=16)
        return Point(x, y, (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF)

    points = [point(p) for p in points]

    image = Image.new('RGB', (w, h))
    pixels = image.load()

    for x in range(w):
        for y in range(h):
            c = blend(points, x, y)
            pixels[x, y] = c

    image.save(file)


def blend(points: List[Point], x: int, y: int) -> Tuple[int, int, int]:
    ratios = [1] * len(points)
    for i1, p1 in enumerate(points):
        for i2, p2 in enumerate(points):
            if i1 != i2:
                _, d2 = project(p1, p2, x, y)
                ratios[i1] *= clamp(d2, 0, 1)

    total = sum(ratios)
    for i, t in enumerate(ratios):
        ratios[i] = t / total
    return mix(points, ratios)


def mix(points: List[Point], ratios: List[float]) -> Tuple[int, int, int]:
    r = g = b = 0
    for p, ra in zip(points, ratios):
        r += p.r * ra
        g += p.g * ra
        b += p.b * ra
    return clamp(round(r), 0, 255), clamp(round(g), 0, 255), clamp(round(b), 0, 255)


def project(a: Point, b: Point, x: int, y: int) -> Tuple[float, float]:
    k2 = b.x * b.x - b.x * a.x + b.y * b.y - b.y * a.y
    k1 = a.x * a.x - b.x * a.x + a.y * a.y - b.y * a.y
    ab2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
    kcom = (x * (a.x - b.x) + y * (a.y - b.y))
    d1 = (k1 - kcom) / ab2
    d2 = (k2 + kcom) / ab2
    return d1, d2


def clamp(t: Number, lower: Number, upper: Number) -> Number:
    return lower if t < lower else (upper if t > upper else t)


if __name__ == '__main__':
    main()
