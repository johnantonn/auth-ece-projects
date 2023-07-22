clear all;
n = 3000;
% Open csv file and split into arrays 
input = csvread('RFM_Data.csv',1,0);

% Initialize arrays
ID = zeros(n,1);
R  = zeros(n,1);
F  = zeros(n,1);
M  = abs(zeros(n,1));
class = zeros(n,1);

ID = input(:,1);
R = input(:,2);
F = input(:,3);
M = round(abs(input(:,4)));

R10 = prctile(R,10);
R20 = prctile(R,20);
R30 = prctile(R,30);
R40 = prctile(R,40);
R50 = prctile(R,50);
R60 = prctile(R,60);
R70 = prctile(R,70);
R80 = prctile(R,80);
R90 = prctile(R,90);
R100 = prctile(R,100);

F10 = prctile(F,10);
F20 = prctile(F,20);
F30 = prctile(F,30);
F40 = prctile(F,40);
F50 = prctile(F,50);
F60 = prctile(F,60);
F70 = prctile(F,70);
F80 = prctile(F,80);
F90 = prctile(F,90);
F100 = prctile(F,100);

M10 = prctile(M,10);
M20 = prctile(M,20);
M30 = prctile(M,30);
M40 = prctile(M,40);
M50 = prctile(M,50);
M60 = prctile(M,60);
M70 = prctile(M,70);
M80 = prctile(M,80);
M90 = prctile(M,90);
M100 = prctile(M,100);

RP = [R10 R20 R30 R40 R50 R60 R70 R80 R90 R100]
FP = [F10 F20 F30 F40 F50 F60 F70 F80 F90 F100]
MP = [M10 M20 M30 M40 M50 M60 M70 M80 M90 M100]
