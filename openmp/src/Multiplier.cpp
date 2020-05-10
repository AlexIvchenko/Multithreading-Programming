//
// Created by ASIvc on 5/4/2020.
//

#include "Multiplier.h"

Matrix *Multiplier::multiply(Matrix *m1, Matrix *m2) {
    if (m1->getColumns() != m2->getRows()) {
        return nullptr;
    }
    unsigned int resultRows = m1->getRows();
    unsigned int resultColumns = m2->getColumns();
    unsigned int n = m1->getColumns();
    return new Matrix(doMultiply(resultRows, resultColumns, n, m1->getMatrix(), m2->getMatrix()), resultRows, resultColumns);
}

int *Multiplier::computeRow(unsigned int rowIdx, unsigned int columns, unsigned int n, int **m1, int **m2) {
    int *row = new int[columns];
    for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
        row[columnIdx] = 0;
    }
    for (int i = 0; i < n; ++i) {
        int pr = m1[rowIdx][i];
        for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
            row[columnIdx] += pr * m2[i][columnIdx];
        }
    }
    return row;
}
