#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
static const int FAC[] = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880};
int cantor(int *a, int n)
{
	assert(n < 10);
	int x = 0;
	for (int i = 0; i < n; ++i) {
		int smaller = 0;
		for (int j = i + 1; j < n; ++j) {
			if (a[j] < a[i])
				smaller++;
		}
		x += FAC[n - i - 1] * smaller;
	}
	return x;
}
int listRemove(int *a, int *n, int i)
{
	for (int j = i; j < *n - 1; ++j)
		a[j] = a[j + 1];
	a[*n - 1] = 0;
	*n = *n - 1;
	return *n;
}
int decantor(int *a, int n, int k)
{
	int *num = malloc(sizeof(int) * n );
	int len = n;
	for (int i = 0; i < n; ++i)
		num[i] = i + 1;
	int cur = 0;
	for (int i = n - 1; i > 0; --i) {
		int index = k / FAC[i];
		k %= FAC[i];
		a[cur++] = num[index];
		listRemove(num, &len, index);
	}
	a[cur] = num[0];
	free(num);
	return 0;
}
int main(int argc, char **argv)
{
	int a[] = {3, 1, 2, 5, 4};
	printf("%d\n", cantor(a, 5));
	int b[9];
	decantor(b, 9, 98884);
	for (int i = 0; i < 9; ++i)
		printf("%d ", b[i]);
	printf("\n");
	printf("%d\n", cantor(b, 9));
	return 0;
}
