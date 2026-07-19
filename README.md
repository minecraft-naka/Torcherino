# Torcherino（加速火把）

[![CI](https://github.com/minecraft-naka/Torcherino/actions/workflows/ci.yml/badge.svg)](https://github.com/minecraft-naka/Torcherino/actions/workflows/ci.yml)
[![许可证：LGPL-3.0-or-later](https://img.shields.io/badge/license-LGPL--3.0--or--later-blue.svg)](LICENSE)

Torcherino 为 Minecraft 26.2 Fabric 添加可配置的加速火把。火把能够对范围内的随机刻方块和方块实体执行额外 tick，适用于作物与支持标准方块实体 ticker 的机器。

## 运行环境

| 组件 | 版本 |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3 或更高版本 |
| Fabric API | 0.155.0+26.2 或兼容版本 |
| Java | 25 或更高版本 |

单人游戏需要安装到客户端。多人游戏需要同时安装到服务端和所有客户端。

## 使用方式

- 空手右键：切换作用范围，包括关闭、`3 × 3 × 3`、`5 × 3 × 5`、`7 × 3 × 7` 和 `9 × 3 × 9`。
- 蹲下右键：切换相对原速倍率，包括 `1`、`4`、`8`、`16` 和 `32` 倍；手持物品不影响操作。`1` 倍保持原版速度，不执行额外加速。
- 红石供电：暂停加速火把。

方块实体按倍率补充 ticker。随机刻方块以原版 `randomTickSpeed / 4096` 的每方块概率为基线放大，因此五档倍率在作物上也保持实际比例。每个火把每游戏刻最多执行 16,384 次额外操作，未加载区块和其他加速火把始终会被跳过。

## 合成配方

```text
空   红石粉   空
钟   火把     钟
空   红石粉   空
```

配方消耗 2 个钟、2 个红石粉和 1 个火把，产出 1 个加速火把。

## 黑名单

数据包可通过以下标签排除不应被加速的内容：

- 方块：`torcherino:acceleration_blacklist`
- 方块实体类型：`torcherino:acceleration_blacklist`

对应资源路径：

```text
data/<namespace>/tags/block/acceleration_blacklist.json
data/<namespace>/tags/block_entity_type/acceleration_blacklist.json
```

## 构建

构建过程会通过 Gradle Toolchains 获取 Java 25：

```shell
./gradlew build
```

Windows 环境使用：

```powershell
.\gradlew.bat build
```

构建产物位于 `build/libs/`。

## 许可证

项目代码采用 [GNU Lesser General Public License v3.0 or later](LICENSE) 发布。经典纹理按 LGPL-3.0-only 分发。

经典加速火把纹理来自早期 Torcherino 项目，许可与来源记录见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
