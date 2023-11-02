def getTestSuite() {
    return '''
        return [
        "",
        "ECO_Sagemcom"
        ]
    ''' 
}

def getTestCase() {
    return '''
        if (TestSuiteName == "ECO_Sagemcom") {
            return [
                "",
                "ECOIOT-6316",
                "ECOIOT-6371",
                "ECOIOT-6458",
                "ECOIOT-6459",
                "ECOIOT-6562",
                "ECOIOT-6563",
                "ECOIOT-6313a",
                "ECOIOT-6313b",
                "ECOIOT-6313c",
                "ECOIOT-6503",
                "ECOIOT-6504",
                "ECOIOT-6506",
                "ECOIOT-6505",
                "ECOIOT-6288",
                "ECOIOT-6291",
                "ECOIOT-6292",
                "ECOIOT-6293",
                "ECOIOT-6320",
                "ECOIOT-6341",
                "ECOIOT-6315",
                "ECOIOT-6332",                            		
                "ECOIOT-6331",
                "ECOIOT-6337",					                
                "ECOIOT-6298",
                "ECOIOT-6323",
                "ECOIOT-6327",
                "ECOIOT-6339"
            ]                           
        } else {
            return ["Unknown testsuite"]
        } 		
    ''' 
}