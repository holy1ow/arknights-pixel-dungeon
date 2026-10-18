# Arknights Pixel Dungeon · 明日方舟像素地牢

**作者 / 维护者：holy1ow · 当前版本：0.0.1**

基于 [Shattered Pixel Dungeon（破碎的像素地牢）4.0.2](https://github.com/00-Evan/shattered-pixel-dungeon/tree/v4.0.2) 开发的非官方同人项目；破碎的像素地牢又基于 [Pixel Dungeon（像素地牢）](https://github.com/watabou/pixel-dungeon)。感谢 Oleg Dolya（Watabou）、Evan Debenham 和所有上游贡献者。

**0.0.1 是当前开发快照，以角色贴图替换及必要的显示适配为主。** 角色选择界面只保留六位换皮角色，职业、战斗、成长、道具机制及职业解锁条件沿用破碎的像素地牢，没有加入角色专属技能或新机制。全角色美术尚未全部验收，本版本不代表美术最终完成。

## 下载与运行

前往 [Releases](https://github.com/holy1ow/arknights-pixel-dungeon/releases) 下载 `arknights-pixel-dungeon-0.0.1-windows-x64.zip`，完整解压后运行 `Start-Game.cmd`，或进入游戏目录双击 `Arknights Pixel Dungeon.exe`。Windows 10/11 x64，显卡需支持 OpenGL 2.0；包内含 Java 17 运行时，无需另装 Java。不要直接在压缩包内启动。

发行包不附带存档。首次运行创建全新进度，默认仅解锁战士对应的 **赫拉格**，其他角色按原版条件解锁；新手引导及教程提示保留。存档目录独立于上游游戏：`%USERPROFILE%\AppData\Roaming\.holy1ow\Arknights Pixel Dungeon\`。测试存档、个人存档、制作候选稿和 trainer 均不随包发布。

## 角色对应与当前预览

以下为当前已实装待机动画，缩放仅用于展示。角色对应的是原版机制，名称不表示已经实现《明日方舟》中的技能。

| 当前角色 | 对应原职业 | 当前动画 |
| --- | --- | --- |
| 赫拉格 Hellagur | 战士 Warrior | ![赫拉格](docs/images/hellagur-idle.gif) |
| 阿米娅 Amiya | 法师 Mage | ![阿米娅](docs/images/amiya-idle.gif) |
| 弑君者 Crownslayer | 盗贼 Rogue | ![弑君者](docs/images/crownslayer-idle.gif) |
| 提丰 Typhon | 女猎手 Huntress | ![提丰](docs/images/typhon-idle.gif) |
| 锏 Degenbrecher | 决斗家 Duelist | ![锏](docs/images/degenbrecher-idle.gif) |
| 闪灵 Shining | 牧师 Cleric | ![闪灵](docs/images/shining-idle.gif) |

![六角色选角界面](docs/images/selection.png)

上图使用全新进度展示，只有赫拉格默认解锁；其他角色的暗色图标表示尚未解锁。

阿米娅当前术师、近卫、医疗人物动画暂时共用已经认可的基础外观。新持剑、蓝光托手、白杖全动作稿仍为待审候选；**医疗阿米娅尚未选型**，候选稿没有安装进 0.0.1，也不包含在这个精简发布仓库中。完整制作材料仍由本地共同开发保全包保留。

## 后续制作计划

角色美术还会继续制作和替换；角色专属技能也计划在后续版本制作、更换。装备及其他游戏道具的替换美术素材正在制作中，尚未完成或接入的内容不计入本次发布。

当前仍处于共同开发阶段。待共同角色美术全部完成并验收后，再从验收后的共同美术基线拆分两条路线：

1. **原版机制换皮版**：保留原版职业、战斗、成长和道具机制，继续替换美术及必要展示内容。
2. **角色特色机制版**：共用角色美术，再开发角色专属技能和机制。

这份 0.0.1 发布副本属于当前纯换皮方向的开发快照；特色机制尚未进入本轮，也没有提前创建特色机制开发分支。

## 源码与构建

仓库包含桌面版完整构建源码、运行资源、Gradle Wrapper、当前实装美术的可编辑源文件及必要说明。为减少下载量，不带旧 Git 历史、Android/iOS 工程、构建缓存、历史候选图或二进制发行包。游戏包放在 Release 附件中。

安装 JDK 21，设置 `JAVA_HOME` 后运行：

```bat
gradlew.bat desktop:check desktop:verifyHeroSelection desktop:verifyPackagedOperators --console=plain --max-workers=2 --no-daemon
Build-Desktop.cmd
Start-Game.cmd
```

首次构建会下载 Gradle、Maven 依赖和打包用 Temurin JDK 17，需联网。构建产物位于 `desktop/build/`。详见 [构建与验证说明](docs/RELEASE-0.0.1.md)、[美术编辑入口](art/README.md) 和 [接续说明](docs/HANDOFF.md)。

## 开源协议与署名

游戏代码采用 **GNU GPL v3 或后续版本（GPL-3.0-or-later）**，完整协议见 [LICENSE.txt](LICENSE.txt)。继承的版权声明和贡献者署名保留；本项目改编署名为 holy1ow。修改范围与日期见 [MODIFICATIONS.md](MODIFICATIONS.md)，第三方素材、依赖及 Java 运行时另见 [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) 与 [NOTICE.md](NOTICE.md)。软件按现状提供，不作担保。

分发修改版时须遵守 GPL，保留版权和许可声明、注明修改，并提供对应版本的完整可修改源码及构建脚本。此仓库的 `v0.0.1` 标签对应本次 Windows 发行；当前实装美术的可编辑文件也随源码提供。

本项目与鹰角网络、Hypergryph、Gryphline/Yostar 及 Pixel Dungeon、Shattered Pixel Dungeon 官方均无隶属或授权背书关系。《明日方舟》角色设计、名称及相关商标的权利归各自权利人所有；GPL 声明不授予这些第三方商标或角色 IP 的额外权利。本项目新增代码及由贡献者可授权的原创修改按 GPL-3.0-or-later 提供。
