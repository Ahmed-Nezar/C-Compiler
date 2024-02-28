#include <ctype.h>

/* Multi-line comment
   with nested single-line // comments */

/* Preprocessor directives with macros */
#define PI 3.14159
#define MAX_VALUE 100

// String with escape sequences
char message[] = "Hello, world!\n\tThis is a test.";

// Hexadecimal and octal values
int hexValue = 0xAB12;
int octValue = 0123;

// Pointers and dereferencing
int *ptr;
int val = 10;
ptr = &val;
printf("Value using pointer: %d\n", *ptr);

// Conditional operators
int x = 5, y = 3;
int result = (x > y) ? x : y;  // Ternary operator

// Bitwise operators
int a = 12, b = 25;
printf("Bitwise AND: %d\n", a & b);
printf("Bitwise XOR: %d\n", a ^ b);

// sizeof operator
printf("Size of int: %zu bytes\n", sizeof(int));

// Example with keywords that could be identifiers
int for = 1;
double if = 3.14;

int main() {
    // ... your test cases here ...
    return 0;
}