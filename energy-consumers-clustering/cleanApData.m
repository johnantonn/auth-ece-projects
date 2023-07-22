function [CleanTable1 CleanTable2 CleanTable3] = cleanApData(Table)
%This function is responsible for cleaning the season Shops'tables 

%% Remove zero rows, i.e. installations that have zeros in all columns
Table = Table( any(Table(:,2:end),2), : );

%% Remove multiple entries
[Temp index] = unique(Table(:,2:end),'rows');
Table = [Table(index,1) Temp];

NumObjects = size(Table,1);

%% Run knn for removing outliers from the table
% Find k Nearest Neighbours with kNN, for k=2
% Use a distance Threshold to remove Outliers

MaxDistance = 100000;

%% First TimeZone 00-08 
TotalEntries = 0;
for i = 1:NumObjects    
    
    Neighbors = Table([1:i-1,i+1:end],[2 5 11]);             
    [id(i,1:2) dist(i,1:2)] = knnsearch(Neighbors ,Table(i,[2 5 11]),'k',2);             
    if dist(i,1) < MaxDistance && dist(i,2) < MaxDistance                              
        TotalEntries = TotalEntries+1;               
        CleanTable1(TotalEntries,:) = Table(i,[1 2 5 11]);       
    else     
        fprintf('\nOutlier Found in timezone1 in Apartments Table id = %d',Table(i,1));   
    end  
end

%% Second TimeZone 08-16
TotalEntries = 0;
for i = 1:NumObjects         
    Neighbors = Table([1:i-1,i+1:end],[3 6 12]);             
    [id(i,1:2) dist(i,1:2)] = knnsearch(Neighbors ,Table(i,[3 6 12]),'k',2);             
    if dist(i,1) < MaxDistance && dist(i,2) < MaxDistance                              
        TotalEntries = TotalEntries+1;               
        CleanTable2(TotalEntries,:) = Table(i,[1 3 6 12]);       
    else     
        fprintf('\nOutlier Found in timezone2 in Apartments Table id = %d',Table(i,1));   
    end  
end

%% Third TimeZone 16-00
TotalEntries = 0;
for i = 1:NumObjects         
    Neighbors = Table([1:i-1,i+1:end],[4 7 13]);             
    [id(i,1:2) dist(i,1:2)] = knnsearch(Neighbors ,Table(i,[4 7 13]),'k',2);             
    if dist(i,1) < MaxDistance && dist(i,2) < MaxDistance                              
        TotalEntries = TotalEntries+1;               
        CleanTable3(TotalEntries,:) = Table(i,[1 4 7 13]);       
    else     
        fprintf('\nOutlier Found in timezone3 in Apartments Table id = %d',Table(i,1));   
    end  
end

%%Remove Zero Rows that may have been left unattended
CleanTable1 = CleanTable1( any(CleanTable1(:,2:end),2), : );
CleanTable2 = CleanTable2( any(CleanTable2(:,2:end),2), : );
CleanTable3 = CleanTable3( any(CleanTable3(:,2:end),2), : );

end

