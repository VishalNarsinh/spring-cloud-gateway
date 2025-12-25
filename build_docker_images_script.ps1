Write-Host "Starting Jib docker image build..." -ForegroundColor Cyan

Get-ChildItem -Directory | ForEach-Object{
    if(Test-Path "$($_.FullName)\pom.xml"){
        Write-Host "Building Jib docker image for $($_.Name)..." -ForegroundColor Green
        Push-Location $_.FullName
        mvn compile jib:dockerBuild
        Pop-Location
    }
}

Write-Host "Jib docker image build completed." -ForegroundColor Cyan
