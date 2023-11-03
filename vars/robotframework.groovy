def copyTestDataFilesWindows(workFlowScript wfs) {
    wfs.bat '''
        if exist "%LOCAL_WORKSPACE%TestData_Custom" ( 
            echo TestData directory %LOCAL_WORKSPACE%TestData_Custom exists 								
        ) else ( 
            echo Copy %LOCAL_WORKSPACE%workspace/ECOManageRobotProject/TestData_Data/TestData_ to %LOCAL_WORKSPACE%TestData_Custom
            xcopy "%LOCAL_WORKSPACE%workspace/ECOManageRobotProject/TestData_Custom" "%LOCAL_WORKSPACE%TestData_Custom" /E /I
        )
    '''

}

def executeRFLinux(workFlowScript wfs) {
    def TestSuiteName = wfs.params.TestSuiteName
    def TestCaseRunFilter = wfs.env.TestCaseRunFilter
    def LOCAL_WORKSPACE = wfs.env.WORKSPACE.split("workspace")[0]
    def RESULT_DIR="workspace/" + wfs.env.JOB_NAME + "/" + wfs.params.OutputDir	
    def PREFIX = wfs.evn.PREFIX	
    def CUSTOMER = wfs.env.CUSTOMER			

    def ACS = ""
    if (TestSuiteName.contains("ECOControl")) {
        ACS = "ECOControl"
    } else {
        ACS = "ECOManage"
    }

    def DOCKER_HOST_PORT = "nexus.qa.ps.arris.com:9001"	
    def DOCKER_IMAGE = "robot-docker"	
    def DOCKER_IMAGE_TAG = "1"	
    def DOCKER_IMAGE_FULL = DOCKER_HOST_PORT + "/" + DOCKER_IMAGE + ":" + DOCKER_IMAGE_TAG
    def DOCKER_CONTAINER = "robot-docker-container"	+ "-" + wfs.env.LOGIN_USER

    wfs.sh '''
    exeTCInDocker () {
        echo Start Robot Framework automation in docker container
        if [ -z "${TestDataFileName}" ] 
        then
            docker exec ${DOCKER_CONTAINER} robot -v BROWSER_HEADLESS:True ${TestCaseRunFilter} --outputdir "${RESULT_DIR}" --log ${PREFIX}_log.html --output ${PREFIX}_output.xml --report ${PREFIX}_report.html --xunit ${PREFIX}_junit.xml workspace/ECOManageRobotProject/TestSuite_ECO${CUSTOMER}/TestSuite_${ACS}/TestSuite_${TestSuiteName}.robot                    			
        else
            docker exec ${DOCKER_CONTAINER} robot -v BROWSER_HEADLESS:True ${TestCaseRunFilter} --outputdir "${RESULT_DIR}" --log ${PREFIX}_log.html --output ${PREFIX}_output.xml --report ${PREFIX}_report.html --xunit ${PREFIX}_junit.xml --variablefile ${TestDataDir}/${TestDataFileName}.py workspace/ECOManageRobotProject/TestSuite_ECO${CUSTOMER}/TestSuite_${ACS}/TestSuite_${TestSuiteName}.robot
        fi
    }

    echo --------------------------------------------
    echo Remove existing ${OutputDir}
    echo --------------------------------------------	
    rm -r -f ${OutputDir}                    										
    
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
}

def executeRFWindows(workFlowScript wfs) {
    def TestSuiteName = wfs.params.TestSuiteName
    def TestCaseRunFilter = wfs.env.TestCaseRunFilter
    def LOCAL_WORKSPACE = env.WORKSPACE.split("workspace")[0]
    def RESULT_DIR="workspace/" + wfs.env.JOB_NAME + "/" + wfs.params.OutputDir	
    def PREFIX = wfs.evn.PREFIX	
    def CUSTOMER = wfs.env.CUSTOMER			

    def ACS = ""
    if (TestSuiteName.contains("ECOControl")) {
        ACS = "ECOControl"
    } else {
        ACS = "ECOManage"
    }

    def DOCKER_HOST_PORT = "nexus.qa.ps.arris.com:9001"	
    def DOCKER_IMAGE = "robot-docker"	
    def DOCKER_IMAGE_TAG = "1"	
    def DOCKER_IMAGE_FULL = DOCKER_HOST_PORT + "/" + DOCKER_IMAGE + ":" + DOCKER_IMAGE_TAG
    def DOCKER_CONTAINER = "robot-docker-container"	+ "-" + wfs.env.LOGIN_USER

    wfs.bat '''
        SETLOCAL ENABLEDELAYEDEXPANSION

        echo --------------------------------------------
        echo Remove existing %OutputDir%
        echo --------------------------------------------
        IF EXIST %OutputDir% RD /S /Q %OutputDir%

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
            docker exec %DOCKER_CONTAINER% robot -v BROWSER_HEADLESS:True %TestCaseRunFilter% --outputdir "%RESULT_DIR%" --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml workspace/ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
        ) else (
            docker exec %DOCKER_CONTAINER% robot -v BROWSER_HEADLESS:True %TestCaseRunFilter% --outputdir "%RESULT_DIR%" --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml --variablefile %TestDataDir%/%TestDataFileName%.py workspace/ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
        )
        goto :eof

        :exeTC
        echo Start Robot Framework automation
        if "%TestDataFileName%" == "" (
            robot %TestCaseRunFilter% --outputdir %OutputDir% --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml ../ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
        ) else (
            robot %TestCaseRunFilter% --outputdir %OutputDir% --log %TestSuiteName%_log.html --output %TestSuiteName%_output.xml --report %TestSuiteName%_report.html --xunit %TestSuiteName%_junit.xml --variablefile ../../%TestDataDir%/%TestDataFileName%.py ../ECOManageRobotProject/TestSuite_ECO%CUSTOMER%/TestSuite_%ACS%/TestSuite_%TestSuiteName%.robot
        )
        goto :eof

        :eof
        '''
}