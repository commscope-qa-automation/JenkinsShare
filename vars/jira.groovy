def strGetRequest() {
	return '''
		def sendGetRequest(url, username, password) {
			def conn = new URL(url).openConnection()
			conn.setRequestProperty("Content-Type", "application/json")
			conn.setRequestProperty("Accept", "application/json")
			conn.setRequestProperty("Authorization", "Basic " + (username+":"+password).bytes.encodeBase64().toString())
			conn.requestMethod = 'GET'
			def code = conn.getResponseCode()
			def content = conn.getInputStream().getText()
			return [code, content]
		}
	'''
}

def strJiraProjectVersion() {
	return '''
		import jenkins.model.Jenkins
		import hudson.model.User
		import groovy.json.JsonOutput
		import groovy.json.JsonSlurper
		
		def user = User.current().getId()
		def filename = "/tmp/project_version_" + user + ".json"

		if (RetrieveProjectVersion == "Retrieve Project Version") {
			// Get Etrack username and password from global credentials
			def username = ""
			def password = ""
			def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
				com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
				Jenkins.instance,
			null,
			null
		)
		for (c in creds) {
			if (c.id == "etrack_creds_" + user) {
				username = c.username
				password = c.password
			}
		}   
		
		def jiraBaseURL = "https://odart.arrisi.com"
		def project = "ECOIOT"
	
		def projectId = ""
		def versionId = ""
		def versionNameId= [:]
		def versionList = []
		
		def jsonSlurper = new JsonSlurper()
		def url = jiraBaseURL+"/rest/api/latest/project/"+project+"/versions"
		content = sendGetRequest(url, username, password)
		jsonResponse = jsonSlurper.parseText(content)
		jsonResponse.each {
			if (it.released == false) {
				versionList.add(it.name)
				projectId = it.projectId
				versionId = it.id
				versionNameId.put(it.name, versionId)
			}
		}
	
		def data = [
			projectId: projectId,
			versionNameId: versionNameId
		]
		def json_str = JsonOutput.toJson(data)
		def json_beauty = JsonOutput.prettyPrint(json_str)                            	
			
		File file = new File(filename)
		file.write(json_beauty)
		
		return versionList
	}                    
	'''
}


def strJiraProjectCycle() {
	return '''
		import jenkins.model.Jenkins
		import hudson.model.User
		import groovy.json.JsonOutput
		import groovy.json.JsonSlurper
		
		def user = User.current().getId()
		def filename = "/tmp/project_version_" + user + ".json"

		def jsonSlurper = new JsonSlurper()
		def data = jsonSlurper.parse(new File(filename))
		
		def jiraBaseURL = "https://odart.arrisi.com"
		def projectId =  data["projectId"]
		def versionId = data["versionNameId"][ProjectVersion]
		def cycleNameId = [:]
		def cycleNameList = []
		
		// Get Etrack username and password from global credentials
		def username = ""
		def password = ""
		def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
			com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
			Jenkins.instance,
			null,
			null
		)
		for (c in creds) {
			if (c.id == "etrack_creds_" + user) {
				username = c.username
				password = c.password
			}
		}
		
		def url = jiraBaseURL+"/rest/zapi/latest/cycle?projectId="+projectId+"&versionId="+versionId
		content = sendGetRequest(url, username, password)
		//assert code == 200
		
		jsonResponse = jsonSlurper.parseText(content)
		jsonResponse.each {
			if (it.key != "recordsCount") {
				cycleNameList.add(it.value.name)
				cycleNameId.put(it.value.name, it.key)
			}
		}
		
		def data1 = [
				projectId: projectId,
				versionId: versionId,
				cycleNameId: cycleNameId
			]
		def json_str = JsonOutput.toJson(data1)
		def json_beauty = JsonOutput.prettyPrint(json_str)
		filename = "/tmp/project_cycle_" + user + ".json"
		File file = new File(filename)
		file.write(json_beauty)
		
		return cycleNameList                   
	''' 
}

def strJiraProjectFolder() {
	return '''
		import jenkins.model.Jenkins
		import hudson.model.User
		import groovy.json.JsonOutput
		import groovy.json.JsonSlurper
		
		def user = User.current().getId()
		def filename = "/tmp/project_cycle_" + user + ".json"

		def jsonSlurper = new JsonSlurper()
		def data = jsonSlurper.parse(new File(filename))
		
		def jiraBaseURL = "https://odart.arrisi.com"
		def projectId =  data["projectId"]
		def versionId = data["versionId"]
		def cycleId = data["cycleNameId"][ProjectCycle]
		def folderNameList = [""]
		
		// Get Etrack username and password from global credentials
		def username = ""
		def password = ""
		def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
			com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
			Jenkins.instance,
			null,
			null
		)
		for (c in creds) {
			if (c.id == "etrack_creds_" + user) {
				username = c.username
				password = c.password
			}
		}
		
		def url = jiraBaseURL+"/rest/zapi/latest/cycle/"+cycleId+"/folders?projectId="+projectId+"&versionId="+versionId
		content = sendGetRequest(url, username, password)
		//assert code == 200
		
		jsonResponse = jsonSlurper.parseText(content)
		jsonResponse.each {
			folderNameList.add(it.folderName)
		}
		
		return folderNameList                   
	'''    
}

