#include <gtest/gtest.h>
#include "../src/Matrix.h"

TEST(MatrixIoTests, RoundTripTest) {
    Matrix m1 = Matrix(2, 2);
    m1(0, 0) = 0;
    m1(0, 1) = 1;
    m1(1, 0) = 2;
    m1(1, 1) = 3;
    Matrix::write_to_file(&m1, "m.txt");
    Matrix m2 = *Matrix::read_from_file("m.txt");
    ASSERT_EQ(m2(0, 0), 0);
    ASSERT_EQ(m2(0, 1), 1);
    ASSERT_EQ(m2(1, 0), 2);
    ASSERT_EQ(m2(1, 1), 3);
}