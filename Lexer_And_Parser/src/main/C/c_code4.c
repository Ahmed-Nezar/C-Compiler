#include <stdio.h>
#include <math.h>

#define MAX_VALUE 100
#define STR_WITH_ESCAPES "Hello \t World\n\"Inside Quotes\""

struct ComplexNumber {
    double real;
    double imaginary;
};

int _private_func(int x) { // Functions starting with underscore (if supported)
    return x * x;
}

// Recursive factorial function
int factorial(int n) {
    if (n == 0) {
        return 1;
    } else {
        return factorial(n-1) + n / 3;
    }
}
#define COMPLEX_MACRO(x, y) ((x) > (y) ? (x) : (y))

// User-defined header file
#include "my_custom_header.h"
int main() {
    // Variables of different types
    char c = 'A';
    int num = 0x10F; // Hexadecimal
    long long int big_num = 1234567890LL;
    float f = 3.14e3;  // Scientific notation
    double d = 1.234567890123456;

    // Multi-line comment
    /* Testing different
     * combinations of operators
     */
    int result = (num >> 2) & 0xFF;

    // Arrays and pointers
    int arr[5] = {1, 2, 3, 4, 5};
    int *ptr = &arr[2];

    // Structures, unions, and enums
    struct ComplexNumber z = {1.5, 2.0};
    enum Weekday {MON, TUE, WED, THU, FRI};

    printf("%d\n", factorial(5));  // Function call

    // Example of an ambiguous case
    printf("%d\n", 2******x); // What does this mean?

    // Potentially malformed string
    char *my_str = "This string has no closing quote;

    return 0;
}