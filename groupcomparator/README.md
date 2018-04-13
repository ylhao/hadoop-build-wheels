# 计算出每组订单中金额的最大记录

1. 自定义分组比较器
2. 需要了解 hadoop 如何进行分组和排序的
3. 自定义类型作为 Key 值

## 基本思路
自定义数据类型 OrderBean，继承了 WritableComparable 接口，实现了该接口的 compareTo 方法，然后用该数据类型作为 Key，Hadoop 默认根据 Key 的 compareTo 方法进行排序和分组。
自定义分组比较器类，订单相同则划为相同组，Hadoop 默认使用同一组的第一条记录的 Key 作为这组记录的 Key，而第一条记录对应的就是最大的订单金额。

## 实验过程
```
// 创建文件夹，准备数据
hdfs dfs -mkdir -p /user/ylhao/groupcomparator/input/
hdfs dfs -put file01 /user/ylhao/groupcomparator/input/

hdfs dfs -ls -R /user/ylhao/groupcomparator
// 结果
drwxr-xr-x   - root supergroup          0 2018-04-13 16:33 /user/ylhao/groupcomparator/input
-rw-r--r--   1 root supergroup        110 2018-04-13 16:33 /user/ylhao/groupcomparator/input/file01

// 执行
hadoop jar groupcomparator-0.0.1-SNAPSHOT.jar GroupSort /user/ylhao/groupcomparator/input /user/ylhao/groupcomparator/output

hdfs dfs -ls -R /user/ylhao/groupcomparator/output
// 结果
-rw-r--r--   1 root supergroup          0 2018-04-13 16:41 /user/ylhao/groupcomparator/output/_SUCCESS
-rw-r--r--   1 root supergroup         80 2018-04-13 16:41 /user/ylhao/groupcomparator/output/part-r-00000
-rw-r--r--   1 root supergroup         40 2018-04-13 16:41 /user/ylhao/groupcomparator/output/part-r-00001

hdfs dfs -cat /user/ylhao/groupcomparator/output/*
// 结果
OrderBean [itemid=order1, amount=888.0]
OrderBean [itemid=order3, amount=888.0]
OrderBean [itemid=order2, amount=666.0]

hdfs dfs -cat /user/ylhao/groupcomparator/output/part-r-00000
// 结果
OrderBean [itemid=order1, amount=888.0]
OrderBean [itemid=order3, amount=888.0]
```
