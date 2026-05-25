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
  "station name": { "x": 1234, "y": 5678 }
}
```