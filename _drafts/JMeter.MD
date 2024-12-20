## localhost

```ps
$jmxfile = 'localhost.jmx'
$output = './output/' + (Get-Date -Format "yyyyMMdd_HHmmss")
jmeter `
    --nongui `
    --testfile ./jmx/$jmxfile `
    --logfile ./$output/results.txt `
    --jmeterlogfile ./$output/jmeter.log `
    --reportatendofloadtests `
    --reportoutputfolder ./$output/report
```

## dev

```ps
$jmxfile = 'dev.jmx'
$output = './output/' + (Get-Date -Format "yyyyMMdd_HHmmss")
jmeter `
    --nongui `
    --testfile ./jmx/$jmxfile `
    --logfile ./$output/results.txt `
    --jmeterlogfile ./$output/jmeter.log `
    --reportatendofloadtests `
    --reportoutputfolder ./$output/report `
    -Djavax.net.ssl.keyStore=$env:CERTPATH `
    -Djavax.net.ssl.keyStorePassword=$env:CERTPASS
```
