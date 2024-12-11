def executeRF(def script) {

    script.env.RF_RESULT_DIR = "workspace/" + script.env.JOB_NAME + "/" + script.env.RF_OUTPUT_DIR

    def DOCKER_HOST_PORT = "nexus.qa.ps.arris.com:9001"	
    def DOCKER_IMAGE = "robot-docker"	
    def DOCKER_IMAGE_TAG = "1"	
    script.env.DOCKER_IMAGE_FULL = DOCKER_HOST_PORT + "/" + DOCKER_IMAGE + ":" + DOCKER_IMAGE_TAG
    script.env.DOCKER_CONTAINER = "robot-docker-container"	+ "-" + script.env.LOGIN_USER
    script.env.LOCAL_WORKSPACE = script.env.WORKSPACE.split("workspace")[0]

    if (script.params.TestSuiteName.contains("ECOControl")) {
        script.env.ACS = "ECOControl"
    } else {
        script.env.ACS = "ECOManage"
    }
    
    if (script.params.TestSuiteName != "") {
        if (script.params.TestCaseRunFilter1 == "") {
            script.env.TestCaseRunFilter = script.params.TestCaseRunFilter2
        } else {
            script.env.TestCaseRunFilter = script.params.TestCaseRunFilter1.split(",").collect { "-t " + it }.join(" ")
        }

        if (script.isUnix()) {
            script.sh '''
                exeTCInDocker () {
                    echo Start Robot Framework automation in docker container
                    if [ -z "${TestDataFileName}" ] 
                    then
                        docker exec ${DOCKER_CONTAINER} robot -v BROWSER_HEADLESS:True ${TestCaseRunFilter} --outputdir "${RF_RESULT_DIR}" --log ${RF_PREFIX}_log.html --output ${RF_PREFIX}_output.xml --report ${RF_PREFIX}_report.html --xunit ${RF_PREFIX}_junit.xml workspace/ECOManageRobotProject/TestSuite_ECO${CUSTOMER}/TestSuite_${ACS}/TestSuite_${TestSuiteName}.robot                    			
                    else
                        docker exec ${DOCKER_CONTAINER} robot -v BROWSER_HEADLESS:True ${TestCaseRunFilter} --outputdir "${RF_RESULT_DIR}" --log ${RF_PREFIX}_log.html --output ${RF_PREFIX}_output.xml --report ${RF_PREFIX}_report.html --xunit ${RF_PREFIX}_junit.xml --variablefile ${TestDataDir}/${TestDataFileName}.py workspace/ECOManageRobotProject/TestSuite_ECO${CUSTOMER}/TestSuite_${ACS}/TestSuite_${TestSuiteName}.robot
                    fi
                }

                echo --------------------------------------------
                echo Remove existing ${RF_OUTPUT_DIR}
                echo --------------------------------------------	
                rm -r -f ${RF_OUTPUT_DIR}                    										
                
                echo --------------------------------------------
                echo Check if the docker container ${DOCKER_CONTAINER} is running
                echo --------------------------------------------
                set +e
                container_status=$( docker container inspect -f '{{.State.Status}}' ${DOCKER_CONTAINER} )
                set -e
                if [ "${container_status}" = "running" ]
                then
                    echo --------------------------------------------
                    echo The docker container ${DOCKER_CONTAINER} is running
                    echo Start Robot Framework automation in docker container	
                    echo --------------------------------------------
                    exeTCInDocker
                elif [ "${container_status}" = "exited" ]
                then
                    echo --------------------------------------------
                    echo The docker container ${DOCKER_CONTAINER} is stopped
                    echo Start the stopped docker container ${DOCKER_CONTAINER}
                    echo --------------------------------------------	
                    docker start ${DOCKER_CONTAINER}	
                    exeTCInDocker
                else
                    echo --------------------------------------------
                    echo The docker container ${DOCKER_CONTAINER} is not running
                    echo Check if docker is installed
                    echo --------------------------------------------
                    if [ -z "$( docker version )" ]
                    then
                        echo --------------------------------------------
                        echo Docker is not installed
                        echo No test case execution
                        echo --------------------------------------------
                    else
                        echo --------------------------------------------
                        echo Start running docker container ${DOCKER_CONTAINER} from image ${DOCKER_IMAGE_FULL}
                        echo --------------------------------------------
                        docker run --name ${DOCKER_CONTAINER} -v ${LOCAL_WORKSPACE}:/usr/src:z -d -t ${DOCKER_IMAGE_FULL}	
                        exeTCInDocker
                    fi
                fi
                '''    
        } else { //Windows
            script.bat '''
                SETLOCAL ENABLEDELAYEDEXPANSION

                echo --------------------------------------------
                echo Remove existing %OUTPUT_DIR%
                echo --------------------------------------------
                IF EXIST %OUTPUT_DIR% RD /S /Q %OUTPUT_DIR%

                echo --------------------------------------------
                echo Check if the docker container %DOCKER_CONTAINER% is running
                echo --------------------------------------------
                set "containter_status="
                for /f %%i in ('docker inspect -f '{{.State.Status}}' %DOCKER_CONTAINER%') do set containter_status=%%i
                if "!containter_status!" == "'running'" (
                    echo --------------------------------------------
                    echo The docker container %DOCKER_CONTAINER% is running
                    echo --------------------------------------------							
                    goto exeTCInDocker
                ) else (
                    if "!containter_status!" == "'exited'" (
                        echo --------------------------------------------
                        echo The docker container %DOCKER_CONTAINER% is stopped
                        echo Start the stopped docker container %DOCKER_CONTAINER%
                        echo --------------------------------------------	
                        docker start %DOCKER_CONTAINER%								
                    ) else (
                        echo --------------------------------------------
                        echo The docker container %DOCKER_CONTAINER% is not running
                        echo Check if docker is installed
                        echo --------------------------------------------
                        set "docker_installed="
                        for /f %%j in ('docker version ^| findstr Server') do set docker_installed=%%j

                        if "!docker_installed!" == "" (
                            echo --------------------------------------------
                            echo Docker is not installed
                            echo Start Robot Framework automation without docker container
                            echo --------------------------------------------
                            goto exeTC	
                        ) else (
                            echo --------------------------------------------
                            echo Start running docker container %DOCKER_CONTAINER% from image %DOCKER_IMAGE_FULL%
                            echo --------------------------------------------
                            docker run --name %DOCKER_CONTAINER% -v %LOCAL_WORKSPACE%:/usr/src -d -t %DOCKER_IMAGE_FULL%	
                            goto exeTCInDocker
                        )
                    )
                )

                :exeTCInDocker
                echo Start Robot Framework automation in docker container
                if "%TestDataFileName%" == "" (
                    docker exec %DOCKER_CONTAINER% robot -v BROWSER_HEADLESS:True %TestCaseRunFilter% --outputdir "%RF_RESULT_DIR%" --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml workspace/ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
                ) else (
                    docker exec %DOCKER_CONTAINER% robot -v BROWSER_HEADLESS:True %TestCaseRunFilter% --outputdir "%RF_RESULT_DIR%" --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml --variablefile %TestDataDir%/%TestDataFileName%.py workspace/ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
                )
                goto :eof

                :exeTC
                echo Start Robot Framework automation
                if "%TestDataFileName%" == "" (
                    robot %TestCaseRunFilter% --outputdir %OUTPUT_DIR% --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml ../ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
                ) else (
                    robot %TestCaseRunFilter% --outputdir %OUTPUT_DIR% --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml --variablefile ../../%TestDataDir%/%TestDataFileName%.py ../ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
                )
                goto :eof

                :eof
                '''
        }
    }   
}

