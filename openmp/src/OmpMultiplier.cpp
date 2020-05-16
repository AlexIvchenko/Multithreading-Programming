#include <iostream>
#include "OmpMultiplier.h"

int **OmpMultiplier::doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2, bool m2Transposed) {
    omp_set_schedule(this->scheduleType, this->chunkSize);
    int **resultMatrix = new int *[rows];
    if (rows < omp_get_max_threads() && columns > rows) {
        for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
            resultMatrix[rowIdx] = new int [columns];
        }
        if (!m2Transposed) {
#pragma omp parallel for
            for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
                for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
                    int res = 0;
                    for (int i = 0; i < n; ++i) {
                        res += m1[rowIdx][i] * m2[i][columnIdx];
                    }
                    resultMatrix[rowIdx][columnIdx] = res;
                }
            }
        } else {
#pragma omp parallel for
            for (int columnId = 0; columnId < columns; ++columnId) {
                for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
                    int res = 0;
                    for (int i = 0; i < n; ++i) {
                        res += m1[rowIdx][i] * m2[columnId][i];
                    }
                    resultMatrix[rowIdx][columnId] = res;
                }
            }
        }
    } else {
        if (!m2Transposed) {
#pragma omp parallel for
            for (int rowIdx = 0; rowIdx < rows; ++rowIdx) {
                resultMatrix[rowIdx] = computeRow(rowIdx, columns, n, m1, m2, m2Transposed);
            }
        }
    }
    return resultMatrix;
}

OmpMultiplier::OmpMultiplier(omp_sched_t scheduleType, int chunkSize) {
    this->scheduleType = scheduleType;
    this->chunkSize = chunkSize;
}
