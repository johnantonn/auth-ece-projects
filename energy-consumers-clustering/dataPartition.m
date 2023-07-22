%%% Import all installation files, partition them into types (shop,appartment) 
%%%and compute the season tables (winter, spring, summer, autumn)
clear all
tic
% Path of csv files (126 installations)
path= dir('D:\Pattern Recognition\Consumption Data\*.csv*');
% Number of installations in path
CriterionInstallations = length(path);

ConsThreshold = 1000;
RecordThreshold = 100;
%% Counters for the Criterion number of each type of installation (shops,
%% apartments)
Appartments = 0;
Shops = 0;
%%Counters for indexing the global sesaon tables for both types (shop, apartment) 
WinterApartments = 0;
SpringApartments = 0;
SummerApartments = 0;
AutumnApartments = 0;
WinterShops = 0;
SpringShops = 0;
SummerShops = 0;
AutumnShops = 0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Scan all files and search for measurements of each season 
%% FOR EACH INSTALLATION
for i = 1:126
    
    % read the name and Data of the installation file 
    FileName = strcat('D:\Pattern Recognition\Consumption Data\',path(i).name);
    str = path(i).name;
    str = str(13:16);
    temp = str2double(str);
    ids(i) = temp;
    Data = csvread(FileName,1,0);

    % Criterion number of measurements of the installation
    N = size(Data,1);

    %%%%--------------Sampling------------------%%%%
    % Due to the large frequency of the measurements we are sampling every
    % 1 hour
    SampleFreq = 60;
    CriterionSampled = floor(N/SampleFreq);
    DataSampled = zeros(CriterionSampled,2);
      
    for j = 1:SampleFreq:N
        DataSampled(floor(j / SampleFreq) + 1,:) = Data(j,:);
        % If the value of power is negative, it becomes zero
        if (DataSampled(floor(j / SampleFreq) + 1,2)<0)
            DataSampled(floor(j / SampleFreq) + 1,2) = 0;
        end
    end

    numOfDays = floor(CriterionSampled/24);
    Criterion_9_18 = numOfDays*10;
    counter = 1;
    for j=0:numOfDays-1
        for k=10:19
           Data_9_18(counter,:) = DataSampled(j*24 + k,:); 
           counter = counter + 1;
        end
    end
    
    meanCons(i) = mean(Data_9_18(:,2));
    meanCons = meanCons';
   
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    %%%Decide whether the installation is a shop or an appartment
    %Partitioning into Seasons%
    SpringCounter = 0;
    WinterCounter = 0;
    SummerCounter = 0;
    AutumnCounter = 0;
    %% The above pointers and tables will be used once for each installation
    
%%%%%%%%%%%%%%%%%% Shop %%%%%%%%%%%%%%%%%%
    if meanCons(i) > ConsThreshold
        fprintf('\nInstallation %d is a shop',ids(i));
    %Partition mesaurments into seasons
    for j = 1:Criterion_9_18
        timestr = num2str(Data_9_18(j,1));
        temp = timestr(5:6);
        month = str2double(temp);

        if ismember(month,[12,1,2])
            WinterCounter = WinterCounter + 1; 
            WinterTemp(WinterCounter,:) = Data_9_18(j,:);             
        end
        if ismember(month,[3,4,5])
            SpringCounter = SpringCounter + 1; 
            SpringTemp(SpringCounter,:) = Data_9_18(j,:);             
        end
        if ismember(month,[6,7,8])
            SummerCounter = SummerCounter + 1; 
            SummerTemp(SummerCounter,:) = Data_9_18(j,:);             
        end
        if ismember(month,[9,10,11])
            AutumnCounter = AutumnCounter + 1; 
            AutumnTemp(AutumnCounter,:) = Data_9_18(j,:);             
        end  
    end
    
    %%%% Call the function to calculate the metrics %%%
    if WinterCounter >RecordThreshold 
        WinterShops = WinterShops + 1;
        [WinterAve, WinterMax, WinterMin,WinterCriterionSum] = calculateMetrics(WinterTemp,'Shop');
        %Final Winter table
        ShopWinterTable(WinterShops,:)   = [ids(i) WinterAve WinterMax WinterMin WinterCriterionSum];
    end
    
    if SpringCounter >RecordThreshold
        SpringShops = SpringShops + 1;
        [SpringAve, SpringMax, SpringMin,SpringCriterionSum] = calculateMetrics(SpringTemp, 'Shop');
        %Final Spring table
        ShopSpringTable(SpringShops,:)   = [ids(i) SpringAve SpringMax SpringMin SpringCriterionSum];i;
    end
   
    if SummerCounter >RecordThreshold
        SummerShops = SummerShops + 1;
        [SummerAve, SummerMax, SummerMin,SummerCriterionSum] = calculateMetrics(SummerTemp, 'Shop');
        %Final Summer table
        ShopSummerTable(SummerShops,:)   = [ids(i) SummerAve SummerMax SummerMin SummerCriterionSum];
    end
    
    if AutumnCounter >RecordThreshold
        AutumnShops = AutumnShops + 1;
        [AutumnAve, AutumnMax, AutumnMin,AutumnCriterionSum] = calculateMetrics(AutumnTemp,'Shop');
        %Final Autumn table
        ShopAutumnTable(AutumnShops,:)   = [ids(i) AutumnAve AutumnMax AutumnMin AutumnCriterionSum]; 
    end
            
%%%%%%%%%%%%%%%%%%% Apartment %%%%%%%%%%%%%%%%%%
    else
        fprintf('\nInstallation %d is an apartment',ids(i));
        Appartments = Appartments + 1;
        %Partition mesaurments into seasons
        for j = 1:CriterionSampled
        timestr = num2str(DataSampled(j,1));
        temp = timestr(5:6);
        month = str2double(temp);
        if ismember(month,[12,1,2])
            WinterCounter = WinterCounter + 1; 
            WinterTemp(WinterCounter,:) = DataSampled(j,:);             
        end
        if ismember(month,[3,4,5])
            SpringCounter = SpringCounter + 1; 
            SpringTemp(SpringCounter,:) = DataSampled(j,:);             
        end
        if ismember(month,[6,7,8])
            SummerCounter = SummerCounter + 1; 
            SummerTemp(SummerCounter,:) = DataSampled(j,:);             
        end
        if ismember(month,[9,10,11])
            AutumnCounter = AutumnCounter + 1; 
            AutumnTemp(AutumnCounter,:) = DataSampled(j,:);             
        end
        end
        
%%%% Call the function to calculate the metrics %%%
    if WinterCounter >RecordThreshold
        WinterApartments = WinterApartments + 1;
        [WinterAve, WinterMax, WinterMin,WinterCriterionSum] = calculateMetrics(WinterTemp,'Apartment');
        %Final Winter table
        ApWinterTable(WinterApartments,:) = [ids(i) WinterAve WinterMax WinterMin WinterCriterionSum];
    end
    
    if SpringCounter >RecordThreshold
        SpringApartments = SpringApartments + 1;
        [SpringAve, SpringMax, SpringMin, SpringCriterionSum] = calculateMetrics(SpringTemp,'Apartment');
        %Final Spring table
        ApSpringTable(SpringApartments,:) = [ids(i) SpringAve SpringMax SpringMin SpringCriterionSum];
    end
   
    if SummerCounter >RecordThreshold
        SummerApartments = SummerApartments + 1;
        [SummerAve, SummerMax, SummerMin,SummerCriterionSum] = calculateMetrics(SummerTemp,'Apartment');
        %Final Summer table
        ApSummerTable(SummerApartments,:) = [ids(i) SummerAve SummerMax SummerMin SummerCriterionSum];
    end
    
    if AutumnCounter >RecordThreshold
        AutumnApartments = AutumnApartments + 1;
        [AutumnAve, AutumnMax, AutumnMin,AutumnCriterionSum] = calculateMetrics(AutumnTemp,'Apartment');
        %Final Autumn table
        ApAutumnTable(AutumnApartments,:) = [ids(i) AutumnAve AutumnMax AutumnMin AutumnCriterionSum];
    end
    end  

end

%%%End of first stage Preprocessing%%%
%%%--------------------------------%%%
%%% Stage 2 - Clean Data from Tables%%%
%Create the final tables used for clustering
Criterion = 10;

fprintf('\nCleaning Process for Apartments');
if WinterApartments > Criterion
[ApWinterFinal1 ApWinterFinal2 ApWinterFinal3] = cleanApData(ApWinterTable);
end
if SummerApartments > Criterion
    [ApSummerFinal1 ApSummerFinal2 ApSummerFinal3] = cleanApData(ApSummerTable);
end
if SpringApartments > Criterion
    [ApSpringFinal1 ApSpringFinal2 ApSpringFinal3] = cleanApData(ApSpringTable);
end
if AutumnApartments > Criterion
    [ApAutumnFinal1 ApAutumnFinal2 ApAutumnFinal3] =cleanApData(ApAutumnTable);
end

fprintf('\nCleaning Process for Shops');
if WinterShops > Criterion
    ShopWinterFinal = cleanShopData(ShopWinterTable);
end
if SpringShops > Criterion
    ShopSpringFinal = cleanShopData(ShopSpringTable);
end
if SummerShops > Criterion
    ShopSummerFinal = cleanShopData(ShopSummerTable); 
end
if AutumnShops > Criterion
    ShopAutumnFinal = cleanShopData(ShopAutumnTable);
end
toc

%%%3D Scatters of the points of each table to be clustered. Tables with too
%%%few records are dumped
%%%Apartments Winter
figure(1)
scatter3(ApWinterFinal1(:,2),ApWinterFinal1(:,3),ApWinterFinal1(:,4));
figure(2)
scatter3(ApWinterFinal2(:,2),ApWinterFinal2(:,3),ApWinterFinal2(:,4));
figure(3)
scatter3(ApWinterFinal3(:,2),ApWinterFinal3(:,3),ApWinterFinal3(:,4));
%%%Apartments Spring
figure(4)
scatter3(ApSpringFinal1(:,2),ApSpringFinal1(:,3),ApSpringFinal1(:,4));
figure(5)
scatter3(ApSpringFinal2(:,2),ApSpringFinal2(:,3),ApSpringFinal2(:,4));
figure(6)
scatter3(ApSpringFinal3(:,2),ApSpringFinal3(:,3),ApSpringFinal3(:,4));
%%%Shops Spring & Summer
figure(7)
scatter3(ShopSpringFinal(:,2),ShopSpringFinal(:,3),ShopSpringFinal(:,4));
figure(8)
scatter3(ShopSummerFinal(:,2),ShopSummerFinal(:,3),ShopSummerFinal(:,4));

%%%Save the clustering tables into .txt files 
mat2text('ApartmentWinter1.txt',ApWinterFinal1(:,2:end));
mat2text('ApartmentWinter2.txt',ApWinterFinal2(:,2:end));
mat2text('ApartmentWinter3.txt',ApWinterFinal3(:,2:end));
mat2text('ApartmentSpring1.txt',ApSpringFinal1(:,2:end));
mat2text('ApartmentSpring2.txt',ApSpringFinal2(:,2:end));
mat2text('ApartmentSpring3.txt',ApSpringFinal3(:,2:end));
mat2text('ShopSpring.txt',ShopSpringFinal(:,2:end));
mat2text('ShopSummer.txt',ShopSummerFinal(:,2:end));

%%---------------------------- End of Basic Script ------------------------------------------%%