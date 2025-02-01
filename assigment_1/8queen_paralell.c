#ifndef _REENTRANT
#define _REENTRANT
#endif
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/time.h>

#define MAX_QUEENS 8
#define N_THREADS 1
#define BATCH_SIZE 1000
#define POISON_PILL NULL
int pushed = 0;
int popped = 0;

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

void push(char* arr){
	pthread_mutex_lock(&queue_lock);
	while (queue.total == BATCH_SIZE){
		pthread_cond_wait(&can_produce, &queue_lock);
	}
	//pushed ++;
	//printf("pushed:%d\n",pushed);

	queue.jobs[queue.last] = arr;
	queue.total++;
	queue.last = (queue.last +1) % BATCH_SIZE;

	pthread_cond_signal(&can_consume);

	pthread_mutex_unlock(&queue_lock);
}


char* pop(){
	char* arr;
	pthread_mutex_lock(&queue_lock);

	while (queue.total == 0){
		pthread_cond_wait(&can_consume, &queue_lock);
	}
	//popped++;
	//printf("popped:%d\n",popped);
	arr = queue.jobs[queue.first];
	queue.jobs[queue.first] = NULL;
	queue.total--;
	queue.first = (queue.first + 1) % BATCH_SIZE;

	pthread_cond_signal(&can_produce);

	pthread_mutex_unlock(&queue_lock);

	return arr;
}



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

//threads
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

void queens_to_array(queen * queens, char * arr){
	for(int i = 0; i < MAX_QUEENS;i++){
		arr[i] = queens[i].c.x;
	}
}

void queue_solutions(queen* queens, int queen_num){
	if(queen_num >= MAX_QUEENS){
		char solution[MAX_QUEENS];
		queens_to_array(queens, solution);
		push(solution);
		return;
	}
	int x = 'a';
	//printf("y=%d\n",queen_num);
	while (x <='h'){
		queen q = queens[queen_num];
		coordinate c;
		c.x = x;
		c.y = queen_num;
		q.c = c;
		queens[queen_num] = q;
		queue_solutions(queens, queen_num +1);
		//printf("x:%d\n",x);
		x++;
	}
}

void print_queens(queen * queens){
	for (int i = 0; i < MAX_QUEENS; i ++){
		queen q = queens[i];
		int num = q.num;
		coordinate c = q.c;
		printf("Queen %d: (%c,%d)\n", num, c.x, c.y);
	}
}
void* thread_pop_test(void* args){
	while(1){
	pthread_mutex_lock(&queue_lock);
	if (queue.total == BATCH_SIZE){
		pthread_mutex_unlock(&queue_lock);
		char c[8];
		pop(c);
		break;
	}
	pthread_mutex_unlock(&queue_lock);
	usleep(1);
	}
	return NULL;
}

void* collect_and_test( void* args){
	while(1){
		char* solution = pop();
		if(solution == POISON_PILL){
			printf("Poison recieved, terminating, %ld\n", pthread_self());
			break;
		}
		verify_solution(solution);
		
	}
	
	return NULL;
}


void queue_poison(){
	printf("Sending poison pill to workers\n");
	for (int i = 0; i < N_THREADS + 10; i++){
		push(POISON_PILL);
	}
	return;
}

int main(){
	init_queue();
	queen *queens = (queen *) malloc(8 * sizeof(queen));
	for (int i = 0; i< MAX_QUEENS; i++){
		queen q;
		q.num = i + 1;
		queens[i] = q;
	}
	pthread_t pids[N_THREADS];
	for(int i = 0; i < N_THREADS;i++){
		pthread_create(&pids[i],NULL, collect_and_test, NULL);
	}

	queue_solutions(queens, 0);

	queue_poison();


	for (int i = 0; i < N_THREADS; i++){
		pthread_join(pids[i], NULL);
	}
	free(queens);
	printf("There are %d solutions for the 8 queen problem\n", found_solutions);
	return 0;
}

