# 当前实装美术编辑入口

`manifest.json` 记录 60 项逻辑素材，按 SHA-256 引用去重的 `.aseprite` 可编辑源。0.0.2 新迁入阿米娅 v4 三形态的分层帧源、真实播放时间轴，以及六组核心效果源；不重复上传完整制作历史。

- `art/source/`：LibreSprite / Aseprite 编辑入口。`source` 是分层帧源，三形态另有 `animationSource` 播放时间轴。
- `art/animations/amiya/`：动作索引、帧率、身体框和效果帧时长。
- `art/exports/`：头像、子职业图标的单格 PNG。
- `core/src/main/assets/`：实际运行资源及完整图集的导出入口。
- `art/provenance/`：原批准安装记录、开发路径、发布路径及散列映射。
- `docs/images/`：展示预览，不进入游戏构建。

`sourceSha256`、`animationSourceSha256`、`animationMetadataSha256` 对应编辑源、时间轴和元数据；`sha256` 对应导出/运行贴图。图集单格还记录整图散列、slot 和 cellSize。原批准记录保持字节不变。效果元数据只将本机输出路径改成仓库相对路径，帧与时长未变。

开发工程的权威制作入口仍是 `art-source/library/current/amiya/`，本目录是公开副本的编辑入口。两个工程不可整目录互相覆盖。历史 v0.0.1 旧源保留，但不再作为当前三形态入口。

阿米娅当前为术师蓝色托手、近卫持剑、医疗贴身白杖与蓝色杖端光。图集 512×256、单格 24×24、21 动作槽和 7 护甲行；身体框 `(2,2,16,21)`，显示边距左 2、上 2、右 6、下 1。不要通过裁切、拉伸或平移认可像素修复定位。六组效果各为 4 帧的 96×24 图集。

审核和实装状态独立。本次九项为 approved / installed，其他素材保留既有状态，全角色美术尚未全部最终验收。医疗已选定，旧版未选型记录仅作历史；未采用候选不进入公开仓库。

后续修改须同步导出与散列，运行 `python scripts/verify_assets.py`（需要 Pillow）和桌面检查；不要只替换预览图。
