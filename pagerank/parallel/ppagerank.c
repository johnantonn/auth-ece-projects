/* 
 * Parallel PageRank Algorithm using pthreads
 * Author: Ioannis Antoniadis 7137 THMMY AUTH
 * Created on 28 Φεβρουάριος 2014, 16:15 μμ
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>
#include <pthread.h>

typedef struct{
  int id;
  double p_old;
  double p_new;
  double e;
  int con;
  int from_size;
  int *from_id;
}node_struct;

void read_data();
void init();
void rand_init();
void print();
void save_results();
void pagerank();
void node_init(int id);
void node_cont(int id);
void node_calc(int id);

//#define N 281904 /*Stanford web graph*/
//#define N 325729 /*NotreDame web graph*/
#define N 685231 /*BerkStan web graph*/
//#define N 916428 /*Google web graph*/

#define NUM_THREADS 2

pthread_mutex_t update = PTHREAD_MUTEX_INITIALIZER;
node_struct *nodes;
double zero_sum, max_dif=1;

int main(int argc, char** argv) {

    struct timeval first, second, lapsed;
    struct timezone tzp;   
    int i;
    double ss_total_p=0;
    
    printf("Parallel PageRank algorithm using pthreads\n");

    nodes = (node_struct*)malloc(N*sizeof(node_struct));
    
    /***Run the next 2 functions for a full web graph PageRank calculation**/
    //rand_init();
    init();
    read_data();

    gettimeofday(&first, &tzp);
    pagerank();
    gettimeofday(&second, &tzp);
     
    /*Calculate total PageRank algorithm execution time*/
    if(first.tv_usec>second.tv_usec){
          second.tv_usec += 1000000;
          second.tv_sec--;
    }
    lapsed.tv_usec = second.tv_usec - first.tv_usec;
    lapsed.tv_sec = second.tv_sec - first.tv_sec;

    /*Calculate the total probability p, must be equal to 1.
     In double precision and very large N, some accuracy is lost*/
      for(i=0;i<N;i++){
        //printf("Steady state p[%d] = %f\n", i, nodes[i].p_new);
        ss_total_p = ss_total_p + nodes[i].p_new;
      }
    printf("Total final sum of p = %f\n", ss_total_p);

    printf("\nTotal time elapsed: %d, %d s\n", lapsed.tv_sec, lapsed.tv_usec); 
    
    //save_results();
    
    return (EXIT_SUCCESS);
}

/*This function reads data from text files containing the graph connections
 Attention : N changes according to the largest value found in the text file !!! */

void read_data(){
    
    printf("Reading data from .txt file\n");
    FILE *fid;
    int from_id, to_id, temp_size;

    fid = fopen("web-BerkStan.txt", "r");
    if(fid==NULL){printf("Error opening the file\n");}

    while (!feof(fid)){
        if (fscanf(fid,"%d\t%d\n",&from_id, &to_id)){		
            nodes[from_id].con++;
            nodes[to_id].from_size++;
            temp_size = nodes[to_id].from_size;	
            nodes[to_id].from_id=(int*)realloc(nodes[to_id].from_id, temp_size *sizeof(int));	
            nodes[to_id].from_id[temp_size - 1] = from_id;	    	
        }
    }
    printf("Data succesfully read\n");
	
    fclose(fid);
}

/*Sets initial values to p_old, p_new and e probabilities*/
void init(){
    int i;
    
    for(i=0;i<N;i++){
        nodes[i].id = i;
        nodes[i].con = 0;
        nodes[i].from_size=0;
        nodes[i].p_old = 0;
        nodes[i].e = (double)1/N;
        nodes[i].p_new = (double)1/N;
    }
}

void rand_init(){
   
    int i;
    double total_sum_p = 0;
    double total_sum_e = 0;
    //srand(time(NULL));
    srand(0);
    
    for(i=0;i<N;i++){
        nodes[i].id = i;
        nodes[i].con = 0;
        nodes[i].from_size=0;
        nodes[i].p_old = 0;
        nodes[i].e = ((double)rand()/(double)RAND_MAX);
        nodes[i].p_new = ((double)rand()/(double)RAND_MAX);
        total_sum_p = total_sum_p + nodes[i].p_new;
        total_sum_e = total_sum_e + nodes[i].e;
    }
    
    for(i=0;i<N;i++){
        nodes[i].p_new = nodes[i].p_new/total_sum_p;
        nodes[i].e = nodes[i].e/total_sum_e; 
    }
}

