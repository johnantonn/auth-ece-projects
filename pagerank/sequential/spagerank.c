/* 
 * Sequential PageRank Algorithm
 * Author: Ioannis Antoniadis 7137 THMMY AUTH
 * Created on 26 Φεβρουάριος 2014, 6:42 μμ
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>

typedef struct{
  int id;
  double p_old;
  double p_new;
  double e;
  int *from_ids;
  int from_size;
  int con;
}node_struct;

void read_data();
void init();
void rand_init();
void print();
void pagerank();
void save_results();


//#define N 281904 /*Stanford web graph*/
//#define N 325729 /*NotreDame web graph*/
//#define N 685231 /*BerkStan web graph*/
#define N 916428 /*Google web graph*/
//#define N 5

node_struct *nodes;
double zero_sum;

int main(int argc, char** argv) {

    struct timeval first, second, lapsed;
    struct timezone tzp;   
    printf("Sequential PageRank algorithm\n");

    nodes = (node_struct*)malloc(N*sizeof(node_struct)); 

    /***Run the next 2 functions for a full web graph PageRank calculation**/
    //rand_init();
    init();
    read_data();

    /*Pagerank algorithm*/ 
    gettimeofday(&first, &tzp);
    pagerank();
    gettimeofday(&second, &tzp);
    
      if(first.tv_usec>second.tv_usec){
          second.tv_usec += 1000000;
          second.tv_sec--;
      }
    
    lapsed.tv_usec = second.tv_usec - first.tv_usec;
    lapsed.tv_sec = second.tv_sec - first.tv_sec;
    printf("\nTotal time elapsed: %d, %d s\n", lapsed.tv_sec, lapsed.tv_usec); 

    //save_results();
    
    return (EXIT_SUCCESS);
}

/*This function reads data from text files containing the graph connections
 Attention : N changes according to the largest value found in the text file !!! */

void read_data(){
    
    printf("Inside read_data\n");
    FILE *fid;
    int from_id, to_id, temp_size;

    fid = fopen("web-Google.txt", "r");
    if(fid==NULL){printf("Error opening the file\n");}

    while (!feof(fid)){
        if (fscanf(fid,"%d\t%d\n",&from_id, &to_id)){		
            nodes[from_id].con++;
            nodes[to_id].from_size++;
            temp_size = nodes[to_id].from_size;	
            nodes[to_id].from_ids=(int*)realloc(nodes[to_id].from_ids, temp_size *sizeof(int));	
            nodes[to_id].from_ids[temp_size - 1] = from_id;	    	
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
    
    int i, j, iter=0, con_id;
    double d = 0.85, ss_total_p=0, max_dif=1, threshold=0.0001;
    
    /*Connectivity elements must be non zero!*/
    while(max_dif > threshold){
        zero_sum=0;
        for(i=0;i<N;i++){
            nodes[i].p_old = nodes[i].p_new ;
            nodes[i].p_new = 0;
        }
        
          for(i=0;i<N;i++){
            if(nodes[i].con==0){
                zero_sum = zero_sum + nodes[i].p_old/N;
            }
            if(nodes[i].from_size!=0){ 
                for(j=0;j<nodes[i].from_size;j++){
                    con_id = nodes[i].from_ids[j]; 
                    nodes[i].p_new= nodes[i].p_new + nodes[con_id].p_old/nodes[con_id].con;
               }
            }
          }
        
        max_dif=-1;
        for(i=0;i<N;i++){
            nodes[i].p_new=d*(nodes[i].p_new+zero_sum)+(1-d)*nodes[i].e;
            if(fabs(nodes[i].p_new-nodes[i].p_old)>max_dif){
                max_dif = fabs(nodes[i].p_new-nodes[i].p_old);
            }
        }
        
        iter++;
        printf("max_dif[%d] = %f\n", iter, max_dif);
    }
    
    
    printf("End of PageRank computations...\n");
    
   for(i=0;i<N;i++){
        //printf("Steady state p[%d] = %f\n", i, nodes[i].p_new);
        ss_total_p = ss_total_p + nodes[i].p_new;
    }
   
    printf("\nIterations till convergence = %d\n", iter);
    printf("Total final sum of p = %f\n", ss_total_p);
}


