> 后续发行状态：原始证据与 ZIP 保持不变。已另建公开工程的可运行封版副本，见 [公开发行说明](../../AMIYA-FROZEN-20261020.md)。以下是原归档时的历史说明，原运行包的许可差异不适用于新的公开 Windows 包。

# 阿米娅原版机制换皮版封版基线 · 2026-10-20

维护者 **holy1ow**。阿米娅状态为 **frozen**：暂停新增素材与修改机制，等待新的明确任务。其他角色状态不变；这不是全角色全部最终验收，也没有启动或拆分特色机制分支。

本目录保存本次封版证据、逐文件清单及归档恢复入口。**此次不是把旧 HEAD 当成本次实装版本**：未提交的实装源码与运行资源实际保存在原始 `05-integration-source.zip`，正式源文件在 01，全部候选历史在 02，完整验证证据在 03。每组 ZIP 及其成员均有 SHA-256。仓库根目录的游戏工程继续保留 v0.0.2 内容，未以开发工程整体覆盖；恢复本次封版请使用下面的归档与补充文件，不能用标签下的根目录游戏源码代替 05。

## 原始封版清单

[封版 manifest](frozen/manifest.json)、[已安装素材](frozen/installed-assets.json)、[逐项图标选版](frozen/approved-icon-selection.json)、[历史候选索引](frozen/candidate-index.json)、[封版检查](frozen/verification.json)、[原始交接](frozen/handoff.md)、[本次完整性复核](publication-integrity.json)。

`frozen/` 中的文件按原字节保存，路径仍相对原开发工程。原文的 `publishedToGitHub:false`、本地同盘备份及“本次不提交/推送”描述的是制作封版当时，不能改写历史证据；本次上传状态与远端位置以 Release 及本目录发布记录为准。

## 五组不可变归档

[本次 Release](https://github.com/holy1ow/arknights-pixel-dungeon/releases/tag/amiya-reskin-frozen-20261020) 记录附件公开状态和下载入口。

| 原归档 | 内容 | 文件数 | 字节数 |
| --- | --- | ---: | ---: |
| 01-formal-art.zip | 正式素材、可编辑源与导出 | 340 | 4,979,969 |
| 02-history-and-candidates.zip | 全部历史方案、未选素材、参考与必要旧来源 | 3,252 | 116,130,003 |
| 03-verification-records.zip | 完整验证档案、隔离测试进度、原始截图及历史日志 | 18,579 | 1,313,893,289 |
| 04-verified-desktop-runtime.zip | 已验证共同 Windows 构建、运行时、发布 JAR、启动脚本 | 230 | 140,173,020 |
| 05-integration-source.zip | 实装源码、资源、测试与构建上下文 | 2,030 | 66,963,461 |

五组共 24,431 个文件条目、1,642,139,742 字节（约 1.53 GiB）。原 ZIP 不改写、不删除成员；大型文件通过 Release 保存，不进入普通 Git 历史。原路径及历史目录保留。清单位于 [inventories](frozen/inventories/)，整体哈希位于 [checksums.json](frozen/checksums.json)。

05 原包漏收普通构建引用的三个禁用 trainer 支持文件，本轮按原字节独立保存于 [recovery-support](recovery-support/README.md)，不改写原包。恢复时将所需归档解压到新空目录，再合并该补充；不要直接覆盖后续开发工作区。01—05 是相对同一项目根目录的恢复材料。03 中的存档为隔离测试夹具，不能导入玩家进度当作自然解锁。

## 实装状态

- V4 三形态完整动作：术师蓝光、近卫持剑、医疗白杖蓝光；头像、装备、进阶物品、职业护甲及能力图标按封版清单。
- 六组核心效果、充能灌注提示、灵魂标记、27 枚天赋最终混选版本、常用状态、空间信标与传送表现。
- T26 最终为 V4。V1/V2 天赋与状态目录均有逐项采用，不能整包判废或整包安装。
- 波涌余威、卷轴赋能、魔力强化采用原图形加阿米娅专属 `#45B5E0` 染色；6 张烘焙 PNG 仅为源稿/预览，没有覆盖共享运行图集。
- 原版护盾、原版机制、元素法杖辨识度和其他角色保持。

## 运行包与公开版差异

04 是包含其他角色的共同开发构建，**不是阿米娅独立游戏，也不是重新打包的 v0.0.2**。按封版要求不更名、不改内部版本、不替换依赖、不重新编译。

原包名称/内部版本为 `Shattered Pixel Dungeon 4.0.2`，Windows 启动入口为解压目录根部 `Start-Game.cmd`，实际 EXE 在 `desktop/build/jpackage/Shattered Pixel Dungeon/`；附带 Java 17.0.17。该包沿用 `.shatteredpixel/Shattered Pixel Dungeon` 存档位置，**并非公开 v0.0.2 的 `.holy1ow` 隔离位置**。使用时应以原构建实际设置为准，避免把旧进度误认为新包附带进度。本次未运行游戏，未触碰玩家存档。

原发布 JAR SHA-256：`b06dc4117b431c5b2540000c932a468eedb78df3a4e68f50c5f823f5d45d71c2`。

04 ZIP SHA-256：`c81afb872cecbf2ba9b22c73721c14bf77e02b53a9b377397c52dd5f03b792f4`。

原包为 JSON-java **20170516**，其历史许可含额外限制，与公开 v0.0.2 的 20240303 不同；GPL 兼容性尚未通过，不能沿用 v0.0.2 的合规结论。来源、原许可、对应源码和补充记录见 [recovery-support](recovery-support/README.md) 与 [依赖比较](dependency-comparison.json)。游戏 GPL 及上游署名保持，归档行为不重新许可第三方作品。

## 验证边界

沿用封版已完成的 [完整流程报告](frozen/full-flow-handoff.md) 和 [结构化结果](frozen/full-flow-verification.json)：9 条连续关键节点路线、206 张截图、784 项连续流程断言，以及逐组功能回归。关键奖励、状态、护甲充能和区域跳转由测试夹具提供；不是自然战斗全通关、完整上行挑战或 Android/iOS 实机验证。

本轮只复核原 ZIP、全部成员 CRC/SHA-256、原目录文件、逐项选版和发布 JAR 哈希。未重新生成素材、修改代码或语言、重跑游戏/编译，未把静态校验写成新一轮游玩验证。
