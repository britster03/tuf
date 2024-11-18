#include <stdlib.h>
void f(int a, int *b)
{
    if (a == 0)
    {
        *b = 0;
    }
    else if ((a % 2) == 0)
    {
        int *z = malloc(sizeof(int));
        f(a - 1, z);
        *b = *z * a;
        free(z);
    }
    else
    {
        int w;
        f(a - 1, &w);
        *b = w * a;
    }
}
int main()
{
    int x;
    f(3, &x);
}

#include <stdio.h>
#include <stdlib.h>

int main()
{
    int **ptr1 = (int**)malloc(sizeof (int*));
    if(ptr1 != NULL)
    {
        *ptr1 = (int *)malloc(sizeof(int));
        if(*ptr1 != NULL)
        {
            **ptr1 = 42;
            printf( "Value : % d\n", **ptr1);
            int *ptr2 = (int *)malloc(sizeof(int));
            free(* ptr1);
            printf( "ptr2 Value : % d\n", *ptr2);
            printf( "ptr1 Value : % d\n", **ptr1);
            *ptr1 = ptr2;
            **ptr1 = 99;
        }
    }
    return 0;
}