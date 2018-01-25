#! /bin/sh
#进程名字可修改
PRO_NAME=java

while true ; do
#    用ps获取$PRO_NAME进程数量
    NUM=`ps -ef | grep ${PRO_NAME} | grep -v grep |wc -l`
    echo "${PRO_NAME}:${NUM}"
#    少于1，重启进程
  if [ "${NUM}" -lt "1" ];then
    echo "mvn -Dtest=FacebookLoginLinuxTest#loginFacebook test"
    mvn -Dtest=FacebookLoginLinuxTest#loginFacebook test
#    大于1，杀掉所有进程，重启
#  elif [ "${NUM}" -gt "1" ];then
#    echo "more than ${NUM} ${PRO_NAME}"
  fi
  echo "sleep 10 second"
    sleep 60
done
exit 0