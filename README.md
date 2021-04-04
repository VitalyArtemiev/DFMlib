# DFMlib
Kotlin multiplatform library for fraction-based matrices

Проект представляет собой кросс-платформенную библиотеку с поддержкой Kotlin-JVM, Kotlin-Native, Java, JavaScript

Библиотека предоставляет два типа матриц - с элементами типа Double и элементами типа Fraction (дробь, операции над дробями также реализованы в библиотеке)

Библиотека реализует операции над матрицами, независимо от типа:
 - отрицание
 - сложение (с числом или матрицей)
 - вычитание (числа или матрицы)
 - умножение (на число или матрицу)
 - деление (на число или матрицу)
 - транспонирование
 - перестановка строк
 - сравнение    
 - LUP-разложение
 - нахождение определителя через LUP-разложение
 - решение линейных уравнений через LUP-разложение
 - нахождение обратной матрицы через LUP-разложение
 - нахождение суммы элементов
 - нахождение среднего
 - конвертация в строку и наоборот
 - конвертация между типами матриц

Для всех операций, где это возможно, предоставляются перегруженные операторы

Корректность работы библиотеки гарантируется набором property-based тестов (https://jqwik.net/)

![Gradle CI](https://github.com/VitalyArtemiev/DFMlib/actions/workflows/gradle.yml/badge.svg)