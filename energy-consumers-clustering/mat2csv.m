function mat2csv( filename, data )
% Export to .csv in case we use weka
fid = fopen(filename,'wt');
fprintf(fid,'%s,', '1');
fprintf(fid,'%s,', '2');
fprintf(fid,'%s', '3');
fprintf(fid, '\n');
fclose(fid);
dlmwrite(filename, data ,'-append', 'delimiter', ',');
end