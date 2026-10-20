# 封版恢复补充文件（2026-10-20）

五个已封版 ZIP 均保持原样。独立核对发现 `05-integration-source.zip` 的 core/desktop 构建脚本仍引用 `trainer/build-support/`，但归档排除了整个 trainer 目录。此补充只按原字节保存普通构建必需的三个文件：两份构建支持脚本和禁用的 TrainerBridge。未提供启用 trainer 的实现、配置或玩家存档，未修改原代码，也未重新编译。逐文件来源和 SHA-256 见 SOURCE-MANIFEST.json。

恢复时先将 05 解压到一个新空目录，再将本补充的 `trainer/` 合并到该目录；不要覆盖开发工作区。普通构建不传 `-Ptrainer=true`。原 settings.gradle 仍含 Android/iOS 模块和相关工具链要求；本次不修改它，也不声称已重新构建成功。可运行包使用已验证的原 04 ZIP。

游戏源码的 GNU GPL v3 或后续版本全文保留在 LICENSE.txt；Pixel Dungeon 的 Oleg Dolya (Watabou)、Shattered Pixel Dungeon 的 Evan Debenham 及贡献者署名保留，改编维护者为 holy1ow。其他媒体/组件版权和署名以原源码及关于页面为准。《明日方舟》角色与商标归各自权利人；此归档不额外授予这些权利。

`licenses/` 保留已有第三方许可，并补存 **JSON-java 20170516** 的原许可、Maven POM 和源码 JAR。其历史许可含额外使用限制，不能套用公开版 v0.0.2 使用的 JSON-java 20240303 公共领域声明，也不声称该历史依赖已通过 GPL 兼容性审核。原许可依据：https://github.com/stleary/JSON-java/blob/20170516/LICENSE 。当前库的公共领域声明不被本记录自动追溯套用到旧发行物。

除旧 JSON 及原包额外携带的其他平台 native JAR 外，25 个共同外部 JAR 与此前公开版字节一致；152 个随附 Java 运行时文件也完全一致。具体比较见上级 dependency-comparison.json。其已有对应源码继续由以下附件提供，不重复上传：

https://github.com/holy1ow/arknights-pixel-dungeon/releases/download/v0.0.1/third-party-sources.zip

SHA-256：`445e6fde5da87223c2b015b25cc190d3264abd6155ca6127cf88693cb14c721d`。

该附件涵盖既有 libGDX、controllers、JLayer、JOrbis、Jamepad、LWJGL 3.3.3 / OpenAL Soft 1.23.1 和 Temurin 17.0.17+10 对应源码。原封版包保留其他平台 native JAR 是原构建事实，不代表这些平台已经实机验证。本补充不是新的游戏构建，不改变原 ZIP 哈希。
