# this code is literally all chatgpt

import numpy as np
from PIL import Image, ImagePalette
import imageio
import math
import os

input_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "image.png")
output_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "globe.webp")
frames = 60
size = 400
ambient = 0.3
diffuse_strength = 0.9
specular_strength = 0.7
shininess = 80

# light and view directions
light_dir = np.array([0.3, 0.2, 1.0])
light_dir = light_dir / np.linalg.norm(light_dir)
view_dir = np.array([0.0, 0.0, 1.0])     # camera looking down +Z

img = Image.open(input_path).convert("RGB")
texture = np.array(img.resize((size, size)))
h, w, _ = texture.shape

r = size // 2
yy, xx = np.indices((size, size))
x = (xx - r) / r
y = (yy - r) / r

mask = (x*x + y*y) <= 1.0

z = np.zeros_like(x)
z[mask] = np.sqrt(1 - x[mask]*x[mask] - y[mask]*y[mask])

result = []

for i in range(frames):
    angle = 2 * math.pi * i / frames

    # rotate sphere around Y axis
    xr = x*np.cos(angle) - z*np.sin(angle)
    zr = x*np.sin(angle) + z*np.cos(angle)

    # backside culling (planet-style terminator)
    valid = mask

    # spherical longitude for wrapping
    lon = np.arctan2(xr, zr)

    # map to texture coordinates
    u = ((lon + math.pi) / (2 * math.pi) * (w-1))
    v = ((y + 1) / 2 * (h-1))

    # sanitize numeric
    u = np.nan_to_num(u, nan=0.0, posinf=0.0, neginf=0.0)
    v = np.nan_to_num(v, nan=0.0, posinf=0.0, neginf=0.0)

    # wrap horizontally, clamp vertically
    u = np.mod(u, w).astype(int)
    v = np.clip(v, 0, h-1).astype(int)

    # normals
    nx = x
    ny = y
    nz = z
    norm = np.sqrt(nx*nx + ny*ny + nz*nz) + 1e-9
    nx, ny, nz = nx/norm, ny/norm, nz/norm

    # phong lighting
    LdotN = np.maximum(0, nx*light_dir[0] + ny*light_dir[1] + nz*light_dir[2])
    diffuse = diffuse_strength * LdotN

    # specular
    reflect = 2*LdotN[...,None]*np.stack([nx, ny, nz], axis=-1) - light_dir
    RdotV = np.maximum(0, reflect[...,0]*view_dir[0] + reflect[...,1]*view_dir[1] + reflect[...,2]*view_dir[2])
    specular = specular_strength * (RdotV ** shininess)

    lighting = ambient + diffuse + specular
    lighting = lighting[..., None]

    # compose RGBA
    out = np.zeros((size, size, 4), dtype=np.uint8)

    tex = texture[v, u] / 255.0
    shaded = np.clip(tex * lighting, 0, 1) * 255

    # fill rgb for visible sphere
    out[valid, :3] = shaded[valid]

    # set alpha
    out[valid, 3] = 255
    out[~valid, 3] = 0

    result.append(Image.fromarray(out, mode="RGBA"))

# convert all frames (RGBA -> P) with a SINGLE shared palette
# generate palette from first frame
p0 = result[0].convert("P", palette=1)
palette = p0.getpalette()

frames_p = []

for img in result:
    p = img.convert("P")
    p.putpalette(palette)
    p.info['transparency'] = 0   # index 0 = transparent
    frames_p.append(p)

frames = result[::-1]

result[0].save(
    output_path,
    save_all=True,
    append_images=frames[1:],
    duration=40,
    lossless=True,
    loop=0
)

print("Saved:", output_path)