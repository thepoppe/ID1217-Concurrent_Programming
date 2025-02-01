#include <stdio.h>
#include <stdlib.h>

#define MAX_QUEENS 8

int found_solutions = 0;

typedef struct coordinate{
	char x;
	int y;
}coordinate;

typedef struct queen{
	int num;
	coordinate c;
}queen;

int valid_position(coordinate *c){
	//printf("x:%d, y:%d\n",c->x,c->y);
	if (c->x < 'a' || c-> x > 'h' || c->y < 1 || c->y > 8) {
		return -1;
	}
	return 0;
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


void verify_solution(char * positions){
	for (int i = 0; i < MAX_QUEENS; i++){
		if (check_remaining_queens(i, positions) == -1){
			return;
		}
	}
	found_solutions++;
}

void queens_to_array(queen * queens, char * arr){
	for(int i = 0; i < MAX_QUEENS;i++){
		arr[i] = queens[i].c.x;
	}
	/*
	for(int i = 0; i< MAX_QUEENS; i++){
		printf("%d,",arr[i]);
	}
	printf("\n");
	*/
}

void find_solutions(queen* queens, int queen_num){
	if(queen_num >= MAX_QUEENS){
		char x_positions[MAX_QUEENS];
		queens_to_array(queens, x_positions);
		verify_solution(x_positions);
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
		find_solutions(queens, queen_num +1);
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

int main(){
	queen *queens = (queen *) malloc(8 * sizeof(queen));
	for (int i = 0; i< MAX_QUEENS; i++){
		queen q;
		q.num = i + 1;
		queens[i] = q;
	}
	find_solutions(queens, 0);
	//print_queens(queens);
	free(queens);
	printf("There are %d solutions for the 8 queen problem\n", found_solutions);
	return 0;
}

