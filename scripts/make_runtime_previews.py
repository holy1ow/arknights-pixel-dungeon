"""Create small viewing copies of actual verifyAmiyaEffects framebuffer recordings.

Usage: python scripts/make_runtime_previews.py NEW_OUTPUT_DIRECTORY
Requires Pillow. Does not change game artwork or synthesize effect frames.
"""
import csv
import hashlib
import json
from pathlib import Path
import shutil
import sys
import zipfile
from PIL import Image, ImageDraw

root = Path(__file__).resolve().parents[1]
recordings = root / 'desktop/build/release-0.0.2-qa/effects'
out = Path(sys.argv[1]).resolve()
out.mkdir(parents=True, exist_ok=False)
docs = root / 'docs/images/0.0.2'
docs.mkdir(parents=True, exist_ok=True)
rows = list(csv.DictReader((recordings / 'frames.csv').open()))
scenarios = sorted({r['scenario'] for r in rows})
manifest = []
sheet = Image.new('RGB', (960, 230 * 7), '#202028')
draw = ImageDraw.Draw(sheet)
for index, name in enumerate(scenarios):
    sample = next(r for r in rows if r['scenario'] == name)
    center = (float(sample['heroX']) + float(sample['targetX'])) / 2
    box = (int(center)-240, 195, int(center)+240, 395)
    paths = sorted((recordings / name).glob('*.png'))
    frames = [Image.open(p).convert('RGB').crop(box) for p in paths]
    durations = [33, 34, 33] * (len(frames)//3+1)
    target = out / (name + '.png')
    frames[0].save(target, save_all=True, append_images=frames[1:], duration=durations[:len(frames)], loop=0)
    if name in ['caster-right', 'guard-right', 'medic-right', 'medic-recovery']:
        frames[0].save(docs / (name+'.gif'), save_all=True, append_images=frames[1:], duration=([30,40,30]*(len(frames)//3+1))[:len(frames)], loop=0)
    frame_index = 20 if name == 'medic-recovery' else 12
    x, y = (index%2)*480, (index//2)*230
    draw.text((x+8,y+6),name,fill='white')
    sheet.paste(frames[frame_index],(x,y+25))
    manifest.append({'scenario':name, 'crop':box, 'frames':len(paths), 'sha256':hashlib.sha256(target.read_bytes()).hexdigest(), 'originalFrames':[{'path':name+'/'+p.name,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in paths]})
for name in ['frames.csv','result.txt']:
    shutil.copy2(recordings/name,out/name)
(out/'provenance.json').write_text(json.dumps({'kind':'Actual GameScene/OpenGL recordings, cropped viewing copies; APNG is lossless; README GIF uses a viewing palette. No offline effect compositing. Automated isolated scenarios, not manual playthrough.','scenarios':manifest},indent=2)+'\n',encoding='utf-8')
(out/'index.html').write_text('<!doctype html><meta charset="utf-8"><title>v0.0.2 runtime previews</title><style>body{background:#202028;color:white;font:16px sans-serif}section{display:inline-block;margin:12px}img{image-rendering:pixelated}</style><h1>v0.0.2 actual renderer recordings</h1><p>Isolated automated scenarios. Cropped lossless APNG; not manual playthrough. See frames.csv and provenance.json.</p>'+''.join('<section><h2>'+name+'</h2><img src="'+name+'.png"></section>' for name in scenarios),encoding='utf-8')
archive = out.parent/'runtime-previews.zip'
assert not archive.exists()
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
    for p in sorted(out.iterdir()):z.write(p,p.name)
with zipfile.ZipFile(archive) as z:assert z.testzip() is None
sheet.save(out.parent/'preview-contact-sheet.png')
print(json.dumps({'scenarios':len(scenarios),'bytes':archive.stat().st_size,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest()}))
