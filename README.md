# CS122B project

### Locations: 
our Fablix application back-end and the web front end are developed in the folder project2.

cs122b Project 5 report: [link](https://github.com/UCI-Chenli-teaching/cs122b-spring18-team-43/blob/master/cs122bProject5report.pdf)

WAR file: [link](https://github.com/UCI-Chenli-teaching/cs122b-spring18-team-43/blob/master/project2/target/ROOT.war)

JMeter report: [link](https://github.com/UCI-Chenli-teaching/cs122b-spring18-team-43/blob/master/testplan/jmeter_report.html)

Address of AWS and Google instances  
Instance 1 (origin): [52.53.40.250](52.53.40.250)  
Instance 2 (master): [52.9.187.8](52.9.187.8)  
Instance 3 (slave): [52.9.214.59 ](52.9.214.59)  
Google cloud: [35.233.214.141 ](35.233.214.141 )



### Task 3.1 script explanation

 We have the code that creates and writes the log about the TS and TJ in our project2/src/MoveListServlet, which is used to do the keyword search. The log file is called "test.txt" by default and is generated behind the WAR file. After each case testing we get the test.txt file from the server, move them to the testplan folder, and renamed it accordingly.

 The script we wrote for calculating the average is very simple since we use Python with the numpy module. To better record the data, we use the jupyter notebook ([link](https://github.com/UCI-Chenli-teaching/cs122b-spring18-team-43/blob/master/testplan/analysis.ipynb)). The basic code for it is: 

```python
import numpy as np
ratio = 1000000 # nano seconds to ms

search_test_pathsearch_t  = "test_single_1.txt" #log file path
array = np.loadtxt(search_test_path) # load test file
tj, ts = np.average(array, axis=0) # calculate the average
print("average TJ is: {tj} nano seconds, equal to {tj_m} ms \n \
and average TS is: {ts} nano seconds, equal to {ts_m} ms".format(
    tj=tj, ts=ts, tj_m = tj/ratio, ts_m = ts/ratio )) # print result
print ("Average Query Time: 2881 ms") # the time is from the JMeter result
```

