```markdown
# tehran metro coordinates

extract station coordinates from pdf and prepare for android

## setup

```bash
sudo apt install poppler-utils python3
```

## steps

### 1. convert pdf to svg

```bash
pdftocairo -svg tehran-metro-map.pdf metro-map.svg
```

### 2. extract stations

```bash
python3 svg_station_parser.py metro-map.svg
```

this creates `station_coords.json` with all detected station positions

### 3. map coordinates to station names

open `mapper.html` using a local server:

```bash
python3 -m http.server 8000
```

then visit `http://localhost:8000/mapper.html`

click red dots and match them with station names from the right list, then click "map selected"

## output

click "export" to generate `map_station_coords.json`

the output format:

```json
{
  "station name": {
    "x": 1234,
    "y": 5678
  }
}
```

to change svg text color to white, replace `fill="rgb(13.729858%, 12.159729%, 12.548828%)"` or
`fill="#231f1f"` with `fill="white"`

## generate transparent png with white text

if you prefer a png instead of svg, convert with transparency and white text:

```bash
# convert pdf to svg first
pdftocairo -svg tehran-metro-map.pdf master-map.svg

# make all text white
sed -i 's/fill="rgb(13\.729858%, 12\.159729%, 12\.548828%)"/fill="white"/g' master-map.svg
sed -i 's/fill="#231f1f"/fill="white"/g' master-map.svg

# generate transparent png at different resolutions
inkscape master-map.svg --export-png=map-2800px.png --export-width=2800 --export-background-opacity=0
inkscape master-map.svg --export-png=map-3000px.png --export-width=3000 --export-background-opacity=0
inkscape master-map.svg --export-png=map-3200px.png --export-width=3200 --export-background-opacity=0
inkscape master-map.svg --export-png=map-3500px.png --export-width=3500 --export-background-opacity=0

# compress pngs (optional)
pngquant --quality=85-95 map-*.png
```