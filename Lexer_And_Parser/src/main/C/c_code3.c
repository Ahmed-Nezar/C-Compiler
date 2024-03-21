#include <stdio.h>

#define MAX(a, b) ((a) > (b) ? (a) : (b))

typedef struct {
    int x;
    int y;
} Point;

void print_array(int arr[], int size) {
    for (int i = 0; i < size; ++i) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

int factorial(int n) {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    int numbers[] = {1, 2, 3, 4, 5};
    int numbers[5] = {1, 2, 3, 4, 5};
    int sum = 0;

    for (int i = 0; i < sizeof(numbers) / sizeof(numbers[0]); ++i) {
        sum += numbers[i];
    }

    printf("Sum of numbers: %d\n", sum);

    Point p = {10, 20};
    printf("Point: (%d, %d)\n", p.x, p.y);

    printf("Factorial of 5: %d\n", factorial(5));

    printf("Maximum of 10 and 20: %d\n", MAX(10, 20));

    return 0;
}
