$dockerUser = "vishalnarsinh"
$tag = "s9"

$services = @(
    "configserver",
    "eurekaserver"
    "accounts",
    "loans",
    "cards",
    "gatewayserver"
)

foreach($service in $services){
    $image = "${dockerUser}/${service}:${tag}"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n Pushing image: $image" -ForegroundColor Cyan
        docker push $image
    }
    else {
        Write-Host "`n⚠ Image not found locally: $image" -ForegroundColor Yellow
    }
}

Write-Host "`n All available images with tag '$tag' pushed successfully." -ForegroundColor Green
