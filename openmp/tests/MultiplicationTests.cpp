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
    Matrix m3 = *sequentialMultiplier.multiply(&m1, &m2, false);
    ASSERT_EQ(m3(0, 0), 50);
    ASSERT_EQ(m3(1, 0), 122);
}

TEST(MultiplicationTests, OmpMultiplierTest) {
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

    OmpMultiplier ompMultiplier = OmpMultiplier(omp_sched_static, -1);
    Matrix m3 = *ompMultiplier.multiply(&m1, &m2, false);
    ASSERT_EQ(m3(0, 0), 50);
    ASSERT_EQ(m3(1, 0), 122);
}

TEST(MultiplicationTests, MultiplicandIsRowVector) {
    Matrix m1 = Matrix(1, 3);
    Matrix m2 = Matrix(3, 2);
    m1(0, 0) = 1;
    m1(0, 1) = 2;
    m1(0, 2) = 3;

    m2(0, 0) = 4;
    m2(0, 1) = 5;
    m2(1, 0) = 6;
    m2(1, 1) = 7;
    m2(2, 0) = 8;
    m2(2, 1) = 9;

    OmpMultiplier ompMultiplier = OmpMultiplier(omp_sched_static, -1);
    Matrix m3 = *ompMultiplier.multiply(&m1, &m2, false);
    ASSERT_EQ(m3.getRows(), 1);
    ASSERT_EQ(m3.getColumns(), 2);
    ASSERT_EQ(m3(0, 0), 40);
    ASSERT_EQ(m3(0, 1), 46);
}

TEST(MultiplicationTests, MultiplicandIsRowVectorSecondTransposed) {
    Matrix m1 = Matrix(1, 3);
    Matrix m2 = Matrix(3, 2);
    m1(0, 0) = 1;
    m1(0, 1) = 2;
    m1(0, 2) = 3;

    m2(0, 0) = 4;
    m2(0, 1) = 5;
    m2(1, 0) = 6;
    m2(1, 1) = 7;
    m2(2, 0) = 8;
    m2(2, 1) = 9;

    OmpMultiplier ompMultiplier = OmpMultiplier(omp_sched_static, -1);
    Matrix m3 = *ompMultiplier.multiply(&m1, m2.transpose(), true);
    ASSERT_EQ(m3.getRows(), 1);
    ASSERT_EQ(m3.getColumns(), 2);
    ASSERT_EQ(m3(0, 0), 40);
    ASSERT_EQ(m3(0, 1), 46);
}