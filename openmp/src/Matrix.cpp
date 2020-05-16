#include "Matrix.h"
#include <fstream>

using namespace std;

Matrix::Matrix() : Matrix(0, 0) {

}

Matrix::Matrix(unsigned int rows, unsigned int columns) : rows(rows), columns(columns) {
    this->data = nullptr;
    resize(rows, columns);
}

Matrix::Matrix(int **data, unsigned int rows, unsigned int columns) {
    this->data = nullptr;
    resize(rows, columns);
    for (int row = 0; row < rows; ++row) {
        for (int column = 0; column < columns; ++column) {
            this->data[row][column] = data[row][column];
        }
    }
}

Matrix::~Matrix() {
    cleanup();
}

std::ostream &operator<<(std::ostream &os, const Matrix &matrix) {
    os << matrix.rows << ' ' << matrix.columns << endl;
    for (int row = 0; row < matrix.rows; ++row) {
        for (int column = 0; column < matrix.columns; ++column) {
            os << matrix(row, column) << ' ';
        }
        os << endl;
    }
    return os;
}

std::istream &operator>>(std::istream &is, Matrix &matrix) {
    int rows, columns;
    is >> rows >> columns;
    matrix.resize(rows, columns);
    for (int row = 0; row < matrix.rows; ++row) {
        for (int column = 0; column < matrix.columns; ++column) {
            is >> matrix(row, column);
        }
    }
    return is;
}

int &Matrix::operator()(const unsigned int &row, const unsigned int &column) const {
    return this->data[row][column];
}

Matrix *Matrix::read_from_file(const string &filename) {
    ifstream file;
    file.open(filename);
    auto *matrix = new Matrix;
    file >> *matrix;
    file.close();
    return matrix;
}

void Matrix::write_to_file(const Matrix *matrix, const std::string &filename) {
    ofstream file;
    file.open(filename);
    file << *matrix;
    file.close();
}

void Matrix::resize(const unsigned int &rows, const unsigned int &columns) {
    cleanup();
    this->rows = rows;
    this->columns = columns;
    this->data = new int *[rows];
    for (int row = 0; row < rows; ++row) {
        this->data[row] = new int[columns];
        for (int column = 0; column < columns; ++column) {
            this->data[row][column] = 0;
        }
    }
}

void Matrix::cleanup() {
    if (data == nullptr) {
        return;
    }
    for (int row = 0; row < rows; ++row) {
        delete data[row];
    }
    delete[] data;
}

unsigned int Matrix::getRows() const {
    return rows;
}

unsigned int Matrix::getColumns() const {
    return columns;
}

int **Matrix::getMatrix() const {
    return data;
}

Matrix *Matrix::transpose() {
    int** transposed = new int* [columns];
    for (int column = 0; column < columns; ++column) {
        transposed[column] = new int [rows];
        for (int row = 0; row < rows; ++row) {
            transposed[column][row] = this->data[row][column];
        }
    }
    return new Matrix(transposed, columns, rows);
}
