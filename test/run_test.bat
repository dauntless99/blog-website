@echo off
echo ============================================
echo      Java 高并发限流测试脚本
echo ============================================
echo.

:: 检查Java是否安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请先安装Java 11+
    pause
    exit /b 1
)

:: 编译测试代码
echo 正在编译测试代码...
javac LoadTest.java
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)
echo 编译成功！
echo.

:: 运行测试
echo 正在运行高并发测试（目标6000 QPS，持续30秒）...
echo 请确保网关服务已启动在 http://localhost:8080
echo.
java LoadTest

echo.
echo 测试完成！
pause