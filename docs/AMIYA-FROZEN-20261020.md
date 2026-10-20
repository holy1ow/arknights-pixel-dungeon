# 阿米娅原版机制换皮版封版 · 2026-10-20

维护者：holy1ow。发行标签：`amiya-reskin-frozen-20261020-public`。内部版本继续为 0.0.2，不改存档版本。这是含六位换皮角色的共同项目构建，阿米娅为 **frozen**；全角色美术尚未全部最终验收，特色机制未开发，也未拆分机制分支。

## 本次改版

- V4 三形态全动作：术师托蓝色术式、近卫持剑、医疗贴身白杖与蓝色杖端光。
- 已认可头像、装备、进阶物品、职业护甲及三枚护甲能力图标。
- 六组核心效果、充能与灌注提示、灵魂标记。
- 27 枚天赋最终混选图标、常用状态图标、空间信标与传送表现。T26 为 V4，其他按逐项选版清单；V1/V2 不能整包判废。
- 波涌余威、卷轴赋能、魔力强化沿用原版图形，仅为阿米娅染蓝；6 份样稿 PNG 留作源文件参考，没有覆盖共享运行图集。

原版职业、伤害、充能、恢复、成长、天赋、转职、护盾及元素法杖辨识度保持。阿米娅仍对应法师，战斗法师显示近卫，术士显示医疗；没有免费远程攻击、主动治疗、自由切换形态等新增机制。其他角色保持既有内容。

公开工程逐项迁入冻结实现：24 个生产 Java 文件和 57 个运行资源差异。其中两个道具语言资源直接复制封版原字节，没有重新撰写语言内容。三形态、六组核心效果的已有素材字节保持。保留六角色选角和随机池、原职业内部标识与存档兼容、Arknights Pixel Dungeon 名称、独立包标识和 `.holy1ow` 存档目录、原解锁与新手引导、关于页面、GPL 及完整第三方许可。

## 下载与源码

从 [本次 Release](https://github.com/holy1ow/arknights-pixel-dungeon/releases/tag/amiya-reskin-frozen-20261020-public) 下载 `arknights-pixel-dungeon-amiya-frozen-20261020-windows-x64.zip`，完整解压后运行 `Start-Game.cmd` 或 `Arknights Pixel Dungeon/Arknights Pixel Dungeon.exe`。包内附带 Windows x64 Java 17.0.17，无需另装 Java。Windows 10/11 x64，需 OpenGL 2.0。

游戏包不附带存档；首次运行是全新进度，仅战士对应的赫拉格解锁，教程保留。没有私人存档、测试夹具、开发源文件、候选稿或启用 trainer。存档在 `%USERPROFILE%/AppData/Roaming/.holy1ow/Arknights Pixel Dungeon/`。

新包对应的完整可修改源码与构建文件是 [公开标签](https://github.com/holy1ow/arknights-pixel-dungeon/tree/amiya-reskin-frozen-20261020-public)，美术源文件按 `art/manifest.json` 中的 SHA-256 查找。源码采用 GPL-3.0-or-later，保留 Watabou、Evan Debenham 及贡献者署名；详见根目录 LICENSE、NOTICE 和第三方许可。

公开依赖继续采用 JSON-java 20240303。第三方依赖和 Java 运行时的源码沿用 [v0.0.1 的 third-party-sources.zip](https://github.com/holy1ow/arknights-pixel-dungeon/releases/download/v0.0.1/third-party-sources.zip)，SHA-256 `445e6fde5da87223c2b015b25cc190d3264abd6155ca6127cf88693cb14c721d`。依赖未变，不重复上传该 126 MB 附件。

## 原封版归档与公开构建的区别

原五组 ZIP 保持不变。原 `04-verified-desktop-runtime.zip` 使用上游名称/存档位置及 JSON-java 20170516，不能直接套用公开版依赖许可结论；已另存维护者可访问的远端草稿和本地原目录，不作为玩家公开下载包。用户后续授权自行处理可运行发行，因此另从既有公开工程构建，新的 ZIP/JAR 哈希与原封版包不同。

本 Release 复用已上传的 `01-formal-art.zip`（正式素材）、`02-history-and-candidates.zip`（全部旧方案、未选稿、参考与制作历史）、`03-verification-records.zip`（完整验证档案）、`05-integration-source.zip`（历史开发源码上下文）。`06-recovery-support-and-notices.zip` 补齐原 05 漏掉的三个禁用 trainer 构建支持文件和历史许可记录。05/06 用于恢复原开发上下文，**不是新公开包的对应源码**，新包应使用上述公开标签。

原封版的清单、逐成员哈希、选版和安装证据位于 `docs/baselines/amiya-reskin-frozen-20261020/`，原始记录逐字节保留，历史“未上传”“尚待公开”等文字不改写。正式目录 340 文件、主要候选目录 3,097 文件均有归档载体。五组原包总计 24,431 个成员，原本地校验和远端下载核对已通过。旧标签及旧 Release 保留。

`FROZEN-SHA256SUMS.txt` 是原归档校验清单，包含未公开的原 04；本次公开附件用 `SHA256SUMS.txt`。两份清单职责不同，不能把新运行包哈希冒充原包哈希。

## 验证边界

公开工程重新执行职业/选角/随机/解锁、新手进度、转职与存档、身体定位/左右朝向/镜像分身、六组效果和元素法杖对照，以及装备、护甲、天赋、状态染色和头像的自动检查与真实 GameScene/OpenGL 渲染。发布附件 `verification.json` 记录最终结果、归档哈希、解压和 EXE 随附 JVM 启动检查；`windows-files.json` 列出发行包每个文件。

旧效果测试的测试鼠随机护甲可吞掉最低伤害，本次仅将该测试夹具的减伤固定为零，使伤害断言稳定；生产怪物和游戏逻辑没有因此改变。失败日志保留，不将首次失败隐藏成一次通过。

原完整流程证据覆盖 9 条连续关键节点路线、206 张截图和 784 项断言。奖励、状态及区域跳转由测试夹具提供，属于原封版验证，不能写成这次公开副本的新自然战斗通关。此次自动渲染、静态核对及 EXE 启动均不等于人工完整游玩，也没有 Android/iOS 实机验证。Windows 程序未签名。

本轮自动回归结果：职业 48,465 项；身体/朝向 10,584 项；效果 436 项 / 13 场景；道具 573 项 / 19 场景；护甲 397 项 / 14 场景；天赋与传送 2,086 项 / 11 场景；染色 495 项 / 11 截图；头像 5 场景；旧存档 18 份通过。详见 [回归摘要](qa/amiya-frozen-public-20261020/regressions.json) 与 [迁入审计](qa/amiya-frozen-public-20261020/precommit-audit.json)。

## 编辑与接续

开发工程的正式入口仍为 `art-source/library/current/amiya/`，制作历史留在 `art-source/operators/amiya-subclass-rework-v1/`。公开工程独立 Git 仓库不是缓存，`art/source/` 和 manifest 是其可编辑副本入口，`core/src/main/assets/` 为实装资源；不要整目录互相覆盖。构建输出和测试进度只留在 build/；大型历史 ZIP 放 Release，不加入普通 Git 历史。

继续冻结阿米娅素材和机制，等待新的明确任务。其他角色全部美术验收后，才从共同美术基线拆出原版机制换皮版与角色特色机制版两条路线。
