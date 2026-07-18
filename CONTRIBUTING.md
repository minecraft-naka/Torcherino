# 贡献指南

## 开发环境

- Java 25
- Minecraft 26.2
- Fabric Loader 0.19.3 或更高版本

Gradle Toolchains 可自动下载匹配的 Java 工具链。本地构建命令：

```shell
./gradlew build
```

## 提交要求

- 保持 Mod ID `torcherino` 和已有存档数据兼容。
- 新增可见文本时同步更新 `en_us.json` 与 `zh_cn.json`。
- 新增二进制或第三方资源时补充来源、版权和 SPDX 许可证标识。
- 修改加速逻辑时说明性能影响，并覆盖未加载区块、自身递归和方块替换等边界情况。
- Pull Request 应包含变更原因、行为影响和验证命令。

提交前运行：

```shell
./gradlew clean build --warning-mode all
```

## 许可证

提交到本仓库的代码默认按 LGPL-3.0-or-later 许可发布。提交第三方内容前，需要确认其许可证允许再分发。
