#include "OmpMultiplier.h"

int **OmpMultiplier::doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2) {
    omp_set_schedule(this->scheduleType, this->chunkSize);
    int **resultMatrix = new int *[rows];
#pragma omp parallel for
    for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
        resultMatrix[rowIdx] = computeRow(rowIdx, columns, n, m1, m2);
    }
    return resultMatrix;
}

OmpMultiplier::OmpMultiplier(omp_sched_t scheduleType, int chunkSize) {
    this->scheduleType = scheduleType;
    this->chunkSize = chunkSize;
}
