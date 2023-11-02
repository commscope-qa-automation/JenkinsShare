def getTestSuite() {
    return '''
        return [
        "",
        "ECOAssistGUI"
        ]
    ''' 
}

def getTestCase() {
    return '''
        if (TestSuiteName == "ECOAssistGUI") {
            return [
                "",
                "ECOIOT-4644",
            "ECOIOT-4646",
            "ECOIOT-4647",
            "ECOIOT-4650",
            "ECOIOT-4651",
            "ECOIOT-4652",
            "ECOIOT-4653",
            "ECOIOT-4658",
            "ECOIOT-4658b",
            "ECOIOT-4659",
            "ECOIOT-4662",
            "ECOIOT-4663",
            "ECOIOT-4665",
            "ECOIOT-4955",
            "ECOIOT-4957",
            "ECOIOT-4958",
            "ECOIOT-4959",
            "ECOIOT-4960",
            "ECOIOT-4961",
            "ECOIOT-4962",
            "ECOIOT-4964",
            "ECOIOT-5144",
            "ECOIOT-5144b",
            "ECOIOT-5146",
            "ECOIOT-5149",
            "ECOIOT-5149b",
            "ECOIOT-5301",
            "ECOIOT-5302",
            "ECOIOT-5303",
            "ECOIOT-5304",
            "ECOIOT-5308",
            "ECOIOT-5309",
            "ECOIOT-5310",
            "ECOIOT-5311",
            "ECOIOT-5312",
            "ECOIOT-5313",
            "ECOIOT-5314",
            "ECOIOT-5315",
            "ECOIOT-5316",
            "ECOIOT-5317",
            "ECOIOT-5318",
            "ECOIOT-5318b",
            "ECOIOT-5319",
            "ECOIOT-5319b",
            "ECOIOT-5326",
            "ECOIOT-5327",
            "ECOIOT-5328",
            "ECOIOT-5329",
            "ECOIOT-5330",
            "ECOIOT-5337",
            "ECOIOT-5340"
                ]
            } 
            else if (TestSuiteName == "test2") {
                return [
                    "",
                    "ECOIOT-392"
                ]
            } else {
                return ["Unknown testsuite"]
            }  
        '''
}