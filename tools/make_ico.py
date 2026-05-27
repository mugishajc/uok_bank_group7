"""
Converts multiple PNG files into a single multi-size .ico file.
Uses only Python stdlib — no Pillow needed.
Each PNG is embedded as-is (PNG-compressed ICO, supported on Windows Vista+).
"""
import struct, os, sys

def make_ico(png_paths, out_path):
    images = []
    for p in png_paths:
        with open(p, 'rb') as f:
            data = f.read()
        # Read width/height from PNG IHDR chunk (bytes 16-23)
        w = struct.unpack('>I', data[16:20])[0]
        h = struct.unpack('>I', data[20:24])[0]
        images.append((w, h, data))

    count  = len(images)
    offset = 6 + count * 16   # header + directory

    header = struct.pack('<HHH', 0, 1, count)
    dirs   = b''
    blobs  = b''

    for (w, h, data) in images:
        size      = len(data)
        # width/height: 0 means 256
        bw = 0 if w == 256 else w
        bh = 0 if h == 256 else h
        dirs  += struct.pack('<BBBBHHII', bw, bh, 0, 0, 1, 32, size, offset)
        offset += size
        blobs += data

    with open(out_path, 'wb') as f:
        f.write(header + dirs + blobs)
    print(f"Created {out_path}  ({count} sizes: {[i[0] for i in images]})")

if __name__ == '__main__':
    base = os.path.dirname(os.path.abspath(__file__))
    sizes = [16, 32, 48, 64, 128, 256]
    pngs  = [os.path.join(base, 'icons', f'icon_{s}.png') for s in sizes]
    make_ico(pngs, os.path.join(base, '..', 'icon.ico'))
