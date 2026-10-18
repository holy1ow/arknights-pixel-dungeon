"""Archive a built Windows x64 app-image without unrelated platform native JARs.

Usage: python scripts/package_windows.py NEW_OUTPUT_DIRECTORY
Run desktop:jpackageImage first. Refuses to overwrite an existing directory/ZIP.
"""
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys
import zipfile

ROOT=Path(__file__).resolve().parents[1]
APP='Arknights Pixel Dungeon'
image=ROOT/'desktop/build/jpackage'/APP
dest=Path(sys.argv[1]).resolve()
archive=dest.parent/'arknights-pixel-dungeon-0.0.1-windows-x64.zip'
if dest.exists() or archive.exists():raise SystemExit('Refusing to overwrite an existing release')
assert (image/(APP+'.exe')).is_file()
dest.mkdir(parents=True)
excluded=[]
for p in image.rglob('*'):
    if not p.is_file():continue
    rel=p.relative_to(image)
    if re.search(r'-natives-(linux|macos|windows-x86)',p.name):
        excluded.append(rel.as_posix());continue
    target=dest/APP/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(p,target)
cfg=dest/APP/'app'/(APP+'.cfg')
text=cfg.read_text(encoding='utf-8')
cfg.write_text('\n'.join(line for line in text.splitlines() if not any(line.endswith(Path(r).name) for r in excluded))+'\n',encoding='utf-8')
for filename in ['LICENSE.txt','NOTICE.md','THIRD-PARTY-NOTICES.md','MODIFICATIONS.md']:
    shutil.copy2(ROOT/filename,dest/filename)
(dest/'Start-Game.cmd').write_text('@echo off\ncd /d "%~dp0"\nstart "" "%~dp0Arknights Pixel Dungeon\\Arknights Pixel Dungeon.exe"\n',encoding='ascii',newline='\r\n')
commit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip()
(dest/'README.txt').write_text('Arknights Pixel Dungeon 0.0.1 — holy1ow\nWindows x64 / bundled Java 17.0.17\n\n完整解压后运行 Start-Game.cmd。首次运行创建全新进度，仅赫拉格默认解锁，原版新手引导保留。\n这是开发快照，全角色美术尚未全部验收；医疗阿米娅未选型，特色技能尚未实装。\n基于 Shattered Pixel Dungeon 4.0.2，游戏代码 GNU GPL v3 或后续版本。\n对应源码：https://github.com/holy1ow/arknights-pixel-dungeon/tree/v0.0.1\nCommit: '+commit+'\n第三方源码：同一 GitHub Release 的 third-party-sources.zip。普通玩家无需下载源码。\n个人存档不在本目录内，游戏使用独立用户目录。\n',encoding='utf-8')
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
files=[{'path':p.relative_to(dest).as_posix(),'bytes':p.stat().st_size,'sha256':sha(p)} for p in sorted(dest.rglob('*')) if p.is_file()]
manifest={'version':'0.0.1','date':'2026-10-18','commit':commit,'excludedOtherPlatformNatives':excluded,'files':files}
(dest.parent/'windows-files.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
    for row in files:z.write(dest/row['path'],row['path'])
verify=dest.parent/'archive-verification-windows'
assert not verify.exists()
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    z.extractall(verify)
assert sorted(p.relative_to(verify).as_posix() for p in verify.rglob('*') if p.is_file())==sorted(r['path'] for r in files)
for row in files:assert sha(verify/row['path'])==row['sha256'],row['path']
print(json.dumps({'archive':archive.name,'sha256':sha(archive),'bytes':archive.stat().st_size,'files':len(files),'excludedOtherPlatformNatives':len(excluded),'extractionHashesVerified':True},indent=2))
