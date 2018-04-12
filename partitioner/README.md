# 自定义分区

## 数据

```
file01:
13333333333 20  100
13333333334 20  100
13333333334 40  200
13333333335 20  100
13533333333 20  100
13533333334 80  1000
13533333334 280 200
13533333335 200 1200
13833333333	1000	20000

file02:
13733333333 20  100
13733333334 20  100
13733333334 40  200
13733333335 20  100
13933333333 20  100
13933333334 80  1000
13933333334 280 200
13933333335 200 1200
```

## 结果分析
最后应该统计出 5 个文件，分别保存 133，135，137，139 开头的手机号码对应的信息和其他字符开头手机号码对应的信息。对应关系如下：
```
133开头，0分区，0分区对应的文件
135开头，1分区，1分区对应的文件
137开头，2分区，2分区对应的文件
139开头，3分区，3分区对应的文件
其它开头，4分区，4分区对应的文件
```

## 基本流程
```
# 打包
mvn package

# 省略部分过程，与之前重合，创建文件夹、上传文件等

# 执行
hadoop jar partitioner-0.0.1-SNAPSHOT.jar FlowCount /user/ylhao/partitioner_flowcount/input /user/ylhao/partitioner_flowcount/output
```

## 结果分析
```
hdfs dfs -ls /user/ylhao/partitioner_flowcount/output

# 命令执行结果:
Found 6 items
-rw-r--r--   1 root supergroup          0 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/_SUCCESS
-rw-r--r--   1 root supergroup        166 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/part-r-00000
-rw-r--r--   1 root supergroup        171 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/part-r-00001
-rw-r--r--   1 root supergroup        166 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/part-r-00002
-rw-r--r--   1 root supergroup        171 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/part-r-00003
-rw-r--r--   1 root supergroup         61 2018-04-13 00:16 /user/ylhao/partitioner_flowcount/output/part-r-00004

hdfs dfs -cat /user/ylhao/partitioner_flowcount/output/part-r-00004

# 命令执行结果如下，可以看到，part-r-00004文件存的就是其它字符开头的手机号码对应的信息
13833333333     FlowBean [upFlow=1000, dFlow=1000, sumFlow=2000]

hdfs dfs -cat /user/ylhao/partitioner_flowcount/output/part-r-00000

# 命令执行结果如下：
13333333333     FlowBean [upFlow=20, dFlow=100, sumFlow=120]
13333333334     FlowBean [upFlow=60, dFlow=200, sumFlow=260]
13333333335     FlowBean [upFlow=20, dFlow=100, sumFlow=120]
```
