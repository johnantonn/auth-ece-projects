function mat2text(filename, data)

FileID = fopen(filename,'w');
fprintf(FileID,'%f\t%f\t%f\n',data');
fclose(FileID);
fprintf('Conversion is finished\n');
end         