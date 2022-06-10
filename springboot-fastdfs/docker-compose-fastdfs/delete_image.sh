echo "----------start delete none image----------"
docker rmi $(docker images | grep "none" | awk '{print $3}')
echo "----------delete none image end----------"

echo "----------start rmi com.fastdfs/springboot-fastdfs:latest image----------"
docker rmi com.fastdfs/springboot-fastdfs:latest
echo "----------delete com.fastdfs/springboot-fastdfs:latest image end----------"
