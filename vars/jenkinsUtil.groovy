def getLoginUser() {
    def loginUser = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')['userId'][0]
    return loginUser
}