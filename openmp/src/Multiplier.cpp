#include "Multiplier.h"

Matrix *Multiplier::multiply(Matrix *m1, Matrix *m2, bool m2Transposed) {
    if ((m1->getColumns() != m2->getRows() && !m2Transposed)
        || (m1->getColumns() != m2->getColumns() && m2Transposed)) {
        return nullptr;
    }
    unsigned int resultRows = m1->getRows();
    unsigned int resultColumns = m2Transposed ? m2->getRows() : m2->getColumns();
    unsigned int n = m1->getColumns();
    return new Matrix(doMultiply(resultRows, resultColumns, n, m1->getMatrix(), m2->getMatrix(), m2Transposed),
                      resultRows, resultColumns);
}

int *Multiplier::computeRow(unsigned int rowIdx, unsigned int columns, unsigned int n, int **m1, int **m2, bool m2Transposed) {
    int *row = new int[columns];
    for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
        row[columnIdx] = 0;
    }
    if (!m2Transposed) {
        for (int i = 0; i < n; ++i) {
            int pr = m1[rowIdx][i];
            for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
                row[columnIdx] += pr * m2[i][columnIdx];
            }
        }
    } else {
        for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
            int res = 0;
            for (int i = 0; i < n; ++i) {
                res += m1[rowIdx][i] * m2[columnIdx][i];
            }
            row[columnIdx] = res;
        }
    }
    return row;
}
