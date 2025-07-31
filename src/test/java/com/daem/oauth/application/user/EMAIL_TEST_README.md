# 邮件发送测试指南

## 测试类说明

### 1. EmailTemplateTest
**用途**: 测试邮件模板的渲染功能
**特点**: 
- 快速执行，不需要实际发送邮件
- 验证模板语法和变量替换
- 测试中英文模板

**运行方式**:
```bash
mvn test -Dtest=EmailTemplateTest -Dfrontend.skip=true
```

### 2. EmailServiceTest
**用途**: 单元测试邮件服务的逻辑
**特点**:
- 使用Mock对象，不实际发送邮件
- 测试方法调用和参数传递
- 验证错误处理逻辑

**运行方式**:
```bash
mvn test -Dtest=EmailServiceTest -Dfrontend.skip=true
```

### 3. EmailServiceIntegrationTest
**用途**: 集成测试，使用GreenMail模拟SMTP服务器
**特点**:
- 实际发送邮件到模拟服务器
- 验证邮件内容和格式
- 测试多语言邮件发送

**运行方式**:
```bash
mvn test -Dtest=EmailServiceIntegrationTest -Dfrontend.skip=true
```

### 4. EmailServicePerformanceTest
**用途**: 性能测试，测试邮件发送的性能表现
**特点**:
- 测试单个邮件发送时间
- 测试批量邮件发送性能
- 测试并发邮件发送能力

**运行方式**:
```bash
mvn test -Dtest=EmailServicePerformanceTest -Dfrontend.skip=true
```

## 手动测试

### 开发环境邮件测试API

启动应用后，可以使用以下API进行手动测试：

#### 1. 测试激活邮件
```bash
POST http://localhost:8080/dev/email-test/activation
Content-Type: application/x-www-form-urlencoded

email=test@example.com&username=TestUser&lang=zh-CN
```

#### 2. 测试密码重置邮件
```bash
POST http://localhost:8080/dev/email-test/password-reset
Content-Type: application/x-www-form-urlencoded

email=test@example.com&username=TestUser&lang=en-US
```

#### 3. 批量测试邮件
```bash
POST http://localhost:8080/dev/email-test/batch
Content-Type: application/x-www-form-urlencoded

emails=test1@example.com,test2@example.com&type=activation&lang=zh-CN
```

#### 4. 多语言测试
```bash
POST http://localhost:8080/dev/email-test/multilang
Content-Type: application/x-www-form-urlencoded

email=test@example.com&username=TestUser
```

### 邮件模板预览

可以在浏览器中预览邮件模板：

- 中文激活邮件: `http://localhost:8080/dev/email-preview/activation?lang=zh`
- 英文激活邮件: `http://localhost:8080/dev/email-preview/activation?lang=en`
- 中文密码重置: `http://localhost:8080/dev/email-preview/password-reset?lang=zh`
- 英文密码重置: `http://localhost:8080/dev/email-preview/password-reset?lang=en`

## 配置说明

### 测试环境配置 (application-test.yml)
```yaml
spring:
  mail:
    host: localhost
    port: 3025  # GreenMail使用的端口
    username: test@example.com
    password: test
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
```

### 开发环境邮件配置
如需在开发环境测试真实邮件发送，请在 `application-dev.yml` 中配置：

```yaml
spring:
  mail:
    host: smtp.gmail.com  # 或其他SMTP服务器
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 模板文件结构

```
src/main/resources/templates/email/
├── activation.html          # 中文激活邮件模板
├── activation_en.html       # 英文激活邮件模板
├── password-reset.html      # 中文密码重置模板
├── password-reset_en.html   # 英文密码重置模板
└── base.html               # 基础模板（未使用）
```

## 常见问题

### 1. 模板找不到错误
**错误**: `class path resource [templates/email/activation_zh.html] cannot be opened`
**解决**: 确保模板文件名正确，中文使用默认模板 `activation.html`，英文使用 `activation_en.html`

### 2. 邮件发送失败
**错误**: `邮件发送失败: Connection refused`
**解决**: 检查SMTP服务器配置，确保服务器地址、端口、认证信息正确

### 3. 模板渲染错误
**错误**: `Error resolving template`
**解决**: 检查模板语法，确保所有变量都有默认值或在Context中设置

## 测试最佳实践

1. **单元测试优先**: 先运行 `EmailServiceTest` 确保基本逻辑正确
2. **模板测试**: 运行 `EmailTemplateTest` 验证模板渲染
3. **集成测试**: 运行 `EmailServiceIntegrationTest` 测试完整流程
4. **性能测试**: 最后运行 `EmailServicePerformanceTest` 检查性能

## 扩展新语言

要添加新语言支持（如法语）：

1. 创建模板文件: `activation_fr.html`, `password-reset_fr.html`
2. 在 `messages_fr.properties` 中添加翻译
3. 在 `LocaleUtil` 中添加语言映射
4. 添加对应的测试用例