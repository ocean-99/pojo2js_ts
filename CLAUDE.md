# pojo2js_ts IntelliJ IDEA 插件开发指南

## 项目概述

开发一个名为 `pojo2js_ts` 的 IntelliJ IDEA 插件，允许用户右键点击 Java 类生成对应的 JSON 示例或 TypeScript 接口定义。

## 功能需求

### 核心功能
1. **右键菜单集成**：在 Java 类文件上（文件导航器或编辑器标签页）右键显示生成选项
2. **JSON 生成**：将 Java POJO 转换为带随机值的 JSON 对象
3. **TypeScript 接口生成**：将 Java POJO 转换为 TypeScript 接口定义
4. **剪贴板复制**：生成结果自动复制到系统剪贴板

### JSON 生成规则
- 字段名使用 Java Bean 属性名
- 基础类型生成随机值
- 复杂对象类型递归生成 JSON
- 支持包级别的转换配置

### TypeScript 生成规则
- 基础类型正确映射（String → string, Integer → number 等）
- 复杂对象递归定义为接口
- 支持包级别的类型映射配置

### 配置功能
1. **包级别转换配置**：指定某些包下的类转换为特定类型
2. **类型转换自定义**：
   - Date 类型的格式和时间范围配置
   - 其他特殊类型的转换规则
3. **设置界面**：集成到 IntelliJ IDEA 设置页面

## 技术架构

### 项目结构
```
src/main/
├── java/
│   └── com/yourname/pojo2jsts/
│       ├── actions/           # 右键菜单动作
│       ├── generators/        # JSON/TS 生成器
│       ├── config/           # 配置管理
│       ├── ui/               # 设置界面
│       └── utils/            # 工具类
└── resources/
    └── META-INF/
        └── plugin.xml        # 插件配置
```

### 主要组件

#### 1. 动作类（Actions）
- `GenerateJsonAction`：生成 JSON 的动作
- `GenerateTypeScriptAction`：生成 TypeScript 的动作
- 继承 `AnAction`，处理右键菜单点击事件

#### 2. 生成器（Generators）
- `JsonGenerator`：JSON 生成逻辑
- `TypeScriptGenerator`：TypeScript 接口生成逻辑
- `RandomValueGenerator`：随机值生成器

#### 3. 配置管理（Config）
- `PluginSettings`：插件设置存储
- `PackageMapping`：包映射配置
- `TypeMapping`：类型映射配置

#### 4. UI 组件
- `SettingsConfigurable`：设置页面主配置类
- `PackageMappingPanel`：包映射配置面板
- `TypeMappingPanel`：类型映射配置面板

## 开发环境设置

### 必需工具
- IntelliJ IDEA 2023.1 或更高版本
- JDK 17
- Gradle 8.0+
- IntelliJ Platform Plugin SDK

### Gradle 配置
```gradle
plugins {
    id 'java'
    id 'org.jetbrains.intellij' version '1.16.1'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    version = '2023.1'
    type = 'IC'
    plugins = ['java']
}

patchPluginXml {
    sinceBuild = '231'
    untilBuild = '241.*'
}
```

## 核心实现要点

### 1. plugin.xml 配置
```xml
<actions>
    <group id="pojo2js_ts.ContextMenu" text="Generate" popup="true">
        <add-to-group group-id="ProjectViewPopupMenu" anchor="after" relative-to-action="$Copy"/>
        <add-to-group group-id="EditorTabPopupMenu" anchor="after" relative-to-action="$Copy"/>
        <action id="pojo2js_ts.GenerateJson" class="com.yourname.pojo2jsts.actions.GenerateJsonAction" text="Generate JSON"/>
        <action id="pojo2js_ts.GenerateTS" class="com.yourname.pojo2jsts.actions.GenerateTypeScriptAction" text="Generate TypeScript Interface"/>
    </group>
</actions>

<extensions defaultExtensionNs="com.intellij">
    <applicationConfigurable instance="com.yourname.pojo2jsts.ui.SettingsConfigurable"/>
</extensions>
```

### 2. Java 反射处理
- 使用 PSI (Program Structure Interface) 解析 Java 类结构
- 获取类的字段、类型信息
- 处理泛型、继承关系

### 3. 随机值生成策略
- String：随机字符串（可配置长度）
- Number：在合理范围内的随机数
- Boolean：随机 true/false
- Date：可配置格式和时间范围的随机日期
- Collection：生成包含 1-3 个元素的数组

### 4. 递归处理防止循环引用
- 维护已访问类的 Set
- 设置最大递归深度
- 循环引用时使用 null 或省略字段

## 配置选项设计

### 包映射配置
```java
public class PackageMapping {
    private String packagePattern;  // 包名模式，支持通配符
    private String targetType;      // 目标类型（JSON: "string", TS: "string"）
    private boolean recursive;      // 是否应用到子包
}
```

### 类型映射配置
```java
public class TypeMapping {
    private Class<?> sourceType;    // 源 Java 类型
    private String jsonValue;       // JSON 值模式
    private String tsType;          // TypeScript 类型
    private String customGenerator; // 自定义生成器类名
}
```

### Date 配置示例
```java
public class DateConfig {
    private String format = "yyyy-MM-dd HH:mm:ss";
    private int pastDays = 365;     // 过去天数范围
    private int futureDays = 0;     // 未来天数范围
}
```

## 开发步骤建议

### 阶段一：基础框架
1. 创建 Gradle 项目和基本目录结构
2. 配置 plugin.xml
3. 实现基础的右键菜单动作
4. 创建简单的 JSON 生成器（仅支持基础类型）

### 阶段二：核心功能
1. 完善 JSON 生成器，支持复杂对象递归
2. 实现 TypeScript 接口生成器
3. 添加循环引用检测
4. 实现剪贴板复制功能

### 阶段三：配置系统
1. 创建设置页面 UI
2. 实现包映射配置
3. 实现类型映射配置
4. 添加 Date 类型自定义配置

### 阶段四：优化和测试
1. 性能优化
2. 错误处理和用户提示
3. 单元测试
4. 集成测试

## 关键API和类

### IntelliJ Platform APIs
- `AnAction`：动作基类
- `PsiClass`：Java 类的 PSI 表示
- `PsiField`：Java 字段的 PSI 表示
- `CopyPasteManager`：剪贴板操作
- `ApplicationConfigurable`：设置页面配置

### 工具类需求
- JSON 生成工具
- TypeScript 接口生成工具
- 随机值生成工具
- 包名匹配工具
- PSI 解析工具

## 测试策略

### 单元测试
- JSON 生成器测试
- TypeScript 生成器测试
- 配置管理测试
- 随机值生成测试

### 集成测试
- 完整的生成流程测试
- UI 交互测试
- 不同 Java 类结构的测试

### 边界测试
- 循环引用处理
- 深度嵌套对象
- 泛型和复杂继承关系

## 发布准备

1. **插件描述**：准备 plugin.xml 中的详细描述
2. **版本管理**：设置合理的版本号和兼容性范围
3. **文档**：创建用户使用说明
4. **图标和截图**：准备插件市场展示材料

## 注意事项

1. **内存管理**：处理大型类时注意内存使用
2. **线程安全**：UI 操作需要在 EDT 线程中执行
3. **用户体验**：生成过程中显示进度提示
4. **错误处理**：优雅处理解析失败和异常情况
5. **兼容性**：确保与不同版本的 IntelliJ IDEA 兼容

开始开发时，建议先实现最小可行版本，然后逐步添加功能。重点关注用户体验和代码质量。