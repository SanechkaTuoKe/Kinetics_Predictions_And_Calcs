import sys
import math

R2_THRESHOLD = 0.95

def linear_fit(x, y):
    n = len(x)
    if n < 2: return 0, 0, 0
    sx, sy, sxy, sxx = sum(x), sum(y), sum(xi*yi for xi, yi in zip(x, y)), sum(xi**2 for xi in x)
    denom = n * sxx - sx**2
    if abs(denom) < 1e-9: return 0, 0, 0
    slope = (n * sxy - sx * sy) / denom
    intercept = (sy - slope * sx) / n
    y_mean = sy / n
    ss_tot = sum((yi - y_mean)**2 for yi in y)
    ss_res = sum((yi - (slope * xi + intercept))**2 for xi, yi in zip(x, y))
    r2 = 1 - (ss_res / ss_tot) if ss_tot > 0 else 0
    return slope, intercept, round(r2, 4)

def main():
    if len(sys.argv) < 3:
        print("ERROR: Not enough args")
        sys.exit(1)

    times = [float(x) for x in sys.argv[1].split(',') if x.strip()]
    percents = [float(x) for x in sys.argv[2].split(',') if x.strip()]

    best_model, best_k, best_t50, best_r2 = "none", 0.0, 0.0, -1.0

    models = {
        'zero':   lambda q: q,
        'first':  lambda q: -math.log(1 - q/100) if q < 100 else 0,
        'second': lambda q: 1/(1 - q/100) - 1 if q < 100 else 0
    }

    for name, transform in models.items():
        y = [transform(q) for q in percents]
        if any(math.isinf(v) or math.isnan(v) for v in y): continue
        slope, _, r2 = linear_fit(times, y)

        if r2 > best_r2 and r2 >= R2_THRESHOLD:
            best_r2 = r2
            best_model = name
            k = abs(slope)
            best_k = k
            if k > 0:
                if name == 'zero': best_t50 = 50.0 / k
                elif name == 'first': best_t50 = 0.693 / k
                else: best_t50 = 1.0 / k

    print(f"{best_model},{best_k:.6f},{best_t50:.2f},{best_r2}")

if __name__ == "__main__":
    main()