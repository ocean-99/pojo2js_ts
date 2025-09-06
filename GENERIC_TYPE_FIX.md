# 泛型类型处理修复说明

## 问题描述
用户报告`List<AccountStatement>`类型在TypeScript生成时被生成为`any`而不是正确的`AccountStatement[]`。

## 根本原因
TypeScript生成器在处理泛型类型时，接口生成的顺序和依赖关系处理有问题：

1. 在处理`List<AccountStatement>`时，`AccountStatement`接口还没有被生成
2. 接口定义的顺序不正确，导致前向引用问题
3. 循环引用检测过于严格，阻止了正确的递归生成

## 解决方案

### 1. 改进的两阶段生成过程
```java
public String generate(PsiClass psiClass, Project project) {
    // 第一阶段：收集所有需要的接口
    collectAllInterfaces(psiClass, project);
    
    // 第二阶段：生成主接口
    String mainInterface = generateInterface(psiClass, project);
    
    // 合并所有接口定义
    return combineResults();
}
```

### 2. 依赖接口预收集
新增`collectAllInterfaces`方法，在生成主接口前先遍历所有字段，收集所有需要的自定义类型接口。

### 3. 改进的接口生成逻辑
- 优先返回接口名称以避免循环引用
- 智能检测是否需要生成接口定义
- 避免重复生成相同接口

## 测试用例

### 输入Java类
```java
public class DateGroupStatement {
    private String date;
    private List<AccountStatement> statements;
}

public class AccountStatement {
    private String id;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
}
```

### 期望的TypeScript输出
```typescript
export interface AccountStatement {
  id: string;
  date: string;
  amount: number;
  description: string;
}

export interface DateGroupStatement {
  date: string;
  statements: AccountStatement[];
}
```

### 修复前的错误输出
```typescript
export interface DateGroupStatement {
  date: string;
  statements: any;  // ❌ 错误：应该是 AccountStatement[]
}
```

## 支持的复杂泛型场景

修复后的生成器现在支持：

1. **基础泛型集合**: `List<T>`, `Set<T>`, `Map<K,V>`
2. **嵌套泛型**: `Map<String, List<AccountStatement>>`
3. **多层嵌套**: `List<Map<String, Object>>`
4. **数组泛型**: `AccountStatement[]`
5. **Optional类型**: `Optional<String>` → `string | null`
6. **枚举类型**: `Status` → `"ACTIVE" | "INACTIVE" | "PENDING"`

## 验证方法

1. 使用`ComprehensiveTest`类测试各种复杂场景
2. 确认所有依赖接口都被正确生成
3. 验证生成顺序和引用关系正确
4. 测试循环引用场景不会导致无限递归

## 相关文件修改

- `TypeScriptGenerator.java`: 主要修复文件
- `ComprehensiveTest.java`: 新增全面测试类
- `DateGroupStatement.java`: 问题重现测试类
- `AccountStatement.java`: 依赖类型定义

修复后的插件现在能够正确处理所有复杂的泛型类型场景，生成准确的TypeScript接口定义。