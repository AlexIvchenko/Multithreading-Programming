#ifndef OPENMP_MATRIX_H
#define OPENMP_MATRIX_H

#include <ostream>
#include <istream>


class Matrix {
private:
    unsigned int rows;
    unsigned int columns;
    int **data;

    void cleanup();

public:
    Matrix();

    Matrix(unsigned int rows, unsigned int columns);

    Matrix(int **data, unsigned int rows, unsigned int columns);

    Matrix* transpose();

    unsigned int getRows() const;

    unsigned int getColumns() const;

    int **getMatrix() const;

    virtual ~Matrix();

    void resize(const unsigned int &rows, const unsigned int &columns);

    friend std::ostream &operator<<(std::ostream &os, const Matrix &matrix);

    friend std::istream &operator>>(std::istream &is, Matrix &matrix);

    int &operator()(const unsigned int &row, const unsigned int &column) const;

    static Matrix *read_from_file(const std::string &filename);

    static void write_to_file(const Matrix *matrix, const std::string &filename);
};


#endif //OPENMP_MATRIX_H
