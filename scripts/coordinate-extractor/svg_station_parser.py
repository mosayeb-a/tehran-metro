#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import re
import json
import sys

def extract_metro_data(svg_file):
    tree = ET.parse(svg_file)
    root = tree.getroot()
    namespaces = {'svg': 'http://www.w3.org/2000/svg'}
    
    stations = []
    lines = []
    
    print("searching for stations...")
    
    # find circle elements
    for circle in root.findall('.//svg:circle', namespaces):
        fill = circle.get('fill', '')
        cx = float(circle.get('cx', 0))
        cy = float(circle.get('cy', 0))
        r = float(circle.get('r', 0))
        
        if ('white' in fill.lower() or 'rgb(100%, 100%, 100%)' in fill) and r < 15:
            stations.append({
                'type': 'circle',
                'x': cx,
                'y': cy,
                'radius': r
            })
    
    # find path elements that form circles
    for path in root.findall('.//svg:path', namespaces):
        fill = path.get('fill', '')
        d = path.get('d', '')
        transform = path.get('transform', '')
        
        if 'white' in fill.lower() or 'rgb(100%, 100%, 100%)' in fill:
            match = re.search(r'M\s+([\d.]+)\s+([\d.]+)\s+C', d)
            if match:
                x = float(match.group(1))
                y = float(match.group(2))
                
                matrix_match = re.search(r'matrix\(([^)]+)\)', transform)
                if matrix_match:
                    parts = [float(p) for p in matrix_match.group(1).split(',')]
                    x = x * parts[0] + parts[4]
                    y = y * parts[3] + parts[5]
                
                stations.append({
                    'type': 'path',
                    'x': x,
                    'y': y,
                })
    
    print("searching for metro lines...")
    for path in root.findall('.//svg:path', namespaces):
        stroke = path.get('stroke', '')
        
        if stroke and 'rgb' in stroke:
            color_match = re.search(r'rgb\(([\d.]+)%?,\s*([\d.]+)%?,\s*([\d.]+)%?\)', stroke)
            if color_match:
                r = float(color_match.group(1))
                g = float(color_match.group(2))
                b = float(color_match.group(3))
                
                if '%' in stroke:
                    r, g, b = r/100, g/100, b/100
                
                lines.append({
                    'color': (r, g, b),
                    'color_rgb': stroke,
                    'path': d[:200]
                })
    
    # remove duplicate stations
    unique_stations = []
    for station in stations:
        is_duplicate = False
        for existing in unique_stations:
            if abs(station['x'] - existing['x']) < 5 and abs(station['y'] - existing['y']) < 5:
                is_duplicate = True
                break
        if not is_duplicate:
            unique_stations.append(station)
    
    return unique_stations, lines

def save_to_json(stations, lines, output_file):
    result = {
        'total_stations': len(stations),
        'total_lines': len(lines),
        'stations': stations,
        'lines': lines
    }
    
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(result, f, indent=2, ensure_ascii=False)
    
    print(f"\nsaved to {output_file}")
    print(f"  - {len(stations)} stations found")
    print(f"  - {len(lines)} line segments found")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage: python3 extract_metro.py <svg_file>")
        sys.exit(1)
    
    svg_file = sys.argv[1]
    
    print(f"processing {svg_file}...")
    stations, lines = extract_metro_data(svg_file)
    
    print(f"\nfound {len(stations)} stations:")
    for i, s in enumerate(stations[:20]):
        print(f"  {i+1:3d}: x={s['x']:8.1f}, y={s['y']:8.1f}")
    
    if len(stations) > 20:
        print(f"  ... and {len(stations)-20} more")
    
    print(f"\nfound {len(lines)} line segments")
    
    save_to_json(stations, lines, 'station_coords.json')