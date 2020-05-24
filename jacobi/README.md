# Лабораторная работа №2
## Цель
Решение системы линейных алгебраических уравнений (далее СЛАУ)
итеративным методом Якоби с использованием библиотеки MPI.

## Задача
Разработать и реализовать параллельный алгоритм решения СЛАУ методом
Якоби с использованием MPI. Построить графики зависимости времени вычисления
систем различного размера (300x300, 800x800, 2000x2000) в зависимости от
количества используемых компьютеров (1,2 или 4).

## Входные данные
1. Имя файла с матрицей коэффициентов уравнения в следующем формате:
```
m n
a11 a12 a13....b1
a21 a22 a23....b2
a31 a32 a33....b3
```
2. Имя файла с начальным приближением в формате
```
m
x1
x2
x3
x4
....
xm
```
3. Вещественное значение точности ε
4. Имя файла для результатов вычисления в формате п. 2
## Решение

## Установка
### Необходимое программное обеспечение
Необходимо установить JDK8 и maven

Сборка
```bash
git clone https://github.com/AlexIvchenko/itmo-master-1-parallel-programming.git
cd itmo-master-1-parallel-programming/jacobi
wget -O ./mpj-v0_44.zip https://sourceforge.net/projects/mpjexpress/files/releases/mpj-v0_44.zip/download
unzip ./mpj-v0_44.zip -d ./lib/
export MPJ_HOME="$(pwd)/lib/mpj-v0_44"
export PATH="$MPJ_HOME/bin:$PATH"
mvn clean install
```
## Запуск
### Генерация систем
#### Linux
```bash
./bin/run.sh generate --help
```
#### Windows
```
bin\run.bat generate --help
```
```
Usage: <main class> generate [-hV] --size=<size> [--solution=<solutionFile>]
                             [--system=<linearSystemFile>]
  -h, --help          Show this help message and exit.
      --size=<size>   Number of equations in system
      --solution=<solutionFile>
                      Output file for the solution
      --system=<linearSystemFile>
                      Output file for linear system in matrix format
  -V, --version       Print version information and exit.
```
Примеры (Linux):
```
./bin/run.sh generate --size 300 --system="./systems/system300.txt" --solution="./systems/solution300.txt"
./bin/run.sh generate --size 800 --system="./systems/system800.txt" --solution="./systems/solution800.txt"
./bin/run.sh generate --size 2000 --system="./systems/system2000.txt" --solution="./systems/solution2000.txt"
./bin/run.sh generate --size 5000 --system="./systems/system5000.txt" --solution="./systems/solution5000.txt"
./bin/run.sh generate --size 10000 --system="./systems/system10000.txt" --solution="./systems/solution10000.txt"
```
Для запуска в OS Windows используется bin\run.bat вместо ./bin/run.sh
### Запуск программы c MPI
#### Linux
```
./bin/run.sh mpi --help
```
#### Windows
```
bin\run.bat mpi --help
```
```
Usage: <main class> mpi [-hV] [--eps=<eps>]
                        [--init=<initialApproximationsFile>]
                        [--max=<maxIterations>] [--min=<minIterations>]
                        -np=<processes> --solution=<solutionFile>
                        --system=<linearSystemFile> [--threads=<threads>]
      --eps=<eps>           accuracy
                              Default: 0.000001
  -h, --help                Show this help message and exit.
      --init=<initialApproximationsFile>
                            file containing initial approximations
      --max=<maxIterations> maximal number of iterations
                              Default: 1000
      --min=<minIterations> minimal number of iterations
                              Default: 0
      -np, processes=<processes>
                            number of processes
      --solution=<solutionFile>
                            output file for the solution
      --system=<linearSystemFile>
                            file containing linear system in matrix format
      --threads=<threads>   0     - to run with common ForkJoinPool with
                              optimal number of threads
                            1     - to run without parallelism
                            other - to run with special ExecutorService with
                              given number of threads
                              Default: 1
  -V, --version             Print version information and exit.
```

#### Примеры (для OS Windows)
Система размера 2000, без начальных приближений
- 1 машина, 1 процесс, 1 поток (бех распараллеливания).
```
bin\run.bat mpi --system="./systems/system2000.txt" --init "./systems/init2000.txt" --solution="./systems/jacobi2000.txt" --eps 0 -np 1 --threads 1
```
- 1 машина, 2 процесса, 1 поток (распараллеливание по процессам).
```
bin\run.bat mpi --system="./systems/system2000.txt" --init "./systems/init2000.txt" --solution="./systems/jacobi2000.txt" --eps 0 -np 2 --threads 1
```
- 1 машина, 1 процесс, оптимальное количество потоков (распараллеливание по потокам).
```
bin\run.bat mpi --system="./systems/system2000.txt" --init "./systems/init2000.txt" --solution="./systems/jacobi2000.txt" --eps 0 -np 1 --threads 0
```
Для запуска в OS Linux используется ./bin/run.sh вместо bin\run.bat

