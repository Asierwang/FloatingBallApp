# Release版本构建说明

## ✅ 构建完成

已成功构建签名的Release版本APK！

## 📦 生成的APK文件

### Debug版本
- **文件名**: FloatingBall-debug.apk
- **大小**: 3.3 MB
- **类型**: Debug版本（未签名）
- **用途**: 测试和调试

### Release版本（已签名）
- **文件名**: FloatingBall-release-signed.apk
- **大小**: 2.8 MB
- **类型**: Release版本（已签名）
- **用途**: 正式发布
- **状态**: ✅ 已签名并验证

## 🔐 签名信息

### 密钥库信息
- **密钥库文件**: floatingball-release.keystore
- **密钥别名**: floatingball
- **密钥算法**: RSA 2048-bit
- **有效期**: 10000天
- **证书信息**: CN=FloatingBall, OU=Development, O=Example, L=Beijing, ST=Beijing, C=CN

### 证书指纹
- **SHA-256**: 364fa2b8a799a4dae8528bc957b66be3191fcf6a39207f113004e68ab74f9110
- **SHA-1**: 20e7c5a38fec44d5f894b46b1168f6d183b19019
- **MD5**: 2bd776b1ca6696bb829b20c3d9ad378c

## 🚀 安装说明

### 方法1: 通过ADB安装
```bash
adb install FloatingBall-release-signed.apk
```

### 方法2: 通过文件管理器安装
1. 将APK文件传输到Android设备
2. 在设备上打开文件管理器
3. 找到APK文件并点击安装
4. 如果提示"未知来源"，请在设置中允许安装未知来源应用

### 方法3: 上传到应用市场
- Release版本已签名，可以上传到Google Play或其他应用市场
- 注意：Google Play要求targetSdkVersion至少为31，当前为29
- 如需上传Google Play，需要更新targetSdkVersion

## 📊 版本对比

| 属性 | Debug版本 | Release版本 |
|-----|----------|------------|
| 文件大小 | 3.3 MB | 2.8 MB |
| 是否签名 | ❌ 否 | ✅ 是 |
| 是否对齐 | ❌ 否 | ✅ 是 |
| 优化级别 | 无 | 已优化 |
| 用途 | 测试调试 | 正式发布 |

## 🔧 构建过程

### 1. 创建签名密钥库
```bash
keytool -genkeypair -v -keystore floatingball-release.keystore \
  -alias floatingball -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass floatingball123 -keypass floatingball123 \
  -dname "CN=FloatingBall, OU=Development, O=Example, L=Beijing, ST=Beijing, C=CN"
```

### 2. 构建Release APK
```bash
gradle assembleRelease
```

### 3. 对齐APK
```bash
zipalign -v 4 app-release.apk FloatingBall-aligned.apk
```

### 4. 签名APK
```bash
apksigner sign --ks floatingball-release.keystore \
  --ks-key-alias floatingball \
  --ks-pass pass:floatingball123 \
  --key-pass pass:floatingball123 \
  --out FloatingBall-release-signed.apk \
  FloatingBall-aligned.apk
```

### 5. 验证签名
```bash
apksigner verify --print-certs FloatingBall-release-signed.apk
```

## ⚠️ 重要提示

### 密钥库安全
- **密钥库密码**: floatingball123
- **密钥密码**: floatingball123
- ⚠️ **警告**: 这是示例密码，正式发布请使用强密码
- ⚠️ **警告**: 请妥善保管密钥库文件，丢失后无法更新应用

### 发布注意事项
1. **Google Play要求**
   - targetSdkVersion需要更新到31或更高
   - 需要提供隐私政策
   - 需要符合Google Play政策

2. **权限说明**
   - 应用需要悬浮窗权限
   - 需要在应用描述中说明权限用途

3. **测试建议**
   - 在多种设备上测试
   - 测试不同Android版本
   - 测试权限申请流程

## 📝 版本信息

- **应用包名**: com.example.floatingball
- **版本号**: 1.01 (versionCode: 2)
- **最低SDK**: Android 8.0 (API 26)
- **目标SDK**: Android 10 (API 29)
- **编译SDK**: API 29

## 📝 版本更新日志

### v1.01 (2026-04-30)
- 菜单系统重构：移除固定DW和VLA菜单，改为动态定制菜单
- 新增AppListActivity：设备已安装应用选择界面
- 定制菜单功能：
  - 未定制位置显示"+"图标，点击弹出应用列表供选择
  - 选择后菜单显示应用图标和名称
  - 长按定制菜单可删除
  - 最多支持6个定制菜单
  - 配置通过SharedPreferences持久化存储，重启后自动恢复

## 🎯 下一步

1. **测试Release版本**
   - 在真实设备上安装测试
   - 验证所有功能正常
   - 检查性能表现

2. **准备发布**
   - 准备应用截图和描述
   - 准备隐私政策文档
   - 准备更新日志

3. **上传发布**
   - 选择合适的应用市场
   - 上传APK和素材
   - 提交审核

---

**构建日期**: 2026-04-30
**构建状态**: ✅ 成功
**签名状态**: ✅ 已验证
