#!/usr/bin/env bash
# Usage: ./simple-interest.sh <principal> <rate_percent> <time_years>
# Example: ./simple-interest.sh 1000 5 2  -> 100.00
set -e
if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <principal> <rate%> <time-years>" >&2
  exit 1
fi
P="$1"; R="$2"; T="$3"
# Simple Interest = (P * R * T) / 100
SI=$(awk -v p="$P" -v r="$R" -v t="$T" 'BEGIN { printf "%.2f", (p*r*t)/100 }')
echo "$SI"
