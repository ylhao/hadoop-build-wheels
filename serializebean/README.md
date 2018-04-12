# 自定义对象序列化
把同一手机号的上行流量、下行流量进行累加，计算出总和。

## 基本流程

```
# 创建文件夹
hdfs dfs -mkdir -p /user/ylhao/flowcount/input

# 上传文件
hdfs dfs -put file01 file02 /user/ylhao/flowcount/input

# 查看文件是否上传成功
hdfs dfs -ls -R /user/ylhao/flowcount/

# 执行
hadoop jar serializebean-0.0.1-SNAPSHOT.jar FlowCount /user/ylhao/flowcount/input /user/ylhao/flowcount/output

# 查看结果
hdfs dfs -cat /user/ylhao/flowcount/output/*
```
