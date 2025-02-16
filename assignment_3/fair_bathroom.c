#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>

#define MEN 10
#define WOMEN 10
#define SHORT_SLEEP 100
#define MAX_SLEEP 10000
#define RUNS 20

unsigned int seed;
int event = 0;
int m_using, w_using;
int m_waiting, w_waiting;
sem_t bathroom_lock;
sem_t m_queue, w_queue;

void sleep_random(int);


void* men_working(void * arg){
	int id = *(int *) arg;
	int temp_runs = RUNS;
	printf("Man %d created\n", id);
	// Work
	while(temp_runs > 0){
		sleep_random(MAX_SLEEP);

		// Acquire lock and if necessary wait in queue
		// # <await(w_using == 0 && w_waiting== 0) m_using++>
		sem_wait(&bathroom_lock);
		if(w_using > 0 || w_waiting > 0){
			m_waiting++;
			printf("%d\tMan %d is waiting. Queue of men:%d\n", event++, id, m_waiting);
			sem_post(&bathroom_lock);
			sem_wait(&m_queue);
		}
		m_using++;
		printf("%d\tMan %d enters the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);
		if (m_waiting > 0){
			m_waiting--;
			sem_post(&m_queue);
		}
		else{
			sem_post(&bathroom_lock);
		}

		// Use Bathroom
		sleep_random(SHORT_SLEEP);

		// update state, check queues and send signal if needed
		// <m_using-->
		sem_wait(&bathroom_lock);
		m_using--;
		printf("%d\tMan %d leaves the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);

		if(m_using == 0 && w_waiting > 0) {
			w_waiting--;
            sem_post(&w_queue);
        }
      	else{
        	sem_post(&bathroom_lock);
		}

		temp_runs--;
	}

	return NULL;
}

void* women_working(void * arg){
	int id = *(int *) arg;
	printf("Woman %d created\n", id);
	int temp_runs = RUNS;
	// Work
	while(temp_runs > 0){
		sleep_random(MAX_SLEEP);

		// Acquire lock and if necessary wait in queue
		// #< await (m_using == 0 && m_waiting == 0) w_using ++>
		sem_wait(&bathroom_lock);

		if(m_using > 0 || m_waiting > 0){
			w_waiting++;
			printf("%d\tWoman %d is waiting. Queue of women:%d\n", event++, id, w_waiting);
			sem_post(&bathroom_lock);
			sem_wait(&w_queue);
		}
		w_using++;
		printf("%d\tWoman %d enters the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);
		sem_post(&bathroom_lock);

		// Use bathroom
		sleep_random(SHORT_SLEEP);

		// update state, check queues and send signal if needed
		// w_using--
		sem_wait(&bathroom_lock);
		w_using--;
		printf("%d\tWoman %d leaves the bathroom. Men in bathroom: %d. Women in bathroom: %d\n", event++, id, m_using, w_using);

        if(m_waiting > 0) {
        	m_waiting--;
        	sem_post(&m_queue);
     	}
     	else if(w_waiting > 0){
			w_waiting--;
        	sem_post(&w_queue);
        }
        else{
        	sem_post(&bathroom_lock);
		}
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