def executeRFMultiple(def script, dataList) {
    println(dataList)
    script.env.RF_RESULT_DIR = "workspace/" + script.env.JOB_NAME + "/" + script.env.RF_OUTPUT_DIR

    def DOCKER_HOST_PORT = "nexus.qa.ps.arris.com:9001"	
    def DOCKER_IMAGE = "robot-docker"	
    def DOCKER_IMAGE_TAG = "1"	
    script.env.DOCKER_IMAGE_FULL = DOCKER_HOST_PORT + "/" + DOCKER_IMAGE + ":" + DOCKER_IMAGE_TAG
    script.env.DOCKER_CONTAINER = "robot-docker-container"	+ "-" + script.env.LOGIN_USER

    /*
    ########################################################################
    Start checking docker container
    ########################################################################
    */  
    if (script.isUnix()) {
        sh '''
            echo --------------------------------------------
            echo Remove existing ${RF_OUTPUT_DIR}
            echo --------------------------------------------
            rm -r -f "${RF_OUTPUT_DIR}"

            echo --------------------------------------------
            echo Check if the docker container ${DOCKER_CONTAINER} is running
            echo --------------------------------------------
            set +e
            container_status=$( docker container inspect -f '{{.State.Status}}' ${DOCKER_CONTAINER} )
            set -e
            if [ "${container_status}" = "running" ]
            then
                echo --------------------------------------------
                echo The docker container ${DOCKER_CONTAINER} is running
                echo --------------------------------------------
            elif [ "${container_status}" = "exited" ]
            then
                echo --------------------------------------------
                echo The docker container ${DOCKER_CONTAINER} is stopped
                echo Start the stopped docker container ${DOCKER_CONTAINER}
                echo --------------------------------------------	
                docker start ${DOCKER_CONTAINER}
            else
                echo --------------------------------------------
                echo Start running docker container ${DOCKER_CONTAINER} from image ${DOCKER_IMAGE_FULL}
                echo --------------------------------------------
                docker run --name ${DOCKER_CONTAINER} -v ${LOCAL_WORKSPACE}:/usr/src:z -d -t ${DOCKER_IMAGE_FULL}
            fi                            
        '''
    } else {
        bat '''
            SETLOCAL ENABLEDELAYEDEXPANSION

            echo --------------------------------------------
            echo Remove existing %RF_OUTPUT_DIR%
            echo --------------------------------------------
            IF EXIST %RF_OUTPUT_DIR% RD /S /Q %RF_OUTPUT_DIR%

            echo --------------------------------------------
            echo Check if the docker container %DOCKER_CONTAINER% is running
            echo --------------------------------------------
            set "containter_status="
            for /f %%i in ('docker inspect -f '{{.State.Status}}' %DOCKER_CONTAINER%') do set containter_status=%%i
            if "!containter_status!" == "'running'" (                             
                echo --------------------------------------------
                echo The docker container %DOCKER_CONTAINER% is running
                echo --------------------------------------------
            ) else (
                if "!containter_status!" == "'exited'" (
                    echo --------------------------------------------
                    echo The docker container %DOCKER_CONTAINER% is stopped
                    echo Start the stopped docker container %DOCKER_CONTAINER%
                    echo --------------------------------------------	
                    docker start %DOCKER_CONTAINER%	
                ) else (
                    echo --------------------------------------------
                    echo Start running docker container %DOCKER_CONTAINER% from image %DOCKER_IMAGE_FULL%
                    echo --------------------------------------------
                    docker run --name %DOCKER_CONTAINER% -v %LOCAL_WORKSPACE%:/usr/src -d -t %DOCKER_IMAGE_FULL%	
                )
            )                        
        '''
    }

    // Execute in docker
    for ( i = 0; i < dataList.size(); i += 5) {
        script.env.TestDataFileName = dataList[i]
        script.env.TestSuiteName = dataList[i+1]
        script.env.TestCaseRunFilter = dataList[i+2]
        script.env.ProjectCycle = dataList[i+3]
        script.env.ProjectFolder = dataList[i+4]                
        script.env.RF_PREFIX = script.env.TestSuiteName + "_" + script.env.ProjectCycle.replaceAll(" ", "") + "_" + script.env.ProjectFolder.replaceAll(" ", "")

        if (script.env.TestSuiteName.contains("ECOControl")) {
            script.env.ACS = "ECOControl"
        } else {
            script.env.ACS = "ECOManage"
        }                        

        echo "Create stage: ${script.env.TestSuiteName} - ${script.env.ProjectCycle}"
        stage(script.env.TestSuiteName + "-" + script.env.ProjectCycle) {
            catchError(stageResult: 'FAILURE') {
                if (script.isUnix()) {
                    sh '''
                    docker exec ${DOCKER_CONTAINER} robot -v BROWSER_HEADLESS:True ${TestCaseRunFilter} --outputdir "${RF_RESULT_DIR}" --log ${RF_PREFIX}_log.html --output ${RF_PREFIX}_output.xml --report ${RF_PREFIX}_report.html --xunit ${RF_PREFIX}_junit.xml --variablefile ${TestDataDir}/${TestDataFileName}.py workspace/ECOManageRobotProject/TestSuite_ECO${CUSTOMER}/TestSuite_${ACS}/TestSuite_${TestSuiteName}.robot
                    '''
                } else {
                    bat '''
                    docker exec %DOCKER_CONTAINER% robot -v BROWSER_HEADLESS:True %TestCaseRunFilter% --outputdir "%RF_RESULT_DIR%" --log %RF_PREFIX%_log.html --output %RF_PREFIX%_output.xml --report %RF_PREFIX%_report.html --xunit %RF_PREFIX%_junit.xml --variablefile %TestDataDir%/%TestDataFileName%.py workspace/ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
                    '''
                }  
            }
        }
    }

    // Combine all the test results
    if (script.isUnix()) {
        sh 'docker exec ${DOCKER_CONTAINER} rebot --processemptysuite --outputdir "${RF_RESULT_DIR}" --output Final_output.xml --report Final_report.html --name Final_output.xml ${RF_RESULT_DIR}/*_output.xml'
    } else {
        bat 'docker exec %DOCKER_CONTAINER% rebot --processemptysuite --outputdir "%RF_RESULT_DIR%" --output Final_output.xml --report Final_report.html --name Final_output.xml %RF_RESULT_DIR%/*_output.xml'
    }  
}