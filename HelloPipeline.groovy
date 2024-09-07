import groovy.json.JsonSlurper

def generateStyledHtmlTable(String jsonContent) {
    def jsonSlurper = new JsonSlurper()
    def upgradeData = jsonSlurper.parseText(jsonContent)

    println "Data[0]: ${upgradeData[0]}"
    println "Data[1]: ${upgradeData[1]}"
    println "Data[2]: ${upgradeData[2]}"

    def jobDetails = upgradeData[0]
    def topologiesData = upgradeData[1]
    def summary = upgradeData[2]

    // Extract the headers from the JSON content
    def tableHeaders = topologiesData.remove("Table_Headers")
    def topologies = new LinkedHashMap<String, Map<String, Map<String, String>>>()

    // Process JSON content
    topologiesData.each { key, value ->
        def (upgradeType, mdr) = key.split('_', 3).with { [it[0] + "_" + it[1], it[2].replace('_MDR', '')] } // Extract upgrade type and MDR
        value.each { topology, status ->
            if (!topologies.containsKey(topology)) {
                topologies[topology] = [:]
            }
            if (!topologies[topology].containsKey(mdr)) {
                topologies[topology][mdr] = [:]
            }
            topologies[topology][mdr][upgradeType] = status
        }
    }

    // Generate HTML table with CSS
    def htmlContent = new StringBuilder()
    htmlContent << """
    <html>
    <head>
        <style>
            body {
                font-family: Arial, sans-serif;
            }

            /* Style for the specific summary table */
            .summary-table {
                border-collapse: collapse;
                width: 50%;
                margin: 20px auto;
                font-family: Arial, sans-serif;
            }

            .summary-table th, .summary-table td {
                border: 1px solid #dddddd;
                text-align: center;
                padding: 8px;
            }

            .summary-table th {
                background-color: #4CAF50;
                color: white;
            }

            .summary-table tr:nth-child(even) {
                background-color: #f2f2f2;
            }

            .summary-table td.passed {
                color: green;
                font-weight: bold;
            }

            .summary-table td.failed {
                color: red;
                font-weight: bold;
            }

            /* Style for the specific vertical table */
            .vertical-table {
                border-collapse: collapse;
                width: 50%;
                margin: 20px auto;
                font-family: Arial, sans-serif;
            }

            .vertical-table th, .vertical-table td {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
            }

            .vertical-table th {
                background-color: #4CAF50;
                color: white;
                width: 30%;
            }

            .vertical-table tr:nth-child(even) {
                background-color: #f2f2f2;
            }

            /* Style for the specific table */
            .styled-table {
                border-collapse: collapse;
                width: 100%;
                margin: 20px auto;
            }

            .styled-table th, .styled-table td {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
            }

            .styled-table tr:nth-child(even) {
                background-color: #f2f2f2;
            }

            .styled-table th {
                background-color: #4CAF50;
                color: white;
            }

            .status-passed {
                color: green;
                font-weight: bold;
            }

            .status-failed {
                color: red;
                font-weight: bold;
            }
        </style>
    </head>
    <body>

    <table class="vertical-table">
        <tr><th>JOB_PARAMETERS</th><th>PARAMETER_VALUES</th></tr>
        <tr><td>TestBranch</td><td>${jobDetails.TestBranch}</td></tr>
        <tr><td>JobLink</td><td>${jobDetails.JobLink}</td></tr>
        <tr><td>PreVersion</td><td>${jobDetails.PreVersion}</td></tr>
        <tr><td>PreVersionJdk</td><td>${jobDetails.PreVersionJdk}</td></tr>
        <tr><td>PostVersion</td><td>${jobDetails.PostVersion}</td></tr>
        <tr><td>PostVersionJdk</td><td>${jobDetails.PostVersionJdk}</td></tr>
        <tr><td>OS</td><td>${jobDetails.OS}</td></tr>
    </table>

    <br><br>

    <!-- Table for TOTAL, PASSED, FAILED summary -->
    <table class="summary-table">
        <tr>
            <th>TOTAL</th>
            <th>PASSED</th>
            <th>FAILED</th>
        </tr>
        <tr>
            <td>${summary.Total}</td>
            <td>${summary.Passed}</td>
            <td>${summary.Failed}</td>
        </tr>
    </table>

    <br><br>

    <table class="styled-table">
        <tr>
              ${tableHeaders.collect { "<th>${it}</th>" }.inject(new StringBuilder()) { sb, th -> sb.append(th) }.toString()}
        </tr>
    """

    // Helper function to format status
    def formatStatus = { status ->
        switch(status) {
            case "PASSED":
                return "<span class='status-passed'>&#10004;</span>"  // Green check mark ✔
            case "FAILED":
                return "<span class='status-failed'>&#10008;</span>"  // Red cross mark ✘
            default:
                return ""  // Empty cell for missing status
        }
    }

    // Generate table rows with intelligent merging of topology names
    topologies.each { topology, mdrMap ->
        def mdrList = mdrMap.collect { [it.key, it.value] }
        mdrList.eachWithIndex { mdrEntry, index ->
            def mdr = mdrEntry[0]
            def upgrades = mdrEntry[1]
            htmlContent << "<tr>"
            if (index == 0) {
                htmlContent << "<td rowspan='${mdrList.size()}'>${topology}</td>"
            }
            htmlContent << "<td>${mdr}</td>"
            tableHeaders[2..-1].each { header ->
                println("upg header ${upgrades[header]}")
                htmlContent << "<td>${formatStatus(upgrades[header] ?: '')}</td>"
            }
            htmlContent << "</tr>\n"
        }
    }

    htmlContent << """
    </table>
    </body>
    </html>
    """

    return htmlContent.toString()
}

def getSummary() {

// Example runtime input
    def jsonContent = '''[
{ "TestBranch": "develop","JobLink": "https://jenkins-02.striim.com/job","PreVersion": "4.1.2","PreVersionJdk": "jdk-8","PostVersion": "4.2.0","PostVersionJdk": "jdk-11","OS": "ubuntu24" }
,
 {
    "Table_Headers":["TOPOLOGIES","MDR","RELEASE_UPGRADE","MANAGEABLE_UPGRADE","PHYSICAL_UPGRADE","PATCHSET_UPGRADE","SELF_UPGRADE"],
    "RELEASE_UPGRADE_DERBY_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "FAILED"
    },
       "SELF_UPGRADE_DERBY_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "FAILED"
    },
      "PATCHSET_UPGRADE_DERBY_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "FAILED"
    },
    "PHYSICAL_UPGRADE_DERBY_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "FAILED"
    },
    "RELEASE_UPGRADE_ORACLE_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "PASSED"
    },
      "RELEASE_UPGRADE_POSTGRES_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "PASSED"
    },
    
        "MANAGEABLE_UPGRADE_DERBY_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "PASSED"
    },
    "MANAGEABLE_UPGRADE_ORACLE_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "PASSED"
    },
      "MANAGEABLE_UPGRADE_POSTGRES_MDR": {
        "OracleToBq": "PASSED",
        "ORacleToOracle": "PASSED",
        "AdsGen1": "PASSED",
        "MysqlToBq": "FAILED",
        "MysqlToMssql": "FAILED",
        "MssqlToOracle": "FAILED",
        "MssqlToMssql": "PASSED"
    }
    
},
{"Total":100,"Passed":60,"Failed" :40}
]'''

// Generate HTML at runtime
    def htmlOutput = generateStyledHtmlTable(jsonContent)
    println htmlOutput
    return htmlOutput
}

return this

