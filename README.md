### How to build
The project uses maven, so:
> mvn package

### How to run
In project directory

#### with maven
> mvn  compile exec:exec -Dtranslate=false -Dcsv.file=Reviews.csv

#### with already package jar
> java -Xmx200mb -jar target/roundforest-task-1.0-SNAPSHOT-jar-with-dependencies.jar translate=false /path/to/csv

Translation reulst is currently put into target/Translated.csv

### memory usage
I didn't monitor memory usege. Just limited it with java -Xmx flag. Works fine on 100mb, 
works faster on 200mb, fails on 50mb with "garbage collector overhead".


### What was implemented:
#### Mutithreading
There is one thread that reads csv file line by line and 4 threads that collect statistics.
If translation is enabled then translation and new csv storage is done in separate thread too.
Though as translation is done much slower then statictics calculation and hence it can block execution 
of statistics threads(buffer for those threads is starving).
#### Statistics
I've implemented data gethering in memory. Thought about imploy H2 db, 
but looks like it does not take to much memory and is enough on current level of project.
word spliting is done by pattern and with help of stop-words.txt in the project, words that are not interesting for use are filtered out. 
Though full lexical analysis wasn't done, no words form detection and some common words like 'don\`t' are not filtered too.
#### Translation
There is GoogleTranslatorMock that pretends to handle multiple requests at once. Yet it checks for limitations: no more then 100 concurent calls, messages less then 1000 simbols. Though I lowered execution time to 10ms, otherwise testing on the 300mb file is too slow(and as requests are suboptimal time can be even greater then expected).
The mock has nothing to do with http requests, though interface it implements can be implemented with http calls support.
Translation raised some questions. 
* some reviews are longer then 1000 simbols(particulary at least one is 21kb)
** thinking of spliting those on sentence ending('!.?')
* in everage review length is 450 chars, so we can pack them tighter, but in conjunction with spliting to much effort currently.
* we should not keep in memory too much records from csv file though it could heppen as statistics and translation is done with different pace. This was implemented by adding two buffers(each record is put in both buffers) and buffers has size limit, so if one is emptied faster then the other the first one will starve.
In total: I hadn't enough time to make things right and optimize requests by size. 
Yet looks like it's not optimal by request per second too.
The reason is that worker retrives records in chunks and hence there is time when 0 requests are on google mock. 
Need to employ few such workers or reimplement this one. Yet messages currently simply cut to fit 900 simbols size.
#### Tests
There is a couple of test. One for csv reading and the other one is integrational that checks statistics calculation.
#### No loggin
No loggin was added except Exception.printStackTrace() here and there.
#### Multiple machines
To support multiple nodes we need:
* add some coordination
** message buss
** do it with centrall db
* open file with random access on each machine trying to access different parts of the file. 
** e.g. split them on 300Mb boundry
** lookup next new line char in file on those baundies
* have centrall storage storage where all results will be merged.
Tough as I see translation api limits make it useless.
