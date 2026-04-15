#!/bin/zsh

# Resize screenshots to App Store required dimensions (1290 x 2796)

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCREENSHOTS_DIR="$SCRIPT_DIR/Screenshots_AppStore"
OUTPUT_DIR="$SCRIPT_DIR/Screenshots_Resized"

mkdir -p "$OUTPUT_DIR"

echo "Resizing screenshots to 1284 x 2778..."
echo ""

for file in "$SCREENSHOTS_DIR"/*.png; do
    if [ -f "$file" ]; then
        filename=$(basename "$file")
        output_file="$OUTPUT_DIR/$filename"

        cp "$file" "$output_file"
        sips -z 2778 1284 "$output_file" > /dev/null 2>&1

        echo "✓ Resized: $filename"
    fi
done

echo ""
echo "Resized screenshots saved to: $OUTPUT_DIR"
echo ""
echo "Verifying dimensions..."
for file in "$OUTPUT_DIR"/*.png; do
    dims=$(sips -g pixelWidth -g pixelHeight "$file" | awk '/pixel/ {printf $2" "}')
    echo "  $(basename "$file"): $dims"
done
