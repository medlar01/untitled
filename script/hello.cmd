@rem 注释信息
@echo off
echo welcome used cmd script!

@rem 执行命令后获取执行结果 默认为0,执行出错为1
dir
echo %errorlevel%

@rem 阻塞时自定义提示
echo loop start ... & pause > nul
set /a var = 0
:start
set /a var += 1
echo %var%
if %var% leq 3 goto start
echo loop end ... & pause > nul