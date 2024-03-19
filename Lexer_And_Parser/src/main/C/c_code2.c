#include <ctype.h>

/* Multi-line comment
   with nested single-line // comments */

/* Preprocessor directives with macros */
#define PI 3.14159
#define MAX_VALUE 100

// Test inside double quotes
printf("The \"value pointed to by\" ptr is: %d\n", *ptr);

// Test bad code
int x = "This is a string!;
int y = 'This is other string;
int badBinary = 0b2146;
int badHexa = 0x21G46;
int badOctal = 0281496;

int main() {
    // String with escape sequences
    char message[] = "Hello, world!\n\tThis is a test.";

    // Hexadecimal and octal values
    int hexValue1 = +0xABE12;
    int hexValue2 = 0xe464;
    int octValue = 0123;
    int binary = 0b0110;
    int floatValue = 46613e4;
    int integerTricky = +-+-+143;
    int integerTricky2 = +-+-+-  763;
    int integerTricky3 = -+-+-8964;
    int integerTricky4 = -+-+-+ 9316;

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
    int *c;
    x = a * b;
    a+++b;
    printf("Bitwise AND: %d\n", a & b);
    printf("Bitwise XOR: %d\n", a ^ b);

    // sizeof operator
    printf("Size of int: %zu bytes\n", sizeof(int));

    // Example with keywords that could be identifiers
    int for = 1;
    double if = 3.14;

    // Example with identifiers that start with a digit
    int 1test = 1;
    char 24611OmarVar1646461 = 'x';
    char 394OmarVa#$ = 'y';
    char* #OmarVa#$ = 'z';
    return 0;
}