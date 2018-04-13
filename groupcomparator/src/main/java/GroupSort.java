import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GroupSort {
	static class SortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable> {
		OrderBean bean = new OrderBean();
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] fields = line.split(",");
			bean.setItemid(new Text(fields[0]));
			bean.setAmount(new DoubleWritable(Double.parseDouble(fields[1])));
			context.write(bean, NullWritable.get());
		}
	}
	static class SortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable> {

		@Override
		protected void reduce(OrderBean bean, Iterable<NullWritable> value,
				Reducer<OrderBean, NullWritable, OrderBean, NullWritable>.Context context)
				throws IOException, InterruptedException {
			context.write(bean, NullWritable.get());
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Job job = Job.getInstance();
		job.setJarByClass(GroupSort.class);
		
		// 设置本 Job 要用到的 Mapper/Reducer业务类
	    job.setMapperClass(SortMapper.class);
	    job.setReducerClass(SortReducer.class);
	    
	    //*********************************************************//
	    // 指定我们自定义的数据分区器
	    job.setPartitionerClass(ItemIdPartitioner.class);
	    // 同时指定分区数量个reducetask
	    job.setNumReduceTasks(2);
	    //*********************************************************//
	    
	    // 指定自定义的分组比较器
	    job.setGroupingComparatorClass(MyGroupComparator.class);
	    
	    // 设置 mapper 输出的 key/value 类型
	    job.setMapOutputKeyClass(OrderBean.class);
	    job.setMapOutputValueClass(NullWritable.class);
	    
	    // 设置 reducer 输出的 key/value 类型，也就是最终输出数据的 key/value 类型
	    job.setOutputKeyClass(OrderBean.class);
	    job.setOutputValueClass(NullWritable.class);   
		
	    // 设置输入输出的路径
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    
	    // 将 job 配置的相关的参数，以及 job 所用的 java 类所在的 jar 包，提交给 yarn 去运行
	    boolean res = job.waitForCompletion(true);
	    System.exit(res ? 0 : 1);	
	}
}
