% This function makes classification of customers data included in a csv file 
% and exports them into an arff file (weka format)

function preprocessing()
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

%histograms of R,F,M
%figure(1)
%ksdensity(R)
%figure(2)
%ksdensity(F)
%figure(3)
%ksdensity(M)

% Calculate the necessary percentiles for R,F,M
R40 = prctile(R,40);
F50 = prctile(F,50);
F70 = prctile(F,70);
F90 = prctile(F,90);
M40 = prctile(M,40);
M70 = prctile(M,70);

tempA = 1;
tempB = 1;
tempC = 1;
tempD = 1;
tempE = 1;
tempG = 1;
tempH = 1;
tempI = 1;

% Arrays for 3D plotting
%A = zeros();
%B = zeros();
%C = zeros();
%D = zeros();
%E = zeros();
%G = zeros();
%H = zeros();
%I = zeros();

%****************************
% Classification of data
%****************************
for i = 1:3000
    %CLASS POLY KALOS  
    if (R(i) <= R40) && (F(i)>F70) && (M(i)>M70) 
        class(i) = 1; % poly kalos
        A(tempA, :)=[R(i) F(i) M(i)];
        tempA = tempA +1;
   %CLASS PISTOS     
    elseif (R(i) <= R40) && (F(i)>F70) && (M(i)<M70)
        class(i) = 2; % pistos
        B(tempB, :)=[R(i) F(i) M(i)];
        tempB = tempB +1;
   %CLASS AVEVAIOS     
    elseif (R(i) <= R40) && (F(i)<=F70) && (M(i)<=M70) 
        class(i) = 3; % avevaios
        C(tempC, :)=[R(i) F(i) M(i)];
        tempC = tempC +1;
   %EYKAIRIA     
    elseif (R(i) <= R40) && (F(i)<=F70) && (M(i)>M70)
        class(i) = 4; % efkairia
        D(tempD, :)=[R(i) F(i) M(i)];
        tempD = tempD +1; 
  %ADIAFOROS     
    elseif (R(i) > R40) && (F(i)<=F70) && (M(i)<=M70)
        class(i) = 5; % adiaforos
        E(tempE, :)=[R(i) F(i) M(i)];
        tempE = tempE +1;
  %PALIA EYKAIRIA
    elseif (R(i)>R40) && (F(i)<=F70) && (M(i)>M70)
        class(i)=6; %palia eykairia
        G(tempG, :)=[R(i) F(i) M(i)];
        tempG = tempG +1;
  %PROIN PISTOS
    elseif (R(i)>R40) && (F(i)>F70) && (M(i)<=M70)
        class(i)=7; %proin pistos
        H(tempH, :)=[R(i) F(i) M(i)];
        tempH = tempH +1;
 %PROIN KALOS
    elseif (R(i)>R40) && (F(i)>F70) && (M(i)>M70)
        class(i)=8; %proin kalos
        I(tempI, :)=[R(i) F(i) M(i)];
        tempI = tempI +1;
    end
end

figure(1);
%Scatter 3D for poly kalos
scatter3(A(:,1),A(:,2),A(:,3));
hold on;
%Scatter 3D for pistos
scatter3(B(:,1),B(:,2),B(:,3));
hold on;
%Scatter 3D for avevaios
scatter3(C(:,1),C(:,2),C(:,3));
hold on;
%Scatter 3D for eykairia
scatter3(D(:,1),D(:,2),D(:,3));
hold on;
%Scatter 3D for adiaforos
scatter3(E(:,1),E(:,2),E(:,3));
hold on;
%Scatter 3D for palia eykairia
scatter3(G(:,1),G(:,2),G(:,3));
hold on;
%Scatter 3D for proin pistos
scatter3(H(:,1),H(:,2),H(:,3));
hold on;
%Scatter 3D for proin kalos
scatter3(I(:,1),I(:,2),I(:,3));
hold on;

for i = 1:3000
    input(i,4) = M(i);
    input(i,5) = class(i);
end

fprintf('\n') 
classes = {'poly-kalos','pistos','avevaios','efkairia','adiaforos','palia-eykairia','proin-pistos','proin-kalos'};
% print # of elements in each class
for j = 1:8
    counter = 0;
    for i = 1:3000
        if class(i)==j
            counter = counter + 1;
        end
    end
    fprintf('%s = %d\n',classes{j},counter);
end


% the array to be exported
array = sortrows(input,5);

%*******************************
% arff conversion
%*******************************
[fout,msg] = fopen('training_set.arff','wt');
if fout == -1
error(msg);
end
fprintf(fout,'@RELATION RFM\n\n');
attributes = {'R','F','M'};

for i = 1:3
fprintf(fout,'@ATTRIBUTE %s REAL\n',attributes{i});
end

fprintf(fout,'@ATTRIBUTE class  {');

for i = 1:7
fprintf(fout,'%s,',classes{i});
end
fprintf(fout,'%s}\n\n',classes{8});
fprintf(fout,'@DATA\n');

for i = 1:3000
fprintf(fout,'%d,%d,%d,%s\n',array(i,2),array(i,3),array(i,4),classes{array(i,5)});
end

fclose(fout);