/*Prints initial probabilities p_old and e*/
void print(){
    
    int i;
    double total_sum_p = 0;
    double total_sum_e = 0;
    
    for(i=0;i<N;i++){
        printf("p[%d] = %f\n",i , nodes[i].p_new);
    }
    
    for(i=0;i<N;i++){
        printf("e[%d] = %f\n",i , nodes[i].e);
    }
    
    for(i=0; i<N; i++){
        total_sum_p = total_sum_p + nodes[i].p_new;
        total_sum_e = total_sum_e + nodes[i].e;
    }
    
    printf("Sum of of initial probabilities p(total)= %f\n", total_sum_p);
    printf("Sum of random probabilities e(total) = %f\n", total_sum_e);
    
}

/*This function sets initial values to 5 nodes so as the pagerank algorithm can
 quickly be tested for small N, N must be redefined to value 5 before test() is called
 from main()*/

void save_results(){
    
    FILE *pf;
    int i;
    
    if((pf=fopen("pf.bin", "wb")) == NULL){
    printf("Can't open p_final output file\n"); 
    }
    
    for(i=0;i<N;i++){
      fwrite(&nodes[i].p_new, sizeof(double), 1, pf);  
    }

    fclose(pf);
}

void pagerank(){
    
    int i, iter=0;
    double threshold=0.0001;

    /*Create a matrix of pthreads to parallelize the computations*/
    pthread_t *threads;
    threads = (pthread_t*)malloc(NUM_THREADS*sizeof(pthread_t));
    
    /*PageRank kernel's while loop*/
    while(max_dif > threshold){
        
        zero_sum=0;
        
        /*Stage 1 - Initialization of probabilities*/
        for(i=0;i<NUM_THREADS;i++){
            pthread_create(&threads[i],NULL,&node_init,i);
        }
        for(i=0;i<NUM_THREADS;i++){
            pthread_join(threads[i],NULL);
        }
        
        /*Stage 2 - Aggregation of nodes' contribution*/
          for(i=0;i<NUM_THREADS;i++){
           pthread_create(&threads[i],NULL,&node_cont,i);
        }
          for(i=0;i<NUM_THREADS;i++){
            pthread_join(threads[i],NULL);
        }
       
        /*Stage 3 - Calculation of p_new probabilities and max_difference*/
        max_dif=-1;
        for(i=0;i<NUM_THREADS;i++){  
         pthread_create(&threads[i],NULL,&node_calc,i);
        }
          for(i=0;i<NUM_THREADS;i++){
            pthread_join(threads[i],NULL);
        }
        
        iter++;
        printf("max_dif[%d] = %f\n", iter, max_dif);
    }
    printf("End of PageRank computations...\n");
    printf("\nIterations till convergence = %d\n", iter);
}

void node_init(int id){
    
    //printf("Thread %d in node_init\n",id);    
    
    int j;
    int start = id*floor(N / NUM_THREADS);
    int end = start + floor(N / NUM_THREADS);
    
    if(id==NUM_THREADS-1){
        end = end +N%NUM_THREADS;
    }
    
    for(j=start;j<end;j++){
        nodes[j].p_old = nodes[j].p_new ; 
        nodes[j].p_new = 0;
    }
    //printf("Thread %d finished\n",id);
}

void node_cont(int id){
    
    double temp_sum=0;
    int j, k, con_id;
    int start = id*floor(N / NUM_THREADS);
    int end = start + floor(N / NUM_THREADS);
    
    if(id==NUM_THREADS-1){
        end = end +N%NUM_THREADS;
    }

    for(j=start;j<end;j++){
        if(nodes[j].con==0){
                temp_sum = temp_sum + nodes[j].p_old/N;
        }
        if(nodes[j].from_size!=0){
            for(k=0;k<nodes[j].from_size;k++){
                    con_id = nodes[j].from_id[k];
                    nodes[j].p_new= nodes[j].p_new + nodes[con_id].p_old/nodes[con_id].con;  
                }
        }
    }
    
    pthread_mutex_lock(&update);
    zero_sum = zero_sum + temp_sum;
    pthread_mutex_unlock(&update);
    
}

void node_calc(int id){
   
    int j;
    int start = id*floor(N / NUM_THREADS);
    int end = start + floor(N / NUM_THREADS);
    double d=0.85, temp_dif=-1;
    
    if(id==NUM_THREADS-1){
        end = end +N%NUM_THREADS;
    }

    for(j=start;j<end;j++){
        nodes[j].p_new=d*(nodes[j].p_new+zero_sum)+(1-d)*nodes[j].e;       
        if(fabs(nodes[j].p_new-nodes[j].p_old)>temp_dif){
            temp_dif = fabs(nodes[j].p_new-nodes[j].p_old);      
        }
    }
    
    pthread_mutex_lock(&update);
    if(temp_dif>max_dif){
        max_dif=temp_dif;
    }
    pthread_mutex_unlock(&update);
}


