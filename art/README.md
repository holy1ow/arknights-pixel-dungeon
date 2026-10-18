# 当前实装美术编辑入口

`manifest.json` 将 54 项逻辑素材映射到 42 份按 SHA-256 去重保存的 `.aseprite` 可编辑源文件。相同源文件只上传一次，以减少体积。逻辑角色、形态和用途由 manifest 标识，不靠散列文件名猜测。

- `art/source/`：LibreSprite / Aseprite 可打开的编辑入口；按 manifest 中的 `source` 找到对应文件。
- `art/exports/`：需要拼入头像、子职业图集的单格 PNG 导出文件。
- `core/src/main/assets/`：实际打包的运行资源；完整贴图直接以这里的文件作为导出入口，不再重复保存同一份 PNG。
- `docs/images/`：README 展示预览；不参与游戏构建。

`sourceSha256` 是源文件散列，`sha256` 是导出文件散列，`runtimeAtlasSha256` 是整张运行图集散列。图集素材的 `slot` 和 `cellSize` 指定单格位置。源文件为精简发布副本；如修改某个共享源但只希望影响一个形态，先另存为独立文件，再更新该逻辑条目的映射，避免误改其他形态。

使用 LibreSprite/Aseprite 编辑和导出，保留帧尺寸、透明度、像素对齐及动画索引。单帧源可能是包含所有动作的整张图集，不能据源帧数判断游戏只有一帧。导出后更新散列，运行 `python scripts/verify_assets.py` 和桌面角色验证。不要只改预览图。

审核状态与实装状态独立。当前 `implementationStatus=installed` 只表示已接入游戏，`reviewStatus=development-art-not-final` 表示尚非最终全角色验收状态。阿米娅当前三形态共用基础动画；医疗造型未选定，其他新动作候选暂停修改和接入。历史稿、来源参考、制作过程和候选稿保留在本地完整保全包中，不属于该精简仓库。
