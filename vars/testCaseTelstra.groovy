def getTestSuite() {
    return '''
        return [
        "",
        "SoftwareModules",
        "TelstraRegression"
        ]
    ''' 
}

def getTestCase() {
    return '''
        if (TestSuiteName == "SoftwareModules") {
            return [
                "",
                "ECOIOT-6523",
                "ECOIOT-6554",
                "ECOIOT-6511",
                "ECOIOT-6518",
                "ECOIOT-6337",
                "ECOIOT-6512",
                "ECOIOT-6516",
                "ECOIOT-6522",
                "ECOIOT-6514",
                "ECOIOT-6513",
                "ECOIOT-6515"
            ]
        } else if (TestSuiteName == "TelstraRegression") {
            return [
                "",
                "ECOIOT-5535",
                "ECOIOT-5533",
                "ECOIOT-5538",
                "ECOIOT-6592",
                "ECOIOT-5537",
                "ECOIOT-3493",
                    "ECOIOT-5810",
                "ECOIOT-5811",
                "ECOIOT-5813",
                "ECOIOT-5814",
                "ECOIOT-5815",
                "ECOIOT-5816"
            ]
        } else {
            return ["Unknown testsuite"]
        } 		
        ''' 
}