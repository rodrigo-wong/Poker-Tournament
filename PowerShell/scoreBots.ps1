Set-Location -Path 'PowerShell'
Remove-Item -Path 'poker-submissions' -Recurse -Force -ErrorAction SilentlyContinue
New-Item -Path 'poker-submissions' -ItemType Directory

Set-Location -Path 'poker-submissions'

# Clone every repository in Github classroom
# Simulate cloning repository
Copy-Item -Path '../../github-repositories/poker-*' -Destination . -Recurse # test folder

New-Item -Name 'test' -ItemType Directory

Copy-Item -Path '../mog-poker/*.java' -Destination './test'

$compiledBots = ''

Get-ChildItem -Directory -Filter "poker-*" | ForEach-Object {
    Set-Location -Path "test" # Change the working directory to "test"

    $publicName = (Split-Path -Path $_.Name -Leaf).Substring("poker-".Length)

    # Delete all .class
    Remove-Item -Path "*.class"

    # Copy student's AI solution from the parent directory
    Copy-Item -Path "../$($_.Name)/$($publicName)Bot.java" -Destination .

    # Echo the directory name in PowerShell
    Write-Host "Compiling $($publicName) solution bot"

    # Compile students solution bot
    # javac pokerPlayer.java
    javac ($publicName+"Bot.java")

    # Check the exit code ($LASTEXITCODE) for the compile result
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compile failed, skipping bot."
    } else {
        $compiledBots += $publicName+'Bot;'
    }

    # Remove-Item "$($publicName)Bot.java" # Delete student's bot solution 

    Set-Location '../'
}

# Prints variable to the terminal
$compiledBots

# Move into test folder to run the tournament
Set-Location -Path 'test'

# Parse compiledBots into java files as arguments to run the tournament
# Compile Java program

# javac PokerTournament.java

# # Check if the compilation was successful
# if ($LASTEXITCODE -ne 0) {
#     Write-Host "Compile failed, skipping bot."
# } else {
#     Write-Host "Compiled"
#     # Array to store results
#     $results = @()

#     # Variables for parsing
#     $line = ''
#     $startSavingResults = $false

#     # Run Java program and parse output
#     java PokerTournament | ForEach-Object {
#         # Print each output line
#         $_
#         # Store the current line
#         $line = $_

#         # Check if the line contains "Results of table"
#         if ($line -match "Results of tournament") {
#             # Set $startSavingResults to true when the line is found
#             $startSavingResults = $true
#         }

#         # Check if $startSavingResults is true, then save the line to $results
#         if ($startSavingResults) {
#             $results += $line
#         }
#     }
# }

# Assume output look like this:
    # Results of table (2500 games, avg 0.021 sec each):
    # Table ran in PT51.8903624S
    # Player,Wins,2nd,3rd,4th,5th,6th,Average Bust Round
    # Shawn's Bot,425,1898,5,25,41,106,4
    # roBot,320,1888,7,28,51,206,6
    # PokerNewbBot,415,1901,16,20,37,111,4
    # aggressiveBot,333,1958,5,14,49,141,2
    # WinBot,396,1441,10,88,186,379,7
    # BradBot,611,1074,10,54,152,599,8
$results = @()
$results += "Results of table (2500 games, avg 0.021 sec each):"
$results += "Table ran in PT51.8903624S"
$results += "Player,Wins,2nd,3rd,4th,5th,6th,Average Bust Round"
$results += "Shawn's Bot,60,1898,5,25,41,106,4"
$results += "roBot,300,1888,7,28,51,206,6"

# cURL command
$url = "http://localhost:8888/poker/backend/updateData.php"

$headers = @{
    'Content-Type' = 'application/json'
}
for($i = 3 ; $i -lt $results.Count ; $i++) {
    $infoArray = $results[$i] -split ','
    $body = @{
        "name" = $infoArray[0]
        "wins" = $infoArray[1]
        "second" = $infoArray[2]
        "third" = $infoArray[3]
        "fourth" = $infoArray[4]
        "fifth" = $infoArray[5]
        "sixth" = $infoArray[6]
        "avg_bust_round" = $infoArray[7]
        "API_KEY" = "BotSteve@2023"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri $url -Method PUT -Body $body -Headers $headers
    $response
}

# Store the output into a variable called $results
# $results;



Set-Location -Path "../../"