# 美术素材库

- 2026-10-19：已确认的27枚天赋、8张状态图标及3组信标/传送动画已接入阿米娅换皮版，T26采用V4。[实机预览](../../docs/qa/amiya-icons-installed-20261019/previews/index.html)。

- 2026-10-19：近卫、医疗人物像已采用确认的面部统一稿，术师人物像与其他素材保持原样。[实机预览](../../docs/qa/amiya-face-installed-20261019/previews/index.html)。

- 2026-10-19：阿米娅职业护甲、三枚护甲能力图标与医疗长白杖头像已接入；其余头像、转职图标保持原样。[实机预览](../../docs/qa/amiya-armor-installed-20261019/previews/index.html)。

- 阿米娅装备、充能灌注、灵魂标记与两阶段进阶王冠已确认接入；源文件在 `current/amiya/items-status/`。[实机预览](../../docs/qa/amiya-items-installed-20261018/previews/index.html)。

- 阿米娅六组核心特效已认可并接入换皮版；正式源文件在 `current/amiya/effects/`，保留元素法杖和原版机制。[实机预览](../../docs/qa/amiya-effects-installed-20261018/previews/index.html)。

2026-10-18 状态补充：当前仍是共同开发快照，全角色美术尚未全部验收。以下旧规格与历史交付记录应与当前 manifest、代码及[快照报告](../../docs/development-snapshot-20261018.md)交叉阅读。

- [统一审核／实装状态索引](development-status-20261018.json)引用当前76项素材和阿米娅三套候选，保留制作目录原路径。
- 阿米娅已采用用户确认的换皮v4：术师手托蓝光、近卫持剑、医疗贴身白杖与蓝色杖头施法。三套24×24全动作已接入；[安装与验证记录](../../docs/qa/amiya-v4-installed-20261018/handoff.md)。旧v2/v3记录保留为历史。
- 当前运行帧以 HeroAppearance 为准：阿米娅24×24（身体16×21）、弑君者16×21、赫拉格20×21、提丰32×21、锏与闪灵36×21。下文统一16×21／512×256属于早期规格，不能用于覆盖较宽的新图集。
- `current/` 是当前正式源文件及导出入口；`operators/`、`drafts/` 保留候选、来源与历史。候选的 review_ready 仅表示可供审核。
- 角色特色机制未进入本轮；全部角色美术完成并验收后，才从共同美术基线拆分两条版本路线。

以后修改角色美术，从 `current/` 对应角色目录开始。这里是当前实装版本的可编辑工作副本；修改后先导出、预览，再按 `manifest.json` 中的运行路径接入。

- `current/<角色>/sprites/<形态>/`：`source.aseprite` 可编辑图集、`atlas.png`、21 张单帧、动画元数据和八种动作预览。
- `current/<角色>/portraits/<形态>/`：人物像源文件与导出 PNG。
- `current/<角色>/icons/<形态>/`：转职图标源文件与导出 PNG。
- `current/<角色>/splash/`：立绘源文件、当前 JPG、高分辨率 PNG。源文件可能是单层栅格。
- `drafts/`：上一轮六张基础站姿草案，以及赫拉格按阿米娅尺度重做的新基础站姿，均未实装。新稿与旧素材总览见 [赫拉格评审页](drafts/hellagur/base-idle-amiya-scale-v1/index.html)。
- `shared-atlases/`：当前人物像和转职图标合图副本；具体槽位见清单。单项修改后需按原槽位重新打包。
- `history-inventory.csv`：全部历史美术源文件、导出、生成原图、帧、参考、预览、脚本与验证文件分类索引。
- `manifest.json`：76 项当前角色素材的来源、哈希、游戏路径／合图槽位，以及全游戏图片和字体目录。
- `index.html`：可视化浏览入口。

阿米娅当前三种形态分别使用已认可换皮v4动作；近卫、医疗人物像采用portrait-face-v1面部统一稿，医疗保留长白杖；术师人物像、转职图标与立绘保持既有版本。正式源文件位于current/amiya/sprites/{base,guard,medic}，v4制作交付保留为不可变来源快照，安装状态以统一manifest和安装记录为准。

小人规格以当前manifest为准；阿米娅为24×24单帧、512×256图集，21个帧位、七档护甲行。不要自动裁边或删除空白区；动画帧序与速度见各目录 `animations.json`，攻击与施法共用帧。`frames/` 和 `previews/` 是便于检查的派生文件，以 `source.aseprite` 为编辑入口；编辑后同步导出，避免只改预览。

源文件结构有两种：阿米娅三份source.aseprite现为24×24、21帧、4层的动画文档，另有animations.aseprite保存真实播放节拍和标签。导出时按21列、7行打包。其他角色尺寸见各自清单。48 份像素源文件已实际用 LibreSprite 导出核对，均与当前素材一致，结果见 `qa/editable-source-check.json`。原立绘源文件属于单层栅格文档，不是拆好人物／背景的分层工程。

历史目录保留原路径以维持旧脚本和记录中的引用。`operators/release-v1` 是旧交接快照；`review-v3-20261016` 包含上一轮小人草案及已选立绘；`amiya-eye-*` 是面部评审过程。旧脚本可能导出旧素材，不能把旧包的通过状态当成当前实装状态。

游戏接入后执行 `gradlew.bat desktop:release desktop:jpackageImage`，从项目根目录 `Start-Game.cmd` 启动。原有 `release/` 发布归档保持历史版本。



赫拉格基础、不屈、连斩现共用用户确认的完整21帧动画及向右跑姿，含待机持刀与攻击刀姿。可编辑源文件分别保存在 `current/hellagur/sprites/{base,berserker,gladiator}/source.aseprite`，便于以后独立调整转职。[当前动画与安装记录](../operators/hellagur-approved-v3-20261016/index.html)。旧版三形态素材已备份。

弑君者基础、烟幕处决、穿烟疾行现共用用户确认的完整21帧动画及向右跑姿，含已确认的全部动作。可编辑源文件分别保存在 `current/crownslayer/sprites/{base,assassin,freerunner}/source.aseprite`，便于以后独立调整转职。[当前动画与安装记录](../operators/crownslayer-approved-v7-20261016/index.html)。旧版三形态素材已备份。

赫拉格当前为头部右移2像素确认版，20×21运行帧，基础/不屈/连斩共用；倒地头部完整保留。[安装交接](../operators/hellagur-head-right2-v4-20261016/handoff.md)。

提丰当前为完整v10（v9跑步、v7倒地），32×21完整运行帧，基础/追猎/荒野守望共用；[安装交接](../operators/typhon-approved-v10-20261017/handoff.md)。

弑君者当前为两像素眼睛v8，基础/烟幕处决/穿烟疾行共用；赫拉格保持原版。[安装交接](../operators/crownslayer-two-pixel-eyes-v8-20261017/handoff.md)。

锏当前为完整v4，36×21，基础/兵刃大师/破阵武者暂共用。[安装交接](../operators/degenbrecher-approved-v4-20261017/handoff.md)。

闪灵当前为完整v7，36×21，基础/执灯守护/执剑守护暂共用。[安装交接](../operators/shining-approved-v7-20261017/handoff.md)。

阿米娅补充三种状态（波涌余威、卷轴赋能、魔力强化）保留原版图形，仅在阿米娅上下文染为 #45B5E0；护盾完整保留原版。染色样稿和源文件在 [status-original-blue](current/amiya/status-original-blue/manifest.json)，正式运行继续使用共享图集，不将样稿PNG覆盖到图集。[安装报告](../../docs/qa/amiya-status-tint-installed-20261019/handoff.md)。
