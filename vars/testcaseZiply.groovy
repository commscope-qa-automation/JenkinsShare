                        
def getTestSuite() {
    return  '''
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
                "ECOIOT-90",
                "ECOIOT-3994",
                "ECOIOT-3995",
                "ECOIOT-3996",
                "ECOIOT-4013",
                "ECOIOT-4014",
                "ECOIOT-4015",
                "ECOIOT-4016",
                "ECOIOT-4017",
                "ECOIOT-4018",
                "ECOIOT-4019",
                "ECOIOT-4020",
                "ECOIOT-4021",
                "ECOIOT-4022",
                "ECOIOT-4023",
                "ECOIOT-4024", 
                "ECOIOT-4025", 
                "ECOIOT-4026",
                "ECOIOT-4027",
                "ECOIOT-4028",
                "ECOIOT-4030",
                "ECOIOT-4031",
                "ECOIOT-4035",
                "ECOIOT-4037",
                "ECOIOT-4040",
                "ECOIOT-4126",
                "ECOIOT-4127",
                "ECOIOT-4506",
                "ECOIOT-4509", 
                "ECOIOT-4510", 
                "ECOIOT-4513",
                "ECOIOT-4516",
                "ECOIOT-4517",
                "ECOIOT-4850",
                "ECOIOT-4852",
                "ECOIOT-4924",
                "ECOIOT-5291",
                "ECOIOT-5292",
                "ECOIOT-5389",
                "ECOIOT-5416",
                "ECOIOT-5417",
                "ECOIOT-5418",
                "ECOIOT-5870",
                "ECOIOT-6021",
                "ECOIOT-6035",
                "ECOIOT-6116",
                "ECOIOT-6208",
                "ECOIOT-6224",
                "ECOIOT-6228"
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