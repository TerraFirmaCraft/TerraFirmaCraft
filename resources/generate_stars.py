import json
import math


def main():
    with open('bsc5p_radec_min.json', 'r', encoding='utf-8') as f:
        data = json.loads(f.read())

    zero_point_luminosity = 3.0128e28
    sun_luminosity = 3.828e26

    output = []
    for star in data:
        if 'K' in star:  # Excludes some non-stars (clusters, galaxies, etc.)
            if star['N'] is not None:  # Excludes stars without distance or luminosity information
                distance_parsec = star['p']
                luminosity = star['N']  # Astronomy data, so in terms of Lo = Luminosity of the sun
                magnitude = -2.5 * math.log10(luminosity * sun_luminosity / zero_point_luminosity)
                apparent_magnitude = magnitude - 5 + 5 * math.log10(distance_parsec)

                # Exclude the stars with the lowest calculated magnitude
                # This dataset has ~9500 visible-to-naked-eye stars, by default vanilla has 1500
                # This threshold ends up with ~5000
                if apparent_magnitude < 6:
                    right_ascension = star['r']
                    declination = star['d']
                    color = star['K']
                    color = '0x' + to_hex(color['r']) + to_hex(color['g']) + to_hex(color['b'])
                    output.append({
                        'z': json_f32(0.5 * math.pi - declination),
                        'a': json_f32(right_ascension),
                        'c': int(color, 16),
                        'm': json_f32(apparent_magnitude)
                    })

    print('Total =', len(data), 'Used =', len(output))
    with open('../src/main/resources/assets/tfc/stars.json', 'w', encoding='utf-8') as f:
        json.dump(output, f, separators=(',', ':'))


def to_hex(c: float):
    return hex(round(c * 255))[2:].zfill(2)


def clamp(x: float, lo: float, hi: float): return min(hi, max(lo, x))
def lerp(t: float, lo: float, hi: float): return lo + t * (hi - lo)
def lerp_inv(x: float, lo: float, hi: float): return (x - lo) / (hi - lo)
def clamp_map(x: float, lo0: float, hi0: float, lo1: float, hi1: float): return lerp(clamp(lerp_inv(x, lo0, hi0), 0, 1), lo1, hi1)
def json_f32(x: float) -> float: return round(x, 5)


if __name__ == '__main__':
    main()
