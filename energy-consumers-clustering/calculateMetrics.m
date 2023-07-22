%% The functions calculateMetrics calculates the four metrics used in
%% clustering. The function is called by any type of installation (shop,
%% appartment) and season (winter, spring, summer, autumn)

function  [SeasonAve, SeasonMax, SeasonMin, SeasonTotalSum] = calculateMetrics(SeasonTempTable, type)

%%%If it is an apartment
if strcmp(type,'Apartment')==1;
Dimensions = size(SeasonTempTable(:,1)) - mod(size(SeasonTempTable(:,1)),24);  
MaxLength = Dimensions(1);
Measurements = SeasonTempTable(1:MaxLength,2);
TotalDays = MaxLength/24;
Sum = zeros(24,1);
Max = zeros(24,1);
Avg = zeros(24,1);

for i=1:24 %for each day take the j-th hours
    Min(i)= Measurements(i);
    for j=0:TotalDays-1
    
        % Total Value value of each of the 24 hours
        Sum(i) = Sum(i) + Measurements(24*j+i);
        
        % Max value of each of the 24 hours
        if Measurements(24*j+i)>Max(i)
            Max(i)=Measurements(j*24+i);
        end
        
        % Min value of each of the 24 hours
       if Measurements(24*j+i)<Min(i)
            Min(i)=Measurements(j*24+i);
        end

    end
    Avg(i) = Sum(i)/TotalDays;
end

SeasonMax = [max(Max(1:8)) max(Max(9:16)) max(Max(17:24))];
SeasonMin = [min(Min(1:8)) min(Min(9:16)) min(Min(17:24))];
SeasonTotalSum = [sum(Sum(1:8)) sum(Sum(9:16)) sum(Sum(17:24))];
SeasonAve = [mean(Avg(1:8)) mean(Avg(9:16)) mean(Avg(17:24))];

end
%%%If it is a shop
if strcmp(type, 'Shop')==1
    Dimensions = size(SeasonTempTable(:,1)) - mod(size(SeasonTempTable(:,1)),10);     
    MaxLength = Dimensions(1);
    Measurements = SeasonTempTable(1:MaxLength,2);
    TotalDays = MaxLength/10;
    Sum = zeros(10,1);
    Max = zeros(10,1);
    Avg = zeros(10,1);

    for i=1:10 %for each day take the j-th hours
        Min(i)= Measurements(i);
        for j=0:TotalDays-1
            % Total Value value of each of the 24 hours
            Sum(i) = Sum(i) + Measurements(10*j+i);
            % Max value of each of the 24 hours
            if Measurements(10*j+i)>Max(i)
                Max(i)=Measurements(j*10+i);
            end        
            % Min value of each of the 24 hours
            if Measurements(10*j+i)<Min(i)  
                Min(i)=Measurements(j*10+i);
            end    
        end 
        Avg(i)= Sum(i)/TotalDays;
        
    end
      
    SeasonMax = max(Max);  
    SeasonMin = min(Min);
    SeasonTotalSum = sum(Sum);
    SeasonAve = mean(Avg);
end 
end
    
    