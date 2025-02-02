#ifndef _REENTRANT
#define _REENTRANT
#endif
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pthread.h>
#include <time.h>
#include <sys/time.h>

#define MAX_QUEENS 8
#define N_THREADS 1
#define BATCH_SIZE 1000
#define POISON_PILL NULL


typedef struct coordinate{
	char x;
	int y;
}coordinate;

typedef struct queen{
	int num;
	coordinate c;
}queen;

typedef struct job_queue{
	char* jobs[BATCH_SIZE];
	int first;
	int last;
	int total;
}job_queue;

int found_solutions = 0;
pthread_mutex_t solutions_lock;

job_queue queue;
pthread_mutex_t queue_lock;
pthread_cond_t can_produce;
pthread_cond_t can_consume;


/* FIFO QUEUE */
void init_queue(){
	pthread_mutex_lock(&queue_lock);
	queue.first = 0;
	queue.last = 0;
	queue.total = 0;
	pthread_mutex_unlock(&queue_lock);
}

void queens_to_array(queen * queens, char * arr){
	for(int i = 0; i < MAX_QUEENS;i++){
		arr[i] = queens[i].c.x;
	}
}

void push(queen* queens){
	pthread_mutex_lock(&queue_lock);
	while (queue.total == BATCH_SIZE){
		pthread_cond_wait(&can_produce, &queue_lock);
	}
	if (queens == POISON_PILL){
		queue.jobs[queue.last] = POISON_PILL;
	}
	else{
		queens_to_array(queens, queue.jobs[queue.last]);
	}
	queue.total++;
	queue.last = (queue.last +1) % BATCH_SIZE;

	pthread_cond_signal(&can_consume);
	pthread_mutex_unlock(&queue_lock);
}


void pop(char * arr){
	pthread_mutex_lock(&queue_lock);
	while (queue.total == 0){
		pthread_cond_wait(&can_consume, &queue_lock);
	}
	if(queue.jobs[queue.first] == POISON_PILL){
		arr = POISON_PILL;
	}
	else{
		for(int i = 0; i < MAX_QUEENS; i++){
			arr[i] = queue.jobs[queue.first][i];
		}
	}
	queue.jobs[queue.first] = NULL;
	queue.total--;
	queue.first = (queue.first + 1) % BATCH_SIZE;

	pthread_cond_signal(&can_produce);
	pthread_mutex_unlock(&queue_lock);

}


// Consumer
int check_remaining_queens(int start, char* positions){
	int x_compare_pos = positions[start];
	int j = 1;
	for(int i = start +1; i < MAX_QUEENS; i++){
		if(positions[i] == x_compare_pos){
			return -1;
		}
		if (positions[i] + j == x_compare_pos){
			return -1;
		}
		if (positions[i] - j == x_compare_pos){
			return -1;
		}
		j++;
	}
	return 0;
}

void verify_solution(char * positions){
	for (int i = 0; i < MAX_QUEENS; i++){
		if (check_remaining_queens(i, positions) == -1){
			return;
		}
	}
	pthread_mutex_lock(&solutions_lock);
	found_solutions++;
	pthread_mutex_unlock(&solutions_lock);
}

void* collect_and_test(void* index){
	int id = *(int*)index;
	char * solution = malloc(8* sizeof(char));
	while(1){
		pop(solution);
		if(solution == POISON_PILL){
			//printf("Poison recieved. Terminating thread %d\n", id);
			break;
		}
		verify_solution(solution);
	}
	free(solution);
	return NULL;
}


// Producer
void queue_poison(){
	//printf("Sending poison pill to workers\n");
	for (int i = 0; i < N_THREADS; i++){
		push(POISON_PILL);
	}
	return;
}

void queue_solutions(queen* queens, int queen_num){
	if(queen_num >= MAX_QUEENS){
		push(queens);
		return;
	}

	int x = 'a';
	while (x <='h'){
		queens[queen_num].c.x = x;
		queens[queen_num].c.y = queen_num;
		queue_solutions(queens, queen_num +1);
		x++;
	}
}


// Utility
void print_queens(queen * queens){
	for (int i = 0; i < MAX_QUEENS; i ++){
		queen q = queens[i];
		int num = q.num;
		coordinate c = q.c;
		printf("Queen %d: (%c,%d)\n", num, c.x, c.y);
	}
}

void init(){
	init_queue();
	pthread_mutex_init(&solutions_lock, NULL);
	pthread_mutex_init(&queue_lock, NULL);
	pthread_cond_init(&can_consume, NULL);
	pthread_cond_init(&can_produce, NULL);

}

/* timer copied from matrixSum.c*/
double read_timer() {
static bool initialized = false;
static struct timeval start;
struct timeval end;
if( !initialized )
{
gettimeofday( &start, NULL );
initialized = true;
}
gettimeofday( &end, NULL );
return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}





int main(){
	init();
	double time;
	queen *queens = (queen *) malloc(8 * sizeof(queen));
	for (int i = 0; i< MAX_QUEENS; i++){
		queens[i].num = i+1;
	}
	for (int i = 0; i < BATCH_SIZE; i++){
		queue.jobs[i] = malloc(8*sizeof(char));
	}

	pthread_t pids[N_THREADS];
	int indexes[N_THREADS];

	time = read_timer();
	for(int i = 0; i < N_THREADS;i++){
		indexes[i] = i;
		if (pthread_create(&pids[i],NULL, collect_and_test, &indexes[i]) != 0){
			perror("Failed to create thread");
			exit(EXIT_FAILURE);
		}
	}

	queue_solutions(queens, 0);
	queue_poison();


	for (int i = 0; i < N_THREADS; i++){
		pthread_join(pids[i], NULL);
	}
	time = read_timer();
	
	free(queens);
	for (int i= 0; i< BATCH_SIZE; i++){
		free(queue.jobs[i]);
	}
	printf("There are %d solutions for the 8 queen problem\n", found_solutions);
    printf("Execution time with %d threads: %.4f\n", N_THREADS, time);
	return 0;
}
