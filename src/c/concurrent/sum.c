#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

int do_sum(const char *path) {
    FILE *file = fopen(path, "rb");  // open in binary mode
    if (file == NULL) {
        return -1;  // indicate error
    }

    int sum = 0;
    int byte;

    while ((byte = fgetc(file)) != EOF) {
        sum += byte;
    }

    fclose(file);
    return sum;
}

void *thread_function(void *arg) {
    const char *path = (char*)arg;
    int sum = do_sum(path);
    if (sum >= 0) {
        printf("%s : %d\n", path, sum);
    }
}

int main(int argc, char *argv[]) {
    int NUM_THREADS = argc - 1;
    pthread_t threads[NUM_THREADS];

    for (int i = 1; i < argc; i++) {
        const char *path = argv[i];
        pthread_create(&threads[i-1], NULL, thread_function, (void*)&path);
    }

    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    return 0;
}
