// int 16461Variable = 3; // Lexical ERROR
// int = 3 // SYNTAX ERROR
 int main() {
     int x = 3;
     float y = 4.2f;
     double arr[3] = {5.54555, 154616.666};
     char z = 'c';
     char* str = "Hello, World!";
     enum Color {
         Red,    // 0
         Green,  // 1
         Blue    // 2
     };
     typedef enum {
         On,
         Off
     } SwitchState;
    do{
        z = x + y;
    } while(x==3);
    for (int i=0; i<3; i++) {
        x = x+i;
    }
    unsigned long a = 444461648488;
    printf("Hello, World 2!\n");
    return 0;
 }