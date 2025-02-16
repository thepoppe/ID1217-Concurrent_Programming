#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>

#define MEN 3
#define WOMEN 3
#define SHORT_SLEEP 100
#define MAX_SLEEP 10000

unsigned int seed;
int event = 0;
int m_using, w_using;
int m_waiting, w_waiting;
sem_t bathroom_lock;
sem_t m_queue, w_queue;

void sleep_random(int);


void* men_working(void * arg){
	int id = *(int *) arg;
	int temp_runs = 10;
	printf("Man %d created\n", id);
	// Work
	while(temp_runs > 0){
		sleep_random(MAX_SLEEP);

		// Acquire lock and if necessary wait in queue
		sem_wait(&bathroom_lock);

		if(w_using > 0 || w_waiting > 0){
			m_waiting++;
			printf("%d\tMan %d is waiting. Queue of men:%d\n", event++, id, m_waiting);
			sem_post(&bathroom_lock);
			sem_wait(&m_queue);

			sem_wait(&bathroom_lock);
			m_waiting--;
		}

		// update state and use bathroom
		m_using++;
		printf("%d\tMan %d enters the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);
		sem_post(&bathroom_lock);
		sleep_random(SHORT_SLEEP);

		// update state, check queues and send signal if needed
		sem_wait(&bathroom_lock);
		m_using--;
		printf("%d\tMan %d leaves the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);


		if (m_using == 0 && w_waiting > 0){
			printf("%d\tBathroom is empty\n",event++);
			sem_post(&w_queue);
		}
		else if(m_waiting > 0){
			sem_post(&m_queue);
		}

		sem_post(&bathroom_lock);


		temp_runs--;
	}

	return NULL;
}

void* women_working(void * arg){
	int id = *(int *) arg;
	printf("Woman %d created\n", id);
	int temp_runs = 10;
	// Work
	while(temp_runs > 0){
		sleep_random(MAX_SLEEP);

		// Acquire lock and if necessary wait in queue
		sem_wait(&bathroom_lock);

		if(m_using > 0 || m_waiting > 0){
			w_waiting++;
			printf("%d\tWoman %d is waiting. Queue of women:%d\n", event++, id, w_waiting);
			sem_post(&bathroom_lock);
			sem_wait(&w_queue);

			sem_wait(&bathroom_lock);
			w_waiting--;
		}

		// update state and use bathroom
		w_using++;
		printf("%d\tWoman %d enters the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);
		sem_post(&bathroom_lock);
		sleep_random(SHORT_SLEEP);

		// update state, check queues and send signal if needed
		sem_wait(&bathroom_lock);
		w_using--;
		printf("%d\tWoman %d leaves the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);


		if (w_using == 0 && m_waiting > 0){
			printf("%d\tBathroom is empty\n",event++);
			sem_post(&m_queue);
		}
		else if(w_waiting > 0){
			sem_post(&w_queue);
		}
		sem_post(&bathroom_lock);


		temp_runs--;
	}

	return NULL;
}

void sleep_random(int duration){
	int time =  rand_r(&seed) % duration;
	usleep(time);
	return;
}

void init(){
	sem_init(&bathroom_lock, 0, 1);
	sem_init(&m_queue, 0, 0);
	sem_init(&w_queue, 0, 0);
	seed = time(0);
}

int main(){

	init();

	// Create men
	pthread_t men[MEN];
	int men_ids[MEN];
	for (int i= 0; i < MEN; i++){
		men_ids[i] = i;
		pthread_create(&men[i],NULL, men_working, &men_ids[i]);
	}

	// Create women
	pthread_t women[WOMEN];
	int women_ids[WOMEN];
	for (int i = 0; i < WOMEN; i++){
		women_ids[i] = i;
		pthread_create(&women[i],NULL, women_working, &women_ids[i]);
	}
	// Run for a certain amount of time

	// Collect workers
	for(int i = 0; i< MEN; i++){
		pthread_join(men[i], NULL);
	}
	for(int i = 0; i< WOMEN; i++){
		pthread_join(women[i], NULL);
	}
	return 0;
}
