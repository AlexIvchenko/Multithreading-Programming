#ifndef OPENMP_SEQUENTIALMULTIPLIER_H
#define OPENMP_SEQUENTIALMULTIPLIER_H

#include "Multiplier.h"


class SequentialMultiplier : public Multiplier {
protected:
    int **doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2, bool m2Transposed) override;
};


#endif //OPENMP_SEQUENTIALMULTIPLIER_H
