#include <gtest/gtest.h>
#include "../src/Matrix.h"
#include "../src/Multiplier.h"
#include "../src/SequentialMultiplier.h"
#include "../src/OmpMultiplier.h"

TEST(MultiplicationTests, SequentialMultiplierTest) {
    Matrix m1 = Matrix(2, 3);
    Matrix m2 = Matrix(3, 1);
    m1(0, 0) = 1;
    m1(0, 1) = 2;
    m1(0, 2) = 3;
    m1(1, 0) = 4;
    m1(1, 1) = 5;
    m1(1, 2) = 6;

    m2(0, 0) = 7;
    m2(1, 0) = 8;
    m2(2, 0) = 9;

    SequentialMultiplier sequentialMultiplier = SequentialMultiplier();
    Matrix m3 = *sequentialMultiplier.multiply(&m1, &m2);
    ASSERT_EQ(m3(0, 0), 50);
    ASSERT_EQ(m3(1, 0), 122);
}
