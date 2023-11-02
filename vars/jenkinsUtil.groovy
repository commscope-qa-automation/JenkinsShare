def getLoginUser(def context) {
    def loginUser = this.currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')['userId'][0]
    return loginUser
}