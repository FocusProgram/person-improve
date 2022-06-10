echo "----------start delete none image----------"
docker rmi $(docker images | grep "none" | awk '{print $3}')
echo "----------delete none image end----------"

echo "----------start rmi quartz/quartz:latest image----------"
docker rmi quartz/quartz:latest
echo "----------delete quartz/quartz:latest image end----------"
