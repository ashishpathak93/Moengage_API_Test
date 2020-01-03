## Brief：
Using Rest-Assured + TestNG + Google sheet Data driver mode，for Api automation testing.

Google sheet excel 
https://docs.google.com/spreadsheets/d/1uSw2qb093svTUMmo3f1v3pz0dC-nKj4CHHoQH7r0xTU/edit#gid=0


## Code module：
* base：Initialization, Extent report initialization and dynamic testNg creation ；
* report：monitoring test report with extent report listener；
* config：defined static variables , e.g. the test file path, test server, common share data；
* utils： Common method；
	* enums：will collect most for variables;
	* excel: will read data from the google sheets.
	* jsonParser: will parse and compare the json diff
* log: Custom logger using log4j.
* testData：separated test data and test script, reduce test cast, improve code transplantation, ceated one test class for each excel file while testing；
* testSuite：manage test component and save to testng xml file，test fail retry mechanism, test listener；
* Pojo : Future usecase of reading each testcases data from the dataprovider.
* tests: Contains all the testcases

### Where to check for reports
* Go to test-output> Html ;
* Open Extent.html in system browser;
* Check logs folder for automation logs;


### How to Rerun the failed testcases
* Go to test-output folder > select testng-failed.xml and rerun as TestNG suite.

### How to run the test:
* Prerequisite is environment where test has to be run.
* clean install exec:java -Dexec.mainClass=com.moengage.apitest.base.DynamicTestngCreation -Denvironment="https://gateway.marvel.com:443" -Dsheetname=marvel_chaining

### Where to find the testng.xml
* It's Created Dynamically during runtime

### POM. xml usecase
* It's pointing to TestNg.xml and environment is the input param.
