#include <time.h>
#include <stdlib.h>

public void random() {

	srand(time(NULL));   // Initialization, should only be called once.
	int value = rand();

	return value;
}
