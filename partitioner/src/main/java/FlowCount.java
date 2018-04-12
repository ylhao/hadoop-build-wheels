import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * 要使用自定义的分区器，只需要在 main 函数中指定一下即可。
 * 流程：
 * 		按行读文件
 * 		maptask 每行调用都调用 map 方法
 * 		在 map 方法中调用 context.write 写出结果保存到本机的结果文件
 * 		保存的时候，分区器根据决定写出的这个结果保存在哪个分区
 *		……
 */
public class FlowCount {
	// 定义 Mapper 类子类，重写 map 方法
	public static class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean> {
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
				throws IOException, InterruptedException {
			// 这里假设每行文本的内容为phoneNbr upFlow dFlow
			// 将一行内容转成 String
			String line = value.toString();
			// 切分字段
			String[] fields = line.split("\t");
			// 取出手机号，字符串类型
			String phoneNbr = fields[0];
			// 取出上下行流量
			// 这里是将字符串转换成长整型
			long upFlow = Long.parseLong(fields[1]);
			long dFlow = Long.parseLong(fields[2]);
			// 输出
			context.write(new Text(phoneNbr), new FlowBean(upFlow, dFlow));
		}
	}
	// 定义 Reducer 类子类，重写 reduce 方法
	public static class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean> {
		@Override
		protected void reduce(Text key, Iterable<FlowBean> values, Reducer<Text, FlowBean, Text, FlowBean>.Context context)
				throws IOException, InterruptedException {
			long sum_upFlow = 0;
			long sum_dFlow = 0;
			// 遍历所有的 bean，将其中的上行流量和下行流量累加
			for(FlowBean bean : values) {
				sum_upFlow += bean.getUpFlow();
				sum_dFlow += bean.getdFlow();
			}
			context.write(key, new FlowBean(sum_upFlow, sum_dFlow));
		}
	}
	// 定义 main 方法
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);

		// 设置 Job 运行时的类
		job.setJarByClass(FlowCount.class);

		// 设置本 Job 要用到的 Mapper/Reducer业务类
	    job.setMapperClass(FlowCountMapper.class);
	    job.setReducerClass(FlowCountReducer.class);

	    //*********************************************************//
	    // 指定我们自定义的数据分区器
	    job.setPartitionerClass(ProvincePartitioner.class);
	    // 同时指定分区数量个reducetask
	    job.setNumReduceTasks(5);
	    //*********************************************************//

	    // 设置 mapper 输出的 key/value 类型
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(FlowBean.class);

	    // 设置 reducer 输出的 key/value 类型，也就是最终输出数据的 key/value 类型
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(FlowBean.class);

	    // 设置输入输出的路径
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));

	    // 将 job 配置的相关的参数，以及 job 所用的 java 类所在的 jar 包，提交给 yarn 去运行
	    boolean res = job.waitForCompletion(true);
	    System.exit(res ? 0 : 1);

	}
}