def sendGetRequest(url, username, password) {
	println("url = " + url)
	def conn = new URL(url).openConnection()
	conn.setRequestProperty("Content-Type", "application/json")
	conn.setRequestProperty("Accept", "application/json")
	conn.setRequestProperty("Authorization", "Basic " + (username+":"+password).bytes.encodeBase64().toString())
	conn.requestMethod = 'GET'
	def code = conn.getResponseCode()
	def content = conn.getInputStream().getText()
	//println("http code" + code)
	return [code, content]
}

def sendPutRequest(url, body, username, password) {
	println("url = " + url)
	println("body = " + body)
	def conn = new URL(url).openConnection()
	conn.setRequestProperty("Content-Type", "application/json")
	conn.setRequestProperty("Accept", "application/json")
	conn.setRequestProperty("Authorization", "Basic " + (username+":"+password).bytes.encodeBase64().toString())
	conn.requestMethod = 'PUT'

	conn.setDoOutput(true)
	OutputStream outStream = conn.getOutputStream()
	OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8")
	outStreamWriter.write(body)
	outStreamWriter.flush()
	outStreamWriter.close()
	outStream.close()

	def code = conn.getResponseCode()
	def content = conn.getInputStream().getText()
	//println("http code" + code)	
	return [code, content]
}  
					
def updateJiraTestCaseStatus(jenkinsContext) {
	/*
	########################################################################
	Start Etrack
	########################################################################
	*/
	/*
	##################################################
	Get values from build parameters
	##################################################
	*/
	def version = jenkinsContext.params.ProjectVersion
	def cycle = jenkinsContext.params.ProjectCycle
	def folder = jenkinsContext.params.ProjectFolder

	def junitFile = jenkinsContext.env.WORKSPACE + "/" + jenkinsContext.params.OutputDir + "/" + jenkinsContext.params.TestSuiteName + "_junit.xml"
	//println("junit xml file: " + junitFile)
	/*
	##################################################
	Start processing
	##################################################
	*/
	println("-----------------------------------------------------------------------------")
	if (version == "" || cycle == "") {
		println("Version or Cycle is not provided. No test case status update!!!")
		println("-----------------------------------------------------------------------------")
	} else {					
		// Get Etrack username and password from global credentials
		println("Get Etrack credentials.")
		//println("LOGIN_USER:" + LOGIN_USER)
		
		def username = ""
		def password = ""
		def creds = jenkinsContext.com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
			com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
			jenkins.model.Jenkins.instance,
			null,
			null
		)
		for (c in creds) {
			if (c.id == "etrack_creds_" + LOGIN_USER) {
				username = c.username
				password = c.password
			}
		}
		//println("username:" + username)
		//println("password:" + password)
		
		// Update JIRA test case status
		println("Start updating Etrack test case status.")
		def jiraBaseURL = "https://odart.arrisi.com"
		def project = "ECOIOT"
		
		// Read the junit file generated from Robot Framework
		def issue = ""
		def code = 0
		def content = ""
		def status = ""
		def projectId = ""
		def versionId = ""
		def cycleId = ""
		def folderId = ""
		def executionId = ""
		
		def jsonSlurper = new groovy.json.JsonSlurperClassic()
		// Get Project Version ID
		(code, content) = sendGetRequest(jiraBaseURL+"/rest/api/latest/project/"+project+"/versions", username, password)
		assert code == 200
		jsonResponse = jsonSlurper.parseText(content)
		projectId = jsonResponse.find {it.name == version}.projectId
		versionId = jsonResponse.find {it.name == version}.id
		
		//println("projectId = " + projectId)
		//println("versionId = " + versionId)
		
		(code, content) = sendGetRequest(jiraBaseURL+"/rest/zapi/latest/cycle?projectId="+projectId+"&versionId="+versionId, username, password)
		assert code == 200
		jsonResponse = jsonSlurper.parseText(content)
		cycleId = jsonResponse.findAll {it.key != "recordsCount"}.find{it.value.name == cycle}.key
		
		//println("cycleId = " + cycleId)
			
		// Get Folder ID						
		(code, content) = sendGetRequest(jiraBaseURL+"/rest/zapi/latest/cycle/"+cycleId+"/folders?projectId="+projectId+"&versionId="+versionId, username, password)
		assert code == 200
		jsonResponse = jsonSlurper.parseText(content)
		folderId = jsonResponse.find {it.folderName == folder}?.folderId
		if (folderId == null) {
			folderId = ""
		}
		
		//println("folderId = " + folderId)
		
		//Get execution id
		println("--------------------------------------------------")
		(code, content) = sendGetRequest(jiraBaseURL+"/rest/zapi/latest/execution?cycleId="+cycleId+"&folderId="+folderId, username, password)
		assert code == 200
		executions = jsonSlurper.parseText(content).executions
		if (executions.size() > 0) {
			def xml = readFile junitFile
			def testcases = new groovy.util.XmlParser().parseText(xml).value()
			//def testcases = new groovy.util.XmlParser().parseText(xml).testcase
			testcases.each {
				issue = it.attributes()['name']
				failure = it.value()
				//issue = it.@name
				//failure = it.failure
				executionId = executions.find {it.issueKey == issue}?.id
				if (executionId != null) {
					println("------------------------\nTest Case: " + issue + "\n------------------------")
					status = "1"
					if (!failure.isEmpty()) {
						status = "2"
					}
					body = '{"status": "' + status + '"}'
					(code, content) = sendPutRequest(jiraBaseURL+"/rest/zapi/latest/execution/"+executionId+"/execute", body, username, password)
					assert code == 200
				}
			}
			println("-----------------------------------------------------------------------------") 
		} else {
			println("No executions...")
		} 
	}
}
