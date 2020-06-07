# Лабораторная работа №3
## Цель
Реализация алгоритмы параллельной быстрой сортировки (QUICK SORT) с
использованием библиотеки MPI.

## Задача
Разработать и реализовать параллельный алгоритм быстрой сортировки с
использованием библиотеки MPI. Построить график зависимостей от времени
сортировки массивов разного размера в зависимости от количества используемых
компьютеров (1, 2 или 4).

## Входные данные
1. Имя файла, содержащего с числами типа int записанные через пробел.
2. Имя файла для записи результата сортировки в том же формате что и входные
данные

## Установка
### Необходимое программное обеспечение
Необходимо установить JDK8 и maven

Сборка
```bash
git clone https://github.com/AlexIvchenko/itmo-master-1-parallel-programming.git
cd itmo-master-1-parallel-programming/quicksort
wget -O ./mpj-v0_44.zip https://sourceforge.net/projects/mpjexpress/files/releases/mpj-v0_44.zip/download
unzip ./mpj-v0_44.zip -d ./lib/
export MPJ_HOME="$(pwd)/lib/mpj-v0_44"
export PATH="$MPJ_HOME/bin:$PATH"
mvn clean install
```
## Запуск
### Генерация чисел для сортировки
#### Linux
```bash
./bin/run.sh generate --help
```
#### Windows
```
bin\run.bat generate --help
```
```
Usage: <main class> generate [-hV] [--bound=<bound>] [--file=<file>]
                             [--origin=<origin>] [--size=<size>]
      --bound=<bound>
      --file=<file>       file for  numbers
  -h, --help              Show this help message and exit.
      --origin=<origin>
      --size=<size>
  -V, --version           Print version information and exit.
```
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
Usage: <main class> mpi [-hV] --input=<inputFile> --iterations=<iterations>
                        -np=<processes> --output=<outputFile>
  -h, --help                Show this help message and exit.
      --input=<inputFile>   input file with numbers to sort
      --iterations=<iterations>
                            number of iterations for benchmark
      -np, --processes=<processes>
                            number of processes
      --output=<outputFile> output file for sorted numbers
  -V, --version             Print version information and exit.
```
## Запуск тестов
### Linux
```bash
./bin/run.sh test
```
### Windows
```
bin\run.bat test
```
```
Usage: <main class> test [-hV] [--format=<format>] [--iterations=<iterations>]
                         [--suites=<testSuitesFile>]
      --format=<format>   format of output statistics, valid values: MARKDOWN
                            Default: MARKDOWN
  -h, --help              Show this help message and exit.
      --iterations=<iterations>
                          number of iterations for benchmark
      --suites=<testSuitesFile>
                          yaml file containing test suites
                            Default: ./suites/suites.yaml
  -V, --version           Print version information and exit.
```

## Результаты
### AMD Ryzen 5 2600 Six-Core Processor 3.40 GHz
|Number of ints|Processes|Elapsed Time|Speedup|
|:------------:|:-------:|:----------:|:-----:|
|10000|1|690100ns|1.00|
|10000|2|1ms|0.42|
|10000|4|2ms|0.25|
|20000|1|1ms|1.00|
|20000|2|2ms|0.61|
|20000|4|4ms|0.32|
|30000|1|1ms|1.00|
|30000|2|1ms|0.88|
|30000|4|4ms|0.36|
|30000|1|1ms|1.00|
|30000|2|1ms|0.79|
|30000|4|3ms|0.50|
|40000|1|2ms|1.00|
|40000|2|1ms|1.34|
|40000|4|2ms|1.00|
|50000|1|2ms|1.00|
|50000|2|2ms|1.20|
|50000|4|1ms|1.38|
|100000|1|5ms|1.00|
|100000|2|3ms|1.57|
|100000|4|3ms|1.73|
|200000|1|12ms|1.00|
|200000|2|7ms|1.69|
|200000|4|6ms|1.79|
|300000|1|18ms|1.00|
|300000|2|14ms|1.29|
|300000|4|8ms|2.00|
|400000|1|24ms|1.00|
|400000|2|14ms|1.71|
|400000|4|10ms|2.40|
|500000|1|30ms|1.00|
|500000|2|22ms|1.36|
|500000|4|16ms|1.88|
|600000|1|37ms|1.00|
|600000|2|25ms|1.48|
|600000|4|20ms|1.85|
|700000|1|44ms|1.00|
|700000|2|28ms|1.57|
|700000|4|23ms|1.91|
|800000|1|51ms|1.00|
|800000|2|33ms|1.55|
|800000|4|24ms|2.12|
|900000|1|59ms|1.00|
|900000|2|35ms|1.69|
|900000|4|24ms|2.46|
|1000000|1|62ms|1.00|
|1000000|2|45ms|1.38|
|1000000|4|36ms|1.72|