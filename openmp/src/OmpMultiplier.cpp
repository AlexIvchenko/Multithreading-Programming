#include <iostream>
#include "OmpMultiplier.h"

int **OmpMultiplier::doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2, bool m2Transposed) {
    omp_set_schedule(this->scheduleType, this->chunkSize);
    int **resultMatrix = new int *[rows];
    for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
        resultMatrix[rowIdx] = new int [columns];
    }
#pragma omp parallel for
    for (int v = 0; v < rows * columns; ++v) {
        int rowIdx = v / columns;
        int columnIdx = v % columns;
        int res = 0;
        for (int i = 0; i < n; ++i) {
            if (!m2Transposed) {
                res += m1[rowIdx][i] * m2[i][columnIdx];
            } else {
                res += m1[rowIdx][i] * m2[columnIdx][i];
            }
        }
        resultMatrix[rowIdx][columnIdx] = res;
    }
    return resultMatrix;
}

OmpMultiplier::OmpMultiplier(omp_sched_t scheduleType, int chunkSize) {
    this->scheduleType = scheduleType;
    this->chunkSize = chunkSize;
}
