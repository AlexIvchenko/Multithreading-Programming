#include "Matrix.h"

#ifndef OPENMP_MULTIPLIER_H
#define OPENMP_MULTIPLIER_H


class Multiplier {
public:
    Matrix* multiply(Matrix *m1, Matrix *m2);
protected:
    virtual int** doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2) = 0;

    int* computeRow(unsigned int rowIdx, unsigned int columns, unsigned int n, int **m1, int **m2);
};


#endif //OPENMP_MULTIPLIER_H
