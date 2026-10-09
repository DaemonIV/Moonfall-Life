#!/usr/bin/env python3
"""
Loot weight calculator for the chest plugin.

Mirrors the Java logic:
  - each chest gets MIN_ROLLS..MAX_ROLLS rolls (uniform, inclusive)
  - each roll picks one entry with chance weight / total_weight
  - the amount is uniform between min and max

Run it, then enter one item per line:
    name weight [min max]
e.g.
    DIAMOND 10 1 64
    IRON_NUGGET 1 1 1
Press Enter on an empty line to see the results.
"""

MIN_ROLLS = 6   # r.nextInt(13 + 1 - 6) + 6  -> 6..13
MAX_ROLLS = 13
BAR_WIDTH = 30


def read_entries():
    entries = []
    print(__doc__)
    while True:
        line = input(f"item {len(entries)}> ").strip()
        if not line:
            if entries:
                return entries
            print("Enter at least one item.")
            continue
        parts = line.split()
        try:
            name = parts[0]
            weight = float(parts[1])
            lo = int(parts[2]) if len(parts) > 2 else 1
            hi = int(parts[3]) if len(parts) > 3 else lo
            if weight <= 0 or lo > hi:
                raise ValueError
        except (IndexError, ValueError):
            print("  Format: name weight [min max]  (weight > 0, min <= max)")
            continue
        entries.append((name, weight, lo, hi))


def bar(pct):
    filled = round(pct / 100 * BAR_WIDTH)
    return "#" * filled + "." * (BAR_WIDTH - filled)


def main():
    entries = read_entries()
    total = sum(e[1] for e in entries)
    roll_counts = range(MIN_ROLLS, MAX_ROLLS + 1)
    avg_rolls = sum(roll_counts) / len(roll_counts)

    rows = []
    for name, weight, lo, hi in entries:
        p = weight / total
        per_roll = p * 100
        # chance the item shows up at least once in a chest, averaged over roll counts
        in_chest = sum(1 - (1 - p) ** n for n in roll_counts) / len(roll_counts) * 100
        stacks = p * avg_rolls
        items = stacks * (lo + hi) / 2
        rows.append((name, weight, per_roll, in_chest, stacks, items))

    w = max(len(r[0]) for r in rows + [("ITEM",) * 6])
    print(f"\nTotal weight: {total:g}   Rolls per chest: {MIN_ROLLS}-{MAX_ROLLS}\n")
    print(f"{'ITEM':<{w}}  {'WEIGHT':>7}  {'PER ROLL':>8}  {'IN CHEST':>8}  {'STACKS':>6}  {'ITEMS':>7}")
    print("-" * (w + 46))
    for name, weight, per_roll, in_chest, stacks, items in rows:
        print(f"{name:<{w}}  {weight:>7g}  {per_roll:>7.2f}%  {in_chest:>7.2f}%  {stacks:>6.2f}  {items:>7.1f}")

    print("\nChance of appearing at least once in a chest:")
    for name, _, _, in_chest, _, _ in rows:
        print(f"{name:<{w}}  |{bar(in_chest)}| {in_chest:5.1f}%")

    print("\nPER ROLL = chance a single roll picks it (weight / total)")
    print("IN CHEST = chance it appears at least once in a whole chest")
    print("STACKS   = average number of stacks per chest")
    print("ITEMS    = average total item count per chest")


if __name__ == "__main__":
    main()