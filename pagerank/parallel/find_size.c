/* 
 * File:   main.c
 * Author: John
 *
 * Created on 2 Μάρτιος 2014, 4:49 μμ
 */

#include <stdio.h>
#include <stdlib.h>

/*
 * 
 */
int main(int argc, char** argv) {
  printf("Inside read_data\n");
    FILE *fid;
    int from_idx, to_idx, global=0;

    fid = fopen("web-BerkStan.txt", "r");
    if(fid==NULL){printf("Error opening the file\n");}
 
    while (!feof(fid)){
        if (fscanf(fid,"%d\t%d\n",&from_idx, &to_idx)){		
            if(from_idx>to_idx){
                if(from_idx>global){
                    global=from_idx;
                }
            }
            else{
                if(to_idx>global){
                    global = to_idx;
                }
            }
        }
    }
    printf("Data succesfully read with global value -> %d\n",global);
	
    fclose(fid);
    return (EXIT_SUCCESS);
}

