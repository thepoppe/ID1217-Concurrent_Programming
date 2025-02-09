#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>

#define MAX_QUEENS 8
#define MAX_THREADS MAX_QUEENS
#define MAX_ROUNDS 20

int found_solutions = 0;

typedef struct solution_t{
	char pos[MAX_QUEENS];
}solution_t;


void verify_solution(solution_t sol){
	for (int i = 0; i < MAX_QUEENS; i++){

		int x_compare_pos = sol.pos[i];
		int k = 1;
		for(int j = i +1; j < MAX_QUEENS; j++){
			if(sol.pos[j] == x_compare_pos){
				return;
			}
			if (sol.pos[j] + k  == x_compare_pos){
				return;
			}
			if (sol.pos[j] - k == x_compare_pos){
				return;
			}
			k++;
		}
	}

	#pragma omp critical
	{
	found_solutions++;
	}
	return;
}



void place_remaining_queens(solution_t queens,int queen_num){
	if(queen_num   == MAX_QUEENS){
		verify_solution(queens);
		return;
	}
	for(int x = 'a'; x <= 'h'; x++){
		queens.pos[queen_num] = x;
		place_remaining_queens(queens, queen_num +1);
	}
}



void find_solutions(int threads){

	omp_set_num_threads(threads);
	#pragma omp parallel
	for(int x = 'a'; x <= 'h'; x++){
		solution_t q;
		q.pos[0] = x;
		place_remaining_queens(q, 1);
	}
}


/* timer*/
double calculate_time(struct timeval start, struct timeval end){
	return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}

void sort(double * arr){
	for (int i = 0; i < MAX_ROUNDS -1; i++){
		int min = i;
		for (int j = 0; j < MAX_ROUNDS; j++){
			if(arr[j] < arr[min]){
				min = j;
			}
		}
		if (min != i){
			double temp = arr[i];
			arr[i] = arr[min];
			arr[min] = temp;
		}
	}
}

int main(){
	printf("%-8s%-8s%-8s\n","Threads", "Time", "Solutions");
	for (int threads = 1; threads <= MAX_THREADS; threads++){
		double times[MAX_ROUNDS];
		for (int round = 0; round < MAX_ROUNDS; round++){
			found_solutions = 0;
    		struct timeval t0,t1;

			gettimeofday(&t0, NULL );
			find_solutions(threads);
			gettimeofday(&t1, NULL );

			times[round] = calculate_time(t0,t1);
		}
		sort(times);
		printf("%-8d%-8.4f%-8d\n", threads, times[MAX_ROUNDS/2], found_solutions);
    }
	return 0;
}
