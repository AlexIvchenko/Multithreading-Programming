#include <iostream>
#include "Matrix.h"
#include "SequentialMultiplier.h"
#include "OmpMultiplier.h"
#include <chrono>
#include <omp.h>

using namespace std::chrono;

void help() {
    std::cout << "At least 3 arguments must be specified:" << std::endl;
    std::cout << "  1. First matrix file path" << std::endl;
    std::cout << "  2. Second matrix file path" << std::endl;
    std::cout << "  3. Result matrix file path" << std::endl;
    std::cout << "Optional arguments:" << std::endl;
    std::cout << "  4. Number of retries to measure min evaluation time (default 5)" << std::endl;
    std::cout << "  5. OpenMP schedule type. Available values are [NONE, STATIC, DYNAMIC, GUIDED] (default STATIC). NONE means sequential execution, openmp is not involved" << std::endl;
    std::cout << "If schedule type in not NONE, the following parameter can be specified:" << std::endl;
    std::cout << "  6. OpenMP chunk size. -1 - omp decides itself. (default -1)" << std::endl;
}

int main(int argc, char *argv[]) {
    if (argc < 4) {
        help();
        exit(1);
    }
    std::cout << "First matrix: " << argv[1] << std::endl;
    std::cout << "Second matrix: "<< argv[2] << std::endl;
    std::cout << "Result matrix: "<< argv[3] << std::endl;
    int retries = 5;
    if (argc >= 5) {
        retries = atoi(argv[4]);
    }
    Multiplier* multiplier = new OmpMultiplier(omp_sched_static, -1);
    if (argc >= 6) {
        std::string scheduleTypeStr = argv[5];
        if (scheduleTypeStr != "NONE") {
            int chunkSize = -1;
            if (argc >= 7) {
                chunkSize = atoi(argv[6]);
            }
            if (scheduleTypeStr == "STATIC") {
                multiplier = new OmpMultiplier(omp_sched_static, chunkSize);
            } else if (scheduleTypeStr == "DYNAMIC") {
                multiplier = new OmpMultiplier(omp_sched_dynamic, chunkSize);
            } else if (scheduleTypeStr == "GUIDED") {
                multiplier = new OmpMultiplier(omp_sched_guided, chunkSize);
            } else {
                std::cerr << scheduleTypeStr << " is not supported" << std::endl;
                help();
                exit(1);
            }
        } else {
            multiplier = new SequentialMultiplier();
        }
    }
    Matrix *m1 = Matrix::read_from_file(argv[1]);
    Matrix *m2 = Matrix::read_from_file(argv[2]);
    Matrix *m3;
    double minTime = -1;
    if (m1->getColumns() != m2->getRows()) {
        std::cerr << "Given matrices cannot be multiplied due to wrong cardinality" << std::endl;
        help();
        exit(1);
    }
    std::cout << "Multiplying matrices: " << "[" << m1->getRows() << "*" << m1->getColumns() << "]" ;
    std::cout << ", [" << m2->getRows() << "*" << m2->getColumns() << "]" << std::endl;
    for (int retry = 0; retry < retries; ++retry) {
        std::cout << "Retry #" << retry << std::endl;
        auto start = high_resolution_clock::now();
        m3 = multiplier->multiply(m1, m2);
        auto finish = high_resolution_clock::now();
        double time = duration<double>(finish - start).count();
        if (minTime == -1 || time < minTime) {
            minTime = time;
        }
        std::cout << "Time " << time << std::endl;
    }
    std::cout << "Min Time " << minTime << std::endl;

    Matrix::write_to_file(m3, argv[3]);
    return 0;
}
