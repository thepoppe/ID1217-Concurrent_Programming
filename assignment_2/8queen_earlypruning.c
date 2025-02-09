#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>

#define MAX_QUEENS 8
#define MAX_THREADS 1
#define MAX_ROUNDS 1

int solutions = 0;

typedef struct solution_t{
	char pos[MAX_QUEENS];
}solution_t;


int is_valid(solution_t sol, char new_pos, int queen_pos){
	int offset = 1;
	for (int i = queen_pos-1; i >= 0; i--){
		if (sol.pos[i] == new_pos) return -1;
		if (sol.pos[i] + offset == new_pos) return -1;
		if (sol.pos[i] - offset == new_pos) return -1;
		offset++;
	}
	return 0;
}



void place_queen(solution_t queens,int queen_count){
	if (queen_count == MAX_QUEENS){
		solutions++;
		return;
	}
	for(int new_pos = 'a'; new_pos <= 'h'; new_pos++){
		if(is_valid(queens, new_pos, queen_count) == 0){
			queens.pos[queen_count] = new_pos;
			place_queen(queens, queen_count + 1);
		}
	}
	return;
}


/* timer*/
double calculate_time(struct timeval start, struct timeval end){
	return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}



int main(){
	printf("%-8s%-8s%-8s\n","Threads", "Time", "Solutions");
	for (int threads = 1; threads <= MAX_THREADS; threads++){
		double times[MAX_ROUNDS];
		for (int round = 0; round < MAX_ROUNDS; round++){
			solutions = 0;
    		struct timeval t0,t1;

			gettimeofday(&t0, NULL );
			solution_t queens;
			place_queen(queens, 0);
			gettimeofday(&t1, NULL );

			times[round] = calculate_time(t0,t1);
		}
		printf("%-8d%-8.4f%-8d\n", threads, times[MAX_ROUNDS/2], solutions);
    }
	return 0;
}
