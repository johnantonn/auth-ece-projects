function plot_3D(indexes, data)
    %data = data(:,2:end);
    idx = indexes(:,1);
    plot3(data(idx==1,1), data(idx==1,2), data(idx==1,3), 'r.','Markersize',12)
    hold on
    plot3(data(idx==2,1), data(idx==2,2), data(idx==2,3), 'b.','Markersize',12)
    hold on
    plot3(data(idx==3,1), data(idx==3,2), data(idx==3,3), 'g.','Markersize',12)
    hold on
    plot3(data(idx==4,1), data(idx==4,2), data(idx==4,3), 'm.','Markersize',12)
    %hold on
    %plot3(data(idx==5,1), data(idx==5,2), data(idx==5,3), 'y.','Markersize',12)
    legend('Cluster1', 'Cluster2','Cluster3','cluster4','Location','NW')
    %legend('Cluster1', 'Cluster2','Location','NW')
    %legend('Cluster1', 'Cluster2','Cluster3','Location','NW')
    %legend('Cluster1', 'Cluster2','Cluster3','cluster4','cluster5','Location','NW')
    title('EM Clustering for Shops in Spring')
end