## Запуск тестов
### Linux
```bash
./bin/run.sh test
```
### Windows
```bash
bin\run.bat test
```
```
Usage: <main class> test [-hV] [--format=<format>] [--suites=<testSuitesFile>]
      --format=<format>   format of output statistics, valid values: MARKDOWN
                            Default: MARKDOWN
  -h, --help              Show this help message and exit.
      --suites=<testSuitesFile>
                          yaml file containing test suites
                            Default: ./suites/suites.yaml
  -V, --version           Print version information and exit.
```
## Результаты
### AMD Ryzen 5 2600 Six-Core Processor 3.40 GHz
|Equations|Parallelism|Processes|Threads per process|Time(max)|Speedup|
|:-------:|:---------:|:-------:|:-----------------:|:-------:|:-----:|
|300|NO|1|1|11|1.00|
|300|MULTI-PROCESS|2|1|10|1.10|
|300|MULTI-PROCESS|4|1|15|0.73|
|300|MULTI-PROCESS|6|1|21|0.52|
|300|MULTI-THREAD|1|12|4|2.75|
|300|MULTI-THREAD|1|4|3|3.67|
|300|MULTI-THREAD|1|6|4|2.75|
|800|NO|1|1|27|1.00|
|800|MULTI-PROCESS|2|1|24|1.12|
|800|MULTI-PROCESS|4|1|18|1.50|
|800|MULTI-PROCESS|6|1|23|1.17|
|800|MULTI-THREAD|1|12|7|3.86|
|800|MULTI-THREAD|1|4|9|3.00|
|800|MULTI-THREAD|1|6|6|4.50|
|2000|NO|1|1|162|1.00|
|2000|MULTI-PROCESS|2|1|92|1.76|
|2000|MULTI-PROCESS|4|1|58|2.79|
|2000|MULTI-PROCESS|6|1|53|3.06|
|2000|MULTI-THREAD|1|12|27|6.00|
|2000|MULTI-THREAD|1|4|42|3.86|
|2000|MULTI-THREAD|1|6|27|6.00|

### Intel Core i5 Dual-Core 2,7 GHz
|Equations|Parallelism|Processes|Threads per process|Time(max)|Speedup|
|:-------:|:---------:|:-------:|:-----------------:|:-------:|:-----:|
|300|NO|1|1|8|1,00|
|300|MULTI-PROCESS|2|1|24|0,33|
|300|MULTI-PROCESS|4|1|54|0,15|
|300|MULTI-PROCESS|6|1|58|0,14|
|300|MULTI-THREAD|1|4|6|1,33|
|300|MULTI-THREAD|1|4|20|0,40|
|300|MULTI-THREAD|1|6|10|0,80|
|800|NO|1|1|35|1,00|
|800|MULTI-PROCESS|2|1|29|1,21|
|800|MULTI-PROCESS|4|1|69|0,51|
|800|MULTI-PROCESS|6|1|92|0,38|
|800|MULTI-THREAD|1|4|17|2,06|
|800|MULTI-THREAD|1|4|25|1,40|
|800|MULTI-THREAD|1|6|23|1,52|
|2000|NO|1|1|173|1,00|
|2000|MULTI-PROCESS|2|1|121|1,43|
|2000|MULTI-PROCESS|4|1|103|1,68|
|2000|MULTI-PROCESS|6|1|167|1,04|
|2000|MULTI-THREAD|1|4|78|2,22|
|2000|MULTI-THREAD|1|4|91|1,90|
|2000|MULTI-THREAD|1|6|89|1,94|

## Анализ
### Parallelism = MULTI-PROCESS (MPI)
Распараллеливание осуществлялось с помощью MPI

На системе размера 300 распараллеливание по 
процессам только ухудшило время исполнения
На системе размера 800 лучшее ускорее дало
использование 2 процессов
На системе размера 2000 использование 4
процессов дало ускорение в 3 раза для 6 ядерного процессора
и в 1.68 раза для двух ядерного процессора

### Parallelism = MULTI-THREAD
Данный вид параллелизма использовался для оценки накладных расходов при использовании MPI
Полученное ускорение для больших систем (800, 2000 уравнений) примерно равно
количеству физических (не логических) ядер процессора. Оно достигается при использовании
количества потоков равного количеству физических ядер процессора.

### Общие выводы
При возможности запуска на нескольких машинах
лучшего результата можно достичь (при достаточно больших системах) если на каждой машине 
будет работать по одному MPI процессу, внутри которого
будут работать несколько потоков (количество потоков должно быть равно количеству физических ядер процессора).
Это позволит загрузить все вычислительные мощности машин и при этом
максимально снизить накладные расходы на коммуникацию процессов.