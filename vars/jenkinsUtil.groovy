def getLoginUser() {
    return currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')['userId'][0]
}