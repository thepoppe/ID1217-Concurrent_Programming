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



void find_solutions(queen* queens, int queen_num){
	if(queen_num >= MAX_QUEENS){
		found_solutions++;
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
	printf("%d\n", found_solutions);
	return 0;
}

