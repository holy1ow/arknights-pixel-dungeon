# 0.0.1 构建与验证记录

日期：2026-10-18。改编作者：holy1ow。本版是当前开发快照，以六角色贴图和必要显示适配为主，未加入专属技能。角色映射及后续计划见 README。

## 构建

构建环境：Windows x64、Microsoft JDK 21.0.10、Gradle Wrapper 9.5.0。原工程 Android/iOS 与在线服务模块不进入这个桌面仓库。Windows 打包使用 Temurin 17.0.17+10 的 jlink/jpackage；游戏包实际包含 Java 运行时及其 `legal/` 许可目录。运行时不需要联网读取原项目新闻或更新服务。

```bat
gradlew.bat desktop:check desktop:verifyHeroSelection desktop:verifyPackagedOperators desktop:jpackageImage --console=plain --max-workers=2 --no-daemon
```

`Build-Desktop.cmd` 构建当前源码，`Start-Game.cmd` 启动当前 `desktop/build/jpackage/Arknights Pixel Dungeon` 中的游戏。GitHub Release 是本次独立归档，重新构建不会自动修改已上传的 Release。

## 验证范围

- 48,465 项机制映射、序列化/存档兼容、资源和角色显示相关断言通过。
- 实际 OpenGL 选角场景验证：六个按钮均为干员身份，布局没有越界、重叠；旧战士默认标识归一为赫拉格。六个角色均有渲染截图。
- 干净用户目录验证：仅赫拉格解锁，`intro` 默认开启。教程代码、提示及初始进度逻辑保留，没有发行用预存档。
- 打包 JAR 的运行验证覆盖十二条子职业路径和随机选择，包含预览/取消/确认、护甲、保存及重新加载、伪装、镜像、记录、下一层、死亡动画与地表显示。
- `scripts/verify_assets.py` 核对 54 项源文件及导出散列、30 个图集单格，阿米娅三形态共用基础动画且无候选安装。传入 app-image 路径可继续核对实际包内资源；该检查使用 Python 3 和 Pillow。
- 最终原生启动、包内资源对照及压缩包校验结果见 Release 附件 `verification.json`；不以静态检查替代实际启动结果。

这些是自动化检查和启动冒烟测试，不是人工完整游玩、通关或全部硬件兼容性验证。新手引导的默认开关和代码保留已检查，未声称人工完成整个教程。

## 发布内容与限制

Windows ZIP 仅包含当前实装游戏、启动入口、运行时、许可与来源说明。代码及可编辑美术通过仓库 `v0.0.1` 标签提供；第三方库和 Java 的对应源码另放 `third-party-sources.zip`，普通玩家无需下载该附件。所有 Release 附件有 SHA-256 校验。

没有打包未实装美术候选、开发源文件、私人存档或 trainer。未提供 Android/iOS 构建，Windows 包未做代码签名。原版的部分通用画面、道具、装备和 UI 美术仍然保留；本版不能表述为全部美术替换完毕。

全角色美术尚未全部验收，医疗阿米娅未选型。角色专属技能属于后续特色机制路线；装备和其他游戏道具替换素材正在制作。未来从验收后的共同美术基线再拆分两条版本路线。
