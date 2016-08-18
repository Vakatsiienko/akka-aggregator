# akkaTestProject
To run project just set program arguments:<br>
first - name of file, from which to read data;<br>
(you don't need to create and generate file by yourself, GeneratorUtil will make this for you, just type destination file, <br>
or you can test program with your own file, in this case you need to disable the call of TestFileGeneratorUtil in Main#main() ) <br>
second - name of file, to which to write data;<br>
third - number of handlers to handle data;<br>
fourth - time limit in seconds of awaiting for actor's work result.<br>
Then run main() in Main.class.
<br>
<br>
Task:<br>
We have file on 100 000 lines, lines have data in format "ID; amount"; Total of different ID in file 1000 pieces.
Using Akka we need to aggregate all operations on each ID and write result in individual file.

Задание:<br>
Есть файл на 100000 строк, в нем записи в формате "ID;amount"; Всего уникальных ID в файле 1000 штук. 
Используя Akka необходимо просуммировать все операции по каждому ID и записать агрегированный результат в отдельный файл.
