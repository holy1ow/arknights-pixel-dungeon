# 0.0.2 迁移与验证

日期：2026-10-18；作者 holy1ow。基准为公开 v0.0.1 / `39bd1a7132bf61bd05534a7f072d596ecb899670`。本版逐项迁入阿米娅 v4 三形态和已认可的六组核心效果，保留原机制与公开发行设置。

## 迁移依据

开发工程的两个安装交接记录及批准安装侧记录确认：术师蓝色托手、近卫持剑、医疗贴身白杖和蓝色杖端光均已认可；六组效果也已认可。原制作 manifest 的待审/未安装字样属于安装前历史，不作为当前状态。两份原批准记录和输入/输出散列映射见 `art/provenance/`。

只迁入三张人物图集、六张效果图集、九份分层源、三份人物播放时间轴、必要动画元数据，以及十个生产 Java 文件涉及的显示适配。没有整体覆盖代码、素材库或桌面配置。关于页面仅更新版本号；公开六角色入口、独立身份和存档目录、JSON-java、许可及上游署名保留。

## 可重跑检查

在本仓库使用 JDK 21 和自带 Gradle Wrapper：

```bat
gradlew.bat desktop:check desktop:verifyHeroSelection desktop:verifyPackagedOperators desktop:verifyOperatorCanvas desktop:verifyAmiyaEffects desktop:jpackageImage --console=plain --max-workers=2 --no-daemon
gradlew.bat desktop:verifyHeroSelection -PqaWidth=540 -PqaHeight=960 --console=plain --no-daemon
python scripts/verify_assets.py "desktop/build/jpackage/Arknights Pixel Dungeon"
```

最后一条需要 Python 3 和 Pillow。`Build-Desktop.cmd` 构建，`Start-Game.cmd` 运行当前构建；已上传 Release 和旧归档不会自动更新。

检查覆盖六角色入口、解锁、随机选择、十二条转职路径、磁盘存读、身体锚点、左右朝向、镜像/分身、光之盟友、三形态和六组效果。效果测试含原法师、闪灵及火焰/冰霜/雷电对照，检查原通用弹道位置/时序/单次回调、实际充能消耗、伤害、正恢复/满血/零恢复、诅咒/变装/投掷/未命中排除。黄色充能标记及共享纹理以基准资源散列检查。

本次已通过 48,465 项角色/存档断言、126 项外观映射、12 条转职 UI 路径及随机选择、10,584 项帧/翻转检查、60 项点击探针、6,912 项粒子定位采样，以及 436 项特效断言和 13 组真实渲染场景。横屏 1280×720 和竖屏 540×960 选角均通过。另用 v0.0.1 实际发行 JAR 写入 18 组隔离角色/转职存档，由 v0.0.2 实际 JAR 读回并核对身份、属性、装备和天赋。`ReleaseSaveChecks` 仅在测试 classpath 中运行。

最终计数见 Release 的 `verification.json` 与本地完整日志；资源和认可源由 `art/manifest.json`、`art/provenance/amiya-0.0.2.json` 和 `windows-files.json` 追溯。打包后在解压副本中启动 EXE，并确认加载随附 Java 17，而不只检查编译结果。

## 实机预览与范围

README 四段预览来自本次发布构建的真实 GameScene/OpenGL 帧缓冲，覆盖蓝色法术、近卫剑击、白杖出手及实际恢复。Release 的 `runtime-previews.zip` 含 13 组精简录制、帧/生命值/效果记录和索引，包括双向及元素/其他角色对照。

测试使用隔离配置、固定房间和静止高血量老鼠；远程从生产 `Wand.execute` 及目标选择触发，近战使用攻击动画回调，回血使用原灵魂标记分支。测试场景和固定录制时钟不编入发行 JAR。预览是实际录制的裁切查看副本，完整原始帧仅留本地，没有把离线特效叠到截图上。

自动化定向验证、截图检查和原生启动不等同人工完整游玩或通关。首次引导开关、原教程代码与干净进度保留，未声称人工走完所有教程。

## 发行与限制

Windows x64 ZIP 含游戏、Java 17.0.17+10、完整许可和来源说明，不含源码、候选素材、私人存档或 trainer。源码与当前可编辑美术由 `v0.0.2` 标签提供。依赖与 v0.0.1 相同，既有 [third-party-sources.zip](https://github.com/holy1ow/arknights-pixel-dungeon/releases/download/v0.0.1/third-party-sources.zip) 继续对应本版；逐项比较及附件散列记录在报告中，不重复上传大文件。

医疗已选定并接入，全角色美术仍未全部验收。原机制、其他角色及元素法杖保持，没有新技能、主动治疗或自由切换。特色机制尚未开发，尚未拆分机制分支。未来从验收后的共同美术基线拆分两条路线。本版是未签名开发快照预发布，不提供 Android/iOS 包。
