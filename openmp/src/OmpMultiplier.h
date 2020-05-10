#ifndef OPENMP_OMPMULTIPLIER_H
#define OPENMP_OMPMULTIPLIER_H

#include <omp.h>
#include "Multiplier.h"


class OmpMultiplier : public Multiplier {
private:
    omp_sched_t scheduleType;
    int chunkSize;
protected:
    int **doMultiply(unsigned int rows, unsigned int columns, unsigned int n, int **m1, int **m2) override;
public:
    OmpMultiplier(omp_sched_t scheduleType, int chunkSize);
};


#endif //OPENMP_OMPMULTIPLIER_H
