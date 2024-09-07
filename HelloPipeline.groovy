import groovy.json.JsonSlurper

import java.lang.reflect.*;
import jenkins.model.Jenkins;
import jenkins.model.*;
import org.jenkinsci.plugins.scriptsecurity.scripts.*;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.*;
import java.lang.reflect.Method

def generateStyledHtmlTable(String jsonContent) {
    def jsonSlurper = new JsonSlurper()
    def upgradeData = jsonSlurper.parseText(jsonContent)

    println "Data[0]: ${upgradeData[0]}"
    println "Data[1]: ${upgradeData[1]}"
    println "Data[2]: ${upgradeData[2]}"

    def jobDetails=upgradeData[0]
    def topologiesData=upgradeData[1]
    def summary=upgradeData[2]
    println topologiesData
    println "done"
    // Extract the headers from the JSON content
    def tableHeaders = topologiesData.remove("Table_Headers") // Remove headers from data to prevent them from being processed as topologies
    def topologies = new LinkedHashMap<String, Map<String, Map<String, String>>>()

    // Process JSON content
    topologiesData.each { key, value ->
        def (upgradeType, mdr) = key.split('_', 3).with { [it[0] +"_"+ it[1], it[2].replace('_MDR', '')] } // Extract upgrade type and MDR, remove '_Mdr' suffix
        value.each { topology, status ->
            topologies.computeIfAbsent(topology) { [:] }.computeIfAbsent(mdr) { [:] }[upgradeType] = status
        }
    }

    def headers = ""
    tableHeaders.each {
        headers += "<th>${it}</th>"
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
            margin: 20px auto; /* Center the table */
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
/* Style for the specific summary table */

    /* Style for the specific vertical table */
        .vertical-table {
            border-collapse: collapse;
            width: 50%;
            margin: 20px auto; /* Center the table */
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
    /* Style for the specific vertical table */

            .styled-table {
                border-collapse: collapse;
                width: 100%;
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
            ${headers}
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
        mdrMap.eachWithIndex { mdrEntry, index ->
            def mdr = mdrEntry.key
            def upgrades = mdrEntry.value
            htmlContent << "<tr>"
            if (index == 0) {
                htmlContent << "<td rowspan='${mdrMap.size()}'>${topology}</td>"
            }
            htmlContent << "<td>${mdr}</td>"
            tableHeaders[2..-1].each { header ->
                htmlContent << "<td>${formatStatus(upgrades[header])}</td>"
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
    approval()
    def htmlOutput = generateStyledHtmlTable(jsonContent)
    println htmlOutput
    return htmlOutput
}



def approval() {
println "///////////////////////////////////////////////////////////////////////////"
println "Init Hook: Whitelist methods/functions for script approval plugin"
println "///////////////////////////////////////////////////////////////////////////"


def scriptApproval = ScriptApproval.get()

// whitelist all org.codehaus.groovy.runtime.DefaultGroovyMethods methods.
Class c = org.codehaus.groovy.runtime.DefaultGroovyMethods.class;
Method[] methods = c.getDeclaredMethods();
for (int i = 0; i < methods.length; i++){
    def m = methods[i]

    //WARNING: this may white list some methods that may introduce security vulnerabilities, you should manually validate this list.
    scriptApproval.approveSignature("staticMethod ${EnumeratingWhitelist.getName(m.getDeclaringClass())} ${m.getName()}${printArgumentTypes(m.getParameterTypes())}")
}

// add all manual whitelist methods here.
scriptApproval.approveSignature('field hudson.model.AbstractItem name')
scriptApproval.approveSignature('field hudson.model.Cause$UpstreamCause upstreamCauses')
scriptApproval.approveSignature('field java.util.HashMap$Entry key')
scriptApproval.approveSignature('field java.util.HashMap$Entry value')
scriptApproval.approveSignature('method groovy.json.JsonBuilder call java.util.List')
scriptApproval.approveSignature('method groovy.json.JsonSlurper parseText java.lang.String')
scriptApproval.approveSignature('method groovy.json.JsonSlurperClassic parseText')
scriptApproval.approveSignature('method hudson.model.Actionable getAction java.lang.Class')
scriptApproval.approveSignature('method hudson.model.Actionable getActions')
scriptApproval.approveSignature('method hudson.model.ItemGroup getItem java.lang.String')
scriptApproval.approveSignature('method hudson.model.Item getUrl')
scriptApproval.approveSignature('method hudson.model.Job getBuildByNumber int')
scriptApproval.approveSignature('method hudson.model.Job getLastBuild')
scriptApproval.approveSignature('method hudson.model.Job getLastSuccessfulBuild')
scriptApproval.approveSignature('method hudson.model.Job isBuilding')
scriptApproval.approveSignature('method hudson.model.Run getCauses')
scriptApproval.approveSignature('method hudson.model.Run getEnvironment hudson.model.TaskListener')
scriptApproval.approveSignature('method hudson.model.Run getParent')
scriptApproval.approveSignature('method hudson.model.Run getNumber')
scriptApproval.approveSignature('method hudson.model.Run getResult')
scriptApproval.approveSignature('method hudson.model.Run getUrl')
scriptApproval.approveSignature('method hudson.model.Run getLogFile')
scriptApproval.approveSignature('method java.io.InputStream getText')
scriptApproval.approveSignature('method java.lang.Appendable append java.lang.CharSequence')
scriptApproval.approveSignature('method java.lang.Class isInstance java.lang.Object')
scriptApproval.approveSignature('method java.lang.Object clone')
scriptApproval.approveSignature('method java.lang.Object getClass')
scriptApproval.approveSignature('method java.lang.String contains java.lang.CharSequence')
scriptApproval.approveSignature('method java.lang.String getBytes')
scriptApproval.approveSignature('method java.lang.String replaceAll java.lang.String java.lang.String')
scriptApproval.approveSignature('method java.lang.String toURL')
scriptApproval.approveSignature('method java.lang.String trim')
scriptApproval.approveSignature('method java.lang.Throwable getMessage')
scriptApproval.approveSignature('method java.net.URL getContent')
scriptApproval.approveSignature('method java.net.URL getText')
scriptApproval.approveSignature('method java.security.MessageDigest digest byte[]')
scriptApproval.approveSignature('method java.text.Format format java.lang.Object')
scriptApproval.approveSignature('method java.util.Collection remove java.lang.Object')
scriptApproval.approveSignature('method java.util.Collection removeAll java.util.Collection')
scriptApproval.approveSignature('method java.util.Collection retainAll java.util.Collection')
scriptApproval.approveSignature('method java.util.Map containsKey java.lang.Object')
scriptApproval.approveSignature('method java.util.Map entrySet')
//... Any others you would like to manually whitelist.

scriptApproval.save()
}
// Utility methods
String printArgumentTypes(Object[] args) {
    StringBuilder b = new StringBuilder();
    for (Object arg : args) {
        b.append(' ');
        b.append(EnumeratingWhitelist.getName(arg));
    }
    return b.toString();
}



return this

