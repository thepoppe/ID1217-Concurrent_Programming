#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>

#define MAX_QUEENS 8

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

	found_solutions++;
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



void find_solutions(){
	for(int x = 'a'; x <= 'h'; x++){
		solution_t q;
		q.pos[0] = x;
		place_remaining_queens(q, 1);
	}
}


/* timer copied from matrixSum.c*/
double read_timer() {
	static bool initialized = false;
	static struct timeval start;
	struct timeval end;
	if( !initialized ){
		gettimeofday( &start, NULL );
		initialized = true;
	}
	gettimeofday( &end, NULL );
	return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}



int main(){
    double t0,t1;
	t0 = read_timer();
	find_solutions();
	t1 = read_timer();
	printf("There are %d solutions for the 8 queen problem\n", found_solutions);
    printf("Execution time: %.4f\n",t1);
	return 0;
}
