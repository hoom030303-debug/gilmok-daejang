# 길목대장 빌드 스크립트
# 사용법:  powershell -ExecutionPolicy Bypass -File build.ps1 [assembleDebug|assembleRelease]
param([string]$Task = 'assembleRelease')
$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = (Get-ChildItem 'C:\Program Files\Eclipse Adoptium' -Directory | Where-Object Name -like 'jdk-17*' | Select-Object -First 1).FullName
$root = $PSScriptRoot
$p = Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" `
    -ArgumentList '-Dfile.encoding=UTF-8','-classpath','gradle\wrapper\gradle-wrapper.jar','org.gradle.wrapper.GradleWrapperMain',$Task,'--console=plain' `
    -WorkingDirectory $root -RedirectStandardOutput "$root\build-log.txt" -RedirectStandardError "$root\build-err.txt" -NoNewWindow -Wait -PassThru
exit $p.ExitCode
