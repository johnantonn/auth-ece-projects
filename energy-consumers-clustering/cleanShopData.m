function [ CleanTable ] = cleanShopData(Table)
%This function is responsible for cleaning the season Shops'tables 

%% Remove zero rows, i.e. installations that have zeros in all columns
Table( ~any(Table,2), : ) = [];

%% Remove multiple entries
[Temp index] = unique(Table(:,2:end),'rows');
Table = [Table(index,1) Temp];
NumObjects = size(Table,1);
idx = zeros(NumObjects,2);
dist = zeros(NumObjects,2);

%% Run knn for removing outliers from the table
% Find k Nearest Neighbours with kNN, for k=2
    for i = 1:NumObjects    
        Neighbors = Table([1:i-1,i+1:end],[2 3 5]);   
        [idx(i,1:2), dist(i,1:2)] = knnsearch(Neighbors ,Table(i,[2 3 5]),'k',2);
    end
    
    % Distance used to remove outliers
    MaxDistance = 10000000;
    FinalEntries = 0;
    for i = 1:NumObjects   
        if dist(i,1) < MaxDistance && dist(i,2) < MaxDistance   
            FinalEntries = FinalEntries + 1;     
            % Use a distance Threshold to remove Outliers       
            CleanTable(FinalEntries,:) = Table(i,[1 2 3 5]);  
        else
             fprintf('\nOutlier Found in Shops Table id = %d',Table(i,1));
        end        
    end
end