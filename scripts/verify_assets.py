"""Check editable sources, exports, atlas cells and optional packaged runtime assets.

Run with Python 3 + Pillow: python scripts/verify_assets.py [path/to/app-image]
Only reads project files. It never installs or alters artwork.
"""
import hashlib
import json
from pathlib import Path
import sys
import zipfile
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
def sha(data): return hashlib.sha256(data).hexdigest()
manifest = json.loads((ROOT / 'art/manifest.json').read_text(encoding='utf-8'))
assert manifest['version'] == '0.0.2'
assert manifest['amiyaMedicalSelection'] == 'white-staff-blue-tip'
assert manifest['amiyaCandidateInstallation'] is False
cells = 0
for a in manifest['assets']:
    assert sha((ROOT/a['source']).read_bytes()) == a['sourceSha256'], a['id']
    assert sha((ROOT/a['export']).read_bytes()) == a['sha256'], a['id']
    for field in ['animationSource', 'animationMetadata']:
        if field in a:
            assert sha((ROOT/a[field]).read_bytes()) == a[field+'Sha256'], a['id']
    if 'runtime' in a:
        assert sha((ROOT/a['runtime']).read_bytes()) == a['sha256'], a['id']
    else:
        atlas_path = ROOT/a['runtimeAtlas']
        assert sha(atlas_path.read_bytes()) == a['runtimeAtlasSha256'], a['id']
        with Image.open(atlas_path) as im, Image.open(ROOT/a['export']) as cell:
            w,h = a['cellSize']; cols=im.width//w
            x=(a['slot']%cols)*w; y=(a['slot']//cols)*h
            assert cell.size == (w,h), a['id']
            assert im.convert('RGBA').crop((x,y,x+w,y+h)).tobytes() == cell.convert('RGBA').tobytes(), a['id']
        cells += 1
amiya = [a['sha256'] for a in manifest['assets'] if a['character']=='amiya' and a['kind']=='sprite']
assert len(amiya)==3 and len(set(amiya))==3, 'Expected three distinct approved Amiya forms'
effects = [a for a in manifest['assets'] if a['character']=='amiya' and a['kind']=='effect' and a['id'].startswith('effect:amiya_')]
assert {a['id'].split('_',1)[1] for a in effects} == {'arts-projectile','arts-impact','sword-arc','sword-impact','staff-release','heal-return'}
for a in effects:
    meta=json.loads((ROOT/a['animationMetadata']).read_text(encoding='utf-8'))
    assert [f['duration'] for f in meta['frames']]==a['durationsMs'], a['id']
    assert Image.open(ROOT/a['runtime']).size == (96,24), a['id']
for a in manifest['assets']:
    if a['character']=='amiya':
        assert a['reviewStatus']=='approved' and a['implementationStatus']=='installed', a['id']
for a in manifest['tintReferences']:
    assert a['bakedPNGInstalled'] is False
    assert sha((ROOT/a['source']).read_bytes()) == a['sourceSha256']
    assert sha((ROOT/a['export']).read_bytes()) == a['sha256']
    assert sha((ROOT/a['runtimeAtlas']).read_bytes()) == a['runtimeAtlasSha256']
result = {'artItems':len(manifest['assets']), 'atlasCells':cells, 'approvedAmiyaForms':3, 'approvedCoreEffects':len(effects), 'unapprovedCandidatesInstalled':False}
if len(sys.argv)>1:
    app=Path(sys.argv[1])/'app'
    entries={}
    for p in app.glob('*.jar'):
        with zipfile.ZipFile(p) as z:
            for n in z.namelist():
                if not n.endswith('/'): entries.setdefault(n,[]).append(sha(z.read(n)))
    count=0
    for module in ['core','desktop']:
        assets=ROOT/module/'src/main/assets'
        for p in assets.rglob('*'):
            if not p.is_file():continue
            key=p.relative_to(assets).as_posix()
            assert entries.get(key) and all(h==sha(p.read_bytes()) for h in entries[key]), key
            count+=1
    assert not any(n.startswith(('art/', 'art-source/', 'save-backups/')) for n in entries)
    trainer=[n for n in entries if '/trainer/' in n]
    assert trainer == ['com/shatteredpixel/shatteredpixeldungeon/trainer/TrainerBridge.class'], trainer
    result['packagedAssets']=count
print(json.dumps(result,ensure_ascii=False,indent=2))
