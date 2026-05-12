import sys

CONFIGS = {
    'CLX': {  # Хлоргексидин
        'MW': 505.5,           # г/моль
        'CONC_INPUT': 0.1,     # мг/мл
        'CAL_SLOPE': 17257.3404,
        'CAL_OFFSET': 0.00716,
        'DILUTION': 1.0,
        'VOL_SAMPLE': 6.0
    },
    'FLC': {  # Фурацилин
        'MW': 290.0,           # г/моль
        'CONC_INPUT': 0.1,     # мг/мл
        'CAL_SLOPE': 15432.12,  # Примерные значения (замени на свои)
        'CAL_OFFSET': 0.00523,
        'DILUTION': 1.0,
        'VOL_SAMPLE': 6.0
    }
}

def parse_list(s):
    return [float(x) for x in s.split(',') if x.strip()]

def main():
    if len(sys.argv) < 5:
        print("ERROR: Not enough args. Usage: python calc_release.py <SUBJECT> <ads_data> <rel_data>")
        sys.exit(1)

    subject = sys.argv[1].upper()


    if subject not in CONFIGS:
        print(f"ERROR: Unknown subject '{subject}'. Available: {list(CONFIGS.keys())}")
        sys.exit(1)

    config = CONFIGS[subject]
    MW = config['MW']
    CONC_INPUT = config['CONC_INPUT']
    CAL_SLOPE = config['CAL_SLOPE']
    CAL_OFFSET = config['CAL_OFFSET']
    DILUTION = config['DILUTION']
    VOL_SAMPLE = config['VOL_SAMPLE']

    ads_data = parse_list(sys.argv[2])
    rel_data = parse_list(sys.argv[3])


    c_start = (CONC_INPUT * 10) / MW
    ads_results = []
    for a in ads_data:
        c_spec = (a / CAL_SLOPE) * DILUTION
        c_ads = c_start - c_spec
        p_ads = (c_ads / c_start) * 100 if c_start != 0 else 0
        ads_results.append(round(p_ads, 2))

    c0_vals = [(c_start - (a / CAL_SLOPE) * DILUTION) for a in ads_data[:3]]
    c0 = sum(c0_vals) / len(c0_vals) if c0_vals else 0.001

    rel_results = []
    sum_c = 0.0
    for a in rel_data:
        c_rel = (a + CAL_OFFSET) / CAL_SLOPE
        c_vol = c_rel * VOL_SAMPLE
        sum_c += c_vol
        p_rel = (sum_c / c0) * 100 if c0 != 0 else 0
        rel_results.append(round(p_rel, 2))

    print(f"C0:{c0:.6f}")
    print("ADS:" + ",".join(map(str, ads_results)))
    print("REL:" + ",".join(map(str, rel_results)))

if __name__ == "__main__":
    main()