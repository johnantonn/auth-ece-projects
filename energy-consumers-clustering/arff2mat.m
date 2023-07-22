function arff2mat()
%Takes data from an arff file and saves it to a .txt file
fid=fopen('C:\Users\John\Documents\Aristotle University\Τομέας Ηλεκτρονικής και Υπολογιστών\Εξάμηνο 9ο\Αναγνώριση Προτύπων\Project 2 Consumption Clustering\Matlab Code\Weka\ShopSpringEM.arff','rt');
StartLine=10;
for k=1:StartLine-1
  fgetl(fid); % read and dump
end
counter = 1;
Fline = fgetl(fid);
while ischar(Fline)
    cluster_string = Fline(end:end);
    % Cluster # of instance
    cluster(counter,:) = str2double(cluster_string(1,1))+1;
    counter = counter + 1;
    Fline=fgetl(fid);
end
fclose(fid);

end