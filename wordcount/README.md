# WordCount

## 基本过程
```
# 打包
mvn package

# 执行
hadoop jar wordcount-0.0.1-SNAPSHOT.jar WordCount /user/ylhao/wordcount/input /user/ylhao/wordcount/output

# 验证结果
hdfs dfs -cat /user/ylhao/wordcount/output/*
```
